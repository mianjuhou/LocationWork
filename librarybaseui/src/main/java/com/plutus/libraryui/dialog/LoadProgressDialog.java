package com.plutus.libraryui.dialog;

import com.plutus.libraryui.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

@SuppressLint("NewApi")
public class LoadProgressDialog {
	private RoateProgressDialog create;
	private Context con;

	public LoadProgressDialog(Context con,boolean isBack) {
		this.con = con;
		create = new RoateProgressDialog(con,R.style.FullHeightDialog);
//		create.setView(v);
		create.setCancelable(isBack);
//		create = create.create();
	}
	

	public LoadProgressDialog(Context con) {
		this(con,true);
	}


	public void show() {

		if (create != null) {
			if (create.isShowing()) {
				return;
			}
			
			create.show();
		}

	}

	public void close() {

		if (create != null) {
			create.dismiss();
		}

	}

	public Dialog getDiaLog() {
		return create;
	}

}
