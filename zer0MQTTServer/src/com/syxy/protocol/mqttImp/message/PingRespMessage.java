package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.server.ClientSession;

public class PingRespMessage extends Message {

	@Override
	public byte[] encode() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
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
