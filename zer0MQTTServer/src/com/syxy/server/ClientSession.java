package com.syxy.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import com.syxy.Aiohandler.AioReadHandler;
import com.syxy.Aiohandler.AioWriteHandler;
import com.syxy.protocol.CoderHandler;
import com.syxy.protocol.DecoderHandler;
import com.syxy.protocol.ProcessHandler;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.util.BufferPool;

/**
 * <li>说明 客户session，每个连接一个ClientSession对象，用于处理客户请求和响应
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */

public class ClientSession {

	private final static Logger Log = Logger.getLogger(ClientSession.class);
	
	// 定义编码处理器，业务处理器，解码处理器
	private CoderHandler coderHandler;// 编码处理器
	private DecoderHandler decoderHandler;// 解码处理器
	private ProcessHandler processHandler;// 业务处理器
	
	private TcpServer socketServer;
	private AsynchronousSocketChannel socketChannel;// 异步socket连结端
	private AioReadHandler readHandler;// 读取处理器
	private AioWriteHandler writeHandler;// 回写处理器	
	
//	private String requestMsg;//请求的信息
	private ByteBuffer byteBuffer;// 缓冲区
	
	private Object index;// 客户端在索引
	
	private Message msg;//协议对象
	
	public ClientSession(AsynchronousSocketChannel socketChannel, 
			AioReadHandler readHandler, 
			AioWriteHandler writeHandler,
			TcpServer socketServer) {
		this.socketChannel = socketChannel;
		this.readHandler = readHandler;
		this.writeHandler = writeHandler;
		this.socketServer = socketServer;
	}
	
	/**
	 * <li>方法名 registeHandler
	 * <li>@param coderHandler
	 * <li>@param decoderHandler
	 * <li>@param processHandler
	 * <li>返回类型 void
	 * <li>说明 把编码，解码和协议处理器注册给client，方便client读数据，写数据
	 *         的时候编码，解码
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-20
	 */
	public void registeHandler(CoderHandler coderHandler, DecoderHandler decoderHandler, ProcessHandler processHandler){
		this.coderHandler = coderHandler;
		this.decoderHandler = decoderHandler;
		this.processHandler = processHandler;
	}
	
	/**
	 * <li>方法名 readEvent
	 * <li>返回类型 void
	 * <li>说明 读请求事件，调用AioReadHandler处理读事件
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-21
	 */
	public void readEvent(){
		try {
			//如果socket通道未关闭，就读数据
			if (this.socketChannel.isOpen()){
//				this.byteBuffer = ByteBuffer.allocate(1024 * 64);   
				this.byteBuffer = BufferPool.getInstance().getBuffer();
	            this.socketChannel.read(this.byteBuffer, this, this.readHandler); 
	        } else {  
	            Log.info("会话被取消或者关闭");  
	        }
		} catch (Exception e) {
			Log.warn(e.getMessage());
			this.close();
		}
	}
	
	/**
	 * <li>方法名 readRequest
	 * <li>返回类型 boolean
	 * <li>说明 读请求完成后，将得到的缓冲区写入程序的另一个缓冲区，如果此缓冲区进行解码处理，以便能识别
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-21
	 */
	public boolean readRequest(){
		Log.info("进行解码处理");	
		boolean returnValue = false;// 返回值	,若数据处理完毕无问题，改为true
		
		this.byteBuffer.flip();
		this.msg = this.decoderHandler.process(this.byteBuffer);
		this.byteBuffer.clear();
		//使用完缓冲区以后释放
		BufferPool.getInstance().releaseBuffer(this.byteBuffer);
		
		returnValue = true;
		this.readEvent();
		
		return returnValue;
	}
	
