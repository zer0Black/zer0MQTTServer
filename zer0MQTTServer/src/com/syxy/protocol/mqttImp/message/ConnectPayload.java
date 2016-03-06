package com.syxy.protocol.mqttImp.message;

import io.netty.buffer.ByteBuf;

/**
 * MQTT协议Connect消息类型的荷载
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-4
 */
public class ConnectPayload{
	private String clientId;//客户端ID
	private String willTopic;
	private String willMessage;
	private String username;//如果设置User Name标识，可以在此读取用户名称
	private String password;//如果设置Password标识，便可读取用户密码
	
	public ConnectPayload(String clientId, String willTopic,
			String willMessage, String username, String password) {
		super();
		this.clientId = clientId;
		this.willTopic = willTopic;
		this.willMessage = willMessage;
		this.username = username;
		this.password = password;
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
