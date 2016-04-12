package com.plutus.libraryui.dialog;

import com.plutus.libraryui.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class RoateProgressDialog extends Dialog {

	private ImageView ivroate = null;

	public RoateProgressDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public RoateProgressDialog(Context context) {
		super(context);
		init();
	}

	private void init() {
		setContentView(R.layout.load_progress_dialog);
		ivroate = (ImageView) findViewById(R.id.img);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void show() {
		if (ivroate == null) {
			ivroate = (ImageView) findViewById(R.id.img);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_roate_animation);
		ivroate.startAnimation(hyperspaceJumpAnimation);
		super.show();
	}

	@Override
	public void dismiss() {
		if (ivroate == null) {
			ivroate = (ImageView) findViewById(R.id.img);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ivroate.clearAnimation();
		super.dismiss();
	}
}
