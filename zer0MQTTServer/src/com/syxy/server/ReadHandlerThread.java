package com.syxy.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <li>说明 读取数据后的信息处理类，开启单独的线程来处理取到的数据，该类
 *         用于开启线程，处理部分调用clientSession类来完成
 * <li>作者 zer0
 * <li>创建日期 2015-2-22
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
	 * <li>方法名 runWork
	 * <li>返回类型 void
	 * <li>说明 不断循环来处理接收到数据的clientSession
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-22
	 */
	private void runWork(){
		while(this.noStopRequested){
			try{	
				process(handoffBox.take());//接收数据进行处理				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <li>方法名 processResponse
	 * <li>@param key
	 * <li>返回类型 void
	 * <li>说明 由AioReadHandler调用，将读取到数据的client添加到该类的队列中，等待处理
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-22
	 */
	public void processResponse(ClientSession key){
		this.handoffBox.add(key);
	}
	
	/**
	 * <li>方法名 process
	 * <li>@param client
	 * <li>返回类型 void
	 * <li>说明 判断数据是否读取，若读取，则进行对应的业务处理
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-22
	 */
	private void process(ClientSession client){
		if(client.readRequest()){ // 已完成握手，从客户端读取数据
			client.process();// 进行业务处理
		}
	}
}
