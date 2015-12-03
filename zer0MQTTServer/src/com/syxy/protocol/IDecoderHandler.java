package com.syxy.protocol;

import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.server.ClientSession;

/**
 *  协议解码接口
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-16
 */
public interface IDecoderHandler {

	/**
	 *  对处理后的结果数据进行解码，并将解码后的请求响应设置给client
	 * @param byteBuffer
	 * @return Message
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-16
	 */
	public Message process(ByteBuffer byteBuffer);
	
}
