package com.syxy.server;

import com.syxy.protocol.mqttImp.MQTTCoder;
import com.syxy.protocol.mqttImp.MQTTDecoder;
import com.syxy.protocol.mqttImp.MQTTProcess;

/**
 * <li>说明 启动服务器，主线程所在
 * <li>作者 zer0
 * <li>创建日期 2015-2-14
 */

public class StartServer {
	
	public static void main(String[] args){
		new TcpServer(new MQTTCoder(), new MQTTDecoder(), new MQTTProcess()).startServer();
		while(true){
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
