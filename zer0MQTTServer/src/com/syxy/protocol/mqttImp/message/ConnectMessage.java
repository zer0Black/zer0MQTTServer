package com.syxy.protocol.mqttImp.message;

/**
 * MQTT协议Connect消息类型实现类，客户端请求服务器连接的消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-2
 */
public class ConnectMessage extends Message {
	
	public ConnectMessage(FixedHeader fixedHeader, ConnectVariableHeader variableHeader,
			ConnectPayload payload) {
		super(fixedHeader, variableHeader, payload);
	}
	
	@Override
	public ConnectVariableHeader getVariableHeader() {
		return (ConnectVariableHeader)super.getVariableHeader();
	}
	
	@Override
	public ConnectPayload getPayload() {
		return (ConnectPayload)super.getPayload();
	}

}
