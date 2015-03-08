//package com.syxy.server;
//
//import java.nio.channels.AsynchronousSocketChannel;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.syxy.Aiohandler.AioReadHandler;
//import com.syxy.Aiohandler.AioWriteHandler;
//
///**
// * <li>MQTT协议客户端会话，继承自clientSession，用于处理仅有MQTT协议才需要的一些会话属性
// * <li>作者 zer0
// * <li>创建日期 2015-3-8
// */
//public class MQTTClient extends ClientSession{
//
//	private Map<Object, Object> attributesKeys = new HashMap<Object, Object>();
//	
//	public MQTTClient(AsynchronousSocketChannel socketChannel,
//			AioReadHandler readHandler, AioWriteHandler writeHandler,
//			TcpServer socketServer) {
//		super(socketChannel, readHandler, writeHandler, socketServer);
//	}
//
//	public Object getAttributesKeys(Object key) {
//		return attributesKeys.get(key);
//	}
//
//	public void setAttributesKeys(Object key, Object value) {
//		attributesKeys.put(key, value);
//	}
//
//	
//	
//}
