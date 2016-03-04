package com.syxy.protocol.mqttImp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import sun.awt.SunHints.Value;

import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.ConnectMessage.ConnectPayload;
import com.syxy.protocol.mqttImp.message.ConnectMessage.ConnectVariableHeader;
import com.syxy.protocol.mqttImp.message.FixedHeader;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.MessageType;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.protocol.mqttImp.message.SubAckMessage;
import com.syxy.protocol.mqttImp.message.SubscribeMessage;
import com.syxy.protocol.mqttImp.message.UnSubscribeMessage;

/**
 *  MQTT协议编码
 *  修改（1）
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-16
 * @date 2016-3-3(1)
 */
public class MQTTEncoder extends MessageToByteEncoder<Message> {
	
	private final static Logger Log = Logger.getLogger(MQTTEncoder.class);
	public static final int MAX_LENGTH_LIMIT = 268435455;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
			throws Exception {
		ByteBufAllocator byteBufAllocator = ctx.alloc();
		ByteBuf encodedByteBuf;
		
		switch (msg.getFixedHeader().getMessageType()) {
		case CONNECT:
			encodedByteBuf = encodeConnectMessage(byteBufAllocator, (ConnectMessage)msg);
			break;
		case CONNACK:
			encodedByteBuf = encodeConnAckMessage(byteBufAllocator, (ConnAckMessage)msg);
			break;
		case PUBLISH:
			encodedByteBuf = encodePublishMessage(byteBufAllocator, (PublishMessage)msg);
			break;
		case SUBSCRIBE:
			encodedByteBuf = encodeSubscribeMessage(byteBufAllocator, (SubscribeMessage)msg);
			break;
		case UNSUBSCRIBE:
			encodedByteBuf = encodeUnSubcribeMessage(byteBufAllocator, (UnSubscribeMessage)msg);
			break;
		case SUBACK:
			encodedByteBuf = encodeSubAckMessage(byteBufAllocator, (SubAckMessage)msg);
			break;
		case UNSUBACK:
		case PUBACK:
		case PUBREC:
		case PUBREL:
		case PUBCOMP:
			encodedByteBuf = encodeMessageByteFixedHeaderAndMessageId(byteBufAllocator, msg);
			break;
		case PINGREQ:
		case PINGRESP:
		case DISCONNECT:
			encodedByteBuf = encodeMessageByteFixedHeader(byteBufAllocator, msg);
			break;
		default:
			throw new IllegalArgumentException(
					"未知的MQTT协议类型："+msg.getFixedHeader().getMessageType().value());
		}
		out.writeBytes(encodedByteBuf);
	}
	
	private ByteBuf encodeConnectMessage(ByteBufAllocator bufAllocator, 
			ConnectMessage message){
		//把消息每个字段从POJO中取出，并计算其大小，写入byteBuf
		FixedHeader fixedHeader = message.getFixedHeader();
		ConnectVariableHeader connectVariableHeader = message.getVariableHeader();
		ConnectPayload connectPayload = message.getPayload();
		
		String mqttName = connectVariableHeader.getProtocolName();
		int mqttVersion = connectVariableHeader.getProtocolVersionNumber();
		
		
		return null;
	}
	
	private ByteBuf encodeConnAckMessage(ByteBufAllocator bufAllocator, 
			ConnAckMessage msg){
		return null;
	}
	
	private ByteBuf encodePublishMessage(ByteBufAllocator bufAllocator, 
			PublishMessage message){
		return null;
	}
	
	private ByteBuf encodeSubscribeMessage(ByteBufAllocator bufAllocator, 
			SubscribeMessage message){
		return null;
	}
	
	private ByteBuf encodeUnSubcribeMessage(ByteBufAllocator bufAllocator, 
			UnSubscribeMessage message){
		return null;
	}
	
	private ByteBuf encodeSubAckMessage(ByteBufAllocator bufAllocator, 
			SubAckMessage message){
		return null;
	}
	
	private ByteBuf encodeMessageByteFixedHeaderAndMessageId(ByteBufAllocator bufAllocator, 
			Message message){
		return null;
	}
	
	private ByteBuf encodeMessageByteFixedHeader(ByteBufAllocator bufAllocator, 
			Message message){
		return null;
	}
	
