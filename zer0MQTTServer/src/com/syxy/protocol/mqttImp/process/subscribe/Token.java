package com.syxy.protocol.mqttImp.process.subscribe;

/**
 * <li>说明 此类用于存储每个Topic解析出来的订阅（Topic：country/china/tianjin）
 * 												country/usa/xxx
 *              								sport/xxxx
 * <li>作者 zer0
 * <li>创建日期 2015-4-19
 */
public class Token {

	static final Token MULTI = new Token("#");
	static final Token SINGLE = new Token("+");
	static final Token EMPTY = new Token("");
	String name;
	
	Token(String name){
		this.name = name;
	}
}
