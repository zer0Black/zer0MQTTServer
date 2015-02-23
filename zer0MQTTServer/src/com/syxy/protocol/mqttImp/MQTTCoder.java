package com.syxy.protocol.mqttImp;

import java.nio.ByteBuffer;

import com.syxy.protocol.CoderHandler;
import com.syxy.server.ClientSession;
import com.syxy.util.coderTool;

/**
 * <li>说明 MQTT协议编码
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTCoder implements CoderHandler {

	@Override
	public ByteBuffer process(String str, ClientSession client) {
		// TODO Auto-generated method stub
		ByteBuffer byteBuffer = coderTool.encode(str);
		return byteBuffer;
	}


}
