package com.jtv.locationwork.imp;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.Effectstype;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.NiftyDialogBuilder;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.activity.RtspVideoAty;
import com.jtv.locationwork.listener.SockMessageCallBack;
import com.jtv.locationwork.socket.SockeThread;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.PDADeviceInfoService;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

//打开实习视频
public class SockStartVideo implements SockMessageCallBack, OnClickListener {
	protected Context con;

	protected SockeThread sock;

	protected Object obj;

	private NiftyDialogBuilder dialog;

	@Override
	public void doSomeThing(SockeThread sock, Context con, String data, Object obj) {
		this.con = con;
		this.obj = obj;
		this.sock = sock;

		dialog = NiftyDialogBuilder.getInstance(con);
		dialog.withTitle(con.getString(R.string.dis_request_video_title));
		dialog.withTitleColor("#666666");
		dialog.withMessage(con.getString(R.string.dis_request_video));
		dialog.withMessageColor("#666666");
		dialog.withButton1Text(con.getString(R.string.ok)).withEffect(Effectstype.Shake);
		dialog.withButton2Text(con.getString(R.string.cancel));
		dialog.withDialogBackGroundColor("#ffffff");
		dialog.withButtonDrawable(R.drawable.selector_btn_pressblue_upgray);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setButton2Click(this);
		dialog.setButton1Click(this);
		dialog.show();

		// AlertDialog showDianLog = DiaLogUtil.showDianLog(con, "打开实时视频界面",
		// "确认", "取消", this);
		// showDianLog.setCanceledOnTouchOutside(false);
	}

	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// switch (which) {
	// case DialogInterface.BUTTON1:
	// if (sock != null) {
	// try {
	// sock.sendSocket(sendWhatingVideo("1"));// 通过
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// Intent intent = new Intent(con, RtspVideoAty.class);
	// try {
	// con.startActivity(intent);
	// } catch (Exception e) {
	// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// con.startActivity(intent);
	// }
	// dialog.dismiss();
	//
	// break;
	//
	// case DialogInterface.BUTTON2:
	// dialog.dismiss();
	// if (sock != null) {
	// try {
	// sock.sendSocket(sendWhatingVideo("2"));// 不通过
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// break;
	// }
	// }

	public String sendWhatingVideo(String value) {
		JSONObject jsonObject = new JSONObject();
		try {
			PDADeviceInfoService pdaDeviceInfoService = new PDADeviceInfoService(con);
			String deviceId = pdaDeviceInfoService.getDeviceId();
			jsonObject.put(Constants.MESSAGEID, obj);
			jsonObject.put(Constants.MESSAGEID_VALUE, value);
			jsonObject.put(Constants.PAD_ATTID, deviceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	@Override
	public void doOver(SockeThread sock, Context con, String data, Object obj) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			if (sock != null) {
				try {
					sock.sendSocket(sendWhatingVideo("1"));// 通过
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent intent = new Intent(con, RtspVideoAty.class);
			try {
				con.startActivity(intent);
			} catch (Exception e) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				con.startActivity(intent);
			}
			dialog.dismiss();

			break;

		case R.id.button2:
			dialog.dismiss();
			if (sock != null) {
				try {
					sock.sendSocket(sendWhatingVideo("2"));// 不通过
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

}
