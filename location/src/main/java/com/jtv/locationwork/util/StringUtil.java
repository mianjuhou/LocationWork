package com.jtv.locationwork.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * 字符串处理.
 * 
 */
public final class StringUtil {
	public StringUtil() {
	}

	/**
	 * 数组转换为字符串.
	 * 
	 * @param strSource
	 *            要分解的数组
	 * @param strDelimiter
	 *            分隔符
	 * @param bProcessEmpty
	 *            是否跳空值
	 * @return String 转换后串
	 * @since BASE 0.1
	 */
	public static final String arrayToString(String[] strSource, String strDelimiter, boolean bProcessEmpty) {
		// 空值返回
		if (strSource.length == 0)
			return "";

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < strSource.length; i++) {
			// 空值跳过
			if (bProcessEmpty) {
				if (strSource[i] == null || strSource[i].equals("") || strSource[i].length() == 0)
					continue;
			}
			String ss = strSource[i] == null ? "" : strSource[i];
			buf.append(ss);
			if (i != strSource.length - 1) {
				buf.append(strDelimiter);
			}

		}
		return buf.toString();
	}

	/**
	 * 
	 * 
	 * @param strSource
	 * @param strDelimiter
	 * @return
	 * @since BASE 0.1
	 */
	//
	public static final String arrayToString(String[] strSource, String strDelimiter) {
		return arrayToString(strSource, strDelimiter, true);
	}

	/**
	 * 
	 * @Title: listToString @Description: list转字符串 @param @param
	 *         strSource @param @param strDelimiter @param @return @return
	 *         String 返回类型 @date 2013-6-4 下午4:06:28 @throws
	 */
	public static final String listToString(List<String> strSource, String strDelimiter) {
		String[] arr = (String[]) strSource.toArray(new String[strSource.size()]);
		return arrayToString(arr, strDelimiter, false);
	}

	/**
	 * 字符串转为数组.
	 * 
	 * @param strSource
	 *            源字符串
	 * @param delimiter
	 *            分隔符
	 * @return 字符串数组
	 * 
	 */
	public static final String[] stringToArray(String strSource, String delimiter) {
		if (strSource == null)
			return null;

		// 去掉尾部分隔符
		if (strSource.substring(strSource.length() - delimiter.length()) == delimiter)
			strSource = strSource.substring(0, strSource.length() - delimiter.length());

		StringTokenizer token = new StringTokenizer(strSource, delimiter);
		String[] array = new String[token.countTokens()];
		int i = 0;
		// 取值
		while (token.hasMoreTokens()) {
			array[i] = token.nextToken();
			i++;
		}
		return array;
	}

	/**
	 * 使用字符串替换.
	 * 
	 * @param strData
	 * @param strSource
	 * @param strTarget
	 * @return
	 */
	public static final String replaceAll(String strData, String strSource, String strTarget) {
		StringBuffer buf = new StringBuffer(strData);
		int pos = buf.indexOf(strSource);
		while (pos >= 0) {
			buf.replace(pos, pos + strSource.length(), strTarget);
			pos = buf.indexOf(strSource);
		}
		return buf.toString();
	}

	public static final String[] stringToArray(String str) {
		// 默认为，号分隔
		return stringToArray(str, ",");
	}

	/**
	 * 判断字符串是否为“空白字字符”.
	 * 
	 * @param strSource
	 * @return
	 */
	public static final boolean isWhiteSpace(String strSource) {
		if (strSource.length() == 0)
			return false;

		char[] ch = strSource.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (!Character.isWhitespace(ch[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获得空白字符串位置.
	 * 
	 * @param source
	 * @param start
	 * @return
	 * @since BASE 0.1
	 */
	public static int indexOfWhiteSpace(String source, int start) {

		for (int i = start; i < source.length(); i++) {
			if (Character.isWhitespace(source.charAt(i)))
				return i;
		}
		return -1;

	}

	/**
	 * 将空白字符串替换成分隔符.
	 * 
	 * @param strSource
	 * @param cReplace
	 * @return
	 */
	public static final String replaceWhiteSpace(String strSource, String strReplace) {
		String data = strSource.trim();
		boolean found = false;
		StringBuffer buf = new StringBuffer();

		if (data.length() == 0 || data == null)
			return "";

		char[] ch = data.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (Character.isWhitespace(ch[i])) {
				// 连续出现空白字符，则移除
				if (found) {
					continue;
				} else {
					buf.append(strReplace);
					found = true;
					continue;
				}
			} else {
				found = false;
			}
			buf.append(ch[i]);
		}
		return buf.toString();
	}

	/**
	 * 二行数据制转换成十六进制字符串.
	 * 
	 * @param b
	 *            byte[] 字节流
	 * @return String 十六进制字符串
	 * @since BASE 0.1
	 */
	public static String byteToHexString(byte[] b) { //

		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}

			if (n < b.length - 1)
				hs = hs + "";
		}
		// return hs.toUpperCase();
		return hs;
	}

	/**
	 * 将字符串编码成UNICODE编码格式字符串.
	 * 
	 * @param s
	 *            原字符串.
	 * @return 编码后的字符串.
	 */
	public static String String2Unicode(String s) {
		if (s == null || s.length() == 0)
			return "";
		char[] charA = s.toCharArray();
		StringBuffer t = new StringBuffer("");
		String tt = "";
		for (int i = 0; i < charA.length; i++) {
			tt = Integer.toHexString((int) charA[i]);
			if (tt.length() == 2)
				tt = "%" + tt;
			else
				tt = "%u" + tt;
			t.append(tt);
		}
		return t.toString();
	}

	/**
	 * 首字大字.
	 * 
	 * @param source
	 *            String
	 * @return String
	 */
	@SuppressWarnings("null")
	public static String wordCap(String source) {
		if (source == null && source.length() == 0) {
			return "";
		}
		char firstChar = source.charAt(0);
		if (Character.isLetter(firstChar)) {
			String rc = Character.toUpperCase(firstChar) + source.substring(1);
			return rc;
		} else {
			return source;
		}

	}

	public static String getContentWipeSpaceOfJson(String content) {
		if (content != null) {
			String result = content.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replace("'", "’");// .replaceAll("\"",
																														// "\\\\\"");
			return result;
		} else {
			return content;
		}
	}

	public static void main(String[] args) {
		// System.out.println("test replaceWhiteSpace");
		// update
		// String data = " \t\rone \n\r\t\t two\t \r\nthree,";
		// System.out.println("源字符串为：\n\r" + data);
		// System.out.println("结果：@@@" + replaceWhiteSpace(data, ",") + "@@@");
		List<String> res = new ArrayList<String>();
		res.add("aaa");
		res.add(null);
		res.add("123");
		String s = listToString(res, "@");
		System.out.println(s);
	}

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim()) || "null".equals(str.trim());
	}

	public static String padLeft(String str, int totalWidth, String paddingChar) {
		if (str.length() >= totalWidth) {
			return str;
		}
		for (int i = str.length(); i < totalWidth; i++)
			str = paddingChar + str;
		return str;
	}

	public static int String2int(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			// 数据转换失败
			return 0;
		}
	}

	public static boolean isPiStart(String str) {
		if (!StringUtil.isEmpty(str)) {
			return str.startsWith("PI");
		}
		return false;
	}

	public static boolean isToolsCode(String toolsNum) {
		if (!StringUtil.isEmpty(toolsNum)) {
			if (toolsNum.length() > 0) {
				Pattern p = Pattern.compile("[A-Z|a-z]{1}[0-9]{5}");
				Matcher m = p.matcher(toolsNum);
				return m.matches();
			}
		}
		return false;
	}

	/**
	 * 
	 * @param ip
	 *            http://192.168 || https://192.168
	 * @return 成功：192.168 不成功：http://192.168
	 */
	public static String subStringHttpEnd(String ip) {
		if (ip.startsWith("http://") || ip.startsWith("Http://")) {
			return ip.substring(7);
		} else if (ip.startsWith("https://") || ip.startsWith("Https://")) {
			return ip.substring(8);
		}
		return ip;
	}

	/**
	 * 
	 * @param ip
	 * @return index 0 prot forgound , index 1 prot error null
	 */
	public static String[] sunStringHttpPort(String ip) {
		if ("".equals(ip) || ip == null)
			return null;
		String[] arr = new String[2];
		int lastIndexOf = ip.lastIndexOf(":");
		if (lastIndexOf < 0) {
			return null;
		}
		try {
			String f = ip.substring(0, lastIndexOf);
			arr[0] = f;
			String e = ip.substring(lastIndexOf + 1);
			arr[1] = e;
		} catch (Exception e) {
			return null;
		}
		return arr;
	}

	public static String replaceChineseChar(String charStr) {
		if (TextUtil.isEmpty(charStr)) {
			return charStr;
		}
		Map<String, String> replaceChar = getReplaceChar();

		Set<String> keySet = replaceChar.keySet();
		for (String string : keySet) {
			if (charStr.contains(string)) {
				String value = replaceChar.get(string);
				charStr.replace(string, value);
			}
		}
		replaceChar.clear();
		replaceChar = null;
		return charStr;
	}

	public static Map<String, String> getReplaceChar() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("，", ",");
		map.put("。", ".");
		map.put("〈", "<");
		map.put("〉", ">");
		map.put("‖", "|");
		map.put("《", "<");
		map.put("》", ">");
		map.put("〔", "[");
		map.put("〕", "]");
		map.put("﹖", "?");
		map.put("？", "?");
		map.put("“", "\"");
		map.put("”", "\"");
		map.put("：", ":");
		map.put("、", ",");
		map.put("（", "(");
		map.put("）", ")");
		map.put("【", "[");
		map.put("】", "]");
		map.put("—", "-");
		map.put("～", "~");
		map.put("！", "!");
		map.put("‵", "'");
		map.put("①", "1");
		map.put("②", "2");
		map.put("③", "3");
		map.put("④", "4");
		map.put("⑤", "5");
		map.put("⑥", "6");
		map.put("⑦", "7");
		map.put("⑧", "8");
		map.put("⑨", "9");
		return map;
	}

	/**
	 * 获取一个url的ip地址
	 * 
	 * @param address
	 *            "192.168.98.09:8090"
	 * @return 192.168.98.09
	 */
	public static String getHttpAddressIP(String address) {
		if ("".equals(address) || null == address) {
			return null;
		}
		address = address.replaceAll("[a-z,A-Z,//,\\\\]", "");

		boolean startsWith = address.startsWith(":");
		int start = 0;
		if (startsWith) {
			start++;
		}
		int lastIndexOf = address.lastIndexOf(":");
		address = address.replaceAll(" ", "");
		if (lastIndexOf > 1) {
			address = address.substring(start, lastIndexOf);
		} else {
		}
		
		if(!TextUtils.isEmpty(address)){
			address = address.replace(":", "");
		}
		
		
		return address;
	}

	/**
	 * 获取有一个端口号
	 * 
	 * @param address
	 *            "192.168.98.09:8090"
	 * @return 8090 else －1
	 */
	public static int getHttpAddressPort(String address) {

		if ("".equals(address) || null == address) {
			return -1;
		}
		address = address.replaceAll("[a-z,A-Z,//,\\\\]", "");

		int lastIndexOf = address.lastIndexOf(":");
		// address = address.replaceAll(" ", "");
		address = address.substring(lastIndexOf + 1, address.length());
		address = address.trim();

		if(!TextUtils.isEmpty(address)){
			address = address.replace(":", "");
		}
		try {
			Integer port = Integer.valueOf(address);
			return port;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 过滤规则: ^-{0,1}[0-9]{1,}(.[0-9]{1,}){0,1} 数量输入框格式是否正确
	 * 
	 * @author:zn
	 * @version:2015-1-5
	 * @param count
	 *            (数量)
	 * @return
	 */
	public static boolean isValidFormat(String count) {
		Pattern pattern = Pattern.compile("^-{0,1}[0-9]{1,}(.[0-9]{1,}){0,1}");
		Matcher mc = pattern.matcher(count);
		return mc.matches();
	}

	/**
	 * 过滤回车和空格以及制表符
	 * 
	 * @author:zn
	 * @version:2015-1-18
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 校验Tag Alias 只能是数字,英文字母和中文
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}
}
