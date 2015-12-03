package com.syxy.util;

import java.util.Date;

public class Uid {
	private static Date date = new Date();
	private static StringBuilder buf = new StringBuilder();
	private static int seq = 0;
	private static final int ROTATION = 999;

	public static synchronized int next() {
		if (seq > ROTATION)
			seq = 0;
		buf.delete(0, buf.length());
		date.setTime(System.currentTimeMillis());
		String str = String.format("%1$tk%1$tM%1$tS%2$03d",
				date, seq++);
		return Integer.parseInt(str);
	}
	
	public static void main(String[] args) {
		System.out.println(next());
	}
}
