package com.syxy.protocol.mqttImp.process;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import sun.util.logging.resources.logging;

import com.mysql.jdbc.Util;
import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnAckMessage.ConnectionStatus;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.DisconnectMessage;
import com.syxy.protocol.mqttImp.message.PingReqMessage;
import com.syxy.protocol.mqttImp.message.PingRespMessage;
import com.syxy.protocol.mqttImp.message.PubAckMessage;
import com.syxy.protocol.mqttImp.message.PubRecMessage;
import com.syxy.protocol.mqttImp.message.PubRelMessage;
import com.syxy.protocol.mqttImp.message.PubcompMessage;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.protocol.mqttImp.message.SubAckMessage;
import com.syxy.protocol.mqttImp.message.SubscribeMessage;
import com.syxy.protocol.mqttImp.message.UnSubAckMessage;
import com.syxy.protocol.mqttImp.message.UnSubscribeMessage;
import com.syxy.protocol.mqttImp.process.Impl.dataHandler.DBPersistentStore;
import com.syxy.protocol.mqttImp.process.Interface.IAuthenticator;
import com.syxy.protocol.mqttImp.process.Interface.IMessagesStore;
import com.syxy.protocol.mqttImp.process.Interface.ISessionStore;
import com.syxy.protocol.mqttImp.process.event.PublishEvent;
import com.syxy.protocol.mqttImp.process.subscribe.SubscribeStore;
import com.syxy.protocol.mqttImp.process.subscribe.Subscription;
import com.syxy.server.ClientSession;
import com.syxy.util.Constant;
import com.syxy.util.StringTool;

/**
 * <li>说明 协议所有的业务处理都在此类，注释中所指协议为MQTT3.3.1协议
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */
public class ProtocolProcess {

	//遗嘱信息类
	static final class WillMessage {
        private final String topic;
        private final byte[] payload;
        private final boolean retained;
        private final QoS qos;

        public WillMessage(String topic, byte[] payload, boolean retained, QoS qos) {
            this.topic = topic;
            this.payload = payload;
            this.retained = retained;
            this.qos = qos;
        }

        public String getTopic() {
            return topic;
        }

        public byte[] getPayload() {
            return payload;
        }

        public boolean isRetained() {
            return retained;
        }

        public QoS getQos() {
            return qos;
        }
    }
	
	private final static Logger Log = Logger.getLogger(ProtocolProcess.class);
	
	private ConcurrentHashMap<Object, ConnectionDescriptor> clients = new ConcurrentHashMap<Object, ConnectionDescriptor>();// 客户端链接映射表
    //存储遗嘱信息，通过ID映射遗嘱信息
	private ConcurrentHashMap<String, WillMessage> willStore = new ConcurrentHashMap<>();
	
	private IAuthenticator authenticator;
	private IMessagesStore messagesStore;
	private ISessionStore sessionStore;
	private SubscribeStore subscribeStore;
	
	public ProtocolProcess(IAuthenticator authenticator, IMessagesStore messagesStore,
			ISessionStore sessionStore){
		this.authenticator = authenticator;
		this.messagesStore = messagesStore;
		this.sessionStore = sessionStore;
		this.subscribeStore = new SubscribeStore();
	}
	
