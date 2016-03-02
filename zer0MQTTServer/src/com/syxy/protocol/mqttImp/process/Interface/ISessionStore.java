package com.syxy.protocol.mqttImp.process.Interface;

import com.syxy.protocol.mqttImp.process.subscribe.Subscription;

/**
 *  会话存储类
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-05-07
 */
public interface ISessionStore {

	/**
	 * 查看是否已储存了该客户端ID，如果存储了则返回true
	 * @param clientID
	 * @return boolean
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-07
	 */
	boolean searchSubscriptions(String clientID);
	
	/**
	 * 清理某个ID所有订阅信息
	 * @param clientID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-19
	 */
	void wipeSubscriptions(String clientID);
	

	/**
	 * 添加某个订阅消息到存储
	 * @param newSubscription
	 * @param clientID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-25
	 */
	void addNewSubscription(Subscription newSubscription, String clientID);
	
	/**
	 * 从会话的持久化存储中移除某个订阅主题中的某个client
	 * @param topic
	 * @param clientID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-25
	 */
	void removeSubscription(String topic, String clientID);
	
}
