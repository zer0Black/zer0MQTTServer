package com.syxy.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * <li>说明 异步读取数据
 * <li>作者 zer0
 *
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
