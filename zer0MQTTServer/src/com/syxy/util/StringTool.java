package com.syxy.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.rmi.runtime.Log;

/**
 * 字符串工具类
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-2-19
 */
public class StringTool {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return boolean
	 * @author zer0
	 * @version 1.0
	 * @date 2015-2-19
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
	 * 将字符串转换为byte数组
	 * 
	 * @param string
	 * @return byte[]
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-3
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
	 * 随机生成字符串
	 * 
	 * @param length
	 * @return String
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-7
	 */
	 public static String generalRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
	 
	 /**
		 * 随机生成一个mac地址
		 * 
		 * @return String
		 * @author zer0
		 * @version 1.0
		 * @date  2015-7-7
		 */
	 public static String generalMacString(){
		 String str = "ABCDEF0123456789";
		 Random random = new Random();
		 StringBuffer sb = new StringBuffer();
		 int macGroupSize = 6;//mac地址长度为6组16进制
		 int macOneGroup = 2;//一组有两个字符
		 for (int i = 0; i < macGroupSize; i++) {
			 for (int j = 0; j < macOneGroup; j++) {
					int number = random.nextInt(16);
					sb.append(str.charAt(number));
			}
		    sb.append("-");
		}
		String macString = sb.toString().substring(0, sb.length()-1);
		return macString;
	 }
	 
	 /**
		 * 判断字符串是否为mac地址
		 * 
		 * @param str
		 * @return boolean
		 * @author zer0
		 * @version 1.0
		 * @date  2015-7-7
		 */
	 public static boolean isMacString(String str){
		 String pattern = "^([A-Fa-f\\d]{2}[-:])([A-Fa-f\\d]{2}[-:]){4}([A-Fa-f\\d]{2})$";
		 Pattern pat = Pattern.compile(pattern); 
		 Matcher mat = pat.matcher(str);
		 Boolean isFind = mat.find();
		 if (isFind) {
			return true;
		}else {
			return false;
		}
	 }
	
	 public static void main(String[] args){
		System.out.println(StringTool.generalRandomString(32));
	 } 
}
