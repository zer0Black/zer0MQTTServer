package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * MQTT协议ConnAck消息类型实现类，连接确认消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-2
 */
public class ConnAckMessage extends Message {
	
	public enum ConnectionStatus {
		ACCEPTED(0x00),

		UNACCEPTABLE_PROTOCOL_VERSION(0x01),

		IDENTIFIER_REJECTED(0x02),

		SERVER_UNAVAILABLE(0x03),

		BAD_USERNAME_OR_PASSWORD(0x04),

		NOT_AUTHORIZED(0x05);
		
		private final int value;
		
		ConnectionStatus(int value) {
			this.value = value;
		}
		
		/**
		 * 获取类型对应的值
		 * @return int
		 * @author zer0
		 * @version 1.0
		 * @date 2016-3-4
		 */
		public int value() {
			return value;
		}
		
		//通过读取到的整型来获取对应的QoS类型
		public static ConnectionStatus valueOf(byte i) {
			for(ConnectionStatus q: ConnectionStatus.values()) {
				if (q.value == i)
					return q;
			}
			throw new IllegalArgumentException("连接响应值无效: " + i);
		}
	}
	
	
	public ConnAckMessage(FixedHeader fixedHeader, ConnAckVariableHeader variableHeader) {
		super(fixedHeader, variableHeader);
	}
	
	@Override
	public ConnAckVariableHeader getVariableHeader(){
		return (ConnAckVariableHeader)super.getVariableHeader();
	}

}
