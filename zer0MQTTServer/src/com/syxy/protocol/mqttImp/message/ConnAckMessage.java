package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.Type;
import com.syxy.server.ClientSession;


/**
 * <li>MQTT协议ConnAck消息类型实现类，连接确认消息类型
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public class ConnAckMessage extends Message {
	
	public enum ConnectionStatus {
		ACCEPTED,

		UNACCEPTABLE_PROTOCOL_VERSION,

		IDENTIFIER_REJECTED,

		SERVER_UNAVAILABLE,

		BAD_USERNAME_OR_PASSWORD,

		NOT_AUTHORIZED
	}
	
	public final static int CONNACK_SIZE = 2;//CONNACK的Remaining Length长度
	
	private ConnectionStatus status;//返回给客户端的状态码
	private int sessionPresent;//sessionPresent是告知客户端服务器是否存储了session的位
//	private String payload;//荷载
	
	public ConnAckMessage(){
		super(Type.CONNACK);
	}
	
	public ConnAckMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	public ConnAckMessage(ConnectionStatus status, int sessionPresent) {
		super(Type.CONNACK);
		if (status == null) {
			throw new IllegalArgumentException("CONNACK的状态码不能为空");
		}
		this.setStatus(status);

		if (status == ConnectionStatus.ACCEPTED) {
			this.setSessionPresent(0);
		}else {
			this.setSessionPresent(sessionPresent);	
		}	
	}
	
	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(byteOut);

		dos.write(this.getSessionPresent());//写入ConnectAcknowledgeFlags字段
		//根据状态写入返回码
		switch (status) {
		case ACCEPTED:
			dos.write(0x00);
			break;
		case UNACCEPTABLE_PROTOCOL_VERSION:
			dos.write(0x01);
			break;
		case IDENTIFIER_REJECTED:
			dos.write(0x02);
			break;
		case SERVER_UNAVAILABLE:
			dos.write(0x03);
			break;
		case BAD_USERNAME_OR_PASSWORD:
			dos.write(0x04);
			break;
		case NOT_AUTHORIZED:
			dos.write(0x05);
			break;
		default:
			throw new UnsupportedOperationException("CONNACK不支持该状态码 " + status);
		}
//		if (payload != null) {
//			dos.writeUTF(payload);
//		}
		dos.flush();
		
		//将dos转换为byte[]，然后写入缓冲区
		byte[] bArray = byteOut.toByteArray();
		return bArray;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("CONNACK无需解码，该类型仅能从服务器发往客户端");
	}

	@Override
	public int messageLength(Message msg) {
		return CONNACK_SIZE;
	}

	@Override
	public boolean isMessageIdRequired() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public ConnectionStatus getStatus() {
		return status;
	}

	public void setStatus(ConnectionStatus status) {
		this.status = status;
	}

	public int getSessionPresent() {
		return sessionPresent;
	}

	public void setSessionPresent(int sessionPresent) {
		this.sessionPresent = sessionPresent;
	}

//	public String getPayload() {
//		return payload;
//	}
//
//	public void setPayload(String payload) {
//		this.payload = payload;
//	}

}
