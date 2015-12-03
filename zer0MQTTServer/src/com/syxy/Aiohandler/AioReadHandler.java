package com.syxy.Aiohandler;

import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

import com.syxy.server.ClientSession;
import com.syxy.server.TcpServer;

/**
 * 异步读取数据
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-14
 */
public class AioReadHandler implements CompletionHandler<Integer, ClientSession>{

	private final static Logger Log = Logger.getLogger(AioReadHandler.class);
	private TcpServer tcpServer;
	
	public AioReadHandler(TcpServer tcpServer){
		this.tcpServer = tcpServer;
	}
	
	@Override
	public void completed(Integer result, ClientSession client) {
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
		Log.warn("客户端(" + client.getIp() + ") 读操作失败");  
		Log.debug("错误代码"+exc);
	}
	
	public void cancelled(ClientSession client) {  
		Log.warn("客户端(" + client.getIp() + ") 读操作取消");  
	}  

}
