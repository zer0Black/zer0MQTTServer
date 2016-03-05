package com.syxy.protocol.mqttImp.message;

/**
 * MQTT协议Connect消息类型的可变头部
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-4
 */
public class ConnectVariableHeader{
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
	private boolean reservedIsZero;//协议的保留位，此位必须校验且必须为0，不为0则断开连接
	
	private int keepAlive;//心跳包时长

	public ConnectVariableHeader(String protocolName,
			byte protocolVersionNumber, boolean hasUsername,
			boolean hasPassword, boolean willRetain, QoS willQoS,
			boolean hasWill, boolean cleanSession, boolean reservedIsZero,
			int keepAlive) {
		this.protocolName = protocolName;
		this.protocolVersionNumber = protocolVersionNumber;
		this.hasUsername = hasUsername;
		this.hasPassword = hasPassword;
		this.willRetain = willRetain;
		this.willQoS = willQoS;
		this.hasWill = hasWill;
		this.cleanSession = cleanSession;
		this.reservedIsZero = reservedIsZero;
		this.keepAlive = keepAlive;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public byte getProtocolVersionNumber() {
		return protocolVersionNumber;
	}

	public boolean isHasUsername() {
		return hasUsername;
	}

	public boolean isHasPassword() {
		return hasPassword;
	}
	
	public boolean isWillRetain() {
		return willRetain;
	}

	public QoS getWillQoS() {
		return willQoS;
	}

	public boolean isHasWill() {
		return hasWill;
	}
	
	public boolean isCleanSession() {
		return cleanSession;
	}

	public boolean isReservedIsZero() {
		return reservedIsZero;
	}

	public int getKeepAlive() {
		return keepAlive;
	}
	
}