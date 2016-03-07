package com.syxy.server;

/**
 *  启动服务器，主线程所在
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-14
 */
public class StartServer {
	
	public static void main(String[] args){
		new TcpServer().startServer();
		
		while (true) {
			try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
