package com.syxy.protocol.mqttImp.message;

import java.util.List;


/**
 * MQTT协议SubAck消息类型实现类，对Subscribe包的确认
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class SubAckMessage extends Message {

	public SubAckMessage(FixedHeader fixedHeader, PackageIdVariableHeader variableHeader,
			SubAckPayload payload) {
		super(fixedHeader, variableHeader, payload);
	}
	
	@Override
	public PackageIdVariableHeader getVariableHeader() {
		return (PackageIdVariableHeader)super.getVariableHeader();
	}
	
	@Override
	public SubAckPayload getPayload() {
		return (SubAckPayload)super.getPayload();
	}
}
