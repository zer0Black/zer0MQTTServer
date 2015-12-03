package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.Type;
import com.syxy.server.ClientSession;
import com.syxy.util.BufferPool;
import com.syxy.util.StringTool;

/**
 * MQTT协议Publish消息类型实现类，发布消息的消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class PublishMessage extends Message {
	
	private String topic;
	private byte[] data;
	
	public PublishMessage(){
		super(Type.PUBLISH);
	}
	
	public PublishMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	public PublishMessage(String topic, String msg) {
		this(topic, StringTool.stringToByte(msg));
	}
	
	public PublishMessage(String topic, byte[] data) {
		super(Type.PUBLISH);
		this.topic = topic;
		this.data = data;
	}
	
	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(byteOut);
		dos.writeUTF(topic);
		if (this.getQos() != QoS.AT_MOST_ONCE) {
			dos.writeShort(this.getPackgeID());
		}
		dos.write(data);
		
		//将dos转换为byte[]
		byte[] bArray = byteOut.toByteArray();	
		return bArray;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
		InputStream in = new ByteArrayInputStream(byteBuffer.array());
		DataInputStream dataInputStream = new DataInputStream(in);
		
		PublishMessage publishMessage = new PublishMessage();
		
		int pos = 0;
		publishMessage.setTopic(dataInputStream.readUTF());
		pos += StringTool.stringToByte(getTopic()).length;
		if (getQos() != QoS.AT_MOST_ONCE) {
			publishMessage.setPackgeID(dataInputStream.readUnsignedShort());
			pos += 2;
		}
		
		publishMessage.setData(new byte[messageLength - pos]);
		dataInputStream.read(publishMessage.getData());
		
		//必须再把协议头的解码对象添加进来一起返回
		publishMessage.setHeaderMessage(this.getHeaderMessage());
		//把缓冲区里已读的数据都移除
		byteBuffer.position(messageLength);
		BufferPool.removeReadedData(byteBuffer);
		
		return publishMessage;
	}
	
	@Override
	public int messageLength(Message msg) {
		PublishMessage publishMessage = (PublishMessage)msg;
		//packgeID的两个字节长度是在Qos>0的情况下才有的
		int length = StringTool.stringToByte(publishMessage.topic).length;
		length += (publishMessage.getQos() == QoS.AT_MOST_ONCE) ? 0 : 2;
		length += publishMessage.data.length;
		
		System.out.println("publishMessage的length="+length);
		
		return length;
	}

	@Override
	public boolean isMessageIdRequired() {
		return true;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
