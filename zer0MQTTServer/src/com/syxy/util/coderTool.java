package com.syxy.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * <li>说明 对数据解码编码的工具类
 * <li>作者 zer0
 * <li>创建日期 2015-2-23
 */
public class coderTool {

	private static Charset charset = Charset.forName("utf-8");
	
	/**
	 * <li>方法名 encode
	 * <li>@param str
	 * <li>返回类型 ByteBuffer
	 * <li>说明 对字符串进行byte编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-23
	 */
	public static ByteBuffer encode(String str) {   
		return charset.encode(str);   
	}
	
	/**
	 * <li>方法名 encode
	 * <li>@param byteBuffer
	 * <li>返回类型 String
	 * <li>说明 对bytebuffer进行解码，解码成字符串
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-23
	 */
	public static String decode(ByteBuffer byteBuffer){
		return charset.decode(byteBuffer).toString();
	}
	
}
