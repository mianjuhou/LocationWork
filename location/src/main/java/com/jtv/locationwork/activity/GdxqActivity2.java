package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jtv.hrb.locationwork.CacheCommit2;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.LineCurveCheckActivity;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.TurnoutCheck2Activity;
import com.jtv.hrb.locationwork.TurnoutCheckActivity;
import com.jtv.hrb.locationwork.comparator.ConfigRowComparator;
import com.jtv.hrb.locationwork.domain.ConfigRowName;
import com.jtv.hrb.locationwork.domain.CurveLine;
import com.jtv.locationwork.entity.CacheCommit;
import com.jtv.locationwork.entity.LineCheckJson;
import com.jtv.locationwork.entity.Locationdata;
import com.jtv.locationwork.entity.Turnout;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.Arrays;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.JsonUtil;
import com.jtv.locationwork.util.TextUtil;

/**
 * 工单详情界面线路检查
 * <p>
 *
 * @author 更生
 * @version 2016年3月13日
 */
public class GdxqActivity2 extends Activity {

	// private ItemWonum iwl;
	private ListView lv_scope;
	private List<String> scopeData;
	LineCheckJson item;
	private PopupWindow loadinPop;
	private View contentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentView = View.inflate(this, R.layout.location_gdxq2, null);
		setContentView(contentView);
		String data = getIntent().getStringExtra(Constants.INTENT_KEY_WONUM);
		if (!TextUtil.isEmpty(data)) {
			item = JsonUtil.parseObject(data, LineCheckJson.class);
		}
		// if (item != null) {
		//// iwl = new ItemWonum();
		// ItemWoListAttribute attr = new ItemWoListAttribute();
		// attr.setColn("schedstartdate");
		// attr.setDisPlayname("开始时间");
		// attr.setDisValue("2016-03-03");
		// iwl.addAttribute(attr);
		// attr.setColn("lead");
		// attr.setDisPlayname("作业负责人");
		// attr.setDisValue("李力");
		// iwl.addAttribute(attr);
		// }

		// HashMap<String, ItemWoListAttribute> data = iwl.get();
		// Set<String> keySet = data.keySet();
		// List<String> keyList=new ArrayList<String>(keySet);
		// String key=keyList.get(0);
		// schedstartdate lead

		getTextView(R.id.tv_time_item).setText("计划时间");
		getTextView(R.id.tv_time).setText(item.getSchedstartdate() + "-" + item.getStarttime());
		getTextView(R.id.tv_people_item).setText("作业负责人");
		getTextView(R.id.tv_people).setText(item.getLead());

		// if (keySet.contains("schedstartdate")) {
		// ItemWoListAttribute itemAttribute = data.get("schedstartdate");
		// getTextView(R.id.tv_time_item).setText(itemAttribute.getDisPlayname());
		// getTextView(R.id.tv_time).setText(itemAttribute.getDisValue());
		// }

		// if (keySet.contains("lead")) {
		// ItemWoListAttribute itemAttribute = data.get("lead");
		// getTextView(R.id.tv_people_item).setText(itemAttribute.getDisPlayname());
		// getTextView(R.id.tv_people).setText(itemAttribute.getDisValue());
		// }
		initListView();
		initBack();
		
