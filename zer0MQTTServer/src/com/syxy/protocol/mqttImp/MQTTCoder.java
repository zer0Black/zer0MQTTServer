package com.syxy.protocol.mqttImp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

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
	
	private final static Logger Log = Logger.getLogger(MQTTCoder.class);
	
	//1、编码协议头第一字节
	//2、编码协议头的第二字节remaining length
	//3、编码消息类型
	@Override
	public ByteBuffer process(Message msg) throws IOException {
		//定死回写的缓冲区为2m，以后再修改
		ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
		byteBuffer.clear();
		HeaderMessage headerMessage = null;
		
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
//			headerMessage = new HeaderMessage(Type.PUBLISH, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
		case PUBACK:
			headerMessage = new HeaderMessage(Type.PUBACK, false, QoS.AT_MOST_ONCE, false);
			encoder(msg, headerMessage, byteBuffer);
			break;
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
		return null;

	}

	private void encoder(Message msg, HeaderMessage headerMessage, ByteBuffer buffer){		
		try {
			buffer.put(headerMessage.encode());
			Log.info("bytebuffer的位置1="+buffer.position());
			buffer.put(headerMessage.lengthToBytes(msg));
			Log.info("bytebuffer的位置2="+buffer.position());
			buffer.put(msg.encode());
			Log.info("bytebuffer的位置3="+buffer.position());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
