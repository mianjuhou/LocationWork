package com.jtv.locationwork.httputil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;

public class HttpUtil {
	public static HttpUtil httpUtil = null;
	private static String TAG = "HttpUtil";
	// 连接超时设置
	public static int HttpTimeOut_SHORT = 30 * 1000;// 设置超时的时间是30s

	public static HttpUtil getHttpUtil() {
		if (httpUtil == null) {
			httpUtil = new HttpUtil();
		}
		return httpUtil;
	}

	/**
	 * POST方式 通过POST方式上传图片
	 *
	 * @author:zn
	 * @version:2015-2-4
	 * @param url
	 * @param imageUrl
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public JSONObject uploadImageByPost(String url, String imageUrl) throws Exception, JSONException {
		try {
			StringBuffer sb = new StringBuffer();
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost(url);
			File file = new File(imageUrl);

			FileEntity reqEntity = new FileEntity(file, "binary/octet-stream");

			httppost.setEntity(reqEntity);
			reqEntity.setContentType("binary/octet-stream");
			HttpResponse httpResponse = httpclient.execute(httppost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				sb.append(EntityUtils.toString(httpEntity, "utf-8"));
				httpEntity.consumeContent();
				httpclient.getConnectionManager().shutdown();
				return new JSONObject(sb.toString());
			}
		} catch (ClientProtocolException e) {
			throw new Exception("无法连接服务,请刷新重试！");
		} catch (IOException e) {
			throw new Exception("文件读写异常!");
		}
		return null;
	}

	/**
	 * 构建上传的文本信息
	 *
	 * @author:zn
	 * @version:2015-2-16
	 * @param params
	 * @return
	 */
	public static String buildSubmitText(Map<String, String> params) {
		String BOUNDARY = "*****";
		String PREFIX = "--", LINEND = "\r\n";
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
			sb.append(LINEND);
			if (entry.getValue() != null) {
				sb.append(entry.getValue());
			} else {
				sb.append("");
			}
			sb.append(LINEND);
		}
		return sb.toString();
	}

	/**
	 *
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param time
	 *            时间 如果小于0代表默认连接超时30s
	 * @return
	 */
	public static String post(String url, Map<String, String> params, int time) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;
		HttpPost post = postForm(url, params);

		body = invoke(httpclient, post, time);

		httpclient.getConnectionManager().shutdown();
		post = null;
		httpclient = null;
		params = null;
		return body;
	}

	/**
	 *
	 * @param url
	 * @param time
	 *            time 时间 如果小于0代表默认连接超时30s
	 * @return
	 */
	public static String get(String url, int time) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;
		HttpGet get = new HttpGet(url);
		body = invoke(httpclient, get, time);
		httpclient.getConnectionManager().shutdown();
		return body;
	}

	private static String invoke(DefaultHttpClient httpclient, HttpUriRequest httpost, int time) {

		HttpResponse response = sendRequest(httpclient, httpost, time);
		if (response == null) {
			return "";
		}
		String body = "";
		try {
			body = paseResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return body;
	}

	private static String paseResponse(HttpResponse response) {
		System.out.println("get response from http server..");
		if (response == null) {
			return "";
		}
		HttpEntity entity = response.getEntity();

		System.out.println("response status_: " + response.getStatusLine());
		String charset = EntityUtils.getContentCharSet(entity);
		System.out.println(charset);
		String body = null;
		try {
			body = EntityUtils.toString(entity);
			entity.consumeContent();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		entity = null;
		charset = null;
		return body;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient, HttpUriRequest httpost, int time) {

		HttpParams params = httpclient.getParams();
		if (params == null) {
			params = new BasicHttpParams();
		}
		if (time < 0) {
			time = HttpTimeOut_SHORT;
		}
		httpclient.getParams().setParameter("http.connection.timeout", time);
		httpclient.getParams().setParameter("http.socket.timeout", time);
		HttpConnectionParams.setConnectionTimeout(params, time);
		HttpConnectionParams.setSoTimeout(params, time);

		// params.setParameter(
		// CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		// params.setParameter(
		// CoreConnectionPNames.SO_TIMEOUT, 30000);
		// httpclient.setParams(params);
		System.out.println("execute post...");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			System.out.println("response null..没开启网络会出现null.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		params = null;
		return response;
	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpost = new HttpPost(url);
		httpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		BasicNameValuePair basicNameValuePair = null;
		for (String key : keySet) {
			basicNameValuePair = new BasicNameValuePair(key, params.get(key));
			nvps.add(basicNameValuePair);
		}
		System.out.println("set utf-8 form entity to httppost");
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		nvps = null;
		basicNameValuePair = null;
		keySet = null;
		return httpost;
	}

	/**
	 * POST方式上传数据
	 *
	 * @author:zn
	 * @version:2015-2-16
	 * @param url
	 * @param params
	 * @param files
	 * @return
	 * @throws AppError
	 */
	public static String httpPOST(String url, Map<String, String> params, Map<String, File> files) throws Exception {
		StringBuffer responseSb = null;
		String BOUNDARY = "*****";
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		if (url.endsWith("?") || url.endsWith("&"))
			url = url.substring(0, url.length() - 1);
		// url = "http://192.168.0.118:8080/operation/VideoServlet";
		URL uri;
		HttpURLConnection conn;
		try {
			uri = new URL(url);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(20 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			conn.setConnectTimeout(20 * 1000);
			// 首先组拼文本类型的参数
			String textSb = buildSubmitText(params);

			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(textSb.getBytes());
			// 发送文件数据
			if (files != null) {
				String CHARSET = "UTF-8";
				StringBuilder sb;
				for (Map.Entry<String, File> file : files.entrySet()) {
					sb = new StringBuilder();
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINEND);
					sb.append("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
							+ file.getValue().getName() + "\"" + LINEND);
					sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
					sb.append(LINEND);
					outStream.write(sb.toString().getBytes());
					InputStream is = new FileInputStream(file.getValue());
					byte[] buffer = new byte[3072];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					is.close();
					outStream.write(LINEND.getBytes());
					buffer = null;
					sb = null;
					is = null;
				}

			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			// int res = conn.getResponseCode();
			InputStream in = conn.getInputStream();
			responseSb = new StringBuffer();
			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String tempLine;
				while ((tempLine = br.readLine()) != null) {
					responseSb.append(tempLine);
				}
				in.close();
				br.close();
			}
			outStream.close();
			end_data = null;
		} catch (MalformedURLException e) {
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
		if (conn != null) {
			conn.disconnect();
		}
		return responseSb.toString();
	}

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
	// public static String GetImageStr(String imageUrl) {
	// String imgFile = imageUrl;//
	// "C:\Users\Administrator\Desktop\DSC_0058.JPG";//待处理的图片
	// InputStream in = null;
	// byte[] data = null;
	// // 读取图片字节数组
	// try {
	// in = new FileInputStream(imgFile);
	// data = new byte[in.available()];
	// in.read(data);
	// in.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// if (data == null || TextUtil.isEmpty(imgFile)) {
	// Log.e(TAG, "数据为null");
	// }
	// // 对字节数组Base64编码
	// BASE64Encoder encoder = new BASE64Encoder();
	// return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	// }
	//
	// /**
	// * 判断当前是否有网络
	// *
	// * @param context
	// * @return
	// */
	// public static boolean isNetworkConnected(Context context) {
	// if (context != null) {
	// ConnectivityManager mConnectivityManager = (ConnectivityManager) context
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
	// if (mNetworkInfo != null) {
	// return mNetworkInfo.isAvailable();
	// }
	// }
	// return false;
	// }
	//
}
