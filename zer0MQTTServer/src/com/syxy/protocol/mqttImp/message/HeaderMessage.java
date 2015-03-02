package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * <li>MQTT协议头类
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public class HeaderMessage extends Message{
	
	private static int HEADER_SIZE = 2;
	
	private Type type;	//MQTT协议头前4bit，代表消息类型
	private boolean dup; //MQTT协议头第5bit，代表打开标志，表示是否第一次发送
	private QoS qos = QoS.AT_MOST_ONCE; //MQTT协议头前6,7bit，代表服务质量
	private boolean retain; //MQTT协议头第8bit，代表是否保持
	
	public HeaderMessage(){
		
	}
	
	public HeaderMessage(Type type, boolean dup, QoS qos, boolean retain){
		this.type = type;
		this.dup = dup;
		this.qos = qos;
		this.retain = retain;
	}


	@Override
	public ByteBuffer encode() throws IOException{
		ByteBuffer buffer =ByteBuffer.allocate(HEADER_SIZE);
		
		byte b = 0;
		b = (byte) (type.val << 4);
		b |= dup ? 0x8 : 0x0;
		b |= qos.val << 1;
		b |= retain ? 0x1 : 0;
		buffer.put(b);
		
		return buffer;			
	}
	
	@Override
	public Message decode(ByteBuffer buffer) throws IOException{
		byte headerData = buffer.get();
		HeaderMessage header = new HeaderMessage();
		
		Type type = Type.valueOf((headerData >> 4) & 0xF);
		Boolean dup = (headerData & 0x8) > 0;
		QoS qos = QoS.valueOf((headerData & 0x6) >> 1);
		Boolean retain = (headerData & 0x1) > 0;
		
		header.setType(type);
		header.setDup(dup);
		header.setQos(qos);
		header.setRetain(retain);
		
		return header;
	}

	@Override
	public String toString() {
		return "Header [type=" + type + ", retain=" + retain + ", qos="
				+ qos + ", dup=" + dup + "]";
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isDup() {
		return dup;
	}

	public void setDup(boolean dup) {
		this.dup = dup;
	}

	public QoS getQos() {
		return qos;
	}

	public void setQos(QoS qos) {
		this.qos = qos;
	}

	public boolean isRetain() {
		return retain;
	}

	public void setRetain(boolean retain) {
		this.retain = retain;
	}		
	
	
}
