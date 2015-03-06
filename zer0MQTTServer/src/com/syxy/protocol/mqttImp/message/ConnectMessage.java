package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.Type;
import com.syxy.protocol.mqttImp.message.ConnAckMessage.ConnectionStatus;
import com.syxy.server.ClientSession;
import com.syxy.util.BufferPool;
import com.syxy.util.StringTool;
import com.syxy.util.coderTool;

/**
 * <li>MQTT协议Connect消息类型实现类，客户端请求服务器连接的消息类型
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public class ConnectMessage extends Message {

	private final static Logger Log = Logger.getLogger(ConnectMessage.class);
	
	private static final int CONNECT_HEADER_SIZE = 10;
	private int CONNECT_SIZE;//connect消息类型总长度（头+消息体）
	
	private String protocolName = "MQTT";//协议规定的协议名
	private byte protocolVersionNumber = 4;//MQTT_v3.1.1协议的版本号
	
	//Connect Flags的六个参数
	private boolean hasUsername;//是否有用户名，与密码一起，要么都为0，要么都为1，否则无效
	private boolean hasPassword;//是否有密码，与用户名一起，要么都为0，要么都为1，否则无效
	private boolean willRetain;//设置Will Flag为1，Will Retain标志就是有效的，当客户端意
							   //外断开服务器发布其Will Message之后，服务器是否应该继续保存
	private QoS willQoS;//设置Will Flag为1，Will QoS标志就是有效的
	private boolean hasWill;//是否设置遗嘱，设置以后，遗嘱生效。遗嘱就是客户端预先定义好，在自己
	                        //异常断开的情况下，所留下的最后遗愿
	private boolean cleanSession;//是否清理session
	private boolean reserved;//协议的保留位，此位必须校验且必须为0，不为0则断开连接
	
	private int keepAlive;//心跳包时长
	
	//消息体
	private String clientId;//客户端ID
	private String willTopic;
	private String willMessage;
	private String username;//如果设置User Name标识，可以在此读取用户名称
	private String password;//如果设置Password标识，便可读取用户密码
	
	public ConnectMessage(){
		super(Type.CONNECT);
	}
	
	public ConnectMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		throw new UnsupportedOperationException("CONNECT无需编码，该类型仅能从客户端发送服务端");
//		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//		DataOutputStream dos = new DataOutputStream(byteOut);
//		
//		dos.writeUTF(this.getProtocolName());
//		dos.write(this.getProtocolVersionNumber());
//		
//		int flags = this.isCleanSession() ? 0x02 : 0;
//		flags |= this.isHasWill() ? 0x04 : 0;
//		flags |= this.getWillQoS() == null ? 0 : this.getWillQoS().val << 3;
//		flags |= this.isWillRetain() ? 0x20 : 0;
//		flags |= this.isHasPassword() ? 0x40 : 0;
//		flags |= this.isHasUsername() ? 0x80 : 0;
//		dos.write((byte)flags);
//		dos.writeChar(this.getKeepAlive());
//		
//		dos.writeUTF(this.getClientId());
//		if (this.isHasWill()) {
//			dos.writeUTF(this.getWillTopic());
//			dos.writeUTF(this.getWillMessage());
//		}
//		if (this.isHasUsername()) {
//			dos.writeUTF(this.getUsername());
//		}
//		if (this.isHasPassword()) {
//			dos.writeUTF(this.getPassword());
//		}
//		
//		dos.flush();
//		
//		//将dos转换为byte[]，然后写入缓冲区
//		byte[] bArray = byteOut.toByteArray();
//		
//		return bArray;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
		//将byteBuffer转换成DataInputStream，方便调用readUTF方法来读取UTF数据
		InputStream in = new ByteArrayInputStream(byteBuffer.array());
		DataInputStream dataInputStream = new DataInputStream(in);
		
		ConnectMessage connectMessage = new ConnectMessage();
		
		connectMessage.setProtocolName(dataInputStream.readUTF());
		connectMessage.setProtocolVersionNumber(dataInputStream.readByte());
		//读取Connect Flags的各个参数
		byte connectFlags = dataInputStream.readByte();
		connectMessage.setHasUsername((connectFlags & 0x80) > 0);//0x80=10000000
		connectMessage.setHasPassword((connectFlags & 0x40) > 0);//0x40=01000000
		connectMessage.setWillRetain((connectFlags & 0x20) > 0);//0x20=00100000
		connectMessage.setWillQoS(QoS.valueOf(connectFlags >> 3 & 0x03));//0x03=00000011
		connectMessage.setHasWill((connectFlags & 0x04) > 0);//0x04=00000100
		connectMessage.setCleanSession((connectFlags & 0x02) > 0);//0x02=00000010
		
		connectMessage.setKeepAlive(dataInputStream.readShort());//读取心跳包
		connectMessage.setClientId(dataInputStream.readUTF());//读取客户端ID
		
		//如果willFlag的值为1，willTopic和WillMessage便有内容
		if (connectMessage.isHasWill()) {
			connectMessage.setWillTopic(dataInputStream.readUTF());
			connectMessage.setWillMessage(dataInputStream.readUTF());
		}
		
		if (connectMessage.isHasUsername()) {
			connectMessage.setUsername(dataInputStream.readUTF());
		}
		
		if (connectMessage.isHasPassword()) {
			connectMessage.setPassword(dataInputStream.readUTF());
		}
		
		
		//必须再把协议头的解码对象添加进来一起返回
		connectMessage.setHeaderMessage(this.getHeaderMessage());
		
		byteBuffer.position(messageLength);
		BufferPool.removeReadedData(byteBuffer);
		
		return connectMessage;
	}

	@Override
	public void handlerMessage(ClientSession client) {
		Log.info("处理Connect的数据");
		client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.ACCEPTED, 1));
	}
	
	@Override
	public int messageLength(Message msg){
		ConnectMessage connectMessage = (ConnectMessage)msg;
		
		int payLoadSize = StringTool.stringToByte(connectMessage.clientId).length;
		payLoadSize += StringTool.stringToByte(connectMessage.willTopic).length;
		payLoadSize += StringTool.stringToByte(connectMessage.willMessage).length;
		payLoadSize += StringTool.stringToByte(connectMessage.username).length;
		payLoadSize += StringTool.stringToByte(connectMessage.password).length;
		
		this.CONNECT_SIZE = payLoadSize + CONNECT_HEADER_SIZE;
		
		return CONNECT_SIZE;
	}
	
	@Override
	public boolean isMessageIdRequired() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	public byte getProtocolVersionNumber() {
		return protocolVersionNumber;
	}

	public void setProtocolVersionNumber(byte protocolVersionNumber) {
		this.protocolVersionNumber = protocolVersionNumber;
	}

	public boolean isHasUsername() {
		return hasUsername;
	}

	public void setHasUsername(boolean hasUsername) {
		this.hasUsername = hasUsername;
	}

	public boolean isHasPassword() {
		return hasPassword;
	}

	public void setHasPassword(boolean hasPassword) {
		this.hasPassword = hasPassword;
	}

	public boolean isWillRetain() {
		return willRetain;
	}

	public void setWillRetain(boolean willRetain) {
		this.willRetain = willRetain;
	}

	public QoS getWillQoS() {
		return willQoS;
	}

	public void setWillQoS(QoS willQoS) {
		this.willQoS = willQoS;
	}

	public boolean isHasWill() {
		return hasWill;
	}

	public void setHasWill(boolean hasWill) {
		this.hasWill = hasWill;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public int getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(int keepAlive) {
		this.keepAlive = keepAlive;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getWillTopic() {
		return willTopic;
	}

	public void setWillTopic(String willTopic) {
		this.willTopic = willTopic;
	}

	public String getWillMessage() {
		return willMessage;
	}

	public void setWillMessage(String willMessage) {
		this.willMessage = willMessage;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
