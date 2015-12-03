package com.syxy.Aiohandler;

import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

import com.syxy.server.ClientSession;

/**
 * 异步回写数据
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-14
 */
public class AioWriteHandler implements CompletionHandler<Integer, ClientSession>{

	private final static Logger Log = Logger.getLogger(AioWriteHandler.class);
	
	@Override
	public void completed(Integer result, ClientSession client) {
		if(result > 0){
			Log.info("回写成功");
		}else if(result == 0){
			Log.info("回写数据为0，请检查");
		}else if(result < 0){
			Log.info("回写数据小于0，请检查");
		}
	}

	@Override
	public void failed(Throwable exc, ClientSession client) {
		Log.info("回写失败，失败原因："+exc);
	}

}
