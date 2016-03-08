package com.syxy.protocol.mqttImp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.MQTTDecoder.DecoderState;
import com.syxy.protocol.mqttImp.message.ConnAckMessage.ConnectionStatus;
import com.syxy.protocol.mqttImp.message.ConnAckVariableHeader;
import com.syxy.protocol.mqttImp.message.ConnectPayload;
import com.syxy.protocol.mqttImp.message.ConnectVariableHeader;
import com.syxy.protocol.mqttImp.message.FixedHeader;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.MessageType;
import com.syxy.protocol.mqttImp.message.PackageIdVariableHeader;
import com.syxy.protocol.mqttImp.message.PublishVariableHeader;
import com.syxy.protocol.mqttImp.message.QoS;
import com.syxy.protocol.mqttImp.message.SubAckPayload;
import com.syxy.protocol.mqttImp.message.SubscribePayload;
import com.syxy.protocol.mqttImp.message.TopicSubscribe;
import com.syxy.protocol.mqttImp.message.UnSubscribePayload;

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
	
	private FixedHeader fixedHeader;
	private Object variableHeader;
	private Object payload;
	
	public MQTTDecoder(){
		super(DecoderState.FIXED_HEADER);
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
		int bytesRemainingInVariablePart = 0;
		
		switch (state()) {
		case FIXED_HEADER:
			fixedHeader = decodeFixedHeader(in);
			bytesRemainingInVariablePart = fixedHeader.getMessageLength();
			checkpoint(DecoderState.VARIABLE_HEADER);
			
		case VARIABLE_HEADER:
			final Result<?> variableHeaderResult = decodeVariableHeader(in, fixedHeader);
			variableHeader = variableHeaderResult.getValue();
			bytesRemainingInVariablePart -= variableHeaderResult.getUseNumOfBytes();
			checkpoint(DecoderState.PAYLOAD);
			
		case PAYLOAD:
			final Result<?> payloadResult = decodePayload(in, fixedHeader.getMessageType(),
					bytesRemainingInVariablePart, variableHeader);
			payload = payloadResult.getValue();
			bytesRemainingInVariablePart -= payloadResult.getUseNumOfBytes();
			if (bytesRemainingInVariablePart != 0) {
				throw new DecoderException("解码的字节数和剩余字节字段长度不匹配，最终剩余:" + bytesRemainingInVariablePart);
			}
			checkpoint(DecoderState.FIXED_HEADER);
			Message message = MQTTMesageFactory.newMessage(fixedHeader, variableHeader, payload);
			fixedHeader = null;
			variableHeader = null;
			payload = null;
			out.add(message);
			break;
		case BAD_MESSAGE:
			in.skipBytes(actualReadableBytes());
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
			break;
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
			break;
		default:
			return fixedHeader;
		}
		return fixedHeader;
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
		switch (fixedHeader.getMessageType()) {
		case CONNECT:
			return decodeConnectVariableHeader(byteBuf);
		case CONNACK:
			return decodeConnAckVariableHeader(byteBuf);
		case PUBLISH:
			return decodePublishVariableHeader(byteBuf, fixedHeader);
		case SUBSCRIBE:
		case UNSUBSCRIBE:
		case SUBACK:
		case UNSUBACK:
		case PUBACK:
		case PUBREC:
		case PUBCOMP:
		case PUBREL:
			return decodePackageIdVariableHeader(byteBuf);
		case PINGREQ:
		case PINGRESP:
		case DISCONNECT:
			return new Result<Object>(null, 0);
		default:
			return new Result<Object>(null, 0);
		}
	}
	
	/**
	 * 解码Connect可变头部
	 * @param byteBuf
	 * @return Result<ConnectVariableHeader>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-5
	 */
	private Result<ConnectVariableHeader> decodeConnectVariableHeader(ByteBuf byteBuf){
		int useNumOfBytes = 0;//已解码字节
		//解码协议名
		Result<String> protoResult = decodeUTF(byteBuf);
		String protoName = protoResult.getValue();
		useNumOfBytes += protoResult.getUseNumOfBytes();
		//解码协议级别
		byte protoLevel = byteBuf.readByte();
		useNumOfBytes += 1;//协议等级长度为1个字节
		//解码连接标志
		byte connectFlags = byteBuf.readByte();
		boolean isHasUserName = (connectFlags & 0x80) > 0;//0x80=10000000
		boolean isHasPassword = (connectFlags & 0x40) > 0;//0x40=01000000
		boolean willRetain = (connectFlags & 0x20) > 0;//0x20=00100000
		QoS willQos = QoS.valueOf(connectFlags >> 3 & 0x03);////0x03=00000011
		boolean hasWill = (connectFlags & 0x04) > 0;//0x04=00000100
		boolean cleanSession = (connectFlags & 0x02) > 0;//0x02=00000010
		boolean reservedIsZero = (connectFlags & 0x01) == 0;//0x00=0000001
		useNumOfBytes += 1;
		//解码心跳包时长
		int keepAlive = byteBuf.readUnsignedShort();
		useNumOfBytes += 2;
		
		ConnectVariableHeader connectVariableHeader = new ConnectVariableHeader(
				protoName, protoLevel, isHasUserName, 
				isHasPassword, willRetain, willQos, 
				hasWill, cleanSession, reservedIsZero, keepAlive);
		return new Result<ConnectVariableHeader>(connectVariableHeader, useNumOfBytes);
	}
	
	/**
	 * 解码ConnAck可变头部
	 * @param byteBuf
	 * @return Result<ConnAckVariableHeader>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */
	private Result<ConnAckVariableHeader> decodeConnAckVariableHeader(ByteBuf byteBuf){
		int useNumOfBytes = 0;//已解码字节
		
		boolean sessionPresent = (byteBuf.readUnsignedByte() == 0)?false:true;
		useNumOfBytes += 1;
		
		byte returnCode = byteBuf.readByte();
		useNumOfBytes += 1;
		
		ConnAckVariableHeader connectVariableHeader = new ConnAckVariableHeader(ConnectionStatus.valueOf(returnCode), sessionPresent);
		return new Result<ConnAckVariableHeader>(connectVariableHeader, useNumOfBytes);
	}
	
	/**
	 * 解码Publish可变头部
	 * @param byteBuf
	 * @return Result<PublishVariableHeader>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */
	private Result<PublishVariableHeader> decodePublishVariableHeader(ByteBuf byteBuf, FixedHeader fixedHeader){
		int useNumOfBytes = 0;//已解码字节
		//解码协议名
		Result<String> topicResult = decodeUTF(byteBuf);
		String topicName = topicResult.getValue();
		//publish消息中不能出现通配符，需要进行校验
		if (!valiadPublishTopicName(topicName)) {
			throw new DecoderException("无效的主题名：" + topicName + ",因为它包含了通配符");
		}
		useNumOfBytes += topicResult.getUseNumOfBytes();
		//解析包ID
		int packageID = -1;
		if (fixedHeader.getQos().value() > 0) {
			packageID = byteBuf.readUnsignedShort();
			//校验包ID
			if (packageID == 0) {
				throw new DecoderException("无效的包ID"+packageID);
			}
			useNumOfBytes += 2;
		}
		PublishVariableHeader publishVariableHeader = new PublishVariableHeader(topicName, packageID);
		
		return new Result<PublishVariableHeader>(publishVariableHeader, useNumOfBytes);
	}
	
	private boolean valiadPublishTopicName(String topicName){
		final char[] TOPIC_WILADCARDS = {'#', '+'};
		for (char c : TOPIC_WILADCARDS) {
			if (topicName.indexOf(c) >= 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 解码只有包ID的可变头部
	 * @param byteBuf
	 * @return Result<ConnAckVariableHeader>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */
	private Result<PackageIdVariableHeader> decodePackageIdVariableHeader(ByteBuf byteBuf){
		int useNumOfBytes = 0;//已解码字节
		
		//解析包ID
		int packageID = byteBuf.readUnsignedShort();
		//校验包ID
		if (packageID == 0) {
			throw new DecoderException("无效的包ID"+packageID);				
		}
		useNumOfBytes += 2;
		
		PackageIdVariableHeader packageIdVariableHeader = new PackageIdVariableHeader(packageID);
		
		return new Result<PackageIdVariableHeader>(packageIdVariableHeader, useNumOfBytes);
	}
	
	/**
	 * 解码荷载，有荷载的消息类型有connect，subscribe，suback，unsubscribe，publish
	 * @param byteBuf
	 * @return FixedHeader
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-5
	 */
	private Result<?> decodePayload(ByteBuf byteBuf, 
			MessageType messageType,
			int bytesRemainVariablePart,
			Object variableHeader){
		switch (messageType) {
		case CONNECT:
			return decodeConnectPayload(byteBuf, (ConnectVariableHeader)variableHeader);
			
		case PUBLISH:
			return decodePublishPayload(byteBuf, bytesRemainVariablePart);
		
		case SUBSCRIBE:
			return decodeSubscribePayload(byteBuf, bytesRemainVariablePart);
		
		case SUBACK:
			return decodeSubAckPayload(byteBuf, bytesRemainVariablePart);
			
		case UNSUBSCRIBE:
			return decodeUnSubscribePayload(byteBuf, bytesRemainVariablePart);

		default:
			return new Result<Object>(null, 0);
		}
	}
	
	/**
	 * 解码connect荷载
	 * @param byteBuf
	 * @return Result<ConnectPayload>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */ 
	private Result<ConnectPayload> decodeConnectPayload(ByteBuf byteBuf, ConnectVariableHeader variableHeader){
		int useNumOfBytes = 0;//已解码字节
		
		Result<String> clientIDResult = decodeUTF(byteBuf);
		String clientID = clientIDResult.getValue();
		useNumOfBytes += clientIDResult.getUseNumOfBytes();
		
		Result<String> willTopicResult = null;
	    Result<String> willMessageResult = null;
	    if (variableHeader.isHasWill()) {
	    	willTopicResult = decodeUTF(byteBuf);	    	
	    	useNumOfBytes += willTopicResult.getUseNumOfBytes();
	    	willMessageResult = decodeUTF(byteBuf);
	    	useNumOfBytes += willMessageResult.getUseNumOfBytes();
		}
	    
	    Result<String> userNameResult = null;
        Result<String> passwordResult = null;
	    if (variableHeader.isHasUsername()) {
			userNameResult = decodeUTF(byteBuf);
			useNumOfBytes += userNameResult.getUseNumOfBytes();
		}
	    
	    if (variableHeader.isHasPassword()) {
	    	passwordResult = decodeUTF(byteBuf);
			useNumOfBytes += passwordResult.getUseNumOfBytes();
		}
		
	    ConnectPayload connectPayload = new ConnectPayload(
	    		clientID, 
	    		willTopicResult != null?willTopicResult.getValue():null, 
	    		willMessageResult != null?willMessageResult.getValue():null, 
	    		userNameResult != null?userNameResult.getValue():null, 
	    		passwordResult != null?passwordResult.getValue():null);
	    
	    return new Result<ConnectPayload>(connectPayload, useNumOfBytes);
	}
	
	/**
	 * 解码publish荷载
	 * @param byteBuf
	 * @return Result<ByteBuf>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */ 
	private Result<ByteBuf> decodePublishPayload(ByteBuf byteBuf, int bytesRemainVariablePart){
		int useNumOfBytes = bytesRemainVariablePart;//已解码字节
		
		ByteBuf b = byteBuf.readSlice(bytesRemainVariablePart).retain();
	    return new Result<ByteBuf>(b, useNumOfBytes);
	}
	
	/**
	 * 解码subscribe荷载
	 * @param byteBuf
	 * @return Result<SubscribePayload>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */ 
	private Result<SubscribePayload> decodeSubscribePayload(ByteBuf byteBuf, int bytesRemainVariablePart){
		int useNumOfBytes = 0;//已解码字节
		
		final List<TopicSubscribe> subscribeTopics = new ArrayList<TopicSubscribe>();
        while (useNumOfBytes < bytesRemainVariablePart) {
            final Result<String> topicNameResult = decodeUTF(byteBuf);
            useNumOfBytes += topicNameResult.getUseNumOfBytes();
            int qos = byteBuf.readUnsignedByte() & 0x03;
            useNumOfBytes++;
            subscribeTopics.add(new TopicSubscribe(topicNameResult.value, QoS.valueOf(qos)));
        }
        return new Result<SubscribePayload>(new SubscribePayload(subscribeTopics), useNumOfBytes);
	}
	
	/**
	 * 解码suback荷载
	 * @param byteBuf
	 * @return Result<SubAckPayload>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */ 
	private Result<SubAckPayload> decodeSubAckPayload(ByteBuf byteBuf, int bytesRemainVariablePart){
		int useNumOfBytes = 0;//已解码字节
		
		final List<Integer> grantedQos = new ArrayList<Integer>();
	    while (useNumOfBytes < bytesRemainVariablePart) {
	        int qos = byteBuf.readUnsignedByte() & 0x03;
	        useNumOfBytes++;
	        grantedQos.add(qos);
	    }
	    return new Result<SubAckPayload>(new SubAckPayload(grantedQos), useNumOfBytes);
	}
	
	/**
	 * 解码UnSubscribe荷载
	 * @param byteBuf
	 * @return Result<UnSubscribePayload>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */ 
	private Result<UnSubscribePayload> decodeUnSubscribePayload(ByteBuf byteBuf, int bytesRemainVariablePart){
		int useNumOfBytes = 0;//已解码字节
		
		final List<String> unsubscribeTopics = new ArrayList<String>();
        while (useNumOfBytes < bytesRemainVariablePart) {
            final Result<String> topicNameResult = decodeUTF(byteBuf);
            useNumOfBytes += topicNameResult.getUseNumOfBytes();
            unsubscribeTopics.add(topicNameResult.value);
        }
        return new Result<UnSubscribePayload>(
                new UnSubscribePayload(unsubscribeTopics),
                useNumOfBytes);
	}
	
	/**
	 * 解码UTF字符串类型，首先读取两个字节的长度，再根据此长度读取后面的数据
	 * @param byteBuf
	 * @return Result<String>
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-6
	 */ 
	private Result<String> decodeUTF(ByteBuf byteBuf){
		final int MAX_LENGTH = 65535;
		final int MIN_LENGTH = 0;
		
		//读取两个字节的无符号short类型，即UTF的长度
		int utfLength = byteBuf.readUnsignedShort();
		if (utfLength < MIN_LENGTH || utfLength > MAX_LENGTH) {
			throw new DecoderException("该UTF字符串长度有误，长度"+utfLength);
		}
		//根据长度解码出String
		String utfStr = byteBuf.readBytes(utfLength).toString(CharsetUtil.UTF_8);
		//计算已解码掉的长度，为字符串长度加最前面两个长度字段字节的长度
		int useNumOfBytes = utfLength + 2;
		return new Result<String>(utfStr, useNumOfBytes);
	}
	
	
	private static class Result<T>{
		private final T value;
		private final int useNumOfBytes;//解码出的内容的长度
		
		Result(T value, int useNumOfBytes) {
			this.value = value;
			this.useNumOfBytes = useNumOfBytes;
		}

		private T getValue() {
			return value;
		}

		private int getUseNumOfBytes() {
			return useNumOfBytes;
		}
		
	}
	
}
