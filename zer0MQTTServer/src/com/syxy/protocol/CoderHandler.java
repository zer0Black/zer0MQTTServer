package com.syxy.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.server.ClientSession;

/**
 * <li>说明 协议编码接口
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public interface CoderHandler {

	/**
	 * <li>方法名 process
	 * <li>@param str
	 * <li>返回类型 ByteBuffer
	 * <li>说明 对处理后的结果数据进行编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-16
	 * @throws IOException 
	 */
	public ByteBuffer process(Message msg) throws IOException;
	
}
