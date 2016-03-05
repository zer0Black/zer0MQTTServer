package com.syxy.protocol.mqttImp.message;

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
	
	public TopicSubscribe(String topicFilter, QoS qos) {
		super();
		this.topicFilter = topicFilter;
		this.qos = qos;
	}
	
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