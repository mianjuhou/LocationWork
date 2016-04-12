package com.plutus.libraryui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DiaLogUtil {
	/**
	 * 
	 * @param con
	 * @param title
	 * @param item
	 * @param listener
	 */
	public static AlertDialog showItemDiaLog(Context con, String title, String[] item,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setTitle(title);
		builder.setItems(item, listener);
		AlertDialog create = builder.create();
		create.show();
		return create;
	}
	/**
	 * 
	 * @param con
	 * @param title
	 * @param item
	 * @param check -1 代笔条目没选中
	 * @param listener
	 * @return
	 */
	public static AlertDialog  showSelectItemDiaLog(Context con, String title, String[] item,int check,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setTitle(title);
//		builder.setItems(item, listener);
	
		builder.setSingleChoiceItems(item, check, listener);
		AlertDialog create = builder.create();
		create.show();
		return create;
	}
	
	public static AlertDialog showDianLog(Context con, String title, String ok, String cancel,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		AlertDialog show = builder.setMessage(title).setPositiveButton(ok, listener).setNegativeButton(cancel, listener).show();
		return show;
	}
	

}
