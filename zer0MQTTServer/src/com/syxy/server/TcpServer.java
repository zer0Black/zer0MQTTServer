package com.syxy.server;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.syxy.Aiohandler.AioAcceptHandler;
import com.syxy.Aiohandler.AioReadHandler;
import com.syxy.Aiohandler.AioWriteHandler;
import com.syxy.protocol.ICoderHandler;
import com.syxy.protocol.IDecoderHandler;
import com.syxy.protocol.IProcessHandler;
import com.syxy.util.MqttTool;

/**
 * 基于JAVA AIO的,面向TCP/IP的,非阻塞式Sockect服务器框架类
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-15
 */
public class TcpServer {
	
	private final static Logger Log = Logger.getLogger(TcpServer.class);
	
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
	private ICoderHandler coderHandler;// 编码处理器
	private IDecoderHandler decoderHandler;// 解码处理器
	private IProcessHandler processHandler;// 业务处理器
	
	ReadHandlerThread readHandlerThread;
	private static TcpServer INSTANCE;
//	private ConcurrentHashMap<Object, ClientSession> clients = new ConcurrentHashMap<Object, ClientSession>();// 客户端链接映射表
	
	private volatile Integer port;//服务器端口
	private volatile boolean noStopRequested = true;// 循环控制变量
	
	private final AtomicInteger keyIndex = new AtomicInteger();// 连接序号,用于标示客户端的编号
	
	public TcpServer(ICoderHandler coderHandler, IDecoderHandler decoderHandler, IProcessHandler processHandler){
		// 设置端口
		this.port = MqttTool.getPropertyToInt(PORT);// 从配置中获取端口号
		if(this.port == null){
			this.port = 8088;// 设置默认端口为8088
		}
		
		// 设置sockect数据接收缓冲区大小
		Integer receiveBuffer = MqttTool.getPropertyToInt(SOCKECT_RECVEID_BUFFER_SIZE);
		if(receiveBuffer != null){
			this.sockectReceiveBufferSize = receiveBuffer;
		}
		
		// 设置sockect数据读取缓冲区大小
		Integer sendBuffer = MqttTool.getPropertyToInt(SOCKECT_SEND_BUFFER_SIZE);
		if(sendBuffer != null){
			this.sockectSendBufferSize = sendBuffer;
		}
		
		// 设置解码器，编码器和业务处理器
		this.coderHandler = coderHandler;
		this.decoderHandler = decoderHandler;
		this.processHandler = processHandler;
		
		readHandlerThread = new ReadHandlerThread();
		this.startMonitor();
	}
	
	public static TcpServer getInstance(ICoderHandler coderHandler, IDecoderHandler decoderHandler, IProcessHandler processHandler) {
        if (INSTANCE == null) {
            INSTANCE = new TcpServer(coderHandler, decoderHandler, processHandler);
        }
        return INSTANCE;
    }

	/**
	 * 启动服务器
	 * 
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-19
	 */
	public void startServer(){
		try{
			this.server.accept(this, this.acceptHandler);
			Log.info("服务器准备就绪，等待请求到来");
		}catch(Exception e){
			Log.info(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化资源
	 * 
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-19
	 */
	private void startMonitor(){		
		try{
			// 创建共享资源池
			AsynchronousChannelGroup resourceGroup = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());  
			
			// 创建服务端异步socket
			this.server = AsynchronousServerSocketChannel.open(resourceGroup); 
			this.server.bind(new InetSocketAddress(this.port), 200);  
			this.acceptHandler = new AioAcceptHandler();
			this.readHandler = new AioReadHandler(this);
			this.writeHandler = new AioWriteHandler();		
		}catch(Exception e){
			Log.error("服务器启动失败");
			e.printStackTrace();
		}
	}

	//get/set变量封装
	public AsynchronousServerSocketChannel getServer() {
		return server;
	}

	public void setServer(AsynchronousServerSocketChannel server) {
		this.server = server;
	}

	public AioReadHandler getReadHandler() {
		return readHandler;
	}

	public void setReadHandler(AioReadHandler readHandler) {
		this.readHandler = readHandler;
	}

	public AioWriteHandler getWriteHandler() {
		return writeHandler;
	}

	public void setWriteHandler(AioWriteHandler writeHandler) {
		this.writeHandler = writeHandler;
	}

	public AtomicInteger getKeyIndex() {
		return keyIndex;
	}

	public ReadHandlerThread getReadHandlerThread() {
		return readHandlerThread;
	}

	public void setReadHandlerThread(ReadHandlerThread readHandlerThread) {
		this.readHandlerThread = readHandlerThread;
	}

	public ICoderHandler getCoderHandler() {
		return coderHandler;
	}

	public void setCoderHandler(ICoderHandler coderHandler) {
		this.coderHandler = coderHandler;
	}

	public IDecoderHandler getDecoderHandler() {
		return decoderHandler;
	}

	public void setDecoderHandler(IDecoderHandler decoderHandler) {
		this.decoderHandler = decoderHandler;
	}

	public IProcessHandler getProcessHandler() {
		return processHandler;
	}

	public void setProcessHandler(IProcessHandler processHandler) {
		this.processHandler = processHandler;
	}

}
