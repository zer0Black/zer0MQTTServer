package com.syxy.protocol.mqttImp;

import com.syxy.protocol.ProcessHandler;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.PubAckMessage;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.server.ClientSession;

/**
 * <li>说明 MQTT协议业务处理
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTProcess implements ProcessHandler {

	@Override
	public void process(Message msg, ClientSession client) {
		
		switch (msg.getType()) {
		case CONNECT:
			msg = new ConnectMessage();
			msg.handlerMessage();
			break;
		case CONNACK:
			msg = new ConnAckMessage();
			msg.handlerMessage();
			break;
		case PUBLISH:
			msg = new PublishMessage();
			msg.handlerMessage();
			break;
		case PUBACK:
			msg = new PubAckMessage();
			msg.handlerMessage();
			break;
		case PUBREC:

			break;
		case PUBREL:

			break;
		case PUBCOMP:

			break;
		case SUBSCRIBE:

			break;
		case SUBACK:

			break;
		case UNSUBSCRIBE:

			break;
		case UNSUBACK:

			break;
		case PINGREQ:

			break;
		case PINGRESP:

			break;
		case DISCONNECT:

			break;
		default:
			throw new UnsupportedOperationException("不支持" + msg.getType()+ "消息类型");
		}
	}

}