		findViewById(R.id.btn_commit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				CacheCommit.getInstance().start(GdxqActivity2.this);
//				showLoadingPop();
			}
		});
	}

	public void showLoadingPop() {
		View loadingView=View.inflate(this, R.layout.page_load_loading, null);
		loadinPop = new PopupWindow(loadingView, 100, 100);
		loadinPop.setFocusable(true);
		loadinPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		loadinPop.showAtLocation(contentView, Gravity.CENTER, 0, 0);
	}
	
	public void dismissLoadingPop() {
		if(loadinPop!=null&&loadinPop.isShowing()){
			loadinPop.dismiss();
			loadinPop=null;
		}
	}
	
	private void initBack() {
		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GdxqActivity2.this.finish();
			}
		});
	}

	private void initListView() {
		initListData();
		lv_scope = (ListView) findViewById(R.id.lv_scope);
		lv_scope.setAdapter(
				new ArrayAdapter<String>(GdxqActivity2.this, android.R.layout.simple_list_item_1, scopeData));
		lv_scope.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				String name = (String) parent.getItemAtPosition(position);
				String zyType = item.getLocationdata().get(position).getClassstructureid();
				if("1322".equals(zyType)){//1322直线
					Intent lineIntent=new Intent(GdxqActivity2.this, LineCurveCheckActivity.class);
					Locationdata locationdata = item.getLocationdata().get(position);
					lineIntent.putExtra("description", locationdata.getDescription());
					lineIntent.putExtra("assetnum", locationdata.getAssetnum());
					String wonum=item.getWonum();
					lineIntent.putExtra("wonum", wonum);
					lineIntent.putExtra("startmeasure", locationdata.getStartmeasure());
					lineIntent.putExtra("endmeasure", locationdata.getEndmeasure());
					ArrayList<CurveLine> curves = (ArrayList<CurveLine>) locationdata.getLine();
					String curvesStr=JSONArray.toJSONString(curves);
					lineIntent.putExtra("curves", curvesStr);
					startActivity(lineIntent);
				}else{//1327道岔
					Intent lineIntent=new Intent(GdxqActivity2.this, TurnoutCheck2Activity.class);
					Locationdata locationdata = item.getLocationdata().get(position);
					lineIntent.putExtra("description", locationdata.getDescription());
					lineIntent.putExtra("assetnum", locationdata.getAssetnum());
					lineIntent.putExtra("wonum", item.getWonum());
					lineIntent.putExtra("assetattrid", locationdata.getTurnout().getAssetattrid());
					startActivity(lineIntent);
				}
				
//				if (name.contains("道岔")) {
//					Intent lineIntent=new Intent(GdxqActivity2.this, TurnoutCheck2Activity.class);
//					Locationdata locationdata = item.getLocationdata().get(position);
//					lineIntent.putExtra("description", locationdata.getDescription());
//					lineIntent.putExtra("assetnum", locationdata.getAssetnum());
//					lineIntent.putExtra("wonum", item.getWonum());
//					lineIntent.putExtra("assetattrid", locationdata.getTurnout().getAssetattrid());
//					startActivity(lineIntent);
//				} else {
//					Intent lineIntent=new Intent(GdxqActivity2.this, LineCurveCheckActivity.class);
//					Locationdata locationdata = item.getLocationdata().get(position);
//					lineIntent.putExtra("description", locationdata.getDescription());
//					lineIntent.putExtra("assetnum", locationdata.getAssetnum());
//					String wonum=item.getWonum();
//					lineIntent.putExtra("wonum", wonum);
//					lineIntent.putExtra("startmeasure", locationdata.getStartmeasure());
//					lineIntent.putExtra("endmeasure", locationdata.getEndmeasure());
//					ArrayList<CurveLine> curves = (ArrayList<CurveLine>) locationdata.getLine();
//					String curvesStr=JSONArray.toJSONString(curves);
//					lineIntent.putExtra("curves", curvesStr);
//					startActivity(lineIntent);
//				}
			}
		});
	}

	private void initListData() {
		scopeData = new ArrayList<String>();

		List<Locationdata> locationdata = item.getLocationdata();
		if (locationdata != null) {
			for (Locationdata data : locationdata) {
				List<CurveLine> line = data.getLine();// 为空就曲线
				Turnout turnout = data.getTurnout();
				if(turnout!=null){
					if(TextUtil.isEmpty(data.getStartmeasure())){
						scopeData.add(data.getDescription()+"   "+"道岔");
					}else{
						scopeData.add(data.getDescription()+"("+data.getStartmeasure()+"-"+data.getEndmeasure()+")   道岔");
					}
				}else{
					if(TextUtil.isEmpty(data.getStartmeasure())){
						scopeData.add(data.getDescription()+"    线路");
					}else{
						scopeData.add(data.getDescription()+"("+data.getStartmeasure()+"-"+data.getEndmeasure()+")    线路");
					}
				}
			}
		}
	}

	private TextView getTextView(int id) {
		TextView tv = (TextView) findViewById(id);
		return tv;
	}
}
