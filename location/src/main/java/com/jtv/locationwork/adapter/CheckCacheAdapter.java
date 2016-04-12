package com.jtv.locationwork.adapter;

import java.util.ArrayList;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.activity.CheckAty;
import com.jtv.locationwork.entity.CheckEntity;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.ShareUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CheckCacheAdapter extends BaseListAdapter<CheckEntity> {

	public CheckCacheAdapter(Context con, ArrayList<CheckEntity> list) {
		super(con, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(con, R.layout.jtv_location_check_adapter, null);
		}
		TextView tv_name = CommonViewHolder.get(convertView, R.id.tv_time);
		Button tv_type = CommonViewHolder.get(convertView, R.id.btn_look);
		CheckEntity work = (CheckEntity) list.get(position);
		int statu = work.getStatu();
		double time = work.getTime();
		String statusText = ShareUtil.getStatusText(statu);
		String textColor="\"#666666\"";
		
		if ("".equals(statusText)||Constants.READY_UPLOAD==statu) {
			statusText = "没上传";
			 textColor="\"#FF0000\"";
		}
		
		String html = "<html>状态:<font color="+textColor+">" + statusText + "</font>, 时间:" + "<font color="+textColor+">"
				+ DateUtil.getStringForTime((long) time, DateUtil.style_nyr_sf) + "</html>";

		tv_name.setText(Html.fromHtml(html));
		tv_type.setOnClickListener(new CheckOnClickListener(work));
		return convertView;
	}

	class CheckOnClickListener implements OnClickListener {
		private CheckEntity work;

		public CheckOnClickListener(CheckEntity work) {
			this.work = work;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(con, CheckAty.class);
			intent.putExtra("id", work.getCheck_id());
			con.startActivity(intent);

			if (con instanceof Activity) {
				((Activity) con).finish();
			}
		}

	}

}