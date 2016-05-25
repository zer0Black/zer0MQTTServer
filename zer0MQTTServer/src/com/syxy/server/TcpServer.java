package com.syxy.server;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.MQTTDecoder;
import com.syxy.protocol.mqttImp.MQTTEncoder;
import com.syxy.protocol.mqttImp.MQTTProcess;
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
	
	private volatile Integer port;//服务器端口
	private Channel channel;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public TcpServer(){
		this.port = MqttTool.getPropertyToInt(PORT);// 从配置中获取端口号
		if(this.port == null){
			this.port = 8088;// 设置默认端口为8088
		}
	}

	/**
	 * 启动服务器
	 * 
	 * @author zer0
	 * @version 1.0
	 * @date  2015-2-19
	 */
	public ChannelFuture startServer(){
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				 .channel(NioServerSocketChannel.class)
				 .childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch)
							throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("MQTTDecoder", new MQTTDecoder());
						pipeline.addLast("MQTTEncoder", new MQTTEncoder());
						pipeline.addLast("MQTTProcess", new MQTTProcess());
						//心跳处理在收到CONNECT消息协议的时候，根据协议内容动态添加
					}
				  })
				  .option(ChannelOption.SO_BACKLOG, 1024)
				  .childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
			channel = future.channel();
			Log.info("服务器已启动，端口:" + port);
			return future;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void destory(){
		if (channel != null) {
			channel.close();
		}
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

}
