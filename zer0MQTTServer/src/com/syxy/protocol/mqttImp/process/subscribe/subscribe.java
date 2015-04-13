package com.syxy.protocol.mqttImp.process.subscribe;

import java.util.LinkedList;

 import com.syxy.protocol.mqttImp.QoS;


/**
 * <li>说明 订阅存储类,存储订阅的topic
 * <li>作者 zer0
 * <li>创建日期 2015-4-11
 */
public class subscribe {
	QoS requestedQos; //max QoS acceptable
    String topicFilter;
    LinkedList<String> clientIDs;
    boolean cleanSession;
    boolean active = true;
    
    public subscribe(LinkedList<String> clientIDs, String topicFilter, QoS requestedQos, boolean cleanSession) {
    	this.clientIDs = clientIDs;
        this.requestedQos = requestedQos;
        this.topicFilter = topicFilter;
        this.cleanSession = cleanSession;
    }

	public QoS getRequestedQos() {
		return requestedQos;
	}

	public void setRequestedQos(QoS requestedQos) {
		this.requestedQos = requestedQos;
	}

	public String getTopicFilter() {
		return topicFilter;
	}

	public void setTopicFilter(String topicFilter) {
		this.topicFilter = topicFilter;
	}

	public LinkedList<String> getClientIDs() {
		return clientIDs;
	}

	public void setClientIDs(LinkedList<String> clientIDs) {
		this.clientIDs = clientIDs;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    

}
