package com.syxy.protocol.mqttImp.process;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnAckMessage.ConnectionStatus;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.server.ClientSession;
import com.syxy.util.StringTool;

/**
 * <li>说明 协议所有的业务处理都在此类，注释中所指协议为MQTT3.3.1协议
 * <li>作者 zer0
 * <li>创建日期 2015-2-16
 */
public class protocolProcess {

	private final static Logger Log = Logger.getLogger(protocolProcess.class);
	
	private ConcurrentHashMap<Object, ConnectionDescriptor> clients = new ConcurrentHashMap<Object, ConnectionDescriptor>();// 客户端链接映射表
	
	/**
	 * <li>方法名 processConnect
	 * <li>@param client
	 * <li>@param connectMessage
	 * <li>返回类型 void
	 * <li>说明 处理协议的CONNECT消息类型
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-7
	 */
	public void processConnect(ClientSession client, ConnectMessage connectMessage){
		Log.info("处理Connect的数据");
		//首先查看保留位是否为0，不为0则断开连接,协议P24
		if (!connectMessage.isReservedIsZero()) {
			client.close();
		}
		//处理protocol name和protocol version, 如果返回码!=0，sessionPresent必为0，协议P24,P32
		if (!connectMessage.getProtocolName().equals("MQTT") || connectMessage.getProtocolVersionNumber() != 4 ) {
			client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.UNACCEPTABLE_PROTOCOL_VERSION, 0));
			client.close();//版本或协议名不匹配，则断开该客户端连接
		}
		//处理clientID为null或长度为0的情况，协议P29
		if (connectMessage.getClientId() == null || connectMessage.getClientId().length() == 0) {
			//clientID为null的时候，cleanSession只能为1,此时给client设置一个23位的ID，否则，断开连接
			if (connectMessage.isCleanSession()) {
				connectMessage.setClientId(StringTool.getRandomString(23));
			} else {
				Log.info("客户端ID为空，cleanSession为0，根据协议，不接收此客户端");
				client.writeMsgToReqClient(new ConnAckMessage(ConnectionStatus.IDENTIFIER_REJECTED, 0));
				client.close();
			}
		}
		//若至此没问题，则将新客户端连接加入client的维护列表中
		ConnectionDescriptor connectionDescriptor = 
				new ConnectionDescriptor(connectMessage.getClientId(), client, connectMessage.isCleanSession());
		this.clients.put(connectMessage.getClientId(), connectionDescriptor);
		
	}
}
