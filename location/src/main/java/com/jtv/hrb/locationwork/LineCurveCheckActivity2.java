package com.jtv.hrb.locationwork;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LineCurveCheckActivity2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curve_check);
		initView();
	}
	
	private void initView() {
		getButton(R.id.btn_next_pointer).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	private Button getButton(int resId){
		Button btn=(Button) findViewById(resId);
		return btn;
	}
}
