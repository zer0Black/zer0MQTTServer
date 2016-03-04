package com.syxy.protocol.mqttImp.process.subscribe;

import java.io.Serializable;

import com.syxy.protocol.mqttImp.message.QoS;

/**
 *  订阅的树节点，保存订阅的每个节点的信息
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-4-11
 */
public class Subscription implements Serializable{

	private static final long serialVersionUID = 6159635655653102856L;
	QoS requestedQos; //max QoS acceptable
    String topicFilter;
    String clientID;
    boolean cleanSession;
    boolean active = true;
    
    public Subscription(String clientID, String topicFilter, QoS requestedQos, boolean cleanSession) {
    	this.clientID = clientID;
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

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getClientID() {
		return clientID;
	}
 
    @Override
    public String toString() {
        return String.format("[filter:%s, cliID: %s, qos: %s, active: %s]", this.topicFilter, this.clientID, this.requestedQos, this.active);
    }
}
