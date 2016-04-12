package com.jtv.hrb.locationwork;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.jtv.hrb.locationwork.domain.CurveLine;

public class CurveListActivity extends Activity {

	private ListView lvCurve;
	private ArrayList<CurveLine> curves;
	private List<String> itemNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curve_list);
		initData();
		initView();
	}

	private void initData() {
		String curvesStr=getIntent().getStringExtra("curves");
		if(!TextUtils.isEmpty(curvesStr)){
			curves =(ArrayList<CurveLine>) JSONArray.parseArray(curvesStr, CurveLine.class);
			itemNames = new ArrayList<String>();
			for (int i = 0; i < curves.size(); i++) {
				itemNames.add(curves.get(i).getLabel());
			}
		}
	}

	private void initView() {
		lvCurve = (ListView) findViewById(R.id.lv_curve);
		lvCurve.setAdapter(new ArrayAdapter<String>(CurveListActivity.this, android.R.layout.simple_list_item_1, itemNames));
		lvCurve.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent resultIntent=new Intent();
				resultIntent.putExtra("selectindex", position);
				setResult(RESULT_OK,resultIntent);
				finish();
			}
		});
	}
}
