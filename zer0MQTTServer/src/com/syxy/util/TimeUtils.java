package com.syxy.util;

import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 从网络上获取时间，若出问题则从系统获取时间
 * 
 * @author lixuetao
 * @version 1.0
 * @date 2015-8-31
 */
public class TimeUtils {

	public static final int DEFAULT_PORT = 37;// NTP服务器端口
	public static final String DEFAULT_HOST = "time-nw.nist.gov";// NTP服务器地址

	/**
	 * 获取当前网络上的时间，出异常就获取本地系统时间
	 * 
	 * @return String
	 * @author lixuetao
	 * @version 1.0
	 * @date 2015-8-31
	 */
	public static String currentTime() {
		// The time protocol sets the epoch at 1900,
		// the java Date class at 1970. This number
		// converts between them.
		long differenceBetweenEpochs = 2208988800L;

		InputStream raw = null;
		try {
			Socket theSocket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
			raw = theSocket.getInputStream();

			long secondsSince1900 = 0;
			for (int i = 0; i < 4; i++) {
				secondsSince1900 = (secondsSince1900 << 8) | raw.read();
			}
			if (raw != null)
				raw.close();
			long secondsSince1970 = secondsSince1900 - differenceBetweenEpochs;
			long msSince1970 = secondsSince1970 * 1000;
			// 把时间戳转为当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(new Date(Long.parseLong(msSince1970
					+ "")));
			return currentTime;
		} catch (Exception e) {
			//e.printStackTrace();
			// 返回本地时间
			return currentSystemTime();
		}
	}

	/**
	 * 获取系统时间
	 * 
	 * @return String
	 * @author lixuetao
	 * @version 1.0
	 * @date 2015-8-31
	 */
	public static String currentSystemTime() {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		java.util.Locale locale = java.util.Locale.CHINA;
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(pattern,
				locale);
		java.util.Date date = new java.util.Date();
		String currentSystemTime = df.format(date);
		return currentSystemTime;
	}
	
	/**
	 * 获取当天的结束时间
	 * @return String
	 * @author lixuetao
	 * @version 1.0
	 * @date 2015-11-13
	 */
	public static long getDayEndTime(){  
        Calendar todayEnd = Calendar.getInstance();  
        todayEnd.set(Calendar.HOUR, 23);  
        todayEnd.set(Calendar.MINUTE, 59);  
        todayEnd.set(Calendar.SECOND, 59);  
        todayEnd.set(Calendar.MILLISECOND, 999);  
        return todayEnd.getTime().getTime();
    }  
}
