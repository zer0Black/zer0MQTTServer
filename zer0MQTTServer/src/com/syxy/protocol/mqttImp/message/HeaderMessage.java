package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.Type;
import com.syxy.util.coderTool;

///**
// * <li>MQTT协议头类
// * <li>作者 zer0
// * <li>创建日期 2015-3-2
// */
//public class HeaderMessage extends Message{
//	
////	private static int HEADER_SIZE = 2;
//	
//	private Type type;	//MQTT协议头前4bit，代表消息类型
//	private boolean dup; //MQTT协议头第5bit，代表打开标志，表示是否第一次发送
//	private QoS qos = QoS.AT_MOST_ONCE; //MQTT协议头前6,7bit，代表服务质量
//	private boolean retain; //MQTT协议头第8bit，代表是否保持
//	
//	public HeaderMessage(){
//		
//	}	
//	
//	public HeaderMessage(Type type, boolean dup, QoS qos, boolean retain){
//		this.type = type;
//		this.dup = dup;
//		this.qos = qos;
//		this.retain = retain;
//	}
//	
//
//
//	@Override
//	public byte[] encode() throws IOException{
//		
//		byte b = 0;
//		b = (byte) (type.val << 4);
//		b |= dup ? 0x8 : 0x0;
//		b |= qos.val << 1;
//		b |= retain ? 0x1 : 0;
//		
//		byte[] bArray = new byte[]{b};
//		return bArray;			
//	}
//	
//	public static Message decodeMessage(ByteBuffer buffer) throws IOException{
//		HeaderMessage message = new HeaderMessage();
//		//此处的0只是为了满足继承，在函数中并未用到
//		return message.decode(buffer, 0);
//	}
//	
//	@Override
//	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
//		// TODO Auto-generated method stub
//		byte headerData = byteBuffer.get();
//		HeaderMessage header = new HeaderMessage();
//		
//		Type type = Type.valueOf((headerData >> 4) & 0xF);
//		Boolean dup = (headerData & 0x8) > 0;
//		QoS qos = QoS.valueOf((headerData & 0x6) >> 1);
//		Boolean retain = (headerData & 0x1) > 0;
//		
//		header.setType(type);
//		header.setDup(dup);
//		header.setQos(qos);
//		header.setRetain(retain);
//		
//		//读完以后将已读的数据移除
//		coderTool.removeReadedData(byteBuffer);
//		
//		return header;
//	}	
//	
//	/**
//	 * <li>方法名 bytesToLength
//	 * <li>@param byteBuffer
//	 * <li>返回类型 int
//	 * <li>说明 对MQTT协议进行编码
//	 * <li>作者 zer0
//	 * <li>创建日期 2015-3-3
//	 */
//	public int bytesToLength(ByteBuffer byteBuffer) throws IOException {
//		InputStream in = new ByteArrayInputStream(byteBuffer.array());
//	    int multiplier = 1;
//	    int length = 0;
//	    int digit = 0;
//	    int count = 0;//记录循环了几次
//	    do {
//	        digit = in.read(); //一个字节的有符号或者无符号，转换转换为四个字节有符号 int类型
//	        length += (digit & 0x7f) * multiplier;
//	        multiplier *= 128;
//	        count++;
//	   } while ((digit & 0x80) != 0);
//	    
//	    //byteBuffer的指针移动了一位
//	    byteBuffer.position(count);
//	    //把已读数据清掉
//	    coderTool.removeReadedData(byteBuffer);
//	    
//	    return length;
//	}
//	
//	/**
//	 * <li>方法名 lengthToBytes
//	 * <li>@param msg
//	 * <li>返回类型 ByteBuffer
//	 * <li>说明 将int数据转换成二进制并写入bytebuffer
//	 * <li>作者 zer0
//	 * <li>创建日期 2015-3-3
//	 */
//	public ByteBuffer lengthToBytes(Message msg)throws IOException {
//	    int val = msg.messageLength();
//	    do {
//	         int digit = val % 128;
//	        val = val / 128;
//	         if (val > 0)
//	             digit = digit | 0x80;
//
////	        out.write(digit);
//	   } while (val > 0);
//	   return null;
//	}
//	
//	@Override
//	public int messageLength(){
//		return 0;         
//	}
//	         
//	@Override
//	public void handlerMessage() {
//		
//	}
//
//	@Override
//	public String toString() {
//		return "Header [type=" + type + ", retain=" + retain + ", qos="
//				+ qos + ", dup=" + dup + "]";
//	}
//
//	public Type getType() {
//		return type;
//	}
//
//	public void setType(Type type) {
//		this.type = type;
//	}
//
//	public boolean isDup() {
//		return dup;
//	}
//
//	public void setDup(boolean dup) {
//		this.dup = dup;
//	}
//
//	public QoS getQos() {
//		return qos;
//	}
//
//	public void setQos(QoS qos) {
//		this.qos = qos;
//	}
//
//	public boolean isRetain() {
//		return retain;
//	}
//
//	public void setRetain(boolean retain) {
//		this.retain = retain;
//	}
//	
//}
