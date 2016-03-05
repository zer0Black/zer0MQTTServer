package com.syxy.protocol.mqttImp.message;

import java.util.List;

/**
 * MQTT协议Connect消息类型的荷载
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-4
 */
public class UnSubscribePayload{
	
	private List<String> topics;
	
	public UnSubscribePayload(List<String> topics) {
		super();
		this.topics = topics;
	}

	public List<String> getTopics() {
		return topics;
	}

}