package com.syxy.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <li>说明 Java Properties属性文件操作类
 * <li>作者 zer0
 * <li>创建日期 2015-2-19
 */

public class PropertiesTool {
	
	private final static Logger Log = Logger.getLogger(PropertiesTool.class);
	
	private static Properties props = new Properties();
	//配置文件路径
	private static final String CONFIG_FILE = System.getProperty("user.dir") + "/resource/mqtt.properties";
	
	static{
		loadProperties(CONFIG_FILE);
	}
	
	/**
	 * <li>方法名 loadProperties
	 * <li>@param propertyFilePath
	 * <li>返回类型 void
	 * <li>说明 加载属性文件
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-19
	 */
	private static void loadProperties(String propertyFilePath){
		try {
			FileInputStream in = new FileInputStream(propertyFilePath);
			props = new Properties();
			props.load(in);
		} catch (IOException e) {
			Log.error("属性文件读取错误");
			e.printStackTrace();
		}
	}
	
	/**
	 * <li>方法名 getProperty
	 * <li>@param key
	 * <li>返回类型 String
	 * <li>说明 从指定的键取得对应的值
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-19
	 */
	public static String getProperty(String key){
		return props.getProperty(key);
	}
	
	/**
	 * <li>方法名 getProperty
	 * <li>@param key
	 * <li>返回类型 String
	 * <li>说明 从指定的键取得整数
	 * <li>作者 zer0
	 * <li>创建日期 2015-2-19
	 */
	public static Integer getPropertyToInt(String key){
		String str = props.getProperty(key);
		if(StringTool.isBlank(str.trim())){
			return null;
		}
		return Integer.valueOf(str.trim()); 
	}
}
