package com.syxy.protocol.mqttImp.message;

import io.netty.buffer.ByteBuf;

/**
 * MQTT协议Publish消息类型实现类，发布消息的消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class PublishMessage extends Message {

	public PublishMessage(FixedHeader fixedHeader, PublishVariableHeader variableHeader,
			ByteBuf payload) {
		super(fixedHeader, variableHeader, payload);
	}
	
	@Override
	public PublishVariableHeader getVariableHeader() {
		return (PublishVariableHeader)super.getVariableHeader();
	}
	
	@Override
	public ByteBuf getPayload() {
		return (ByteBuf)super.getPayload();
	}

}
