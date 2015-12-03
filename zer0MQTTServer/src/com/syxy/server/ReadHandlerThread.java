package com.syxy.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *   读取数据后的信息处理类，开启单独的线程来处理取到的数据，该类
 *   用于开启线程，处理部分调用clientSession类来完成
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-22
 */
public class ReadHandlerThread {
	
	private Thread internalThread;//内部工作线程
	private BlockingQueue<ClientSession> handoffBox;
	private volatile boolean noStopRequested;
	
	public ReadHandlerThread(){
		handoffBox = new ArrayBlockingQueue<ClientSession>(100);
		noStopRequested = true;
		
		Runnable r = new Runnable(){
			public void run(){
				try{
					runWork();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		
		this.internalThread = new Thread(r);
		this.internalThread.start();
	}
	
	/**
	 *  不断循环来处理接收到数据的clientSession
	 * 
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-22
	 */
	private void runWork(){
		while(this.noStopRequested){
			try{	
				process(handoffBox.take());//接收数据进行处理		
				Thread.sleep(1);
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * 由AioReadHandler调用，将读取到数据的client添加到该类的队列中，等待处理
	 * 
	 * @param key
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-22
	 */
	public void processResponse(ClientSession key){
		this.handoffBox.add(key);
	}
	
	/**
	 * 判断数据是否读取，若读取，则进行对应的业务处理
	 * 
	 * @param client
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-22
	 */
	private void process(ClientSession client){
		if(client.readRequest()){ // 已完成握手，从客户端读取数据
			client.process();// 进行业务处理
		}
	}
}
