package com.syxy.protocol;

import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.server.ClientSession;

/**
 * <li>说明 协议业务处理接口
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public interface ProcessHandler {

	/**
	 * <li>方法名 process
	 * <li>@param msg
	 * <li>@param client
	 * <li>返回类型 void
	 * <li>说明 对用户的请求进行业务上的处理并将处理结果放回到client中，等待编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-16
	 */
	public void process(Message msg, ClientSession client);
	
}
