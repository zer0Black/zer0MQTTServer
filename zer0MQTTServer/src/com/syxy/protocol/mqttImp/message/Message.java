package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.Type;
import com.syxy.server.ClientSession;
import com.syxy.util.BufferPool;

/**
 * <li>定义MQTT协议固定头部，并作为细分的message的基类
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public abstract class Message {
	
	//包ID是两个字节，所以最大的是65535，最小是1
	private static final int MIN_MSG_ID = 1;		
	private static final int MAX_MSG_ID = 65535;
	private static int nextMsgId = MIN_MSG_ID - 1;
	private static Hashtable inUseMsgIds = new Hashtable();
		
	private int packgeID;//包ID	
	
	private HeaderMessage headerMessage;
	

	public Message(){
		
	}
	
	public Message(Type type) {
		headerMessage = new HeaderMessage(type, false, QoS.AT_MOST_ONCE, false);
	}
	
	public Message(HeaderMessage headerMessage){
		this.headerMessage = headerMessage;
	}
	
	/**
	 * <li>方法名 encode
	 * <li>返回类型 byte[]
	 * <li>说明 对MQTT协议进行编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-2
	 */
	public abstract byte[] encode() throws IOException;

	/**
	 * <li>方法名 decode
	 * <li>@param buffer
	 * <li>返回类型 Message
	 * <li>说明 对MQTT协议头部进行解码，并返回整个类
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-2
	 */
	public abstract Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException;

	/**
	 * <li>方法名 handlerMessage
	 * <li>@param client
	 * <li>返回类型 void
	 * <li>说明 根据协议，对解码后的信息做相应的处理,即调用client来回写信息之类
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-3
	 */
	public abstract void handlerMessage(ClientSession client);

	/**
	 * <li>方法名 messageLength
	 * <li>返回类型 int
	 * <li>说明 计算整个协议的字节数(可变协议头+消息体)
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-3
	 */
	public abstract int messageLength(Message msg);
	
	/**
	 * <li>方法名 isMessageIdRequired
	 * <li>返回类型 boolean
	 * <li>说明 所有消息类型都需要实现该函数，告知是否需要包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-5
	 */
	public abstract boolean isMessageIdRequired();
	
	/**
	 * <li>方法名 releaseMessageId
	 * <li>@param msgId
	 * <li>返回类型 void
	 * <li>说明释放不用的包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-5
	 */
	public synchronized void releaseMessageId(int msgId) {
		inUseMsgIds.remove(new Integer(msgId));
	}
	
	/**
	 * <li>方法名 getNextMessageId
	 * <li>返回类型 int
	 * <li>说明 获取包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-5
	 */
	public static synchronized int getNextMessageId(){
		int startingMessageId = nextMsgId;
		//循环两次是为了给异步出问题提供一个容错范围
		int loopCount = 0;
	    do {
	        nextMsgId++;
	        if ( nextMsgId > MAX_MSG_ID ) {
	            nextMsgId = MIN_MSG_ID;
	        }
	        if (nextMsgId == startingMessageId) {
	        	loopCount++;
	        	if (loopCount == 2) {
	        		throw new UnsupportedOperationException("获取不到可用的包ID");
	        	}
	        }
	    } while( inUseMsgIds.containsKey( new Integer(nextMsgId) ) );
	    Integer id = new Integer(nextMsgId);
	    inUseMsgIds.put(id, id);
	    return nextMsgId;
	}
	
	/**
	 * <li>方法名 encodePackageID
	 * <li>返回类型 byte[]
	 * <li>说明 对MQTT协议的PackageID进行编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-5
	 */
	public byte[] encodePackageID() throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeShort(this.getPackgeID());
		dos.flush();
		return baos.toByteArray();
	}

	/**
	 * <li>方法名 decodePackageID
	 * <li>@param byteBuffer
	 * <li>返回类型 int
	 * <li>说明对MQTT协议的PackageID进行解码
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-5
	 */
	public int decodePackageID(ByteBuffer byteBuffer) throws IOException{
		InputStream in = new ByteArrayInputStream(byteBuffer.array());
		DataInputStream dataInputStream = new DataInputStream(in);
		
		int pacakgeID = dataInputStream.readUnsignedShort();
		return pacakgeID;
	};
	
	public int getPackgeID() {
		return packgeID;
	}

	public void setPackgeID(int packgeID) {
		this.packgeID = packgeID;
	}

	public Type getType() {
		return headerMessage.type;
	}

	public void setType(Type type) {
		this.headerMessage.type = type;
	}

	public HeaderMessage getHeaderMessage() {
		return headerMessage;
	}

	public void setHeaderMessage(HeaderMessage headerMessage) {
		this.headerMessage = headerMessage;
	}
	
//	public boolean isDup() {
//		return headerMessage.dup;
//	}
//
//	public void setDup(boolean dup) {
//		this.headerMessage.dup = dup;
//	}
//
	public QoS getQos() {
		return headerMessage.qos;
	}
//
	public void setQos(QoS qos) {
		this.headerMessage.qos = qos;
	}
