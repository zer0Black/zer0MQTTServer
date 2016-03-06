package com.syxy.protocol.mqttImp.message;

/**
 * MQTT协议Publish消息类型的可变头部
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-4
 */
public class PublishVariableHeader{
	private String topic;
	private int packageID;
	
	public PublishVariableHeader(String topic) {
		this.topic = topic;
	}
	
	public PublishVariableHeader(String topic, int packageID) {
		this.topic = topic;
		this.packageID = packageID;
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public int getPackageID() {
		return packageID;
	}
	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}
	
}