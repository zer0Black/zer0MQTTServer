package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.server.ClientSession;

public class DisconnectMessage extends Message {

	private final static Logger Log = Logger.getLogger(DisconnectMessage.class);
	
	public DisconnectMessage(){
		super(Type.DISCONNECT);
	}
	
	public DisconnectMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		throw new UnsupportedOperationException("DISCONNECT无需编码，该类型仅能从客户端发送服务端");
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		Log.info("DISCONNET除固定头外无任何可变头或消息体，无需解码");

		DisconnectMessage disconnectMessage = new DisconnectMessage();
		disconnectMessage.setHeaderMessage(this.getHeaderMessage());
		
		return disconnectMessage;
	}


	@Override
	public int messageLength(Message msg) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMessageIdRequired() {
		// TODO Auto-generated method stub
		return false;
	}
}
