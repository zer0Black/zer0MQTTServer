package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.server.ClientSession;


/**
 * <li>MQTT协议PubAck消息类型实现类，Publish确认的消息类型
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public class PubAckMessage extends Message {

	
	public PubAckMessage(){
		super(Type.PUBACK);
	}
	
	public PubAckMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handlerMessage(ClientSession client) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int messageLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
