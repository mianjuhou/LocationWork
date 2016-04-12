package com.plutus.libraryui.dialog;

import com.plutus.libraryui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadDataDialog {

	private Activity context;
	private Builder builder;
	private boolean isCaccel;

	private android.app.AlertDialog ldialog;
	private LinearLayout linear;
	private TextView title;
	private String text;

	public LoadDataDialog(Activity context) {
		this(context, true);
	}

	public void setTilte(String title) {
		this.text = title;
		if (this.title != null) {
			this.title.setText(title);
		}
	}

	public LoadDataDialog(Activity context, boolean isCaccel) {
		this(context, isCaccel, null);
	}

	public LoadDataDialog(Activity context, boolean isCaccel, String title) {

		this.context = context;
		this.text = title;
		builder = new AlertDialog.Builder(context);

		this.isCaccel = isCaccel;
	}

	public void open() {

		if (ldialog != null && ldialog.isShowing()) {
			return;
		}
		try {
			if (ldialog != null) {

				ldialog.show();
				return;
			} else {
				linear = (LinearLayout) context.getLayoutInflater().inflate(R.layout.base_loaddialog, null);
				linear.setVisibility(View.VISIBLE);
				title = (TextView) linear.findViewById(R.id.login_tip);

				if (!TextUtils.isEmpty(this.text)) {
					title.setText(this.text);
				}
				builder.setView(linear);
				ldialog = builder.create();
//				ldialog.setCanceledOnTouchOutside(isCaccel);
				ldialog.setCancelable(isCaccel);
				ldialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 关闭正在登录dialog
	public void close() {
		try {
			if (ldialog != null && ldialog.isShowing()) {
				ldialog.dismiss();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
