package com.syxy.server;

import io.netty.channel.ChannelFuture;

/**
 * 启动服务器，主线程所在
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-14
 */
public class StartServer {
	
	public static void main(String[] args){
		final TcpServer tcpServer = new TcpServer();
		ChannelFuture future = tcpServer.startServer();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				tcpServer.destory();
			}
		});
		future.channel().closeFuture().syncUninterruptibly();
	}
}