	/**
	 * 编码固定头部第一个字节
	 * @param fixedHeader
	 * @return byte[]
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-4
	 */
	private byte[] encodeFixHeader(FixedHeader fixedHeader) throws IOException{
		byte b = 0;
		b = (byte) (fixedHeader.getMessageType().value() << 4);
		b |= fixedHeader.isDup() ? 0x8 : 0x0;
		b |= fixedHeader.getQos().value() << 1;
		b |= fixedHeader.isRetain() ? 0x1 : 0;
		
		byte[] bArray = new byte[]{b};
		return bArray;			
	}
	
	/**
	 * 把消息长度信息编码成字节
	 * @param length
	 * @return ByteBuf
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-4
	 */
	private ByteBuf encodeRemainLength(int length){
		if (length > MAX_LENGTH_LIMIT || length < 0) {
			throw new CorruptedFrameException(
					"消息长度不能超过‘消息最大长度’:"+MAX_LENGTH_LIMIT+",当前长度："+length);
		}
		//剩余长度字段最多可编码4字节
		ByteBuf encoded = Unpooled.buffer(4);
		byte digit;
		do{
			digit = (byte)(length%128);
			length = length/128;
			if (length > 0) {
				digit = (byte)(digit | 0x80);
			}
			encoded.writeByte(digit);
		}while(length>0);
		return encoded;
	}
	
//	//定死回写的缓冲区为2m，以后再修改
//	ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
//
//	
//	//1、编码协议头第一字节
//	//2、编码协议头的第二字节remaining length
//	//3、编码消息类型
//	@Override
//	public ByteBuffer process(Message msg) throws IOException {
//		
//		byteBuffer.clear();
//		HeaderMessage headerMessage = null;
//		
//		//先判断是否需要包ID，需要就给该消息类型加上一个包ID
//		if (msg.isMessageIdRequired() && (msg.getPackgeID() == 0)) {
//			msg.setPackgeID(Message.getNextMessageId());
//		}
//		
//		switch (msg.getType()) {
//		case CONNECT:
//			headerMessage = new HeaderMessage(Type.CONNECT, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			return byteBuffer;
//		case CONNACK:
//			headerMessage = new HeaderMessage(Type.CONNACK, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			return byteBuffer;
//		case PUBLISH:
//			QoS qos = msg.getQos();
//			if (qos == null) {
//				qos = QoS.AT_MOST_ONCE;
//			}
//			boolean dup = msg.isDup();
//			boolean retain = msg.isRetain();
//			headerMessage = new HeaderMessage(Type.PUBLISH, dup, qos, retain);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case PUBACK:
//			headerMessage = new HeaderMessage(Type.PUBACK, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			return byteBuffer;
//		case PUBREC:
//			headerMessage = new HeaderMessage(Type.PUBREC, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case PUBREL:
//			headerMessage = new HeaderMessage(Type.PUBREL, false, QoS.AT_LEAST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case PUBCOMP:
//			headerMessage = new HeaderMessage(Type.PUBCOMP, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case SUBSCRIBE:
//			headerMessage = new HeaderMessage(Type.SUBSCRIBE, false, QoS.AT_LEAST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case SUBACK:
//			headerMessage = new HeaderMessage(Type.SUBACK, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case UNSUBSCRIBE:
//			headerMessage = new HeaderMessage(Type.UNSUBSCRIBE, false, QoS.AT_LEAST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case UNSUBACK:
//			headerMessage = new HeaderMessage(Type.UNSUBACK, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case PINGREQ:
//			headerMessage = new HeaderMessage(Type.PINGREQ, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case PINGRESP:
//			headerMessage = new HeaderMessage(Type.PINGRESP, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		case DISCONNECT:
//			headerMessage = new HeaderMessage(Type.DISCONNECT, false, QoS.AT_MOST_ONCE, false);
//			encoder(msg, headerMessage, byteBuffer);
//			break;
//		default:
//			throw new UnsupportedOperationException("不支持" + msg.getType()+ "消息类型");
//		}
//		return byteBuffer;
//
//	}
//
//	private void encoder(Message msg, HeaderMessage headerMessage, ByteBuffer buffer){		
//		try {
//			buffer.put(headerMessage.encode());
//			buffer.put(headerMessage.lengthToBytes(msg));
//			buffer.put(msg.encode());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
//	}

	
}
