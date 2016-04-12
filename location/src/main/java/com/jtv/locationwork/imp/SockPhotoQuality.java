package com.jtv.locationwork.imp;


import org.json.JSONObject;

import com.jtv.base.ui.SnackBar;
import com.jtv.base.util.CollectionActivity;
import com.jtv.base.util.UToast;
import com.jtv.locationwork.listener.SockMessageCallBack;
import com.jtv.locationwork.socket.SockeThread;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;

public class SockPhotoQuality implements SockMessageCallBack {
	String messageValue;

	public SockPhotoQuality(String messageValue) {
		this.messageValue = messageValue;
	}

	private Vibrator vibrator;

	private SnackBar sb;

	private String snackBarName;

	private Context context;

	private boolean vibratorClose = false;

	String wonum;
	String img;
	String type;
	@Override
	public void doSomeThing(SockeThread sock, Context con, String data, Object obj) {
		this.context = con;
//		{"messagevalue":"行车-开车前-卡控项目人员到位情况-不通过","img":"1","type":"3","wonum":"run3182"}
		try {
			JSONObject mMessage = new JSONObject(messageValue);
			wonum=mMessage.optString("wonum");
			type=mMessage.optString("type");
			img=mMessage.optString("img");
			messageValue=mMessage.optString("messagevalue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
		 */
		vibrator = (Vibrator) con.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 300, 1000, 400, 1000 };   // 停止 开启 停止 开启
		vibrator.vibrate(pattern, 2);
		if (con != null) {
			if (sb != null && sb.isShowing()) {
				sb.dismiss();
			}
			snackBarName = "知道了";

//			if (messageValue.contains("不通过")) {
//				snackBarName = "重拍照片";
//			}
			Activity topAty = CollectionActivity.getTopActivity();
//			if (topAty instanceof WoNumTakePhoto) {
//				snackBarName = "知道了";
//			}
			sb = new SnackBar((Activity) con, messageValue, snackBarName, new OnClickListener() {

				@Override
				public void onClick(View v) {}
			});
			// sb.setDismissTimer(30*1000);//默认是三秒中
			sb.setIndeterminate(true);
			sb.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					if (vibrator != null)
						vibrator.cancel();
				}
			});
			try {
				sb.show();
			} catch (Exception e) {
				System.out.println(e);
				UToast.makeLongTxt(con, messageValue);
			}

			if (vibratorClose) {
				return;
			}
			new Thread() {// 关闭震动
				public void run() {
					try {
						vibratorClose = true;
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (vibrator != null) {
						vibrator.cancel();
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (vibrator != null) {
						vibrator.cancel();
					}
					vibratorClose = false;
				};
			}.start();
		}
	}

	@Override
	public void doOver(SockeThread sock, Context con, String data, Object obj) {
		// if (sock != null) {
		// JSONObject jsonObject = new JSONObject();
		// try {
		// jsonObject.put(Constants.MESSAGEID, obj);
		// try {
		// sock.sendSocket(jsonObject.toString());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// }
	}

}
