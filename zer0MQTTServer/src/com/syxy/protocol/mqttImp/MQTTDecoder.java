package com.syxy.protocol.mqttImp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.IDecoderHandler;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.PubAckMessage;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.protocol.mqttImp.message.SubscribeMessage;

/**
 * <li>说明 MQTT协议解码
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTDecoder implements IDecoderHandler {
	
	private final static Logger Log = Logger.getLogger(MQTTDecoder.class);

	@Override
	public Message process(ByteBuffer byteBuffer) {
		try {
			//首先判断缓存中协议头是否读完（MQTT协议头为2字节）	
			if (byteBuffer.limit() > 2) {
				HeaderMessage headerMessage = (HeaderMessage) HeaderMessage.decodeMessage(byteBuffer);
				Message msg = null;
				
				//解码头部后，声明出对应的消息类型
				switch (headerMessage.getType()) {
				case CONNECT:
					msg = new ConnectMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case CONNACK:
					msg = new ConnAckMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PUBLISH:
					msg = new PublishMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PUBACK:
					msg = new PubAckMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PUBREC:

					break;
				case PUBREL:

					break;
				case PUBCOMP:

					break;
				case SUBSCRIBE:
					msg = new SubscribeMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
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
					Log.error("消息类型" + headerMessage.getType() + "不支持");
					throw new UnsupportedOperationException("不支持" + headerMessage.getType()+ "消息类型");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
