package com.syxy.protocol.mqttImp.process.subscribe;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	TreeNode parent;
	List<TreeNode> childrens = new ArrayList<TreeNode>();
	
	public TreeNode(TreeNode parent) {
		this.parent = parent;
	}	

	public void root(TreeNode treeNode){
		
	}
	
}
