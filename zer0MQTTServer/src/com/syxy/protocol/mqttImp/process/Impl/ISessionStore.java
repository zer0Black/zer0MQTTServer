package com.syxy.protocol.mqttImp.process.Impl;

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
}
