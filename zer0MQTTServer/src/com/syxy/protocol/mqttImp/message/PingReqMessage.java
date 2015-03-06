package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.protocol.mqttImp.message.Message.HeaderMessage;
import com.syxy.server.ClientSession;

public class PingReqMessage extends Message {

	private final static Logger Log = Logger.getLogger(PingReqMessage.class);
	
	public PingReqMessage(){
		super(Type.PINGREQ);
	}
	
	public PingReqMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		throw new UnsupportedOperationException("PINGREQ无需编码，该类型仅能从客户端发送服务端");
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		Log.info("PINGREQ除固定头外无任何可变头或消息体，无需解码");

		PingReqMessage pingReqMessage = new PingReqMessage();
		pingReqMessage.setHeaderMessage(this.getHeaderMessage());
		
		return pingReqMessage;
	}

	@Override
	public void handlerMessage(ClientSession client) {
		// TODO Auto-generated method stub

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
