package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.Type;

/**
 * MQTT协议PingResp消息类型实现类，客户端心跳包回应消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-2
 */
public class PingRespMessage extends Message {
	
	private final static Logger Log = Logger.getLogger(PingRespMessage.class);
	
	public PingRespMessage(){
		super(Type.PINGRESP);
	}
	
	public PingRespMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		Log.info("PINGRESP除固定头外无任何可变头或消息体，无需对其编码");
		return new byte[0];
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		throw new UnsupportedOperationException("SUBSCRIBE无需解码，该类型仅能从服务器发送到客户端");
	}

	@Override
	public int messageLength(Message msg) {
		return 0;
	}

	@Override
	public boolean isMessageIdRequired() {
		return false;
	}
}
