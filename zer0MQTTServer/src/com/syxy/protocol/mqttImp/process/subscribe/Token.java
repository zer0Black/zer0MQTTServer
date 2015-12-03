package com.syxy.protocol.mqttImp.process.subscribe;

/**
 *  此类用于存储每个Topic解析出来的订阅（Topic：country/china/tianjin）
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-4-19
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
