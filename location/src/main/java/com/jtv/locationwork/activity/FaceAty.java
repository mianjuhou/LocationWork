package com.jtv.locationwork.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.location.libraryface.activity.FSDKlistener;
import com.jtv.location.libraryface.activity.MainActivity;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.LocalCache;
import com.jtv.locationwork.httputil.HttpApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.imp.CacheFace;
import com.jtv.locationwork.util.Base64UtilCst;
import com.jtv.locationwork.util.Cache.Entry;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.DiskBasedCache;
import com.jtv.locationwork.util.DiskCacheUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TTSUtil;
import com.jtv.locationwork.util.TextUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class FaceAty extends MainActivity implements FSDKlistener {
	public String wonum;
	public String gps_wd1;
	public String gps_jd1;
	public String crewid;
	public String siteid;
	public String persion64;
	private TTSUtil tts;

	private int state = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		tts = new TTSUtil(this);
		super.onCreate(savedInstanceState);
		allowModify = true;
		getIntentParmter();
	}

	@Override
	public String getLisenseCode() {
		return SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.FACE_LICENCE,
				"uKTAEoswrmpDk4M1aTuxTEzPltWjuCdpJH8RiK+kNKdjr0uGHVtoA3mHPuBDM+6KZeOINo2UwdXpXmoiF9FVx2hlrJQhTDUSrD4Dwajvb8B8ttUtqk0l9tho1due6fi7n/p9s1ZhGL1XxUNsRlNT4edDpx1TATwUi7PU29woF10=");
	}

	@Override
	protected void onClickError(DialogInterface dialogInterface, int i) {
		HomeFragment.exit(this);
	}

	@Override
	public String getDateBaseAddres() {
		return CreatFileUtil.getRootFile(this).getAbsolutePath() + File.separator + Constants.FACEDATEBASE;
	}

	public void getIntentParmter() {
		Intent intent = getIntent();
		wonum = intent.getStringExtra(CoustomKey.WONUM);
		double[] gps = SpUtiles.BaseInfo.getGps();
		gps_jd1 = gps[0] + "";
		gps_wd1 = gps[1] + "";
		crewid = GlobalApplication.getAreaIdOrShopId();
		siteid = GlobalApplication.siteid;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mIsFailed) {
			onClickError(null, 1);
		}
	}

	@Override
	public void successfule(String name) {
		if (TextUtil.isEmpty(siteid)) {
			return;
		}

		if (state == 1) {
			return;
		}
		state = 1;

		if (tts != null && !tts.isSpeaking()) {
			tts.speak("识别成功:" + name);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		upload(name);
	}

	public void upload(final String persion1name) {
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		persion64 = Base64UtilCst.encodeUrl(persion1name);
		paramsMap.put("wonum", wonum);
		paramsMap.put("personid", persion64);
		paramsMap.put("geox", gps_wd1);
		paramsMap.put("geoy", gps_jd1);
		paramsMap.put("crewid", crewid);
		paramsMap.put("attid", GlobalApplication.attid);
		paramsMap.put("siteid", siteid);
		// paramsMap.put("method", methods);

		TrackAPI.uploadFace(GlobalApplication.mContext, new ObserverCallBack() {

			@Override
			public void back(String data, int method, Object obj) {
				if (tts != null) {
					tts.speak("数据已上传");
				}
				cacheFace(persion1name);
			}

			@Override
			public void badBack(String error, int method, Object obj) {
				BaseDaoImpl<LocalCache> localCacheDao = DBFactory.getLocalCacheDao(GlobalApplication.mContext);
				LocalCache localCache = new LocalCache();
				localCache.setKey("wonum");
				localCache.setKey1("personid");
				localCache.setKey2("geox");
				localCache.setKey3("geoy");
				localCache.setKey4("crewid");
				localCache.setKey5("attid");
				localCache.setKey6("siteid");
				// localCache.setKey7("method");
				localCache.setClasspath(CacheFace.class);
				localCache.setIp(HttpApi.Http_Interface_uploadFace);
				localCache.setValue(wonum);
				localCache.setValue1(persion64);
				localCache.setValue2(gps_wd1);
				localCache.setValue3(gps_jd1);
				localCache.setValue4(crewid);
				localCache.setValue5(GlobalApplication.attid);
				localCache.setValue6(siteid);
				// localCache.setValue7(methods);

				long insert = localCacheDao.insert(localCache);
				localCache.setCache_id((int) insert);

				if (tts != null) {
					tts.speak("数据上传失败,已缓存到本地");
				}
			}
		}, paramsMap);
	}

	// 缓存人脸数据方便下次查看
	private void cacheFace(String person1name) {

		person1name = person1name + "   考勤时间: "
				+ DateUtil.getCurrDateFormat(DateUtil.style_nyr + "/" + DateUtil.style_sf);

		// 获取一个缓存
		DiskBasedCache cache = DiskCacheUtil.getCache(this);

		// 获取原来的缓存
		Entry entry = cache.get(DiskCacheUtil.FACE_KEY);// 获取缓存

		// 创建一个新缓存
		Entry entry2 = DiskCacheUtil.getEntry(person1name, 8);// 创建新的缓存

		Map<String, String> responseHeaders = null;

		if (entry != null) {

			if (!entry.isExpired()) {// 如果没有过期

				responseHeaders = entry.responseHeaders;
				responseHeaders.put(person1name, person1name);

			} else {
				cache.remove(DiskCacheUtil.FACE_KEY);
			}

		}

		if (responseHeaders == null) {
			responseHeaders = new HashMap<String, String>();
			responseHeaders.put(person1name, person1name);
		}

		entry2.responseHeaders = responseHeaders;

		// 保存缓存
		cache.put(DiskCacheUtil.FACE_KEY, entry2);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tts.onDestroy();
	}

	@Override
	public void failed() {

	}
}
