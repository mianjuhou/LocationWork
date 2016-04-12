package com.jtv.locationwork.httputil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.jtv.locationwork.util.LogUtil;

public class WebServiceUtil {

	public static SoapObject defaultSoap(String nameSpace, String methodname) {
		SoapObject rpc = new SoapObject(nameSpace, methodname);
		return rpc;
	}

	public static String request(String endpoint, SoapObject rpc) {

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
		envelope.bodyOut = rpc;
		// 设置是否调用的是dotNet开发的WebService
		envelope.dotNet = false;
		// 等价于envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE transport = new HttpTransportSE(endpoint, 12000);// 执行12秒
		Object object = null;
		try {
			// 调用WebService
			transport.call(null, envelope);
			// 获取返回的数据
			object = envelope.getResponse();
		} catch (Exception e) {// 有时候会报错
			e.printStackTrace();
			LogUtil.e("[ web-services method --> " + rpc.getName() + " ] 访问网络报错: " + e.toString());
		}

		if (object != null) {
			String result = object.toString();
			return result;
		}
		// 获取返回的结果
		return null;
	}

}
