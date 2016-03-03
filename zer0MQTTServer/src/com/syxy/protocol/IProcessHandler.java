package com.syxy.protocol;

import io.netty.channel.Channel;

import com.syxy.protocol.mqttImp.message.Message;

/**
 * 协议业务处理接口
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-16
 */
public interface IProcessHandler {

	/**
	 * 对用户的请求进行业务上的处理并将处理结果放回到client中，等待编码
	 * @param msg
	 * @param client
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-16
	 */
	public void process(Message msg, Channel client);
	
}
