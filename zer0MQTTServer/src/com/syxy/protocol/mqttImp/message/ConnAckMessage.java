package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * MQTT协议ConnAck消息类型实现类，连接确认消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-2
 */
public class ConnAckMessage extends Message {
	
	public enum ConnectionStatus {
		ACCEPTED(0x00),

		UNACCEPTABLE_PROTOCOL_VERSION(0x01),

		IDENTIFIER_REJECTED(0x02),

		SERVER_UNAVAILABLE(0x03),

		BAD_USERNAME_OR_PASSWORD(0x04),

		NOT_AUTHORIZED(0x05);
		
		private final int value;
		
		ConnectionStatus(int value) {
			this.value = value;
		}
		
		/**
		 * 获取类型对应的值
		 * @return int
		 * @author zer0
		 * @version 1.0
		 * @date 2016-3-4
		 */
		public int value() {
			return value;
		}
		
		//通过读取到的整型来获取对应的QoS类型
		public static ConnectionStatus valueOf(byte i) {
			for(ConnectionStatus q: ConnectionStatus.values()) {
				if (q.value == i)
					return q;
			}
			throw new IllegalArgumentException("连接响应值无效: " + i);
		}
	}
	
	
	public ConnAckMessage(FixedHeader fixedHeader, ConnAckVariableHeader variableHeader) {
		super(fixedHeader, variableHeader);
	}
	
	@Override
	public ConnAckVariableHeader getVariableHeader(){
		return (ConnAckVariableHeader)super.getVariableHeader();
	}

	
//	@Override
//	public byte[] encode() throws IOException {
//		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//		DataOutputStream dos = new DataOutputStream(byteOut);
//
//		dos.write(this.getSessionPresent());//写入ConnectAcknowledgeFlags字段
//		//根据状态写入返回码
//		switch (status) {
//		case ACCEPTED:
//			dos.write(0x00);
//			break;
//		case UNACCEPTABLE_PROTOCOL_VERSION:
//			dos.write(0x01);
//			break;
//		case IDENTIFIER_REJECTED:
//			dos.write(0x02);
//			break;
//		case SERVER_UNAVAILABLE:
//			dos.write(0x03);
//			break;
//		case BAD_USERNAME_OR_PASSWORD:
//			dos.write(0x04);
//			break;
//		case NOT_AUTHORIZED:
//			dos.write(0x05);
//			break;
//		default:
//			throw new UnsupportedOperationException("CONNACK不支持该状态码 " + status);
//		}
//		dos.flush();
//		
//		//将dos转换为byte[]，然后写入缓冲区
//		byte[] bArray = byteOut.toByteArray();
//		return bArray;
//	}
//
//	@Override
//	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
//		throw new UnsupportedOperationException("CONNACK无需解码，该类型仅能从服务器发往客户端");
//	}
//
//	@Override
//	public int messageLength(Message msg) {
//		return CONNACK_SIZE;
//	}
//
//	@Override
//	public boolean isMessageIdRequired() {
//		return false;
//	}
//	
//	public ConnectionStatus getStatus() {
//		return status;
//	}
//
//	public void setStatus(ConnectionStatus status) {
//		this.status = status;
//	}
//
//	public int getSessionPresent() {
//		return sessionPresent;
//	}
//
//	public void setSessionPresent(int sessionPresent) {
//		this.sessionPresent = sessionPresent;
//	}

}
