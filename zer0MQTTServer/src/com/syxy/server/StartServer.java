package com.syxy.server;

import com.syxy.protocol.mqttImp.MQTTCoder;
import com.syxy.protocol.mqttImp.MQTTDecoder;
import com.syxy.protocol.mqttImp.MQTTProcess;

/**
 *  启动服务器，主线程所在
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-14
 */
public class StartServer {
	
	public static void main(String[] args){
		TcpServer.getInstance(new MQTTCoder(), new MQTTDecoder(), new MQTTProcess()).startServer();
		while(true){
			try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
