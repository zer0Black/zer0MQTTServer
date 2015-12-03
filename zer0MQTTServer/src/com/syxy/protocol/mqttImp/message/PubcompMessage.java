package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.server.ClientSession;

/**
 * MQTT协议PubComp消息类型实现类，Publish的QoS=2时最终确认的消息类型，确认PubRel包
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class PubcompMessage extends Message {

	private static final int PUBCOMP_SIZE = 2;
	
	public PubcompMessage(){
		super(Type.PUBCOMP);
	}
	
	public PubcompMessage(int packageID){
		super(Type.PUBCOMP);
		this.setPackgeID(packageID);
	}
	
	public PubcompMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		return this.encodePackageID();
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
		PubcompMessage pubcompMessage = new PubcompMessage();
		pubcompMessage.setPackgeID(this.decodePackageID(byteBuffer));
		pubcompMessage.setHeaderMessage(this.getHeaderMessage());
		
		return pubcompMessage;
	}

	@Override
	public int messageLength(Message msg) {
		return PUBCOMP_SIZE;
	}

	@Override
	public boolean isMessageIdRequired() {
		return true;
	}
}
