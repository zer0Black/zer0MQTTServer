package com.syxy.protocol.mqttImp;

import com.syxy.protocol.IProcessHandler;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.PubAckMessage;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.protocol.mqttImp.process.protocolProcess;
import com.syxy.server.ClientSession;

/**
 * <li>说明 MQTT协议业务处理
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTProcess implements IProcessHandler {

	private protocolProcess protocolProcess = new protocolProcess();
	
	@Override
	public void process(Message msg, ClientSession client) {
		
		switch (msg.getType()) {
		case CONNECT:
			ConnectMessage connectMessage = (ConnectMessage)msg;
			protocolProcess.processConnect(client, connectMessage);
			break;
		case CONNACK:
		case PUBLISH:
		case PUBACK:
		case PUBREC:
		case PUBREL:
		case PUBCOMP:
		case SUBSCRIBE:
		case UNSUBSCRIBE:
		case UNSUBACK:
		case PINGREQ:
		case PINGRESP:
		case DISCONNECT:
			msg.handlerMessage(client);
			break;
		default:
			throw new UnsupportedOperationException("不支持" + msg.getType()+ "消息类型");
		}
	}

}
