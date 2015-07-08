package com.syxy.protocol.mqttImp.process.Impl.dataHandler;

import java.util.ArrayList;
import java.util.List;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.process.Interface.IMessagesStore;
import com.syxy.protocol.mqttImp.process.Interface.ISessionStore;
import com.syxy.protocol.mqttImp.process.event.PublishEvent;
import com.syxy.protocol.mqttImp.process.subscribe.Subscription;

public class DBPersistentStore implements IMessagesStore, ISessionStore {

	
	
	@Override
	public boolean contains(String clientID) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return new ArrayList<PublishEvent>();
	}

	@Override
	public void removeMessageInSessionForPublish(String clientID,
			Integer packgeID) {
		// TODO Auto-generated method stub

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
	public void storeTempMessageForPublish(PublishEvent pubEvent) {
		// TODO Auto-generated method stub

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

}
