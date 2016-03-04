package com.syxy.protocol.mqttImp.process;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class NettyAttrManager {

	public static final String CLIENT_ID = "ClientID";//客户端ID
    public static final String CLEAN_SESSION = "cleanSession";
    public static final String KEEP_ALIVE = "keepAlive";//心跳包时长
    
    private static final AttributeKey<Object> ATTR_KEY_KEEPALIVE = AttributeKey.valueOf(KEEP_ALIVE);
    private static final AttributeKey<Object> ATTR_KEY_CLEANSESSION = AttributeKey.valueOf(CLEAN_SESSION);
    private static final AttributeKey<Object> ATTR_KEY_CLIENTID = AttributeKey.valueOf(CLIENT_ID);
	
    public static String getAttrClientId(Channel channel) {
		return (String)channel.attr(NettyAttrManager.ATTR_KEY_CLIENTID).get();
	}
    
    public static void setAttrClientId(Channel channel, String clientID) {
    	channel.attr(NettyAttrManager.ATTR_KEY_CLIENTID).set(clientID);
	}
    
    public static Boolean getAttrCleanSession(Channel channel) {
    	return (Boolean)channel.attr(NettyAttrManager.ATTR_KEY_CLEANSESSION).get();
	}
    
    public static void setAttrCleanSession(Channel channel, Boolean cleansession) {
    	channel.attr(NettyAttrManager.ATTR_KEY_CLEANSESSION).set(cleansession);
	}
    
    public static int getAttrKeepAlive(Channel channel) {
    	return (int)channel.attr(NettyAttrManager.ATTR_KEY_KEEPALIVE).get();
	}
    
    public static void setAttrKeepAlive(Channel channel, int keepAlive) {
    	channel.attr(NettyAttrManager.ATTR_KEY_KEEPALIVE).set(keepAlive);
	}
    
}
