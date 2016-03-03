package com.syxy.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.syxy.protocol.mqttImp.message.Message;

/**
 *  协议编码接口
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-16
 */
public interface ICoderHandler {

	/**
	 *  对处理后的结果数据进行编码
	 * @param str
	 * @return ByteBuffer
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-16
	 */
	public ByteBuffer process(Message msg) throws IOException;
	
}
