package com.syxy.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * <li>说明 log4j封装，用类方法来输出日志
 * <li>作者 zer0
 * <li>创建日期 2015-2-15
 */

public class Log{
	
	public static Logger logger;
	 
	//静态块加载配置文件
	static{	
		try {
			InputStream in = Log.class.getResourceAsStream("/resource/log4j.properties");     
			Properties properties = new Properties();
			properties.load(in);
			PropertyConfigurator.configure(properties);
			logger = Logger.getLogger(Log.class );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void debug(Object message) {
		// TODO Auto-generated method stub
		logger.debug(message);
	}
	
	public static void info(Object message) {
		// TODO Auto-generated method stub
		logger.info(message);
	}

	public static void warn(Object message) {
		// TODO Auto-generated method stub
		logger.warn(message);
	}
	
	public static void error(Object message) {
		// TODO Auto-generated method stub
		logger.error(message);
	}
	
}
