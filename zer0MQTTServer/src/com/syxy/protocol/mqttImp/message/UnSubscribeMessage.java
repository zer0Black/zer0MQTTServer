package com.syxy.protocol.mqttImp.message;

import java.util.List;

/**
 * MQTT协议UnSubscribe消息类型实现类，用于取消订阅topic
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class UnSubscribeMessage extends Message {

	public UnSubscribeMessage(FixedHeader fixedHeader, PackageIdVariableHeader variableHeader,
			UnSubscribePayload payload) {
		super(fixedHeader, variableHeader, payload);
	}
	
	@Override
	public PackageIdVariableHeader getVariableHeader() {
		return (PackageIdVariableHeader)super.getVariableHeader();
	}
	
	@Override
	public UnSubscribePayload getPayload() {
		return (UnSubscribePayload)super.getPayload();
	}
	
	/**
	 * MQTT协议Connect消息类型的荷载
	 * 
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-4
	 */
	public class UnSubscribePayload{
		
		private List<String> topics;
		
		public List<String> getTopics() {
			return topics;
		}

		public void setTopics(List<String> topics) {
			this.topics = topics;
		}
		
	}
	
//	private List<String> topicFilter = new ArrayList<String>();
//	
//	public UnSubscribeMessage(){
//		super(Type.UNSUBSCRIBE);
//	}
//	
//	public UnSubscribeMessage(HeaderMessage headerMessage){
//		super(headerMessage);
//	}
//	
//	@Override
//	public byte[] encode() throws IOException {
//		throw new UnsupportedOperationException("UNSUBSCRIBE无需编码，该类型仅能从客户端发送服务端");
//	}
//
//	@Override
//	public Message decode(ByteBuffer byteBuffer, int messageLength)
//			throws IOException {
//		InputStream in = new ByteArrayInputStream(byteBuffer.array());
//		DataInputStream dataInputStream = new DataInputStream(in);
//		
//		UnSubscribeMessage unSubscribeMessage = new UnSubscribeMessage();
//		unSubscribeMessage.setPackgeID(dataInputStream.readUnsignedShort());
//		
//		int pos=2;
//		while(pos < messageLength){
//			unSubscribeMessage.getTopicFilter().add(dataInputStream.readUTF());
//			pos+=StringTool.stringToByte(unSubscribeMessage.getTopicFilter().get(unSubscribeMessage.getTopicFilter().size()-1)).length;
//		}
//				
//		unSubscribeMessage.setHeaderMessage(this.getHeaderMessage());
//		
//		byteBuffer.position(messageLength);
//		BufferPool.removeReadedData(byteBuffer);
//		
//		return unSubscribeMessage;
//	}
//
//	@Override
//	public int messageLength(Message msg) {
//		int length = 2; // message id length
//		for (String topicfilter : topicFilter) {
//			length += StringTool.stringToByte(topicfilter).length;
//		}
//		return length;
//	}
//
//	@Override
//	public boolean isMessageIdRequired() {
//		return true;
//	}
//
//	public List<String> getTopicFilter() {
//		return topicFilter;
//	}
//
//	public void setTopicFilter(List<String> topicFilter) {
//		this.topicFilter = topicFilter;
//	}
	
}
