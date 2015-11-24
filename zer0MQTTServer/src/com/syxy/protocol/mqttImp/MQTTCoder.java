package com.syxy.protocol.mqttImp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.ICoderHandler;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;

/**
 * <li>说明 MQTT协议编码
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTCoder implements ICoderHandler {
	
	private final static Logger Log = Logger.getLogger(MQTTCoder.class);
	//定死回写的缓冲区为2m，以后再修改
	ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

	
	//1、编码协议头第一字节
	//2、编码协议头的第二字节remaining length
	//3、编码消息类型
	@Override
	public ByteBuffer process(Message msg) throws IOException {
		
		byteBuffer.clear();
		HeaderMessage headerMessage = null;
		
		//先判断是否需要包ID，需要就给该消息类型加上一个包ID
		if (msg.isMessageIdRequired() && (msg.getPackgeID() == 0)) {
			msg.setPackgeID(Message.getNextMessageId());
		}
		
		switch (msg.getType()) {
		case CONNECT:
			headerMessage = new HeaderMessage(Type.CONNECT, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			return byteBuffer;
		case CONNACK:
			headerMessage = new HeaderMessage(Type.CONNACK, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			return byteBuffer;
		case PUBLISH:
			QoS qos = msg.getQos();
			if (qos == null) {
				qos = QoS.AT_MOST_ONCE;
			}
			boolean dup = msg.isDup();
			boolean retain = msg.isRetain();
			headerMessage = new HeaderMessage(Type.PUBLISH, dup, qos, retain);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case PUBACK:
			headerMessage = new HeaderMessage(Type.PUBACK, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			return byteBuffer;
		case PUBREC:
			headerMessage = new HeaderMessage(Type.PUBREC, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case PUBREL:
			headerMessage = new HeaderMessage(Type.PUBREL, false, QoS.AT_LEAST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case PUBCOMP:
			headerMessage = new HeaderMessage(Type.PUBCOMP, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case SUBSCRIBE:
			headerMessage = new HeaderMessage(Type.SUBSCRIBE, false, QoS.AT_LEAST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case SUBACK:
			headerMessage = new HeaderMessage(Type.SUBACK, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case UNSUBSCRIBE:
			headerMessage = new HeaderMessage(Type.UNSUBSCRIBE, false, QoS.AT_LEAST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case UNSUBACK:
			headerMessage = new HeaderMessage(Type.UNSUBACK, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case PINGREQ:
			headerMessage = new HeaderMessage(Type.PINGREQ, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case PINGRESP:
			headerMessage = new HeaderMessage(Type.PINGRESP, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case DISCONNECT:
			headerMessage = new HeaderMessage(Type.DISCONNECT, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		default:
			throw new UnsupportedOperationException("不支持" + msg.getType()+ "消息类型");
		}
		return byteBuffer;

	}

	private void encoder(Message msg, HeaderMessage headerMessage, ByteBuffer buffer){		
		try {
			buffer.put(headerMessage.encode());
			buffer.put(headerMessage.lengthToBytes(msg));
			buffer.put(msg.encode());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
