package com.android.bookbook.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Calendar string2Cal(String arg) {
		try{
			SimpleDateFormat sdf = null;
			String trimString = arg.trim();
			if (trimString.length() > 14)
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			else
				sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = null;
			try {
				d = sdf.parse(trimString);
			} catch (ParseException e) {
				return null;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal;
		}catch (Exception e) {
			return null;
		}
	}

	/*
	 * 转化calendar时间为calendar的当天0:00
	 */
	public static Calendar Calendar2Zero(Calendar calendar){
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}
	
	/**
    *
    * @param dateValue 单位毫秒
    * @return
    */
   public static String formatTime(Long dateValue) {
       return formatTime(dateValue, "yyyy-MM-dd HH:mm:ss.SSS");
   }
   
   /**
   *
   * @param dateValue 单位毫秒
   * @param format
   * @return
   */
  public static String formatTime(Long dateValue, String format) {
      Calendar canlendar = Calendar.getInstance();
      canlendar.setTimeInMillis(dateValue);
      return formatTime(canlendar.getTime(), format);
  }
  
  public static String formatTime(Date dateValue, String format) {
      DateFormat dateformat = new SimpleDateFormat(format);
      String date = dateformat.format(dateValue);
      return date;
  }
   
}