	/**
	 * <li>方法名 processConnect
	 * <li>@param client
	 * <li>@param connectMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的CONNECT消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-7
	 */
	public void processConnect(ClientSession client, ConnectMessage connectMessage){
		Log.info("处理Connect的数据");
		//首先查看保留位是否为0，不为0则断开连接,协议P24
		if (!connectMessage.isReservedIsZero()) {
			client.close();
			return;
		}
		//处理protocol name和protocol version, 如果返回码!=0，sessionPresent必为0，协议P24,P32
		if (!connectMessage.getProtocolName().equals("MQTT") || connectMessage.getProtocolVersionNumber() != 4 ) {
			client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.UNACCEPTABLE_PROTOCOL_VERSION, 0));
			client.close();//版本或协议名不匹配，则断开该客户端连接
			return;
		}
		
		//处理clientID为null或长度为0的情况，协议P29
		if (connectMessage.getClientId() == null || connectMessage.getClientId().length() == 0) {
			//clientID为null的时候，cleanSession只能为1,此时给client设置一个随即的mac地址为ID，否则，断开连接
			if (connectMessage.isCleanSession()) {
//				connectMessage.setClientId(StringTool.generalRandomString(23));
				connectMessage.setClientId(StringTool.generalMacString());
			} else {
				Log.info("客户端ID为空，cleanSession为0，根据协议，不接收此客户端");
				client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.IDENTIFIER_REJECTED, 0));
				client.close();
				return;
			}
		}
		
		//检查clientID的格式符合与否
		if (!StringTool.isMacString(connectMessage.getClientId())) {
			Log.info("客户端ID为{"+connectMessage.getClientId()+"}，拒绝此客户端");
			client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.IDENTIFIER_REJECTED, 0));
			client.close();
			return;
		}
		
		//如果会话中已经存储了这个新连接的ID，就关闭之前的clientID
		if (clients.containsKey(connectMessage.getClientId())) {
			Log.error("客户端ID{"+connectMessage.getClientId()+"}已存在，强制关闭老连接");
			ClientSession oldClientSession = clients.get(connectMessage.getClientId()).getClient();
			boolean cleanSession = (Boolean)oldClientSession.getAttributesKeys(Constant.CLEAN_SESSION); 
			if (cleanSession) {
				cleanSession(connectMessage.getClientId());
			}
			oldClientSession.close();
		}
		
		//若至此没问题，则将新客户端连接加入client的维护列表中
		ConnectionDescriptor connectionDescriptor = 
				new ConnectionDescriptor(connectMessage.getClientId(), client, connectMessage.isCleanSession());
		this.clients.put(connectMessage.getClientId(), connectionDescriptor);
		//处理心跳包时间，把心跳包时长和一些其他属性都添加到会话中，方便以后使用
		int keepAlive = connectMessage.getKeepAlive();
		Log.debug("连接的心跳包时长是 {" + keepAlive + "} s");
		client.setAttributesKeys(Constant.CLIENT_ID, connectMessage.getClientId());//clientID属性用于subscribe和publish的处理
		client.setAttributesKeys(Constant.CLEAN_SESSION, connectMessage.isCleanSession());
		//协议P29规定，在超过1.5个keepAlive的时间以上没收到心跳包PingReq，就断开连接(但这里要注意把单位是s转为ms)
		client.setAttributesKeys(Constant.KEEP_ALIVE, keepAlive *1000);
		//开启心跳包计时验证
		client.keepAliveHandler(Constant.CONNECT_ARRIVE);
		
		//处理Will flag（遗嘱信息）,协议P26
		if (connectMessage.isHasWill()) {
			QoS willQos = connectMessage.getWillQoS();
			byte[] willPayload = connectMessage.getWillMessage().getBytes();//获取遗嘱信息的具体内容
			WillMessage will = new WillMessage(connectMessage.getWillTopic(),
					willPayload, connectMessage.isWillRetain(),willQos);
			//把遗嘱信息与和其对应的的clientID存储在一起
			willStore.put(connectMessage.getClientId(), will);
		}
		//处理身份验证（userNameFlag和passwordFlag）
		if (connectMessage.isHasUsername() && connectMessage.isHasPassword()) {
			String userName = connectMessage.getUsername();
			String pwd = connectMessage.getPassword();
			//此处对用户名和密码做验证
			if (!authenticator.checkValid(userName, pwd)) {
				ConnAckMessage badAckMessage = new ConnAckMessage(ConnectionStatus.BAD_USERNAME_OR_PASSWORD, 0);
				client.writeMsgToReqClient(badAckMessage);
				return;
			}
		}
		
		//处理cleanSession为1的情况
        if (connectMessage.isCleanSession()) {
            //移除所有之前的session并开启一个新的，并且原先保存的subscribe之类的都得从服务器删掉
            cleanSession(connectMessage.getClientId());
        }
        
        //TODO 此处生成一个token(以后每次客户端每次请求服务器，都必须先验证此token正确与否)，并把token保存到本地以及传回给客户端
        //鉴权获取不应该在这里做
        
