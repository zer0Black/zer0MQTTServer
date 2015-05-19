package com.syxy.protocol.mqttImp.process.Impl;

import java.util.List;

import com.syxy.protocol.mqttImp.process.event.PublishEvent;

/**
 * <li>说明 消息存储接口
 * <li>作者 zer0
 * <li>创建日期 2015-05-07
 */
public interface IMessagesStore {

	/**
	 * <li>方法名 listMessagesInSession
	 * <li>@param clientID
	 * <li>返回参数 List<PublishEvent>
	 * <li>说明 返回某个clientID的离线消息列表
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-18
	 */
	List<PublishEvent> listMessagesInSession(String clientID);
	
	/**
	 * <li>方法名 removeMessageInSession
	 * <li>@param clientID
	 * <li>@param messageID
	 * <li>返回参数 void
	 * <li>说明 移除某个clientID的离线消息
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-18
	 */
	void removeMessageInSession(String clientID, int packgeID);
	

}
