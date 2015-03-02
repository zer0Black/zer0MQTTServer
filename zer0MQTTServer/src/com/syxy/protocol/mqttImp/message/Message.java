package com.syxy.protocol.mqttImp.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * <li>定义MQTT协议固定头部，并作为细分的message的基类
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public abstract class Message {

	public enum Type {
		CONNECT     ( 1),
		CONNACK     ( 2),
		PUBLISH     ( 3),
		PUBACK      ( 4),
		PUBREC      ( 5),
		PUBREL      ( 6),
		PUBCOMP     ( 7),
		SUBSCRIBE   ( 8),
		SUBACK      ( 9),
		UNSUBSCRIBE (10),
		UNSUBACK    (11),
		PINGREQ     (12),
		PINGRESP    (13),
		DISCONNECT  (14);

		final protected int val;
		
		Type(int val) {
			this.val = val;
		}
		
		//通过读取到的整型来获取对应的Type类型
		static Type valueOf(int i) {
			for(Type t: Type.values()) {
				if (t.val == i)
					return t;
			}
			return null;
		}
	}
	
	public enum QoS {
		AT_MOST_ONCE  (0),
		AT_LEAST_ONCE (1),
		EXACTLY_ONCE  (2);
		
		final public int val;
		
		QoS(int val) {
			this.val = val;
		}
		
		//通过读取到的整型来获取对应的QoS类型
		static QoS valueOf(int i) {
			for(QoS q: QoS.values()) {
				if (q.val == i)
					return q;
			}
			throw new IllegalArgumentException("Qos值无效: " + i);
		}
	}
	
	/**
	 * <li>方法名 encodeHeader
	 * <li>返回类型 byte
	 * <li>说明 对MQTT协议进行编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-2
	 */
	public abstract ByteBuffer encode() throws IOException;

	/**
	 * <li>方法名 decode
	 * <li>@param buffer
	 * <li>返回类型 HeaderMessage
	 * <li>说明 对MQTT协议头部进行解码，并返回整个头部类
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-2
	 */
	public abstract Message decode(ByteBuffer byteBuffer) throws IOException;

	
}
