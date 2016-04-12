package com.jtv.locationwork.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

public class DateUtil {
	public static String style_nyrsfm = "yyyy-MM-dd HH:mm:ss";

	public static String style_nyr = "yyyy-MM-dd";

	public static String style_sfm = "HH:mm:ss";

	public static String style_sf = "HH:mm";
	
	public static String style_nyr_sf="yyyy-MM-dd HH:mm";

	/**
	 * 把一个字符串解析成date
	 * 
	 * @param pattern
	 *            常见格式 yyyy-MM-dd HH:mm:ss 年月日时分秒
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static Date parseTime(String pattern, String time) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.parse(time);
	}
	public static long getCurrTime(){
		return Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * 更具日期和样式获取到自己的样式日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateFormat(Date date, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String dateFormat = simpleDateFormat.format(date);
		return dateFormat;
	}

	/**
	 * 获取到当前的日期样式
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurrDateFormat(String format) {
		return getDateFormat(new Date(), format);
	}

	/**
	 * 判断当前时间是否在开始时间和结束时间的范围内 结束时间必须大于开始时间
	 * 
	 * @param strttime
	 *            开始时间
	 * @param endttime
	 *            结束时间
	 * @param currtime
	 *            当前时间
	 * @param differnt
	 *            开始时间和结束时间允许的偏差,可以为0，当大于0代表开始时间减少，当小于0代表开始时间增大，当大于0结束时间相当于增加，
	 *            当小于0结束时间相当于减少
	 * @return 如果在这个范围返回true
	 */
	public static boolean isScrope(long strttime, long endttime, long currtime, int differnt) {

		if (currtime <= (strttime - differnt)) {
			return false;
		}

		if (currtime >= (endttime + differnt)) {
			return false;
		}

		return true;
	}

	/**
	 * 判断时间是否在当前的时间范围内
	 * 
	 * @param time
	 *            时间的毫秒值
	 * @param scope
	 *            范围的毫秒值
	 * @return
	 */
	public boolean isScope(long time, long scope) {

		long cutime = new Date().getTime();

		if ((time + scope) < cutime)
			return true;

		return false;
	}

	/**
	 * 获取两个时间相差的分钟数
	 * 
	 * @return
	 */
	public static int differenceMinutes(long time1, long time2) {
		long abs = Math.abs(time1 - time2); // 取得他们的差距

		int i = (int) (abs / (60 * 1000));// 1分钟等于60*1000

		return i;
	}

	/**
	 * 获取当前的时间，获取到dis前的时间
	 * 范围
	 * @param dis
	 * @param format
	 * @return
	 */
	public static String getCurrDateFormat(long dis, String format) {
		long time = new Date().getTime();
		time = time - (long) dis;
		Date date = new Date(time);
		return getDateFormat(date, format);
	}

	/**
	 * 截取一个时分08:00,中文判断
	 * 
	 * @param hhmm
	 * @return
	 */
	public static String convretHHmm(String hhmm) {
		if (TextUtils.isEmpty(hhmm)) {
			return hhmm;
		}
		hhmm = StringUtil.replaceChineseChar(hhmm);

		if (hhmm.length() > 5 && hhmm.contains(":")) {// 08:00:9
			int indexOf = hhmm.indexOf(":");
			if (indexOf == 2) {
				return hhmm.substring(0, 5);
			}
		}
		if (hhmm.length() > 5 && hhmm.contains("/")) {// 08/00/9
			int indexOf = hhmm.indexOf("/");
			if (indexOf == 2) {
				return hhmm.substring(0, 5);
			}
		}
		return hhmm;
	}

	/**
	 * 截取一个日期返回长度10位
	 * 
	 * @param hhmm
	 * @return
	 */
	public static String convretyyMMdd(String yymmdd) {
		if (TextUtils.isEmpty(yymmdd)) {
			return yymmdd;
		}
		yymmdd = StringUtil.replaceChineseChar(yymmdd);

		if (yymmdd.length() >= 10) {// "2015-11-23"
			return yymmdd.substring(0, 10);
		}

		return yymmdd;
	}

	/**
	 * 把一条数据分开2015-10-05 008:00
	 * 
	 * @param str
	 * @return 0 index is date 1 index is time
	 */
	public static String[] convertyyMMddhhss(String str) {
		String[] date = new String[2];
		if (TextUtils.isEmpty(str)) {
			return date;
		}

		str = StringUtil.replaceChineseChar(str);
		if (str.length() >= 15) {
			String substring = str.substring(0, 10);
			date[0] = substring;
			String time = str.replace(substring, "");
			time = time.trim();

			String huhao = "";

			if (TextUtil.isEmpty(time) || time.length() < 3) {
				time = "00:00";
			}

			if (time.contains(":")) {
				huhao = ":";
			}
			if (time.contains("/")) {
				huhao = "/";
			}
			if (time.contains("-")) {
				huhao = "-";
			}

			if (time.contains(huhao) && time.length() < 5) {

				String[] split = time.split(huhao);// 08:02

				if (split.length == 2) {

					String clock = split[0];// 08
					String speed = split[1];// 08

					if (clock.length() < 2) {// 8
						clock = "0" + clock;// 08
					}

					if (speed.length() < 2) {// 2
						speed = "0" + speed;// 02
					}

					time = clock + huhao + speed;// 08:02
				}
			}

			date[1] = time.substring(0, 5);
			if (time.startsWith("-") || time.startsWith("/") || time.startsWith(":")) {
				date[1] = time.substring(1, 6);
			}
		}
		return date;
	}
	
	public static String getStringForTime(long time,String style){
		Date date = new Date();
		date.setTime(time);
		return getDateFormat(date, style);
	}

	/**
	 * 2015-10-12 08:00:00 或者 2015-10-12 08:00 的毫秒值
	 * 
	 * @param data
	 *            毫秒值
	 * @return
	 */
	public static long getTimesFotString(String data) {
		Date time = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// String dateFormat = simpleDateFormat.format(data);
			time = simpleDateFormat.parse(data);

		} catch (Exception e) {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			// String dateFormat = simpleDateFormat.format(data);
			try {
				time = simpleDateFormat.parse(data);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

		}

		if (time != null) {
			return time.getTime();
		}
		return 0;

	}

}
