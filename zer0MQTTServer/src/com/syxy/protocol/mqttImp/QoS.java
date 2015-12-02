package com.syxy.protocol.mqttImp;

public enum QoS {
	AT_MOST_ONCE  (0),
	AT_LEAST_ONCE (1),
	EXACTLY_ONCE  (2),
	RESERVE(3);
	
	final public int val;
	
	QoS(int val) {
		this.val = val;
	}
	
	//通过读取到的整型来获取对应的QoS类型
	public static QoS valueOf(int i) {
		for(QoS q: QoS.values()) {
			if (q.val == i)
				return q;
		}
		throw new IllegalArgumentException("Qos值无效: " + i);
	}
}