package com.jtv.locationwork.util;

import com.jtv.hrb.locationwork.R;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

public class AnimationUtils {
	
	public static AnimationDrawable playSoundAnim(View view) {
		view.setBackgroundResource(R.anim.anim_playsound);
		AnimationDrawable mAnim = (AnimationDrawable) view.getBackground();
		mAnim.stop();
		mAnim.start();
		return mAnim;
	}
	public static AnimationDrawable recordAnim(View view){
		view.setBackgroundResource(R.anim.animation_record);
		AnimationDrawable mAnim = (AnimationDrawable) view.getBackground();
		return mAnim;
	}
}