//        String token = StringTool.generalRandomString(32);
//        sessionStore.addSession(connectMessage.getClientId(), token);
//        //把荷载封装成json字符串
//        JSONObject jsonObject = new JSONObject();
//        try {
//			jsonObject.put("token", token);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
        
        //处理回写的CONNACK,并回写，协议P29
        ConnAckMessage okResp = new ConnAckMessage();
        okResp.setStatus(ConnAckMessage.ConnectionStatus.ACCEPTED);
        //协议32,session present的处理
        if (!connectMessage.isCleanSession() && sessionStore.contains(connectMessage.getClientId())) {
        	okResp.setSessionPresent(1);
		}else{
			okResp.setSessionPresent(0);
		}
//        okResp.setPayload(jsonObject.toString());
        client.writeMsgToReqClient(okResp);
        Log.info("CONNACK处理完毕并成功发送");
        Log.info("连接的客户端clientID="+connectMessage.getClientId()+", cleanSession为"+connectMessage.isCleanSession());
        
        //如果cleanSession=0,需要在重连的时候重发同一clientID存储在服务端的离线信息
        if (!connectMessage.isCleanSession()) {
            //force the republish of stored QoS1 and QoS2
        	republishMessage(connectMessage.getClientId());
        }
	}
	
	/**
	 * <li>方法名 processPublic
	 * <li>@param client
	 * <li>@param publishMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的publish消息类型,该方法先把public需要的事件提取出来
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-18
	 */
	public void processPublic(ClientSession client, PublishMessage publishMessage){
		Log.info("处理publish的数据");
		String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		final String topic = publishMessage.getTopic();
	    final QoS qos = publishMessage.getQos();
	    final byte[] message = publishMessage.getData();
	    boolean retain = publishMessage.isRetain();
	    final int packgeID = publishMessage.getPackgeID();
	    Log.info("收到的荷载为"+message.toString());
	    processPublic(clientID, topic, qos, message, retain, packgeID);
	}
	
	/**
	 * <li>方法名 processPublic
	 * <li>@param client
	 * <li>@param willMessage
	 * <li>返回类型 void
	 * <li>说明 处理遗言消息的发送
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-26
	 */
	public void processPublic(ClientSession client, WillMessage willMessage){
		Log.info("处理遗言的publish数据");
		String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		final String topic = willMessage.getTopic();
	    final QoS qos = willMessage.getQos();
	    final byte[] message = willMessage.getPayload();
	    boolean retain = willMessage.isRetained();
	    
	    processPublic(clientID, topic, qos, message, retain, null);
	}
	
	/**
	 * <li>方法名 processPublic
	 * <li>@param client
	 * <li>@param topic
	 * <li>@param qos
	 * <li>@param message
	 * <li>@param retain
	 * <li>@param PackgeID
	 * <li>返回类型 void
	 * <li>说明 根据协议进行具体的处理，处理不同的Qos等级下的public事件
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-19
	 */
	private void processPublic(String clientID, String topic, QoS qos, byte[] message, boolean retain, Integer packgeID){
		Log.info("接收public消息:{clientID="+clientID+",Qos="+qos+",topic="+topic+",packageID="+packgeID+"}");
		String publishKey = null;
		
		//根据协议P52，qos=0，则把消息发送给所有注册的客户端即可
		if (qos == QoS.AT_MOST_ONCE) {
			sendPublishMessage(topic, qos, message, retain, packgeID);
		}
		
		//根据协议P53，publish的接受者需要发送该publish(Qos=1,Dup=0)消息给其他客户端，然后发送pubAck给该客户端。
		//发送该publish消息时候，按此流程： 存储消息→发送给所有人→等待pubAck到来→删除消息
		if (qos == QoS.AT_LEAST_ONCE) {
			publishKey = String.format("%s%d", clientID, packgeID);//针对每个重生成key，保证消息ID不会重复
			PublishEvent storePubEvent = new PublishEvent(topic, qos, message, retain,
                    clientID, packgeID);
			messagesStore.storeTempMessageForPublish(publishKey, storePubEvent);
			sendPublishMessage(topic, qos, message, retain, packgeID);
			sendPubAck(clientID, packgeID);
		}
		
		//根据协议P54，P55
		//接收端：publish接收消息→存储包ID→发给其他客户端→发回pubRec→收到pubRel→抛弃第二步存储的包ID→发回pubcomp
		//发送端：存储消息→发送publish(Qos=2,Dup=0)→收到pubRec→抛弃第一步存储的消息→存储pubRec的包ID→发送pubRel→收到pubcomp→抛弃pubRec包ID的存储
		if (qos == QoS.EXACTLY_ONCE) {
			publishKey = String.format("%s%d", clientID, packgeID);//针对每个重生成key，保证消息ID不会重复
			
			messagesStore.storePublicPackgeID(clientID, packgeID);
			
			PublishEvent pubEvent = new PublishEvent(topic, qos, message, retain, clientID, packgeID);
			messagesStore.storeTempMessageForPublish(publishKey, pubEvent);
			sendPublishMessage(topic, qos, message, retain, packgeID);
			
			sendPubRec(clientID, packgeID);
		}
		
		if (retain) {
			if (qos == QoS.AT_MOST_ONCE) {
				messagesStore.cleanRetained(topic);
			} else {
				messagesStore.storeRetained(topic, message, qos);
			}
		}
	}
	
	/**
	 * <li>方法名 processPubAck
	 * <li>@param client
	 * <li>@param pubAckMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的pubAck消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-21
	 */
	public void processPubAck(ClientSession client, PubAckMessage pubAckMessage){		
		 String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		 int packgeID = pubAckMessage.getPackgeID();
		 String publishKey = String.format("%s%d", clientID, packgeID);
		 messagesStore.removeTempMessageForPublish(publishKey);
	}
	
	/**
	 * <li>方法名 processPubRec
	 * <li>@param client
	 * <li>@param pubRecMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的pubRec消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-23
	 */
	public void processPubRec(ClientSession client, PubRecMessage pubRecMessage){
		 String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		 int packgeID = pubRecMessage.getPackgeID();
		 String publishKey = String.format("%s%d", clientID, packgeID);
		 messagesStore.removeTempMessageForPublish(publishKey);
		 //此处须额外处理，根据不同的事件，处理不同的包ID
		 messagesStore.storePubRecPackgeID(clientID, packgeID);
		 //发回PubRel
		 sendPubRel(clientID, packgeID);
	}
	
	/**
	 * <li>方法名 processPubRel
	 * <li>@param client
	 * <li>@param pubRelMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的pubRel消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-23
	 */
	public void processPubRel(ClientSession client, PubRelMessage pubRelMessage){
		 String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		 //删除的是接收端的包ID
		 int packgeID = pubRelMessage.getPackgeID();
		 
		 messagesStore.removePublicPackgeID(clientID);
		 sendPubComp(clientID, packgeID);
	}
	
	/**
	 * <li>方法名 processPubComp
	 * <li>@param client
	 * <li>@param pubcompMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的pubComp消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-23
	 */
	public void processPubComp(ClientSession client, PubcompMessage pubcompMessage){
		 String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		 //删除存储的PubRec包ID
		 messagesStore.removePubRecPackgeID(clientID);
	}
	
	/**
	 * <li>方法名 processSubscribe
	 * <li>@param client
	 * <li>@param subscribeMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的subscribe消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-24
	 * @throws Exception 
	 */
	public void processSubscribe(ClientSession client, SubscribeMessage subscribeMessage) { 
		 String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		 boolean cleanSession = (Boolean) client.getAttributesKeys(Constant.CLEAN_SESSION);
		 Log.info("处理subscribe数据包，客户端ID={"+clientID+"},cleanSession={"+cleanSession+"}");
		 //一条subscribeMessage信息可能包含多个Topic和Qos
		 List<String> topicFilters = subscribeMessage.getTopicFilter();
		 List<QoS> Qoss = subscribeMessage.getRequestQos();
		 if (topicFilters.size() == Qoss.size()) {
			//依次处理订阅
			for (int i = 0; i < topicFilters.size(); i++) {
				QoS qos = Qoss.get(i);
				String topic = topicFilters.get(i);
				Subscription newSubscription = new Subscription(clientID, topic, qos, cleanSession);
				//订阅新的订阅
				subscribeSingleTopic(newSubscription, topic);
			}
			SubAckMessage subAckMessage = new SubAckMessage(subscribeMessage.getPackgeID());
			 for (int i = 0; i < Qoss.size(); i++) {
				 QoS qos = Qoss.get(i);
				 subAckMessage.addGrantedQoSs(qos);
			 }
			 Log.info("回写subAck消息给订阅者，包ID={"+subscribeMessage.getPackgeID()+"}");
			 client.writeMsgToReqClient(subAckMessage);
		 }else{
			try {
				throw new Exception("订阅的主题和Qos数量不等，终端订阅");
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
	}
	
	/**
	 * <li>方法名 processUnSubscribe
	 * <li>@param client
	 * <li>@param unSubscribeMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的unSubscribe消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-24
	 */
	public void processUnSubscribe(ClientSession client, UnSubscribeMessage unSubscribeMessage){
		 String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		 int packgeID = unSubscribeMessage.getPackgeID();
		 Log.info("处理unSubscribe数据包，客户端ID={"+clientID+"}");
		 List<String> topicFilters = unSubscribeMessage.getTopicFilter();
		 for (String topic : topicFilters) {
			//TODO 取消订阅树里的订阅
			subscribeStore.removeSubscription(topic, clientID);
			sessionStore.removeSubscription(topic, clientID);
		 }
		 
		 UnSubAckMessage unSubAckMessage = new UnSubAckMessage(packgeID);
		 Log.info("回写unSubAck信息给客户端，包ID为{"+packgeID+"}");
		 client.writeMsgToReqClient(unSubAckMessage);
	}
	
	/**
	 * <li>方法名 processPingReq
	 * <li>@param client
	 * <li>@param pingReqMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的pingReq消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-24
	 */
	public void processPingReq(ClientSession client, PingReqMessage pingReqMessage){
		 Log.info("收到心跳包");
		 PingRespMessage pingRespMessage = new PingRespMessage();
		 //重置心跳包计时器
		 client.keepAliveHandler(Constant.PING_ARRIVE);
		 client.writeMsgToReqClient(pingRespMessage);
	}
	
	/**
	 * <li>方法名 processDisconnect
	 * <li>@param client
	 * <li>@param disconnectMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的disconnect消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-24
	 */
	public void processDisconnet(ClientSession client, DisconnectMessage disconnectMessage){
		 String clientID = (String) client.getAttributesKeys(Constant.CLIENT_ID);
		 boolean cleanSession = (Boolean) client.getAttributesKeys(Constant.CLEAN_SESSION);
		 if (cleanSession) {
			cleanSession(clientID);
		 }
		 
		 //如果有遗言消息，就发遗言出去
		 if (willStore.containsKey(clientID)) {
			WillMessage will = willStore.get(clientID);
			processPublic(client, will);
			willStore.remove(clientID);
		 }
		 
		 this.clients.remove(clientID);
		 client.close();
	}
	
	/**
	 * <li>方法名 cleanSession
	 * <li>@param clientID
	 * <li>返回类型 void
	 * <li>说明 清除会话，除了要从订阅树中删掉会话信息，还要从会话存储中删除会话信息
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-07
	 */
	private void cleanSession(String clientID) {
		subscribeStore.removeForClient(clientID);
		//从会话存储中删除信息
		sessionStore.wipeSubscriptions(clientID);
	}
	
	/**
	 * <li>方法名 republishMessage
	 * <li>@param clientID
	 * <li>返回类型 void
	 * <li>说明 在客户端重连以后，针对QoS1和Qos2的消息，重发存储的离线消息
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-18
	 */
	private void republishMessage(String clientID){
		//取出需要重发的消息列表
		//查看消息列表是否为空，为空则返回
		//不为空则依次发送消息并从会话中删除此消息
		List<PublishEvent> publishedEvents = messagesStore.listMessagesInSession(clientID);
		if (publishedEvents.isEmpty()) {
			Log.info("没有客户端{"+clientID+"}存储的离线消息");
			return;
		}
		
		Log.info("重发客户端{"+ clientID +"}存储的离线消息");
		for (PublishEvent pubEvent : publishedEvents) {
			sendPublishMessage(pubEvent.getClientID(), 
							   pubEvent.getTopic(), 
							   pubEvent.getQos(), 
							   pubEvent.getMessage(), 
							   pubEvent.isRetain(), 
							   pubEvent.getPackgeID());
			messagesStore.removeMessageInSessionForPublish(clientID, pubEvent.getPackgeID());
		}
	}
	
	/**
	 * <li>方法名 sendPulicMessage
	 * <li>@param topic
	 * <li>@param qos
	 * <li>@param message
	 * <li>@param retain
	 * <li>@param PackgeID
	 * <li>返回类型 void
	 * <li>说明 取出所有匹配topic的客户端，然后发送public消息给客户端
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-19
	 */
	private void sendPublishMessage(String topic, QoS qos, byte[] message, boolean retain, Integer packgeID){
		Log.info("发送pulicMessage给客户端");
		for (final Subscription sub : subscribeStore.getClientListFromTopic(topic)) {
			//协议P43提到， 假设请求的QoS级别被授权，客户端接收的PUBLISH消息的QoS级别小于或等于这个级别，PUBLISH 消息的级别取决于发布者的原始消息的QoS级别
			if (qos.ordinal() > sub.getRequestedQos().ordinal()) {
				qos = sub.getRequestedQos(); 
			}
			String clientID = sub.getClientID();
			
			Log.info("服务器发送消息给客户端{"+clientID+"},topic{"+topic+"},qos{"+qos+"}");
			
			PublishMessage publishMessage = new PublishMessage();
			publishMessage.setRetain(retain);
			publishMessage.setTopic(topic);
			publishMessage.setQos(qos);
			publishMessage.setData(message);
			
			if (publishMessage.getQos() != QoS.AT_MOST_ONCE) {
				publishMessage.setPackgeID(packgeID);
			}
			
			if (!sub.isCleanSession()) {
				 PublishEvent newPublishEvt = new PublishEvent(topic, qos, message, retain, sub.getClientID(), packgeID != null ? packgeID : 0);
                 messagesStore.storeMessageToSessionForPublish(newPublishEvt);
			}
			
			if (this.clients == null) {
				throw new RuntimeException("内部错误，clients为null");
			} else {
				Log.debug("clients为{"+this.clients+"}");
			}
			
			if (this.clients.get(clientID) == null) {
				throw new RuntimeException("不能从会话列表{"+this.clients+"}中找到clientID:{"+clientID+"}");
			} else {
				Log.debug("从会话列表{"+this.clients+"}查找到clientID:{"+clientID+"}");
			}
			
			//从会话列表中取出会话，然后通过此会话发送publish消息
			this.clients.get(clientID).getClient().writeMsgToReqClient(publishMessage);
		}
	}
	
	/**
	 * <li>方法名 sendPulicMessage
	 * <li>@param topic
	 * <li>@param qos
	 * <li>@param message
	 * <li>@param retain
	 * <li>@param PackgeID
	 * <li>返回类型 void
	 * <li>说明 发送publish消息给指定客户端
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-19
	 */
	private void sendPublishMessage(String clientID, String topic, QoS qos, byte[] message, boolean retain, Integer packgeID){
		Log.info("发送pulicMessage给指定客户端");
			
		PublishMessage publishMessage = new PublishMessage();
		publishMessage.setRetain(retain);
		publishMessage.setTopic(topic);
		publishMessage.setQos(qos);
		publishMessage.setData(message);
		
		if (publishMessage.getQos() != QoS.AT_MOST_ONCE) {
			publishMessage.setPackgeID(packgeID);
		}
		
		if (this.clients == null) {
			throw new RuntimeException("内部错误，clients为null");
		} else {
			Log.debug("clients为{"+this.clients+"}");
		}
		
		if (this.clients.get(clientID) == null) {
			throw new RuntimeException("不能从会话列表{"+this.clients+"}中找到clientID:{"+clientID+"}");
		} else {
			Log.debug("从会话列表{"+this.clients+"}查找到clientID:{"+clientID+"}");
		}
		
		//从会话列表中取出会话，然后通过此会话发送publish消息
		this.clients.get(clientID).getClient().writeMsgToReqClient(publishMessage);
	}
	
	/**
	 * <li>方法名 sendPubAck
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回类型 void
	 * <li>说明 回写PubAck消息给发来publish的客户端
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-21
	 */
	private void sendPubAck(String clientID, Integer packgeID) {
	        Log.info("发送PubAck消息给客户端");

	        PubAckMessage pubAckMessage = new PubAckMessage();
	        pubAckMessage.setPackgeID(packgeID);
	        
	        try {
	        	if (this.clients == null) {
					throw new RuntimeException("内部错误，clients为null");
				} else {
					Log.debug("clients为{"+this.clients+"}");
				}
	        	
	        	if (this.clients.get(clientID) == null) {
					throw new RuntimeException("不能从会话列表{"+this.clients+"}中找到clientID:{"+clientID+"}");
				} else {
					Log.debug("从会话列表{"+this.clients+"}查找到clientID:{"+clientID+"}");
				}	            
	        	
				this.clients.get(clientID).getClient().writeMsgToReqClient(pubAckMessage);
	        }catch(Throwable t) {
	            Log.error(null, t);
	        }
	    }
	
	/**
	 * <li>方法名 sendPubRec
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回类型 void
	 * <li>说明 回写PubRec消息给发来publish的客户端
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-21
	 */
	private void sendPubRec(String clientID, Integer packgeID) {
	        Log.trace("发送PubRec消息给客户端");

	        PubRecMessage pubRecMessage = new PubRecMessage();
	        pubRecMessage.setPackgeID(packgeID);
	        
	        try {
	        	if (this.clients == null) {
					throw new RuntimeException("内部错误，clients为null");
				} else {
					Log.debug("clients为{"+this.clients+"}");
				}
	        	
	        	if (this.clients.get(clientID) == null) {
					throw new RuntimeException("不能从会话列表{"+this.clients+"}中找到clientID:{"+clientID+"}");
				} else {
					Log.debug("从会话列表{"+this.clients+"}查找到clientID:{"+clientID+"}");
				}	            
	        	
	        	this.clients.get(clientID).getClient().writeMsgToReqClient(pubRecMessage);
	        }catch(Throwable t) {
	            Log.error(null, t);
	        }
	    }
	
	/**
	 * <li>方法名 sendPubRel
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回类型 void
	 * <li>说明 回写PubRel消息给发来publish的客户端
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-23
	 */
	private void sendPubRel(String clientID, Integer packgeID) {
	        Log.trace("发送PubRel消息给客户端");

	        PubRelMessage pubRelMessage = new PubRelMessage();
	        pubRelMessage.setPackgeID(packgeID);
	        
	        try {
	        	if (this.clients == null) {
					throw new RuntimeException("内部错误，clients为null");
				} else {
					Log.debug("clients为{"+this.clients+"}");
				}
	        	
	        	if (this.clients.get(clientID) == null) {
					throw new RuntimeException("不能从会话列表{"+this.clients+"}中找到clientID:{"+clientID+"}");
				} else {
					Log.debug("从会话列表{"+this.clients+"}查找到clientID:{"+clientID+"}");
				}	            
	        	
	        	this.clients.get(clientID).getClient().writeMsgToReqClient(pubRelMessage);
	        }catch(Throwable t) {
	            Log.error(null, t);
	        }
	    }
	
	/**
	 * <li>方法名 sendPubComp
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回类型 void
	 * <li>说明 回写PubRel消息给发来publish的客户端
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-23
	 */
	private void sendPubComp(String clientID, Integer packgeID) {
	        Log.trace("发送PubComp消息给客户端");

	        PubcompMessage pubcompMessage = new PubcompMessage();
	        pubcompMessage.setPackgeID(packgeID);
	        
	        try {
	        	if (this.clients == null) {
					throw new RuntimeException("内部错误，clients为null");
				} else {
					Log.debug("clients为{"+this.clients+"}");
				}
	        	
	        	if (this.clients.get(clientID) == null) {
					throw new RuntimeException("不能从会话列表{"+this.clients+"}中找到clientID:{"+clientID+"}");
				} else {
					Log.debug("从会话列表{"+this.clients+"}查找到clientID:{"+clientID+"}");
				}	            
	        	
	        	this.clients.get(clientID).getClient().writeMsgToReqClient(pubcompMessage);
	        }catch(Throwable t) {
	            Log.error(null, t);
	        }
	    }
	
	/**
	 * <li>方法名 subscribeSingleTopic
	 * <li>@param newSubscription
	 * <li>@param topic
	 * <li>返回类型 void
	 * <li>说明 处理一个单一订阅，存储到会话和订阅数
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-25
	 */
	private void subscribeSingleTopic(Subscription newSubscription, final String topic){
		Log.info("订阅topic{"+topic+"},Qos为{"+newSubscription.getRequestedQos()+"}");
		String clientID = newSubscription.getClientID();
		sessionStore.addNewSubscription(newSubscription, clientID);
		subscribeStore.addSubscrpition(newSubscription);
		//TODO 此处还需要将此订阅之前存储的信息发出去
	}
}
