package com.syxy.protocol.mqttImp.process.Interface;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.process.event.PublishEvent;

/**
 * <li>说明 消息存储接口
 * <li>作者 zer0
 * <li>创建日期 2015-05-07
 */
public interface IMessagesStore {

	public static class StoredMessage implements Serializable {
        final QoS qos;
        final byte[] payload;
        final String topic;

        public StoredMessage(byte[] message, QoS qos, String topic) {
            this.qos = qos;
            this.payload = message;
            this.topic = topic;
        }

        public QoS getQos() {
            return qos;
        }

        public byte[] getPayload() {
			return payload;
		}

		public String getTopic() {
            return topic;
        }
    }
	
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
	void storePublicPackgeID(String clientID, Integer packgeID);
	
	/**
	 * <li>方法名 removePubRecPackgeID
	 * <li>@param clientID
	 * <li>返回参数 void
	 * <li>说明 移除包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void removePubRecPackgeID(String clientID);
	
	/**
	 * <li>方法名 storePubRecPackgeID
	 * <li>@param clientID
	 * <li>@param packgeID
	 * <li>返回参数 void
	 * <li>说明 存储包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void storePubRecPackgeID(String clientID, Integer packgeID);
	
	/**
	 * <li>方法名 removePackgeID
	 * <li>@param clientID
	 * <li>返回参数 void
	 * <li>说明 移除包ID
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	void removePublicPackgeID(String clientID);
	
	/**
	 * <li>方法名 storeTempMessageForPublish
	 * <li>@param publishKey
	 * <li>@param pubEvent
	 * <li>返回参数 void
	 * <li>说明 存储临时的Publish信息
	 * <li>作者 zer0
	 * <li>创建日期 2015-05-21
	 */
	/**
	 * 当Qos>0的时候，临时存储Publish消息，用于重发
	 * @param publishKey
	 * @param pubEvent
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void storeQosPublishMessage(String publishKey, PublishEvent pubEvent);
	
	/**
	 * 在收到对应的响应包后，删除Publish消息的临时存储
	 * @param publishKey
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void removeQosPublishMessage(String publishKey);

	/**
	 * 获取临时存储的Publish消息，在等待时间过后未收到对应的响应包，则重发该Publish消息
	 * @param publishKey
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void searchQosPublishMessage(String publishKey);
	
	/**
	 * 持久化存储保留Retain为1的指定topic的最新信息，该信息会在新客户端订阅某主题的时候发送给此客户端
	 * @param topic
	 * @param message
	 * @param qos
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-26
	 */
    void storeRetained(String topic, byte[] message, QoS qos);
    
    /**
	 * 删除指定topic的Retain信息
	 * @param topic
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-26
	 */
    void cleanRetained(String topic);
    
    /**
	 * 从Retain中搜索对应topic中保存的信息
	 * @param topic
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-27
	 */
    Collection<StoredMessage> searchRetained(String topic);
}
