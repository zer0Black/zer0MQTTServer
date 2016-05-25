package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.syxy.util.BufferPool;
import com.syxy.util.StringTool;

/**
 * MQTT协议Subscribe消息类型实现类，用于订阅topic，订阅了消息的客户端，可以接受对应topic的信息
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class SubscribeMessage extends Message {

	public SubscribeMessage(FixedHeader fixedHeader, PackageIdVariableHeader variableHeader,
			SubscribePayload payload) {
		super(fixedHeader, variableHeader, payload);
	}
	
	@Override
	public PackageIdVariableHeader getVariableHeader() {
		return (PackageIdVariableHeader)super.getVariableHeader();
	}
	
	@Override
	public SubscribePayload getPayload() {
		return (SubscribePayload)super.getPayload();
	}
	
}
