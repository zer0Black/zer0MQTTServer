package com.syxy.Aiohandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * <li>说明 异步读取数据
 * <li>作者 zer0
 * <li>创建日期：2015-2-14
 */

public class AioReadHandler implements CompletionHandler<AsynchronousSocketChannel, Object>{

	@Override
	public void completed(AsynchronousSocketChannel result, Object attachment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		// TODO Auto-generated method stub
		
	}

}
