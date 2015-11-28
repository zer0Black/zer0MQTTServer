package com.syxy.protocol.mqttImp.process.Impl.dataHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.process.Interface.IMessagesStore;
import com.syxy.protocol.mqttImp.process.Interface.ISessionStore;
import com.syxy.protocol.mqttImp.process.event.PubRelEvent;
import com.syxy.protocol.mqttImp.process.event.PublishEvent;
import com.syxy.protocol.mqttImp.process.subscribe.Subscription;

/**
 * <li>说明 对数据进行保存，视情况决定是临时保存还是持久化保存
 * <li>作者 zer0
 * <li>创建日期 2015-7-7
 */
public class MapDBPersistentStore implements IMessagesStore, ISessionStore {

	private final static Logger Log = Logger.getLogger(MapDBPersistentStore.class);
	
	//为Session保存的的可能需要重发的消息
	private ConcurrentMap<String, List<PublishEvent>> persistentOfflineMessage = new ConcurrentHashMap<String, List<PublishEvent>>();
	//为Qos1和Qos2临时保存的消息
	private ConcurrentMap<String, PublishEvent> persistentQosTempMessage = new ConcurrentHashMap<String, PublishEvent>();
	//持久化存储session状态和对应session的token
	private ConcurrentMap<String, String> persisitentSessionStore = new ConcurrentHashMap<String, String>();
	//持久化存储session和与之对应的subscription Set
	private ConcurrentMap<String, Set<Subscription>> persistentSubscriptionStore = new ConcurrentHashMap<String, Set<Subscription>>();
	//持久化的Retain
	private ConcurrentMap<String, StoredMessage> retainedStore = new ConcurrentHashMap<String, StoredMessage>();
	//保存publish包ID
	private ConcurrentMap<String, Integer> publishPackgeIDStore = new ConcurrentHashMap<String, Integer>();
	//保存pubRec包ID
	private ConcurrentMap<String, Integer> pubRecPackgeIDStore = new ConcurrentHashMap<String, Integer>();
	
	@Override
	public boolean contains(String clientID) {
		return persisitentSessionStore.containsKey(clientID);
	}

	@Override
	public void wipeSubscriptions(String clientID) {
		persistentSubscriptionStore.remove(clientID);
	}

	@Override
	public void addNewSubscription(Subscription newSubscription, String clientID) {
		Log.info("添加新订阅，订阅:" + newSubscription + ",客户端ID:" + clientID );
		 if (!persistentSubscriptionStore.containsKey(clientID)) {
	            Log.info("没客户端ID" + clientID + " , 为它创建订阅集");
	            persistentSubscriptionStore.put(clientID, new HashSet<Subscription>());
	        }
		 
		 Set<Subscription> subs = persistentSubscriptionStore.get(clientID);
		  if (!subs.contains(newSubscription)) {
	            Log.info("更新客户端" + clientID + "的订阅集");
	            Subscription existingSubscription = null;
	            //遍历订阅集里所有的订阅，查看是否有相同topic的订阅，有的话，移除之前的订阅，添加新的
	            for (Subscription scanSub : subs) {
	                if (newSubscription.getTopicFilter().equals(scanSub.getTopicFilter())) {
	                    existingSubscription = scanSub;
	                    break;
	                }
	            }
	            if (existingSubscription != null) {
	                subs.remove(existingSubscription);
	            }
	            subs.add(newSubscription);
	            persistentSubscriptionStore.put(clientID, subs);
	            Log.debug("客户端" + clientID + "的订阅集现在是这样的" + subs);
	        }
	}

	@Override
	public void removeSubscription(String topic, String clientID) {
		Log.info("删除客户端" + clientID + "的" + topic + "订阅");
		if (!persistentSubscriptionStore.containsKey(clientID)) {
            Log.debug("没客户端ID" + clientID + " , 无法删除");
            return;
        }
		Set<Subscription> subs = persistentSubscriptionStore.get(clientID);
		 Subscription existingSubscription = null;
		for (Subscription subscription : subs) {
			String topicfilter = subscription.getTopicFilter();
			if (topicfilter.equals(topic)) {
				existingSubscription = subscription;
			}
		}
		if (existingSubscription != null) {
            subs.remove(existingSubscription);
        }
	}

	@Override
	public List<PublishEvent> listMessagesInSession(String clientID) {
		List<PublishEvent> allEvents = new ArrayList<PublishEvent>();
		List<PublishEvent> storeEvents = persistentOfflineMessage.get(clientID);
		//如果该client无离线消息，则把storeEvents设置为空集合
		if (storeEvents == null) {
			storeEvents = Collections.<PublishEvent>emptyList();
		}
		for (PublishEvent event : storeEvents) {
			allEvents.add(event);
		}
		return allEvents;
	}

	@Override
	public void removeMessageInSessionForPublish(String clientID,
			Integer packgeID) {
		List<PublishEvent> events = persistentOfflineMessage.get(clientID);
		if (events == null) {
			return;
		}
		PublishEvent toRemoveEvt = null;
		for (PublishEvent evt : events) {
	            if (evt.getPackgeID()== packgeID) {
	                toRemoveEvt = evt;
	            }
	     }
		events.remove(toRemoveEvt);
		persistentOfflineMessage.put(clientID, events);
	}

	@Override
	public void storeMessageToSessionForPublish(PublishEvent pubEvent) {
		 List<PublishEvent> storedEvents;
	        String clientID = pubEvent.getClientID();
	        if (!persistentOfflineMessage.containsKey(clientID)) {
	            storedEvents = new ArrayList<PublishEvent>();
	        } else {
	            storedEvents = persistentOfflineMessage.get(clientID);
	        }
	        storedEvents.add(pubEvent);
	        persistentOfflineMessage.put(clientID, storedEvents);
	}

	@Override
	public void storeQosPublishMessage(String publishKey, PublishEvent pubEvent) {
		persistentQosTempMessage.put(publishKey, pubEvent);
	}

	@Override
	public void removeQosPublishMessage(String publishKey) {
		persistentQosTempMessage.remove(publishKey);
	}
	
	@Override
	public PublishEvent searchQosPublishMessage(String publishKey) {
		return persistentQosTempMessage.get(publishKey);
	}
	
	@Override
	public void storePubRelMessage(String pubRelKey, PubRelEvent pubRelEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePubRelMessage(String pubRelKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PubRelEvent searchPubRelMessage(String pubRelKey) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeRetained(String topic, byte[] message, QoS qos) {
		if (message.length <= 0) {
			retainedStore.remove(topic);
		} else {
			StoredMessage storedMessage = new StoredMessage(message, qos, topic);
			retainedStore.put(topic, storedMessage);
		}	
	}

	@Override
	public void cleanRetained(String topic) {
		retainedStore.remove(topic);
	}

	@Override
	public Collection<StoredMessage> searchRetained(String topic) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addSession(String clientID, String token) {
		persisitentSessionStore.put(clientID, token);
	}

	@Override
	public void storePublicPackgeID(String clientID, Integer packgeID) {
		publishPackgeIDStore.put(clientID, packgeID);
	}

	@Override
	public void removePublicPackgeID(String clientID) {
		publishPackgeIDStore.remove(clientID);
	}

	@Override
	public void storePubRecPackgeID(String clientID, Integer packgeID) {
		pubRecPackgeIDStore.put(clientID, packgeID);
	}
	
	@Override
	public void removePubRecPackgeID(String clientID) {
		pubRecPackgeIDStore.remove(clientID);
	}

}
