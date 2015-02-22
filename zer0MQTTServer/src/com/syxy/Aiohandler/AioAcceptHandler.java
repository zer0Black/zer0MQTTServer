package com.syxy.Aiohandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.syxy.server.ClientSession;
import com.syxy.server.TcpServer;
import com.syxy.util.Log;

/**
 * <li>说明 异步接收链接
 * <li>作者 zer0
 * <li>创建日期 2015-2-14
 */

public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, TcpServer>{

	@Override
	public void completed(AsynchronousSocketChannel socketChannel,
			TcpServer attachment) {
		// TODO Auto-generated method stub
		try {
			/**
			 * 接收到新连接后，实例化一个ClientSession对象，并给该对象一个唯一
			 * 标示keyIndex，放入程序管理的client队列中，此后操作client时，
			 * 只需要从client队列取出对应的client来操作即可。
			 * ConcurrentHashMap是线程安全的
			 */
			Log.info("来自" + socketChannel.getRemoteAddress() + "的连接已建立");
			Integer index = 0;
			
			ClientSession client = new ClientSession(socketChannel, 
					attachment.getReadHandler(), 
					attachment.getWriteHandler(),
					attachment);
			index = attachment.getKeyIndex().incrementAndGet();
			client.setIndex(index);// 设置索引
			attachment.getClients().put(index, client);// 放入到连接中
			
			//建立连接以后开启读事件
			client.readEvent();
		} catch (Exception e) {
			Log.warn(e.getMessage());
		} finally{
			attachment.getServer().accept(attachment, this);// 监听新的请求
		}
	}

	@Override
	public void failed(Throwable exc, TcpServer attachment) {
		// TODO Auto-generated method stub
        try{  
        	Log.error(exc.getMessage());
        }finally{  
        	attachment.getServer().accept(attachment,this);// 监听新的请求
        }  
	}

}
