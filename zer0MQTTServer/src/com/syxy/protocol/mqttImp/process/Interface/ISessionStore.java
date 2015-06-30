package com.syxy.protocol.mqttImp.process.Interface;

import com.syxy.protocol.mqttImp.process.subscribe.Subscription;

/**
 * <li>说明 会话存储类
 * <li>作者 zer0
 * <li>创建日期 2015-05-07
 */
public interface ISessionStore {

	/**
	 * <li>方法名 contains
	 * <li>@param clientID
	 * <li>返回类型 boolean
	 * <li>说明 查看是否已储存了该客户端ID，如果存储了则返回true
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-07
	 */
	boolean contains(String clientID);
	
	/**
	 * <li>方法名 wipeSubscriptions
	 * <li>@param clientID
	 * <li>返回参数 void
	 * <li>说明 清理某个ID所有订阅信息
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-19
	 */
	void wipeSubscriptions(String clientID);
	
	/**
	 * <li>方法名 addNewSubscription
	 * <li>@param newSubscription
	 * <li>@param clientID
	 * <li>返回参数 void
	 * <li>说明 添加某个订阅消息到存储
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-25
	 */
	void addNewSubscription(Subscription newSubscription, String clientID);
	
	/**
	 * <li>方法名 removeSubscription
	 * <li>@param topic
	 * <li>@param clientID
	 * <li>返回参数 void
	 * <li>说明 从会话的持久化存储中移除某个订阅主题中的某个client
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-26
	 */
	void removeSubscription(String topic, String clientID);
}
