package com.syxy.Aiohandler;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

import com.syxy.server.ClientSession;
import com.syxy.server.TcpServer;

/**
 * <li>说明 异步读取数据
 * <li>作者 zer0
 * <li>创建日期 2015-2-14
 */

public class AioReadHandler implements CompletionHandler<Integer, ClientSession>{

	private final static Logger Log = Logger.getLogger(AioReadHandler.class);
	private TcpServer tcpServer;
	
	public AioReadHandler(TcpServer tcpServer){
		this.tcpServer = tcpServer;
	}
	
	@Override
	public void completed(Integer result, ClientSession client) {
		// TODO Auto-generated method stub
		if (result < 0) {// 客户端关闭了连接  
			client.close();  
		    return;  
		 }
		 
		 if(result == 0){
			 client.processBlankRead();// 处理空读的情况
		 }
		 
		 if (result > 0){// 读取到客户端的数据  
		     try {
		    	 Log.info("读取到客户端的数据");
		    	 this.tcpServer.getReadHandlerThread().processResponse(client);
			} catch (Exception e) {
				Log.info(e.getMessage());
			}     
		 }
	}

	@Override
	public void failed(Throwable exc, ClientSession client) {
		// TODO Auto-generated method stub
		
	}
	
	public void cancelled(ClientSession client) {  
		Log.warn("客户端(" + client.getIp() + ") 读操作取消");  
	}  

}
