package com.syxy.protocol.mqttImp.process.subscribe;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * <li>说明 订阅存储类,存储订阅的topic,即订阅树
 * <li>作者 zer0
 * <li>创建日期 2015-4-11
 */
public class SubscribeStore {
    
	private TreeNode root = new TreeNode(null);
	
	/**
	 * <li>方法名 addSuscription
	 * <li>@param newSubscription
	 * <li>返回类型 void
	 * <li>说明 添加新的订阅到订阅树里
	 * <li>作者 zer0
	 * <li>创建日期 2015-4-19
	 */
	public void addSubscrpition(Subscription newSubscription){	
		List<TreeNode> treeNodes = searchNodeList(newSubscription.topicFilter);
		for (TreeNode t : treeNodes) {
			t.addSubscription(newSubscription);
		}
	} 

	 /**
	 * <li>方法名  searchNodeList
	 * <li>@param topic
	 * <li>返回类型 {@link List<TreeNode>}
	 * <li>说明 根据topic搜索节点，若无此节点则创建,最后返回一个所搜索的所有节点的列表
	 * <li>作者 zer0
	 * <li>创建日期 2015-4-25
     */
	public List<TreeNode> searchNodeList(String topic){
		List<Token> tokens = new ArrayList<Token>();
		List<TreeNode> childList = new ArrayList<TreeNode>();
		
		try {
			tokens = parseTopic(topic);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		TreeNode current = root;
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			TreeNode matchingChildren = current;
 
			//TODO 为实现简便，此处违反协议
			if (token == Token.SINGLE) {
				//判断‘+’是不是在队列最后一个
				if (i == tokens.size() - 1) {
					childList.addAll(matchingChildren.children);
				}
				return childList;
			}
			
			//遇到#，证明已搜索到底部，不再生成节点
			if (token == Token.MULTI) {	
				if (i == tokens.size() - 1) {
					//判断‘#’是不是在队列最后一个
					childList.add(matchingChildren);
					childList.addAll(matchingChildren.getAllDescendant());
				}
				return childList;		
			}			
			
			if ((matchingChildren = current.childWithToken(token)) != null) {
				current = matchingChildren;
			} else {
				matchingChildren = new TreeNode(current);
				matchingChildren.setToken(token);
				current.addChild(matchingChildren);
				current = matchingChildren;
			}
			
			//如果该token是最后一个，则添加到返回列表里
			if (i == tokens.size() - 1) {
				childList.add(current);
			}
		}
		return childList;
	}
	
    /**
	 * <li>方法名 getClientListFromTopic
	 * <li>@param topic
	 * <li>返回类型List<Subscription
	 * <li>说明 解析topic,从topic获取到对应的客户端ID群
	 * <li>作者 zer0
	 * <li>创建日期 2015-5-04 
     */
	public List<Subscription> getClientListFromTopic(String topic) {
		List<Token> tokens;
		try {
			tokens = parseTopic(topic);
		} catch (ParseException e) {
			return Collections.EMPTY_LIST;
		}
		Queue<Token> tokenQueue = new LinkedBlockingDeque<Token>(tokens);
		List<Subscription> matchingSubs = new ArrayList<Subscription>();
		root.getSubscription(tokenQueue, matchingSubs);
		return matchingSubs;
	}
	
	/**
  	 * <li>方法名 removeForClient
  	 * <li>param clientID
  	 * <li>返回类型 {@link void}
  	 * <li>说明  把某个clientID从订阅树里移走
  	 * <li>作者 zer0
  	 * <li>创建日期 2015-5-04
     */
	public void removeForClient(String clientID) {
	    root.removeClientSubscription(clientID);
	}
	
	/**
	 * <li>方法名 removeSubscription
	 * <li>@param topic
	 * <li>@param clientID
	 * <li>返回参数 void
	 * <li>说明 从订阅结构树中移除某个订阅主题中的某个client
	 * <li>作者 zer0
	 * <li>创建日期 2015-06-29
	 */
	public void removeSubscription(String topic, String clientID){
		List<TreeNode> treeNodes = searchNodeList(topic);
		for (TreeNode t : treeNodes) {
			t.removeClientSubscription(clientID);
			//如果某个节点的订阅量为空，并且该节点无子节点，则删除它
			if (t.subscriptions.size() == 0 && t.children.size() == 0) {
				t.parent.children.remove(t);
			}
		}
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
				}else {
					tokens.add(Token.MULTI);
				}	
			}else if (s.contains("#")){
				throw new ParseException("无效的topic"+s, i);
			}else if (s.equals("+")) {
				//违反协议，不好实现，视情况看以后是否处理
				if (i != token.length - 1) {
					throw new ParseException("无效的的topic，" + s + "此服务器暂不支持通配符‘+’作为中层分隔符", i);
				}else {
					tokens.add(Token.SINGLE);
				}
			}else if (s.contains("+")) {
				throw new ParseException("无效的topic"+s, i);
			}else{
				tokens.add(new Token(s));
			}
		}
		return tokens;
	} 
}