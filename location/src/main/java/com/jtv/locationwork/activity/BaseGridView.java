package com.jtv.locationwork.activity;

import com.jtv.base.activity.BaseAty;
import com.jtv.hrb.locationwork.R;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

public abstract class BaseGridView extends BaseAty {

	protected GridView mGridView;

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.base_gridview);
		mGridView = (GridView) findViewById(R.id.grid);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

}
