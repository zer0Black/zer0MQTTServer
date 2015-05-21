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
	 * <li>方法名 removeMessageInSessionForPublish
	 * <li>@param pubEvent
	 * <li>返回参数 void
	 * <li>说明 移除某个publish事件的离线消息，与storeMessageToSessionForPublish对应
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-18
	 */
	void removeMessageInSessionForPublish(PublishEvent pubEvent);
	
	/**
	 * <li>方法名 storeMessageToSessionForPublish
	 * <li>@param pubEvent
	 * <li>返回参数 void
	 * <li>说明 存储publish消息事件，为以后重发做准备,与removeMessageInSessionForPublish对应
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void storeMessageToSessionForPublish(PublishEvent pubEvent);

}
