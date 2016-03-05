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

	public List<TopicSubscribe> getTopicSubscribes() {
		return topicSubscribes;
	}

	public void setTopicSubscribes(List<TopicSubscribe> topicSubscribes) {
		this.topicSubscribes = topicSubscribes;
	}
	
	/**
	 * Subscribe荷载的封装，一个topic和一个qos是一组荷载
	 * 
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-4
	 */
	public class TopicSubscribe{
		private String topicFilter;
		private QoS qos;
		public String getTopicFilter() {
			return topicFilter;
		}
		public void setTopicFilter(String topicFilter) {
			this.topicFilter = topicFilter;
		}
		public QoS getQos() {
			return qos;
		}
		public void setQos(QoS qos) {
			this.qos = qos;
		}
	}
	
}

