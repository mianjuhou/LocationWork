package com.jtv.locationwork.util;

import com.gitonway.niftydialogeffects.widget.niftydialogeffects.Effectstype;
import com.jtv.hrb.locationwork.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * 一个简单的提及dialog
 * 
 * @author beyound
 *
 */
public class SimpleDialog {

	// 一个简单的提示dialog
	public static void showSimpleDialog(Context con, DialogInterface.OnClickListener listener, String title,
			String message, String ok, String cancel) {
		showSimpleDialog(con, listener, title, message, ok, cancel, Effectstype.Fliph);
	}
	
	// 一个简单的提示dialog
		public static void showSimpleDialog(Context con, DialogInterface.OnClickListener listener, String title,
				String message, String ok, String cancel,Effectstype eff) {

			AlertDialog.Builder builder = new AlertDialog.Builder(con);
			builder.setTitle(title);
			builder.setMessage(message).setPositiveButton(ok, listener).setNegativeButton(cancel, listener);
			AlertDialog create = builder.create();
			AnimationDialogUtil.startAnimation(eff, create);
			create.show();

		}

}
