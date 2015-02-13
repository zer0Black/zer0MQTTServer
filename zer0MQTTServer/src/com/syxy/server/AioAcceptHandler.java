package com.syxy.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * <li>说明 异步接收链接
 * <li>作者 zer0
 *
 */

public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object>{

	@Override
	public void completed(AsynchronousSocketChannel result,
			Object attachment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		// TODO Auto-generated method stub
		
	}

}
