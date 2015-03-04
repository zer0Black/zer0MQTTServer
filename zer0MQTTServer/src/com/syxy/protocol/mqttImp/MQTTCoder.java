package com.syxy.protocol.mqttImp;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.CoderHandler;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.protocol.mqttImp.message.PubAckMessage;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.server.ClientSession;
import com.syxy.util.coderTool;

/**
 * <li>说明 MQTT协议编码
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTCoder implements CoderHandler {

	//1、编码协议头第一字节
	//2、编码协议头的第二字节remaining length
	//3、编码消息类型
	@Override
	public ByteBuffer process(Message msg) throws IOException {
		//定死回写的缓冲区为2m，以后再修改
		ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

		
		switch (msg.getType()) {
		case CONNECT:
			HeaderMessage headerMessage = new HeaderMessage(Type.CONNECT, false, QoS.AT_MOST_ONCE, false);
			byteBuffer.put(headerMessage.encode());
			byteBuffer.put(headerMessage.lengthToBytes(msg));
			byteBuffer.put(msg.encode());
			return byteBuffer;
		case CONNACK:

			break;
		case PUBLISH:

			break;
		case PUBACK:

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
		return null;

	}


}
