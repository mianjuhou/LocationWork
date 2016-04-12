package com.jtv.locationwork.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
	/**
	 * Returns true if the string is null or 0-length.
	 * 
	 * @param str
	 *            the string to be examined
	 * @return true if str is null or zero length
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else {
			if ("null".equals(str)) {
				return true;
			} else if ("Null".equals(str)) {
				return true;
			} else if ("NULL".equals(str)) {
				return true;
			}
			return false;
		}
	}

	public static boolean isChinesse(String str) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(str);
		boolean find = matcher.find();
		return find;
	}

}
