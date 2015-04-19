package com.syxy.protocol.mqttImp.process.subscribe;

import java.util.ArrayList;
import java.util.LinkedList;

import com.syxy.protocol.mqttImp.QoS;


/**
 * <li>说明 订阅的树节点，保存订阅的每个节点的信息
 * <li>作者 zer0
 * <li>创建日期 2015-4-11
 */
public class subscriptionTreeNode{
	QoS requestedQos; //max QoS acceptable
    String topicFilter;
    LinkedList<String> clientIDs;
    ArrayList<subscriptionTreeNode> childrens;
    boolean cleanSession;
    boolean active = true;
    
    public subscriptionTreeNode(LinkedList<String> clientIDs, String topicFilter, QoS requestedQos, boolean cleanSession) {
    	this.clientIDs = clientIDs;
        this.requestedQos = requestedQos;
        this.topicFilter = topicFilter;
        this.cleanSession = cleanSession;
        initChildrens();
    }
    
    public void initChildrens(){
    	if (childrens == null) {
			childrens = new ArrayList<subscriptionTreeNode>();
		}
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

	public ArrayList<subscriptionTreeNode> getChildrens() {
		return childrens;
	}

	public void setChildrens(ArrayList<subscriptionTreeNode> childrens) {
		this.childrens = childrens;
	}
    
}
