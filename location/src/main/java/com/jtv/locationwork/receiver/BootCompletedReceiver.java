package com.jtv.locationwork.receiver;

import com.jtv.base.util.ApkUtils;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.util.IntentUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean installed = ApkUtils.isInstalled(context, context.getString(R.string.hlr_packagename));
		if (installed) // 如果海拉尔的现场作业安装了就不自动打开
			return;
		Intent startAPP = IntentUtils.startAPP(context, context.getPackageName());
		context.startActivity(startAPP);
	}
}
