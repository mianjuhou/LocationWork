package com.jtv.hrb.locationwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TurnoutListActivity extends Activity {
	private String[] turnouts={"道岔1","道岔2","道岔3","道岔4","道岔5","道岔6"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_turnout_list);
		
		
		ListView lvTurnout= (ListView) findViewById(R.id.lv_turnout);
		lvTurnout.setAdapter(new ArrayAdapter<String>(TurnoutListActivity.this, android.R.layout.simple_list_item_1, turnouts));
		lvTurnout.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent resultIntent=new Intent();
				resultIntent.putExtra("turnoutName", turnouts[position]);
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});
	}
}
