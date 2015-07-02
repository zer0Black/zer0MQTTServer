package com.syxy.protocol.mqttImp;

import com.syxy.protocol.IProcessHandler;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.DisconnectMessage;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.PingReqMessage;
import com.syxy.protocol.mqttImp.message.PubAckMessage;
import com.syxy.protocol.mqttImp.message.PubRecMessage;
import com.syxy.protocol.mqttImp.message.PubRelMessage;
import com.syxy.protocol.mqttImp.message.PubcompMessage;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.protocol.mqttImp.message.SubscribeMessage;
import com.syxy.protocol.mqttImp.message.UnSubscribeMessage;
import com.syxy.protocol.mqttImp.process.ProtocolProcess;
import com.syxy.protocol.mqttImp.process.Impl.IdentityAuthenticator;
import com.syxy.protocol.mqttImp.process.Impl.dataHandler.DBPersistentStore;
import com.syxy.protocol.mqttImp.process.Interface.IAuthenticator;
import com.syxy.protocol.mqttImp.process.Interface.IMessagesStore;
import com.syxy.protocol.mqttImp.process.Interface.ISessionStore;
import com.syxy.server.ClientSession;

/**
 * <li>说明 MQTT协议业务处理
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTProcess implements IProcessHandler {

	DBPersistentStore storage = new DBPersistentStore();
	IMessagesStore messagesStore = storage;
	ISessionStore sessionStore = storage;
	IAuthenticator authenticator = new IdentityAuthenticator();
	ProtocolProcess protocolProcess = new ProtocolProcess(authenticator, messagesStore, sessionStore);
	
	@Override
	public void process(Message msg, ClientSession client) {
		
		switch (msg.getType()) {
		case CONNECT:
			ConnectMessage connectMessage = (ConnectMessage)msg;
			protocolProcess.processConnect(client, connectMessage);
			break;
		case CONNACK:
			break;
		case PUBLISH:
			PublishMessage publishMessage = (PublishMessage)msg;
			protocolProcess.processPublic(client, publishMessage);
			break;
		case PUBACK:
			PubAckMessage pubAckMessage = (PubAckMessage)msg;
			protocolProcess.processPubAck(client, pubAckMessage);
			break;
		case PUBREC:
			PubRecMessage pubRecMessage = (PubRecMessage)msg;
			protocolProcess.processPubRec(client, pubRecMessage);
			break;
		case PUBREL:
			PubRelMessage pubRelMessage = (PubRelMessage)msg;
			protocolProcess.processPubRel(client, pubRelMessage);
			break;
		case PUBCOMP:
			PubcompMessage pubcompMessage = (PubcompMessage)msg;
			protocolProcess.processPubComp(client, pubcompMessage);
			break;
		case SUBSCRIBE:
			SubscribeMessage subscribeMessage = (SubscribeMessage)msg;
			protocolProcess.processSubscribe(client, subscribeMessage);
			break;
		case UNSUBSCRIBE:
			UnSubscribeMessage unSubscribeMessage = (UnSubscribeMessage)msg;
			protocolProcess.processUnSubscribe(client, unSubscribeMessage);
			break;
		case UNSUBACK:
			break;
		case PINGREQ:
			PingReqMessage pingReqMessage = (PingReqMessage)msg;
			protocolProcess.processPingReq(client, pingReqMessage);
			break;
		case PINGRESP:
			break;
		case DISCONNECT:
			DisconnectMessage disconnectMessage = (DisconnectMessage)msg;
			protocolProcess.processDisconnet(client, disconnectMessage);
			break;
		default:
			throw new UnsupportedOperationException("不支持" + msg.getType()+ "消息类型");
		}
	}

}
