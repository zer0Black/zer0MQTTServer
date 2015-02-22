package com.syxy.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;




public class readHandlerThread {
	
	private Thread internalThread;//内部工作线程
	private BlockingQueue<ClientSession> handoffBox;
	private volatile boolean noStopRequested;
	
	public readHandlerThread(){
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
	
	private void runWork(){
		while(this.noStopRequested){
			try{	
				process(handoffBox.take());//接收数据进行处理				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void process(ClientSession client){
		if(client.readRequest()){ // 已完成握手，从客户端读取报刊
			client.process();// 进行业务处理
		}
	}
}
