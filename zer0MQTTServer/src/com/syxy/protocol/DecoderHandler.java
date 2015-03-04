package com.syxy.protocol;

import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.server.ClientSession;

/**
 * <li>说明 协议解码接口
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public interface DecoderHandler {

	/**
	 * <li>方法名 process
	 * <li>@param client
	 * <li>@param byteBuffer
	 * <li>返回类型 Message
	 * <li>说明 对处理后的结果数据进行解码，并将解码后的请求响应设置给client
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-16
	 */
	public Message process(ByteBuffer byteBuffer);
	
}
