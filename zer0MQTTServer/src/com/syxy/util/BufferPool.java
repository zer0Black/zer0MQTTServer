package com.syxy.util;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * 缓冲池
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class BufferPool{
	private static Logger log = Logger.getLogger(BufferPool.class);// 日志记录器
	
	private static final String MAX_BUFFER_POOL_SIZE = "maxBufferPoolSize";// 直接缓冲区池上限大小
	private static final String MIN_BUFFER_POOL_SIZE = "minBufferPoolSize";// 直接缓冲区池下限大小
	private static final String WRITE_BUFFER = "writeBuffer";
	
	private static int maxBufferPoolSize = 1000;// 默认的直接缓冲区池上限大小1000
	private static int minBufferPoolSize = 1000;// 默认的直接缓冲区池下限大小1000
	private static int writeBufferSize = 64;// 响应缓冲区大小默认为64k
	
	private static BufferPool bufferPool = new BufferPool();// BufferPool的单实例
	
	private AtomicInteger usableCount = new AtomicInteger();// 可用缓冲区的数量
	private AtomicInteger createCount = new AtomicInteger();// 已创建了缓冲区的数量
	private ConcurrentLinkedQueue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();// 保存直接缓存的队列
	
	static{
		// 设置缓冲区池上限大小
		Integer maxSize = MqttTool.getPropertyToInt(MAX_BUFFER_POOL_SIZE);// 获取配置中的线程优先级
		if(maxSize != null){
			maxBufferPoolSize = maxSize;
		}
		
		// 设置缓冲区池下限大小
		Integer minSize = MqttTool.getPropertyToInt(MIN_BUFFER_POOL_SIZE);// 获取配置中的线程优先级
		if(minSize != null){
			minBufferPoolSize = minSize;
		}
		
		// 设置响应缓冲区大小
		Integer bufferSize = MqttTool.getPropertyToInt(WRITE_BUFFER);
		if(bufferSize != null){
			writeBufferSize = bufferSize;
		}
	}
	
	private BufferPool(){
		// 预先创建直接缓冲区
		for(int i = 0; i < minBufferPoolSize; ++i){
			ByteBuffer bb = ByteBuffer.allocate(writeBufferSize * 1024);
			this.queue.add(bb);
		}
		
		// 设置可用的缓冲区和已创建的缓冲区数量
		this.usableCount.set(minBufferPoolSize);
		this.createCount.set(minBufferPoolSize);
	}
	
	/**
	 * 获取缓冲区
	 * 
	 * @return ByteBuffer
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-5
	 */
	public ByteBuffer getBuffer(){
		ByteBuffer bytebuffer = this.queue.poll();
		
		if(bytebuffer == null){// 如果缓冲区不够则创建新的缓冲区
			bytebuffer = ByteBuffer.allocate(writeBufferSize * 1024);
			this.createCount.incrementAndGet();
		}else{
			this.usableCount.decrementAndGet();
		}
		
		return bytebuffer;
	}
	
	/**
	 * 释放缓冲区
	 * 
	 * @param bytebuffer
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-5
	 */
	public void releaseBuffer(ByteBuffer bytebuffer){
		if(this.createCount.intValue() > maxBufferPoolSize && (this.usableCount.intValue() > (this.createCount.intValue() / 2) ) ){
			bytebuffer = null;
			this.createCount.decrementAndGet();
		}else{
			this.queue.add(bytebuffer);
			this.usableCount.incrementAndGet();
		}
	}
	
	/**
	 * 移除bytebuffer中的已读数据
	 * 
	 * @param byteBuffer
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-5
	 */
	public static void removeReadedData(ByteBuffer byteBuffer){
		byteBuffer.compact();
	    byteBuffer.flip();
	}
	
	public static BufferPool getInstance(){
		return bufferPool;
	}
}
