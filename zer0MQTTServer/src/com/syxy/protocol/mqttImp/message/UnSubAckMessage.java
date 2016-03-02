package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.server.ClientSession;

/**
 * MQTT协议UnSubAck消息类型实现类，对UnSubscribe包的确认
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class UnSubAckMessage extends Message {

	private static int UNSUBACK_SIZE = 2;
	
	public UnSubAckMessage(){
		super(Type.UNSUBACK);
	}
	
	public UnSubAckMessage(int packageID){
		super(Type.UNSUBACK);
		this.setPackgeID(packageID);
	}
	
	public UnSubAckMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		return this.encodePackageID();
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		throw new UnsupportedOperationException("UNSUBACK无需解码，该类型仅能从服务器发送到客户端");
	}

	@Override
	public int messageLength(Message msg) {
		return UNSUBACK_SIZE;
	}

	@Override
	public boolean isMessageIdRequired() {
		return true;
	}
}
