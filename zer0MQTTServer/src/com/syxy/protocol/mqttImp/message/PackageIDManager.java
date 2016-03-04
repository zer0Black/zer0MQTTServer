package com.syxy.protocol.mqttImp.message;

import java.util.Hashtable;

/**
 * 协议中的某些类型需要使用的包ID管理类
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-3
 */
public class PackageIDManager {

	//包ID是两个字节，所以最大的是65535，最小是1
	private static final int MIN_MSG_ID = 1;		
	private static final int MAX_MSG_ID = 65535;
	private static int nextMsgId = MIN_MSG_ID - 1;
	private static Hashtable<Integer, Integer> inUseMsgIds = new Hashtable<Integer, Integer>();
		
	private int packgeID;//包ID	
	
	
	/**
	 * 获取包ID
	 * @return int
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-5
	 */
	public static synchronized int getNextMessageId(){
		int startingMessageId = nextMsgId;
		//循环两次是为了给异步出问题提供一个容错范围
		int loopCount = 0;
	    do {
	        nextMsgId++;
	        if ( nextMsgId > MAX_MSG_ID ) {
	            nextMsgId = MIN_MSG_ID;
	        }
	        if (nextMsgId == startingMessageId) {
	        	loopCount++;
	        	if (loopCount == 2) {
	        		throw new UnsupportedOperationException("获取不到可用的包ID");
	        	}
	        }
	    } while( inUseMsgIds.containsKey( new Integer(nextMsgId) ) );
	    Integer id = new Integer(nextMsgId);
	    inUseMsgIds.put(id, id);
	    return nextMsgId;
	}
	
	/**
	 * 释放不用的包ID
	 * @param msgId
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-3
	 */
	public synchronized static void releaseMessageId(int msgId) {
		inUseMsgIds.remove(new Integer(msgId));
	}
	
}
