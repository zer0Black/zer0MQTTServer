package com.syxy.protocol.mqttImp;

import java.nio.ByteBuffer;

import com.syxy.protocol.DecoderHandler;
import com.syxy.server.ClientSession;
import com.syxy.util.coderTool;

/**
 * <li>说明 MQTT协议解码
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class MQTTDecoder implements DecoderHandler {

	@Override
	public String process(ByteBuffer byteBuffer, ClientSession client) {
		// TODO Auto-generated method stub
		String str = coderTool.decode(byteBuffer);
		return str;
	}

}
