package com.syxy.protocol.mqttImp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import org.apache.log4j.Logger;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.syxy.protocol.mqttImp.MQTTDecoder.DecoderState;
import com.syxy.protocol.mqttImp.message.FixedHeader;
import com.syxy.protocol.mqttImp.message.MessageType;
import com.syxy.protocol.mqttImp.message.QoS;

/**
 *  MQTT协议解码
 *  修改（1）使用netty的ReplayingState来进行解码，该类使用状态机的方式防止代码的反复执行
 * @author zer0
 * @version 1.0
 * @date 2015-2-16
 * @date 2016-3-4（1）
 */
public class MQTTDecoder extends ReplayingDecoder<DecoderState> {
	
	private final static Logger Log = Logger.getLogger(MQTTDecoder.class);

	enum DecoderState{
		FIXED_HEADER,
		VARIABLE_HEADER,
		PAYLOAD,
		BAD_MESSAGE,
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		switch (state()) {
		case FIXED_HEADER:
			
		case VARIABLE_HEADER:
			
		case PAYLOAD:
			
			break;
		case BAD_MESSAGE:
			
			break;
		default:
			throw new Error();
		}
	}
	

	/**
	 * 解压缩固定头部，mqtt协议的所有消息类型，固定头部字段类型和长度都是一样的。
	 * @param byteBuf
	 * @return FixedHeader
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-4
	 */
	private FixedHeader decodeFixedHeader(ByteBuf byteBuf){
		//解码头部第一个字节
		byte headerData = byteBuf.readByte();
		MessageType type = MessageType.valueOf((headerData >> 4) & 0xF);
		Boolean dup = (headerData & 0x8) > 0;
		QoS qos = QoS.valueOf((headerData & 0x6) >> 1);
		Boolean retain = (headerData & 0x1) > 0;
		
		//解码头部第二个字节，余留长度
		int multiplier = 1;
	    int remainLength = 0;
	    byte digit = 0;
	    int loop = 0;
	    do {
	    	digit = byteBuf.readByte();
	    	remainLength += (digit & 0x7f) * multiplier;
	        multiplier *= 128;
	        loop++;
	    }while ((digit & 0x80) != 0 && loop < 4);
	    
	    if (loop == 4 && (digit & 0x80) != 0) {
			throw new DecoderException("保留字段长度超过4个字节，与协议不符，消息类型:" + type);
		}
	   
	    FixedHeader fixedHeader = new FixedHeader(type, dup, qos, retain, remainLength);
		
	    //返回时，针对所有协议进行头部校验
		return validateFixHeader(fixedHeader);
	}
	
	/**
	 * 对所有消息类型进行头部校验，确保保留位的字段正确
	 * @param fixedHeader
	 * @return FixedHeader
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-4
	 */
	private FixedHeader validateFixHeader(FixedHeader fixedHeader){
		switch (fixedHeader.getMessageType()) {
		case PUBREL:
		case SUBSCRIBE:
		case UNSUBSCRIBE:
			if (fixedHeader.getQos() != QoS.AT_LEAST_ONCE) {
				throw new DecoderException(fixedHeader.getMessageType().name()+"的Qos必须为1");
			}
			
			if (fixedHeader.isDup()) {
				throw new DecoderException(fixedHeader.getMessageType().name()+"的Dup必须为0");
			}
			
			if (fixedHeader.isRetain()) {
				throw new DecoderException(fixedHeader.getMessageType().name()+"的Retain必须为0");
			}
		case CONNECT:
		case CONNACK:
		case PUBACK:
		case PUBREC:
		case PUBCOMP:
		case SUBACK:
		case UNSUBACK:
		case PINGREQ:
		case PINGRESP:
		case DISCONNECT:
			if (fixedHeader.getQos() != QoS.AT_MOST_ONCE) {
				throw new DecoderException(fixedHeader.getMessageType().name()+"的Qos必须为0");
			}
			
			if (fixedHeader.isDup()) {
				throw new DecoderException(fixedHeader.getMessageType().name()+"的Dup必须为0");
			}
			
			if (fixedHeader.isRetain()) {
				throw new DecoderException(fixedHeader.getMessageType().name()+"的Retain必须为0");
			}
		default:
			return fixedHeader;
		}
	}

	/**
	 * 解码可变头部，由于可变头部返回的值不确定，需要使用泛型来作为返回值
	 * @param byteBuf
	 * @return FixedHeader
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-4
	 */
	private Result<?> decodeVariableHeader(ByteBuf byteBuf, FixedHeader fixedHeader){
		return new Result<FixedHeader>(fixedHeader,19);
	}
	
	private static class Result<T>{
		private final T value;
		private final int useNumOfBytes;//解码出的内容的长度
		
		Result(T value, int useNumOfBytes) {
			this.value = value;
			this.useNumOfBytes = useNumOfBytes;
		}
	}

}
