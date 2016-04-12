package com.jtv.locationwork.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.kobjects.base64.Base64;

import android.util.Log;
/**
 * 关于服务器解析不到数据解决方案
 * @author beyound
 * @email  whatl@foxmail.com
 * @date   2015年7月14日
 */
public class Base64UtilCst{
	
	/**
	 * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	 * 
	 * @author zhaoyj
	 * @date 2015-3-26
	 * @param woNum
	 * @param imageUrl
	 * @return
	 * 
	 */
	public static String GetImageStr(String imageUrl) {
		String imgFile = imageUrl;// "C:\Users\Administrator\Desktop\DSC_0058.JPG";//待处理的图片
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (data == null || TextUtil.isEmpty(imgFile)) {
			return imageUrl;
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	}

	/**
	 * 中文编码
	 * 
	 * @param chinese
	 * @return
	 */
	private static String encode(String chinese) {
		String str = null;
		try {
			if (TextUtil.isEmpty(chinese)) {
				Log.e("Base64Util", "空指针异常" + chinese);
				return chinese;
			}
			return str = Base64.encode(chinese.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 中文编码,并对编码后的字符串进行处理
	 * ＋－／特殊处理服务器作出相对应的解析
	 * 
	 * @param chinese
	 * @return
	 */
	public static String encodeUrl(String chinese) {
		String str = null;
		try {
			if (TextUtil.isEmpty(chinese)) {
				Log.e("Base64Util", "空指针异常" + chinese);
				return chinese;
			}
			str = Base64.encode(chinese.getBytes("UTF-8"));
			str = str.replace("/", "-");
			str = str.replace("+", "_");
			str = str.replace("=", ".");
			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 中文解码
	 * 
	 * @param decodeChinese
	 * @return
	 */
	public static String decode(String decodeChinese) {
		byte[] decode = Base64.decode(decodeChinese);
		return decode.toString();
	}

	/**
	 * 中文解码
	 * 
	 * @param decodeChinese
	 * @return
	 */
	public static String decodeUrl(String decodeChinese) {
		return decodeChinese.toString();
	}

}
