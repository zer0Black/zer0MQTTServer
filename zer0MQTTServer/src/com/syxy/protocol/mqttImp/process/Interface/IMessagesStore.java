package com.syxy.protocol.mqttImp.process.Interface;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import com.syxy.protocol.mqttImp.message.QoS;
import com.syxy.protocol.mqttImp.process.event.PubRelEvent;
import com.syxy.protocol.mqttImp.process.event.PublishEvent;

/**
 *  消息存储接口
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-05-07
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
	 * 初始化存储
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-30
	 */
	void initStore();
	
	/**
	 * 返回某个clientID的离线消息列表
	 * @param clientID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-18
	 */
	List<PublishEvent> listMessagesInSession(String clientID);
	

	/**
	 * 在重发以后，移除publish的离线消息事件
	 * @param clientID
	 * @param packgeID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-18
	 */
	void removeMessageInSessionForPublish(String clientID, Integer packgeID);
	
	/**
	 * 存储publish的离线消息事件，为CleanSession=0的情况做重发准备
	 * @param pubEvent
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void storeMessageToSessionForPublish(PublishEvent pubEvent);

	/**
	 * 存储Publish的包ID
	 * @param clientID
	 * @param packgeID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void storePublicPackgeID(String clientID, Integer packgeID);
	
	/**
	 * 移除Publish的包ID
	 * @param clientID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void removePublicPackgeID(String clientID);
	
	/**
	 * 移除PubRec的包ID
	 * @param clientID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void removePubRecPackgeID(String clientID);
	
	/**
	 * 存储PubRec的包ID
	 * @param clientID
	 * @param packgeID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void storePubRecPackgeID(String clientID, Integer packgeID);
	
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
	 * @return PublishEvent
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-28
	 */
	PublishEvent searchQosPublishMessage(String publishKey);
	
	/**
	 * 当Qos=2的时候，临时存储PubRel消息，在未收到PubComp包时用于重发
	 * @param pubRelKey
	 * @param pubRelEvent
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-28
	 */
	void storePubRelMessage(String pubRelKey, PubRelEvent pubRelEvent);
	
	/**
	 * 在收到对应的响应包后，删除PubRel消息的临时存储
	 * @param pubRelKey
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-28
	 */
	void removePubRelMessage(String pubRelKey);

	/**
	 * 获取临时存储的PubRel消息，在等待时间过后未收到对应的响应包，则重发该PubRel消息
	 * @param pubRelKey
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-28
	 */
	PubRelEvent searchPubRelMessage(String pubRelKey);
	
	/**
	 * 持久化存储保留Retain为1的指定topic的最新信息，该信息会在新客户端订阅某主题的时候发送给此客户端
	 * @param topic
	 * @param message
	 * @param qos
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-26
	 */
    void storeRetained(String topic, ByteBuf message, QoS qos);
    
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
