package com.syxy.Aiohandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Map;

/**
 * <li>说明 异步接收链接
 * <li>作者 zer0
 * <li>创建日期 2015-2-14
 */

public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Map<String, Object>>{

	@Override
	public void completed(AsynchronousSocketChannel result,
			Map<String, Object> attachment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void failed(Throwable exc, Map<String, Object> attachment) {
		// TODO Auto-generated method stub
		
	}

}
