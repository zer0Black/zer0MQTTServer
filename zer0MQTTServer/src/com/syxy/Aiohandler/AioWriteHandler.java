package com.syxy.Aiohandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.syxy.server.ClientSession;

/**
 * <li>说明 异步回写数据
 * <li>作者 zer0
 * <li>创建日期 2015-2-14
 */

public class AioWriteHandler implements CompletionHandler<Integer, ClientSession>{

	@Override
	public void completed(Integer result, ClientSession client) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void failed(Throwable exc, ClientSession client) {
		// TODO Auto-generated method stub
		
	}

}
