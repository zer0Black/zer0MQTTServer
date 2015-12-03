package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.server.ClientSession;

/**
 * MQTT协议PubAck消息类型实现类，Publish的QoS=1时确认的消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class PubAckMessage extends Message {
	
	private static final int PUBACK_SIZE = 2;
	
	public PubAckMessage(){
		super(Type.PUBACK);
	}
	
	public PubAckMessage(int packageID){
		super(Type.PUBACK);
		this.setPackgeID(packageID);
	}
	
	public PubAckMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		return this.encodePackageID();
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
		PubAckMessage pubAckMessage = new PubAckMessage();
		pubAckMessage.setPackgeID(this.decodePackageID(byteBuffer));
		pubAckMessage.setHeaderMessage(this.getHeaderMessage());
		
		return pubAckMessage;
	}

	@Override
	public int messageLength(Message msg) {
		return PUBACK_SIZE;
	}
	
	@Override
	public boolean isMessageIdRequired() {
		return true;
	}
}