//
//	public boolean isRetain() {
//		return headerMessage.retain;
//	}
//
//	public void setRetain(boolean retain) {
//		this.headerMessage.retain = retain;
//	}
	
	
	
	/****************************************************************
	 * <li>MQTT协议头类
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-2
	 ****************************************************************/
	public static class HeaderMessage extends Message{
		
		private static int HEADER_SIZE = 2;
		
		private Type type;	//MQTT协议头前4bit，代表消息类型
		private boolean dup; //MQTT协议头第5bit，代表打开标志，表示是否第一次发送
		private QoS qos = QoS.AT_MOST_ONCE; //MQTT协议头前6,7bit，代表服务质量
		private boolean retain; //MQTT协议头第8bit，代表是否保持
		private int messageLength; //第二个字节
		
		public HeaderMessage(){
			
		}	
		
		public HeaderMessage(Type type, boolean dup, QoS qos, boolean retain){
			this.type = type;
			this.dup = dup;
			this.qos = qos;
			this.retain = retain;
		}

		@Override
		public byte[] encode() throws IOException{
			
			byte b = 0;
			b = (byte) (type.val << 4);
			b |= dup ? 0x8 : 0x0;
			b |= qos.val << 1;
			b |= retain ? 0x1 : 0;
			
			byte[] bArray = new byte[]{b};
			return bArray;			
		}
		
		public static HeaderMessage decodeMessage(ByteBuffer byteBuffer) throws IOException{
			HeaderMessage headerMessage = new HeaderMessage();
			HeaderMessage returnMessage = headerMessage.decode(byteBuffer, 0);
			returnMessage.setMessageLength(returnMessage.bytesToLength(byteBuffer));
			
			System.out.println("解码头部处bytebuffer的position="+byteBuffer.position());
			System.out.println("解码头部处bytebuffer的limit="+byteBuffer.limit());
			
			//如果缓冲区的数据大于等于协议该有的数据，证明数据读取完毕
			if (byteBuffer.limit() >= returnMessage.getMessageLength()) {
				BufferPool.removeReadedData(byteBuffer);
				
				System.out.println("解码头部处bytebuffer移除已读后的position="+byteBuffer.position());
				System.out.println("解码头部处bytebuffer移除已读后的limit="+byteBuffer.limit());
				
				return returnMessage;
			}else{
				return null;
			}
			
			
		}
		
		@Override
		public HeaderMessage decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
			// TODO Auto-generated method stub
			byte headerData = byteBuffer.get();
			HeaderMessage header = new HeaderMessage();
			
			Type type = Type.valueOf((headerData >> 4) & 0xF);
			Boolean dup = (headerData & 0x8) > 0;
			QoS qos = QoS.valueOf((headerData & 0x6) >> 1);
			Boolean retain = (headerData & 0x1) > 0;
			
			header.setType(type);
			header.setDup(dup);
			header.setQos(qos);
			header.setRetain(retain);	
			
			//读完以后将已读的数据移除
//			BufferPool.removeReadedData(byteBuffer);

			return header;
		}	
		
		/**
		 * <li>方法名 bytesToLength
		 * <li>@param byteBuffer
		 * <li>返回类型 int
		 * <li>说明 对MQTT协议进行编码
		 * <li>作者 zer0
		 * <li>创建日期 2015-3-3
		 */
		private int bytesToLength(ByteBuffer byteBuffer) throws IOException {
		    int multiplier = 1;
		    int length = 0;
		    int digit = 0;
		    do {
		    	digit = byteBuffer.get();
		        length += (digit & 0x7f) * multiplier;
		        multiplier *= 128;
		   } while ((digit & 0x80) != 0);
		    
		    return length;
		}
		
		/**
		 * <li>方法名 lengthToBytes
		 * <li>@param msg
		 * <li>返回类型 ByteBuffer
		 * <li>说明 将int数据转换成二进制并写入bytebuffer
		 * <li>作者 zer0
		 * <li>创建日期 2015-3-3
		 */
		public byte[] lengthToBytes(Message msg)throws IOException {
		    int val = msg.messageLength(msg);
		    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(byteOut);
		    do {
				byte b = (byte) (val & 0x7F);
				val >>= 7;
				if (val > 0) {
					b |= 0x80;
				}
				dos.write(b);
			} while (val > 0);
		    
		   byte[] bArray = byteOut.toByteArray();	   
		   return bArray;
		}
		
		@Override
		public int messageLength(Message msg){
			return HEADER_SIZE;         
		}
		         
		@Override
		public void handlerMessage(ClientSession client) {
			
		}

		@Override
		public String toString() {
			return "Header [type=" + type + ", retain=" + retain + ", qos="
					+ qos + ", dup=" + dup + "]";
		}

		@Override
		public boolean isMessageIdRequired() {
			// TODO Auto-generated method stub
			return false;
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

		public int getMessageLength() {
			return messageLength;
		}

		public void setMessageLength(int messageLength) {
			this.messageLength = messageLength;
		}
			
	}

}
