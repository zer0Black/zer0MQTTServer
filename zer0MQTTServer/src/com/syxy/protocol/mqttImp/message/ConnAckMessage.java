package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * <li>MQTT协议ConnAck消息类型实现类，连接确认消息类型
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public class ConnAckMessage extends Message {

	@Override
	public ByteBuffer encode() throws IOException {
		return null;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
