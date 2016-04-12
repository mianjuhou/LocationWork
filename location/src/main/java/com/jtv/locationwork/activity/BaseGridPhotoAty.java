package com.jtv.locationwork.activity;

import com.jtv.base.activity.BaseAty;
import com.jtv.hrb.locationwork.R;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public abstract class BaseGridPhotoAty extends BaseAty {
	protected GridView mGrid;

	protected LinearLayout mLinearlayHead;

	protected RelativeLayout mRellayBottom;

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.base_grid_photo);
		mGrid = (GridView) findViewById(R.id.mgrid);
		mLinearlayHead = (LinearLayout) findViewById(R.id.ll_mhead);
		mRellayBottom = (RelativeLayout) findViewById(R.id.rl_mbottom);
	}

}
