package com.syxy.protocol.mqttImp;

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

	final public int val;
	
	Type(int val) {
		this.val = val;
	}
	
	//通过读取到的整型来获取对应的Type类型
	public static Type valueOf(int i) {
		for(Type t: Type.values()) {
			if (t.val == i)
				return t;
		}
		return null;
	}
}
