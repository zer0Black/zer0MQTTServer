package com.syxy.protocol.mqttImp.message;

/**
 * Mqtt协议总共有十四种类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-3
 */
public enum MessageType {
	CONNECT(1),
	CONNACK(2),
	PUBLISH(3),
	PUBACK(4),
	PUBREC(5),
	PUBREL(6),
	PUBCOMP(7),
	SUBSCRIBE(8),
	SUBACK(9),
	UNSUBSCRIBE(10),
	UNSUBACK(11),
	PINGREQ(12),
	PINGRESP(13),
	DISCONNECT(14);
	
	private final int value;
	
	MessageType(int value) {
		this.value = value;
	}
	
	/**
	 * 获取类型对应的值
	 * @return int
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-3
	 */
	public int value() {
		return value;
	}
	
	/**
	 * 把值转变成对应的类型并返回
	 * @return int
	 * @author zer0
	 * @version 1.0
	 * @date 2016-3-3
	 */
	public static MessageType valueOf(int type){
		for (MessageType m : values()) {
			if (m.value == type) {
				return m;
			}
		}
		throw new IllegalArgumentException("未知的MQTT协议类型："+type);
	}
	
}
