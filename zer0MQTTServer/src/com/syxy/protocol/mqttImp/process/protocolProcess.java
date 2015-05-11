package com.syxy.protocol.mqttImp.process;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnAckMessage.ConnectionStatus;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.process.Impl.IAuthenticator;
import com.syxy.protocol.mqttImp.process.Impl.IMessagesStore;
import com.syxy.protocol.mqttImp.process.Impl.ISessionStore;
import com.syxy.protocol.mqttImp.process.subscribe.SubscribeStore;
import com.syxy.server.ClientSession;
import com.syxy.util.Constant;
import com.syxy.util.StringTool;

/**
 * <li>说明 协议所有的业务处理都在此类，注释中所指协议为MQTT3.3.1协议
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */
public class protocolProcess {

	//遗嘱信息类
	static final class WillMessage {
        private final String topic;
        private final ByteBuffer payload;
        private final boolean retained;
        private final QoS qos;

        public WillMessage(String topic, ByteBuffer payload, boolean retained, QoS qos) {
            this.topic = topic;
            this.payload = payload;
            this.retained = retained;
            this.qos = qos;
        }

        public String getTopic() {
            return topic;
        }

        public ByteBuffer getPayload() {
            return payload;
        }

        public boolean isRetained() {
            return retained;
        }

        public QoS getQos() {
            return qos;
        }
        
    }
	
	private final static Logger Log = Logger.getLogger(protocolProcess.class);
	
	private ConcurrentHashMap<Object, ConnectionDescriptor> clients = new ConcurrentHashMap<Object, ConnectionDescriptor>();// 客户端链接映射表
    //存储遗嘱信息，通过ID映射遗嘱信息
	private ConcurrentHashMap<String, WillMessage> willStore = new ConcurrentHashMap<>();
	
	private IAuthenticator authenticator;
	private IMessagesStore messagesStore;
	private ISessionStore sessionStore;
	private SubscribeStore subscribeStore;
	
	/**
	 * <li>方法名 init
	 * <li>@param authenticator 该参数用于做权限管理
	 * <li>返回类型 void
	 * <li>说明 初始化处理程序
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-8
	 */
	void init(IAuthenticator authenticator){
		this.authenticator = authenticator;
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
		}
		//处理protocol name和protocol version, 如果返回码!=0，sessionPresent必为0，协议P24,P32
		if (!connectMessage.getProtocolName().equals("MQTT") || connectMessage.getProtocolVersionNumber() != 4 ) {
			client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.UNACCEPTABLE_PROTOCOL_VERSION, 0));
			client.close();//版本或协议名不匹配，则断开该客户端连接
		}
		//处理clientID为null或长度为0的情况，协议P29
		if (connectMessage.getClientId() == null || connectMessage.getClientId().length() == 0) {
			//clientID为null的时候，cleanSession只能为1,此时给client设置一个23位的ID，否则，断开连接
			if (connectMessage.isCleanSession()) {
				connectMessage.setClientId(StringTool.getRandomString(23));
			} else {
				Log.info("客户端ID为空，cleanSession为0，根据协议，不接收此客户端");
				client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.IDENTIFIER_REJECTED, 0));
				client.close();
			}
		}
		//若至此没问题，则将新客户端连接加入client的维护列表中
		ConnectionDescriptor connectionDescriptor = 
				new ConnectionDescriptor(connectMessage.getClientId(), client, connectMessage.isCleanSession());
		this.clients.put(connectMessage.getClientId(), connectionDescriptor);
		//处理心跳包时间，把心跳包时长和一些其他属性都添加到会话中，方便以后使用
		int keepAlive = connectMessage.getKeepAlive();
		Log.debug("Connect with keepAlive {" + keepAlive + "} s");
		client.setAttributesKeys(Constant.CLIENT_ID, connectMessage.getClientId());//clientID属性用于subscribe和publish的处理
		client.setAttributesKeys(Constant.CLEAN_SESSION, connectMessage.isCleanSession());
		client.setAttributesKeys(Constant.KEEP_ALIVE, keepAlive);
		//协议P29规定，在超过1.5个keepAlive的时间以上没收到心跳包PingReq，就断开连接
//		client.setIdleTime(Math.round(keepAlive * 1.5f));
		
		//处理Will flag（遗嘱信息）,协议P26
		if (connectMessage.isHasWill()) {
			QoS willQos = connectMessage.getWillQoS();
			byte[] willPayload = connectMessage.getWillMessage().getBytes();//获取遗嘱信息的具体内容
			ByteBuffer byteBuffer = (ByteBuffer) ByteBuffer.allocate(willPayload.length).put(willPayload).flip();
			WillMessage will = new WillMessage(connectMessage.getWillTopic(),
					byteBuffer, connectMessage.isWillRetain(),willQos);
			//把遗嘱信息与和其对应的的clientID存储在一起
			willStore.put(connectMessage.getClientId(), will);
		}
		//处理身份验证（userNameFlag和passwordFlag）
		if (connectMessage.isHasUsername()) {
			String userName = connectMessage.getUsername();
			String pwd = null;
			if (connectMessage.isHasPassword()) {
				 pwd = connectMessage.getPassword();
			}
			//此处对用户名和密码做验证
			if (!authenticator.checkValid(userName, pwd)) {
				ConnAckMessage badAckMessage = new ConnAckMessage(ConnectionStatus.BAD_USERNAME_OR_PASSWORD, 0);
				client.writeMsgToReqClient(badAckMessage);
			}
		}
		
		//处理cleanSession为1的情况
        if (connectMessage.isCleanSession()) {
            //移除所有之前的session并开启一个新的，并且原先保存的subscribe之类的都得从服务器删掉
            cleanSession(connectMessage.getClientId());
        }
        
        //处理回写的CONNACK,并回写，协议P29
        ConnAckMessage okResp = new ConnAckMessage();
        okResp.setStatus(ConnAckMessage.ConnectionStatus.ACCEPTED);
        //协议32,session present的处理
        if (!connectMessage.isCleanSession() && sessionStore.contains(connectMessage.getClientId())) {
        	okResp.setSessionPresent(1);
		}else{
			okResp.setSessionPresent(0);
		}
        client.writeMsgToReqClient(okResp);
        Log.info("CONNACK处理完毕并成功发送");
        Log.info("连接的客户端clientID="+connectMessage.getClientId()+", cleanSession为"+connectMessage.isCleanSession());
        
        //如果cleanSession=0,需要在重连的时候重发同一clientID存储在服务端的离线信息
        if (!connectMessage.isCleanSession()) {
            //force the republish of stored QoS1 and QoS2
//            republishStoredInSession(msg.getClientID());
        }
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
		//TODO 未从会话存储中删除信息
	}
}
