package com.syxy.protocol.mqttImp.process.subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * <li>说明 订阅树的节点，包含父节点、代表节点的token、子节点列表和该节点包含的客户端ID，此部分参考moquette
 * <li>作者 zer0
 * <li>创建日期 2015-4-26
 */
public class TreeNode {
	
    TreeNode parent;
    Token token;
    List<TreeNode> children = new ArrayList<TreeNode>();//子节点列表
    List<Subscription> subscriptions = new ArrayList<Subscription>();//客户ID列表，每个subscription代表一个clientID
    
    public TreeNode(TreeNode parent) {
    	this.parent = parent;
	}
    
    void addSubscription(Subscription subscription){
    	//避免同样的订阅添加进来
    	if (subscriptions.contains(subscription)) {
			return;
		}
    	
    	//同一节点中topic是一样的，所以判断clientID是否重复，若topic和clientID一样，但qos不一样，就移除，重新添加
    	for (Subscription s : subscriptions) {
    		if (s.clientID.equals(subscription.clientID)) {
				return;
			}
    	}
    	
    	subscriptions.add(subscription);
    }
}
