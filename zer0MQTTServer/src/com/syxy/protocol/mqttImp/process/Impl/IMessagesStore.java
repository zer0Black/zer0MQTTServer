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
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回参数 void
	 * <li>说明 移除某个publish事件的离线消息，与storeMessageToSessionForPublish对应
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-18
	 */
	void removeMessageInSessionForPublish(String clientID, Integer packgeID);
	
	/**
	 * <li>方法名 storeMessageToSessionForPublish
	 * <li>@param pubEvent
	 * <li>返回参数 void
	 * <li>说明 存储publish消息事件，为以后重发做准备,与removeMessageInSessionForPublish对应
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void storeMessageToSessionForPublish(PublishEvent pubEvent);

	/**
	 * <li>方法名 storePackgeID
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回参数 void
	 * <li>说明 存储包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void storePackgeID(String clientID, Integer packgeID);
	
	/**
	 * <li>方法名 removePackgeID
	 * <li>@param clientID
	 * <li>返回参数 void
	 * <li>说明 移除包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void removePackgeID(String clientID);
	
	/**
	 * <li>方法名 storeTempMessageForPublish
	 * <li>@param pubEvent
	 * <li>返回参数 void
	 * <li>说明 存储临时的Publish信息
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void storeTempMessageForPublish(PublishEvent pubEvent);
	
	/**
	 * <li>方法名 removeTempMessageForPublish
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回参数 void
	 * <li>说明 删除临时的Publish信息
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void removeTempMessageForPublish(String clientID, Integer packgeID);
}
