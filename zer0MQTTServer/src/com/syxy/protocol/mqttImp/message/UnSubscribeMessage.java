package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.server.ClientSession;
import com.syxy.util.BufferPool;
import com.syxy.util.StringTool;

public class UnSubscribeMessage extends Message {
	
	private List<String> topicFilter = new ArrayList<String>();
	
	public UnSubscribeMessage(){
		super(Type.UNSUBSCRIBE);
	}
	
	public UnSubscribeMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		throw new UnsupportedOperationException("UNSUBSCRIBE无需编码，该类型仅能从客户端发送服务端");
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		InputStream in = new ByteArrayInputStream(byteBuffer.array());
		DataInputStream dataInputStream = new DataInputStream(in);
		
		UnSubscribeMessage unSubscribeMessage = new UnSubscribeMessage();
		unSubscribeMessage.setPackgeID(dataInputStream.readUnsignedShort());
		
		int pos=2;
		while(pos < messageLength){
			unSubscribeMessage.getTopicFilter().add(dataInputStream.readUTF());
			pos+=StringTool.stringToByte(unSubscribeMessage.getTopicFilter().get(unSubscribeMessage.getTopicFilter().size()-1)).length;
		}
				
		unSubscribeMessage.setHeaderMessage(this.getHeaderMessage());
		
		byteBuffer.position(messageLength);
		BufferPool.removeReadedData(byteBuffer);
		
		return unSubscribeMessage;
	}

	@Override
	public int messageLength(Message msg) {
		int length = 2; // message id length
		for (String topicfilter : topicFilter) {
			length += StringTool.stringToByte(topicfilter).length;
		}
		return length;
	}

	@Override
	public boolean isMessageIdRequired() {
		// TODO Auto-generated method stub
		return true;
	}

	public List<String> getTopicFilter() {
		return topicFilter;
	}

	public void setTopicFilter(List<String> topicFilter) {
		this.topicFilter = topicFilter;
	}
	
}
