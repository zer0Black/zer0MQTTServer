package com.syxy.protocol.mqttImp.process.subscribe;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * <li>说明 订阅存储类,存储订阅的topic,即订阅树
 * <li>作者 zer0
 * <li>创建日期 2015-4-11
 */
public class SubscribeStore {
    
	
	/**
	 * <li>方法名 addSuscription
	 * <li>@param 
	 * <li>返回类型 subscriptionTreeNode
	 * <li>说明 通过topic取得对应的订阅对象
	 * <li>作者 zer0
	 * <li>创建日期 2015-4-19
	 * @return 
	 */
	public void addSubscrpition(){	
		
	} 

	
    /**
	 * <li>方法名 searchNode
	 * <li>@param treeNode
	 * <li>@param topic
	 * <li>返回类型 {@link Subscription}
	 * <li>说明 解析topic，根据协议得到对应的token列表
	 * <li>作者 zer0
	 * <li>创建日期 2015-4-25
     */
	public Subscription searchNode(Subscription treeNode, String topic){
		List<Token> tokens = new ArrayList<Token>();
		try {
			tokens = parseTopic(topic);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Subscription temp = null;
		return temp;
	}
	
    /**
	 * <li>方法名 parseTopic
	 * <li>@param topic
	 * <li>返回类型 List<Token>
	 * <li>说明 解析topic，根据协议得到对应的token列表
	 * <li>作者 zer0
	 * <li>创建日期 2015-4-19
     * <li>@throws ParseException 
     */
	static List<Token> parseTopic(String topic) throws ParseException{
		List<Token> tokens = new ArrayList<Token>();
		String[] token = topic.split("/");
		
		if (token.length == 0) {
			 tokens.add(Token.EMPTY);
		}
		
		for (int i = 0; i < token.length; i++) {
			String s = token[i];
			if (s.isEmpty()) {
				tokens.add(Token.EMPTY);
			}else if (s.equals("#")) {
				if (i != token.length - 1) {
					throw new ParseException("无效的的topic，" + s + "#必须放在分隔符的最尾", i);
				}
				tokens.add(Token.MULTI);
			}else if (s.contains("#")){
				throw new ParseException("无效的topic"+s, i);
			}else if (s.equals("+")) {
				tokens.add(Token.SINGLE);
			}else if (s.contains("+")) {
				throw new ParseException("无效的topic"+s, i);
			}else{
				tokens.add(new Token(s));
			}
		}
		
		return tokens;
	} 
}