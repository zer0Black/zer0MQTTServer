package com.syxy.protocol.mqttImp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.IDecoderHandler;
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
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.protocol.mqttImp.message.SubscribeMessage;
import com.syxy.protocol.mqttImp.message.UnSubscribeMessage;

/**
 *  MQTT协议解码
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-16
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
					break;
				case PUBLISH:
					msg = new PublishMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PUBACK:
					msg = new PubAckMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PUBREC:
					msg = new PubRecMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PUBREL:
					msg = new PubRelMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PUBCOMP:
					msg = new PubcompMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case SUBSCRIBE:
					msg = new SubscribeMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case SUBACK:
					break;
				case UNSUBSCRIBE:
					msg = new UnSubscribeMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case UNSUBACK:
					break;
				case PINGREQ:
					msg = new PingReqMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
				case PINGRESP:
					break;
				case DISCONNECT:
					msg = new DisconnectMessage(headerMessage).decode(byteBuffer, headerMessage.getMessageLength());
					return msg;
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
