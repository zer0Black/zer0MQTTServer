package com.syxy.protocol.mqttImp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.DecoderHandler;
import com.syxy.protocol.Message;
import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.HeaderMessage;
import com.syxy.protocol.mqttImp.message.PubAckMessage;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.server.ClientSession;
import com.syxy.util.coderTool;

/**
 * <li>说明 MQTT协议解码
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTDecoder implements DecoderHandler {
	
	private final static Logger Log = Logger.getLogger(MQTTDecoder.class);

	@Override
	public Message process(ByteBuffer byteBuffer, ClientSession client) {
		// TODO Auto-generated method stub
//		String str = coderTool.decode(byteBuffer);
		try {
			//首先判断缓存中的内容是否大于2个字节（MQTT协议头为2字节）	
			if (byteBuffer.limit() > 2) {
				HeaderMessage headerMessage = (HeaderMessage) HeaderMessage.decodeMessage(byteBuffer);
				int messageLength = headerMessage.bytesToLength(byteBuffer);
				
				
				Message msg = null;
				
				//解码头部后，声明出对应的消息类型
				switch (headerMessage.getType()) {
				case CONNECT:
					msg = new ConnectMessage();
					ConnectMessage connectMessage = (ConnectMessage) msg.decode(byteBuffer);
					return connectMessage;
				case CONNACK:
					msg = new ConnAckMessage();
					ConnAckMessage connAckMessage = (ConnAckMessage) msg.decode(byteBuffer);
					return connAckMessage;
				case PUBLISH:
					msg = new PublishMessage();
					PublishMessage publishMessage = (PublishMessage) msg.decode(byteBuffer);
					return publishMessage;
				case PUBACK:
					msg = new PubAckMessage();
					PubAckMessage pubAckMessage = (PubAckMessage) msg.decode(byteBuffer);
					return pubAckMessage;
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
