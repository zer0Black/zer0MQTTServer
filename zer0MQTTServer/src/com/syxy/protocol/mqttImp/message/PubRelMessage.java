package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.server.ClientSession;

/**
 * MQTT协议PubRel消息类型实现类，Publish的QoS=2时最终确认的消息类型，确认PubRec包
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class PubRelMessage extends Message {

	private static final int PUBREL_SIZE = 2;
	
	public PubRelMessage(){
		super(Type.PUBREL);
	}
	
	public PubRelMessage(int packageID){
		super(Type.PUBREL);
		this.setPackgeID(packageID);
	}
	
	public PubRelMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		// TODO Auto-generated method stub
		return this.encodePackageID();
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		PubRelMessage pubRelMessage = new PubRelMessage();
		pubRelMessage.setPackgeID(this.decodePackageID(byteBuffer));
		pubRelMessage.setHeaderMessage(this.getHeaderMessage());
		
		return pubRelMessage;
	}

	@Override
	public int messageLength(Message msg) {
		return PUBREL_SIZE;
	}

	@Override
	public boolean isMessageIdRequired() {
		return true;
	}
}
