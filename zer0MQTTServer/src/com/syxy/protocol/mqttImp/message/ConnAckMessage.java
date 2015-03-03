package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.Message;

/**
 * <li>MQTT协议ConnAck消息类型实现类，连接确认消息类型
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public class ConnAckMessage extends HeaderMessage {

	@Override
	public byte[] encode() throws IOException {
		return null;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handlerMessage() {
		// TODO Auto-generated method stub
		
	}
	
}
