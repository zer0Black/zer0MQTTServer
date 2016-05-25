package com.syxy.protocol.mqttImp.message;

import java.util.List;

/**
 * MQTT协议UnSubscribe消息类型实现类，用于取消订阅topic
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class UnSubscribeMessage extends Message {

	public UnSubscribeMessage(FixedHeader fixedHeader, PackageIdVariableHeader variableHeader,
			UnSubscribePayload payload) {
		super(fixedHeader, variableHeader, payload);
	}
	
	@Override
	public PackageIdVariableHeader getVariableHeader() {
		return (PackageIdVariableHeader)super.getVariableHeader();
	}
	
	@Override
	public UnSubscribePayload getPayload() {
		return (UnSubscribePayload)super.getPayload();
	}
	
}
