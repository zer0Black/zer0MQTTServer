package com.syxy.util;

/**
 * <li>说明 字符串工具类
 * <li>作者 zer0
 * <li>创建日期 2015-2-19
 */

public class StringTool {

	/**
	 * <li>方法名 isBlank
	 * <li>@param str
	 * <li>返回类型 boolean
	 * <li>说明 判断字符串是否为空
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-19
	 */
	public static boolean isBlank(String str){
		if(str==null){
			return true;
		}
		if(str.trim().length()<1){
			return true;
		}
		
		if(str.trim().equals("")){
			return true;
		}
		
		if(str.trim().toLowerCase().equals("null")){
			return true;
		}
		return false;
	}
	
}
