package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * MQTT协议PubRec消息类型实现类，Publish的QoS=2时最终确认的消息类型，确认Publish包
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class PubRecMessage extends Message {

	private static int PUBREC_SIZE = 2;
	
	public PubRecMessage(){
		super(Type.PUBREC);
	}
	
	public PubRecMessage(int packageID){
		super(Type.PUBREC);
		this.setPackgeID(packageID);
	}
	
	public PubRecMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		return this.encodePackageID();
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		PubRecMessage pubRecMessage = new PubRecMessage();
		pubRecMessage.setPackgeID(this.decodePackageID(byteBuffer));
		pubRecMessage.setHeaderMessage(this.getHeaderMessage());
		
		return pubRecMessage;
	}

	@Override
	public int messageLength(Message msg) {
		return PUBREC_SIZE;
	}

	@Override
	public boolean isMessageIdRequired() {
		return true;
	}
}
