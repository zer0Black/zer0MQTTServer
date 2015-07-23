package com.syxy.protocol.mqttImp.process.Impl.dataHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.process.Interface.IMessagesStore;
import com.syxy.protocol.mqttImp.process.Interface.ISessionStore;
import com.syxy.protocol.mqttImp.process.event.PublishEvent;
import com.syxy.protocol.mqttImp.process.subscribe.Subscription;

/**
 * <li>说明 对数据进行保存，视情况决定是临时保存还是持久化保存
 * <li>作者 zer0
 * <li>创建日期 2015-7-7
 */
public class DBPersistentStore implements IMessagesStore, ISessionStore {

	//为Session保存的的可能需要重发的消息
	private ConcurrentMap<String, List<PublishEvent>> persistentOfflineMessage = new ConcurrentHashMap<String, List<PublishEvent>>();
	//为Qos1和Qos2临时保存的消息
	private ConcurrentMap<String, PublishEvent> persistentQosTempMessage = new ConcurrentHashMap<String, PublishEvent>();
	//持久化存储session状态和对应session的token
	private ConcurrentMap<String, String> persisitentSessionStore = new ConcurrentHashMap<String, String>();
	//持久化存储session和与之对应的subscription Set
	private ConcurrentMap<String, Set<Subscription>> persistentSubscriptionStore = new ConcurrentHashMap<String, Set<Subscription>>(); 
	
	@Override
	public boolean contains(String clientID) {
		return persisitentSessionStore.containsKey(clientID);
	}

	@Override
	public void wipeSubscriptions(String clientID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addNewSubscription(Subscription newSubscription, String clientID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSubscription(String topic, String clientID) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void storePackgeID(String clientID, Integer packgeID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePackgeID(String clientID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeTempMessageForPublish(String publishKey, PublishEvent pubEvent) {
		// TODO Auto-generated method stub
//		persistentQosTempMessage.put
	}

	@Override
	public void removeTempMessageForPublish(String clientID, Integer packgeID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeRetained(String topic, byte[] message, QoS qos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanRetained(String topic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSession(String clientID, String token) {
		persisitentSessionStore.put(clientID, token);
	}

}
