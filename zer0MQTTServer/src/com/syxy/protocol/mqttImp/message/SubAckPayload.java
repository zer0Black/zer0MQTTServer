package com.syxy.protocol.mqttImp.message;

import java.util.List;

/**
 * MQTT协议SubAck消息类型的荷载
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-4
 */
public class SubAckPayload{
	private List<Integer> grantedQosLevel;
	
	public SubAckPayload(List<Integer> grantedQosLevel) {
		this.grantedQosLevel = grantedQosLevel;
	}

	public List<Integer> getGrantedQosLevel() {
		return grantedQosLevel;
	}
	
}
