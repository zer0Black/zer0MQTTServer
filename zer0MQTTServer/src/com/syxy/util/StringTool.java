package com.syxy.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import sun.rmi.runtime.Log;

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
	
	/**
	 * <li>方法名 stringToByte
	 * <li>@param string
	 * <li>返回类型 byte[]
	 * <li>说明 将字符串转换为byte数组
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-3
	 */
	public static byte[] stringToByte(String string) {
		if (string == null) {
			return new byte[0];
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(byteOut);
		try {
			dos.writeUTF(string);
		} catch (IOException e) {
			return new byte[0];
		}
		return byteOut.toByteArray();
	}
	
	/**
	 * <li>方法名 getRandomString
	 * <li>@param length
	 * <li>返回类型 length
	 * <li>说明 随机生成字符串
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-7
	 */
	 public static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
	
}
