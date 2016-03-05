package com.syxy.protocol.mqttImp.message;

import java.util.List;

/**
 * MQTT协议Subscribe消息类型的荷载
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-4
 */
public class SubscribePayload{
	private List<TopicSubscribe> topicSubscribes;

	public SubscribePayload(List<TopicSubscribe> topicSubscribes) {
		super();
		this.topicSubscribes = topicSubscribes;
	}

	public List<TopicSubscribe> getTopicSubscribes() {
		return topicSubscribes;
	}

}