	/**
	 * <li>方法名 close
	 * <li>返回类型 void
	 * <li>说明 关闭socket连接
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-21
	 */
	public void close(){
		try{
			this.socketServer.closeServer(index);// 清除
			this.socketChannel.close();		
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{			
			try{
				socketChannel.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <li>方法名 getIp
	 * <li>返回类型 String
	 * <li>说明 获取客户端IP地址
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-22
	 */
	public String getIp(){		
		try {
			return this.socketChannel.getRemoteAddress().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * <li>方法名 process
	 * <li>返回类型 void
	 * <li>说明 处理读取的数据，在处理的函数中来调用回写函数，并告诉回写函数该写哪些内容
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-22
	 */
	public void process(){
		try{
			this.processHandler.process(msg, this);// 业务处理
		}catch(Exception e){
			e.printStackTrace();
			this.close();
		}
	}
	
	/**
	 * <li>方法名 processBlankRead
	 * <li>返回类型 void
	 * <li>说明 处理读取到的数据为空的情况
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-22
	 */
	public void processBlankRead(){
		//暂不处理
//		this.close();
	}
	
	/**
	 * <li>方法名 writeMsgToEveryOne
	 * <li>@param msg
	 * <li>@param byteBuffer
	 * <li>返回类型 void
	 * <li>说明 对消息类型编码并回写给所有客户端，由协议业务处理类调用
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-4
	 */
	public void writeMsgToEveryOne(Message msg){
		try {
			ByteBuffer byteBuffer = this.encodeProtocol(msg);
			byteBuffer.flip();
			this.sendMsgToEveryOne(byteBuffer);
		} catch (Exception e) {
			Log.error("回写数据给所有人出错，请检查");
			e.printStackTrace();
		}
	}
	
	/**
	 * <li>方法名 writeMsgToEveryOne
	 * <li>@param msg
	 * <li>@param byteBuffer
	 * <li>返回类型 void
	 * <li>说明 对消息类型编码并回写给所有客户端，由协议业务处理类调用
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-4
	 */
	public void writeMsgToReqClient(Message msg){
		try {
			ByteBuffer byteBuffer = this.encodeProtocol(msg);
			Log.info("byteBuffer的position="+byteBuffer.position());
			Log.info("byteBuffer的limit="+byteBuffer.limit());
			byteBuffer.flip();
			Log.info("byteBuffer flip后的position="+byteBuffer.position());
			Log.info("byteBuffer flip后的limit="+byteBuffer.limit());
			this.sendMsgToReqClient(byteBuffer);
		} catch (Exception e) {
			Log.error("回写数据给请求者出错，请检查");
			e.printStackTrace();
		}
	}
	
	/**
	 * <li>方法名 encodeProtocol
	 * <li>@param msg
	 * <li>返回类型 void
	 * <li>说明 对协议进行编码，针对相应消息类型编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-4
	 * @throws IOException 
	 */
	private ByteBuffer encodeProtocol(Message msg) throws IOException{
		if (msg != null) {
			ByteBuffer byteBuffer = this.coderHandler.process(msg);// 协议编码处理
			return byteBuffer;
		}	
		return null;
	}
	
	/**
	 * <li>方法名 sendMsgToEveryOne
	 * <li>@param byteBuffer
	 * <li>返回类型 void
	 * <li>说明 回写数据
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-22
	 */
	private void sendMsgToEveryOne(ByteBuffer byteBuffer) throws Exception{	
		Log.info("回写数据给所有客户端");
		Iterator<ClientSession> it = socketServer.getClients().values().iterator();
		while(it.hasNext()){
			ClientSession client = it.next();
			client.socketChannel.write(byteBuffer, this, this.writeHandler);
		}
	}
	
	/**
	 * <li>方法名 sendMsgToReqClient
	 * <li>@param byteBuffer
	 * <li>返回类型 void
	 * <li>说明 回写数据给请求的客户端
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-4
	 */
	private void sendMsgToReqClient(ByteBuffer byteBuffer) throws Exception{
		this.socketChannel.write(byteBuffer, this, this.writeHandler);
	}
	
	public Object getIndex() {
		return index;
	}

	public void setIndex(Object index) {
		this.index = index;
	}

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	public void setByteBuffer(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}

	public AsynchronousSocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(AsynchronousSocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
	
	
}
