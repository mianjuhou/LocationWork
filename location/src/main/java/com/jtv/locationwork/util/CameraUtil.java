package com.jtv.locationwork.util;

import java.io.File;
import net.majorkernelpanic.streaming.MediaStream;

import com.jtv.locationwork.activity.PainterAty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

public class CameraUtil {
	public static final int REQUESTCAMERA = 12;

	private int MASK = 0;
	private int quality = 1;
	private int editor = 2;
	private Activity act;

	private String mroot_image;

	public void startCamera(Activity act, String path) {
		this.act = act;
		mroot_image = path;
		MASK = quality;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
		act.startActivityForResult(intent, REQUESTCAMERA);
	}

	public void startCameraEditor(Activity act, String path) {
		this.act = act;
		mroot_image = path;
		MASK = editor;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
		act.startActivityForResult(intent, REQUESTCAMERA);
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUESTCAMERA == requestCode) {
			if (MASK == quality) {

				if (!TextUtils.isEmpty(mroot_image)) {
					mroot_image = YaSuoUtil.ctrlCameraImage(mroot_image);
					return true;
				}
			} else if (MASK == editor) {

				if (!TextUtils.isEmpty(mroot_image)) {
					mroot_image = YaSuoUtil.ctrlCameraImage(mroot_image);
					startEditor(act, mroot_image);
					return true;
				}
			}

		}
		return false;
	}

	public static void startEditor(Activity act, String path) {
		Intent intent = new Intent(act, PainterAty.class);
		intent.putExtra("path", path);
		act.startActivity(intent);
	}

}