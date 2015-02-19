package com.syxy.server;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.syxy.Aiohandler.AioAcceptHandler;
import com.syxy.Aiohandler.AioReadHandler;
import com.syxy.Aiohandler.AioWriteHandler;
import com.syxy.protocol.CoderHandler;
import com.syxy.protocol.DecoderHandler;
import com.syxy.protocol.ProcessHandler;
import com.syxy.util.Log;
import com.syxy.util.PropertiesTool;

/**
 * <li>说明 基于JAVA AIO的,面向TCP/IP的,非阻塞式Sockect服务器框架类
 * <li>作者 zer0
 * <li>创建日期 2015-2-15
 */

public class TcpServer {
	
	//系统常量配置
	private static final String PORT = "port";//端口号
	private static final String SOCKECT_SEND_BUFFER_SIZE = "sockectSendBufferSize";// sockect读取缓冲区大小
	private static final String SOCKECT_RECVEID_BUFFER_SIZE = "sockectReceiveBufferSize";// sockect发送缓冲区大小
	
	//服务器配置、参数、对象
	private AsynchronousServerSocketChannel server;// 异步服务端
	private AioAcceptHandler acceptHandler;// 连接处理器
	private AioReadHandler readHandler;// 读取处理器
	private AioWriteHandler writeHandler;// 回写处理器
	
	private int sockectSendBufferSize = 64;// 默认为64k
	private int sockectReceiveBufferSize = 5;// 默认为5k
	
	// 定义编码处理器，业务处理器，解码处理器
	private CoderHandler coderHandler;// 编码处理器
	private DecoderHandler decoderHandler;// 解码处理器
	private ProcessHandler processHandler;// 业务处理器
	
	private volatile Integer port;//服务器端口
	private volatile boolean noStopRequested = true;// 循环控制变量
	
	public TcpServer(CoderHandler coderHandler, DecoderHandler decoderHandler, ProcessHandler processHandler){
		// 设置端口
		this.port = PropertiesTool.getPropertyToInt(PORT);// 从配置中获取端口号
		if(this.port == null){
			this.port = 8088;// 设置默认端口为8088
		}
		
		// 设置sockect数据接收缓冲区大小
		Integer receiveBuffer = PropertiesTool.getPropertyToInt(SOCKECT_RECVEID_BUFFER_SIZE);
		if(receiveBuffer != null){
			this.sockectReceiveBufferSize = receiveBuffer;
		}
		
		// 设置sockect数据读取缓冲区大小
		Integer sendBuffer = PropertiesTool.getPropertyToInt(SOCKECT_SEND_BUFFER_SIZE);
		if(sendBuffer != null){
			this.sockectSendBufferSize = sendBuffer;
		}
		
		// 设置解码器，编码器和业务处理器
		this.coderHandler = coderHandler;
		this.decoderHandler = decoderHandler;
		this.processHandler = processHandler;
		
		this.startMonitor();
	}
	
	/**
	 * <li>方法名 startServer
	 * <li>@param str
	 * <li>返回类型 void
	 * <li>说明 启动服务器
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-19
	 */
	public void startServer(){
		try{
			//把读处理器，写处理器传递给接收处理器，以方便建立连接后，处理读写数据
			Map<String, Object> serverMap = new HashMap<String, Object>();
			serverMap.put("readHandler", this.readHandler);
			serverMap.put("writeHandler", this.writeHandler);
			this.server.accept(serverMap, this.acceptHandler);// 创建监听处理器			
			Log.info("服务器准备就绪，等待请求到来");
		}catch(Exception e){
			Log.info(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * <li>方法名 startMonitor
	 * <li>返回类型 void
	 * <li>说明 初始化资源
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-19
	 */
	private void startMonitor(){		
		try{
			// 创建共享资源池
			AsynchronousChannelGroup resourceGroup = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());  
			
			// 创建服务端异步socket
			this.server = AsynchronousServerSocketChannel.open(resourceGroup); 
			this.server.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			this.server.setOption(StandardSocketOptions.SO_RCVBUF, sockectReceiveBufferSize * 1024);
			this.server.setOption(StandardSocketOptions.SO_SNDBUF, sockectSendBufferSize * 1024);
			this.server.bind(new InetSocketAddress(this.port), 200);  
			this.acceptHandler = new AioAcceptHandler();
			this.readHandler = new AioReadHandler();
			this.writeHandler = new AioWriteHandler();		
		}catch(Exception e){
			Log.error("服务器启动失败");
			e.printStackTrace();
		}
	}
	
}
