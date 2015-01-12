package com.tpv.androidtool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeFormat {
	
	static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static long TimeS2L(String formattime) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+08"));
		cal.get(Calendar.YEAR);
		return cal.getTime().getTime();
	}

	public static String TimeL2S(long utctime) {
		return format.format(new Date(utctime));
	}
}
