package com.jtv.hrb.locationwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jtv.hrb.locationwork.comparator.ConfigRowComparator;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.ConfigRowName;
import com.jtv.hrb.locationwork.domain.TurnoutUiData;
import com.jtv.hrb.locationwork.fragment.TurnoutConfigFragment;
import com.jtv.hrb.locationwork.fragment.TurnoutPage4Fragment;
import com.jtv.hrb.locationwork.fragment.TurnoutPage4Fragment2;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;

public class TurnoutCheck2Activity extends Activity {
	public ArrayList<TurnoutUiData> uiDatas=new ArrayList<TurnoutUiData>();
	private TurnoutConfigFragment configFragment1;
	private TurnoutConfigFragment configFragment2;
	private TurnoutConfigFragment configFragment3;
	private TurnoutPage4Fragment2 configFragment4;
	public StaticCheckDbService service;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentView = View.inflate(this,R.layout.activity_turnout_check, null);
		setContentView(contentView);
		service=new StaticCheckDbService(this);
		initData();
	}
	
	private void afterData() {
		configFragment1=new TurnoutConfigFragment(uiDatas.get(0));
		configFragment2=new TurnoutConfigFragment(uiDatas.get(1));
		configFragment3=new TurnoutConfigFragment(uiDatas.get(2));
		configFragment4=new TurnoutPage4Fragment2(uiDatas.get(3));
		initView();
		changePart(0);
		
		dismissLoadingPop();
	}
	
	private void initView() {
		etLineName = (TextView) findViewById(R.id.tv_line_name);
		etLineName.setText(description);
		TextView tvTurnoutNum=(TextView) findViewById(R.id.tv_turnout_num);//道岔编号
		tvTurnoutNum.setText(assetnum);
		TextView tvTurnoutType=(TextView) findViewById(R.id.tv_turnout_type);//道岔型号
		tvTurnoutType.setText(assetattrid);
		
		etPartName = (TextView) findViewById(R.id.part_name);
		findViewById(R.id.previous_part).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentPageIndex>0){
					//更新本页同步标识为0
					if(currentPageIndex!=3){
						TurnoutConfigFragment configFragment=(TurnoutConfigFragment) currentFragment;
						configFragment.updateSyncState();
					}else{
						TurnoutPage4Fragment2 configFragment=(TurnoutPage4Fragment2) currentFragment;
						configFragment.updateSyncState();
					}
					
					changePart(currentPageIndex-1);
				}
			}
		});
		
		findViewById(R.id.next_part).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentPageIndex<3){
					//更新本页同步标识为0
					if(currentPageIndex!=3){
						TurnoutConfigFragment configFragment=(TurnoutConfigFragment) currentFragment;
						configFragment.updateSyncState();
					}else{
						TurnoutPage4Fragment configFragment=(TurnoutPage4Fragment) currentFragment;
						configFragment.updateSyncState();
					}
					
					changePart(currentPageIndex+1);
				}
			}
		});
	}
	private int currentPageIndex=-1;
	private Fragment currentFragment;
	private TextView etPartName;
	public String assetnum;
	public String wonum;
	public String description;
	private TextView etLineName;
	private String assetattrid;
	private void changePart(int index){
		if(index!=currentPageIndex){
			if(index==0){
				currentFragment=configFragment1;
				currentPageIndex=0;
			}else if(index==1){
				currentFragment=configFragment2;
				currentPageIndex=1;
			}else if(index==2){
				currentFragment=configFragment3;
				currentPageIndex=2;
			}else if(index==3){
				currentFragment=configFragment4;
				currentPageIndex=3;
			}
			etPartName.setText(uiDatas.get(index).partName);
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.change_view, currentFragment);
			fragmentTransaction.commit();
		}
	}
	private void initData() {
		assetnum = getIntent().getStringExtra("assetnum");
		wonum = getIntent().getStringExtra("wonum");
		description=getIntent().getStringExtra("description");
		assetattrid = getIntent().getStringExtra("assetattrid");
		
		once=true;
		TrackAPI.queryPersonCheckWoInfoConfig(getApplicationContext(), new ObserverCallBack() {
			@Override
			public void badBack(String error, int method, Object obj) {
				getDefaultConfigDatas();
				afterData();
			}
			@Override
			public void back(String data, int method, Object obj) {
				if(method==MethodApi.HTTP_QUERY_CHECK_CONFIG){
					try {
						getNetworkConfigDatas(data);
					} catch (Exception e) {
						e.printStackTrace();
						getDefaultConfigDatas();
					}
				}else{
					getDefaultConfigDatas();
				}
				afterData();
			}
			
		}, GlobalApplication.siteid);
	}
	private void getNetworkConfigDatas(String data) {
		List<ConfigRowName> configRowData = JSON.parseArray(data, ConfigRowName.class);
//			List<ConfigRowName> straightLineConfig=getRowConfig("", configRowData);
//			List<ConfigRowName> turnoutZjConfig=getRowConfig("支距",configRowData);
		
		TurnoutUiData uiData1 = new TurnoutUiData();
		uiData1.partName="转辙部分";
		uiData1.partOrder=0;
		List<ConfigRowName> turnoutZzConfig=getRowConfig("转辙部位",configRowData);
		for (ConfigRowName configRowName : turnoutZzConfig) {
			uiData1.rowNames.add(configRowName.getDESCRIPTION());
			uiData1.rowNums.add(configRowName.getCP1NANUMCFGROWNUM());
		}
		uiData1.rowNum=uiData1.rowNames.size();
		
		TurnoutUiData uiData2 = new TurnoutUiData();
		uiData2.partName="导曲线部分";
		uiData2.partOrder=1;
		List<ConfigRowName> turnoutDqxConfig=getRowConfig("导曲线部分",configRowData);
		for (ConfigRowName configRowName : turnoutDqxConfig) {
			uiData2.rowNames.add(configRowName.getDESCRIPTION());
			uiData2.rowNums.add(configRowName.getCP1NANUMCFGROWNUM());
		}
		uiData2.rowNum=uiData2.rowNames.size();
		
		TurnoutUiData uiData3 = new TurnoutUiData();
		uiData3.partName="辙叉部分";
		uiData3.partOrder=2;
		List<ConfigRowName> turnoutZcConfig=getRowConfig("辙叉部分",configRowData);
		for (ConfigRowName configRowName : turnoutZcConfig) {
			uiData3.rowNames.add(configRowName.getDESCRIPTION());
			uiData3.rowNums.add(configRowName.getCP1NANUMCFGROWNUM());
		}
		uiData3.rowNum=uiData3.rowNames.size();
		uiData3.editMatrix.add(getRowEditable(true,true));
		uiData3.editMatrix.add(getRowEditable(true,true));
		uiData3.editMatrix.add(getRowEditable(true,false));
		uiData3.editMatrix.add(getRowEditable(true,false));
		uiData3.editMatrix.add(getRowEditable(true,true));
		uiData3.editMatrix.add(getRowEditable(true,true));
		uiData3.editMatrix.add(getRowEditable(true,false));
		uiData3.editMatrix.add(getRowEditable(true,false));
		uiData3.editMatrix.add(getRowEditable(true,false));
		uiData3.editMatrix.add(getRowEditable(true,false));
		
		TurnoutUiData uiData4 = new TurnoutUiData();
		uiData4.partName="支距";
		uiData4.partOrder=3;
		uiData4.rowNum=10;
		List<ConfigRowName> turnoutZjConfig=getRowConfig("支距",configRowData);
		uiData4.rowNums.add(turnoutZjConfig.get(0).getCP1NANUMCFGROWNUM());
		uiDatas.add(uiData1);
		uiDatas.add(uiData2);
		uiDatas.add(uiData3);
		uiDatas.add(uiData4);
	}
	
	private List<ConfigRowName> getRowConfig(String partType,List<ConfigRowName> configRowData) {
		List<ConfigRowName> rows=new ArrayList<ConfigRowName>();
		for (ConfigRowName row : configRowData) {
			if(partType.equals(row.getPARTTYPE())){
				rows.add(row);
			}
		}
		Collections.sort(rows, new ConfigRowComparator());
		return rows;
	}
	private void getDefaultConfigDatas() {
		String json="[{\"LINENUM\":1,\"HASLD\":0,\"DESCRIPTION\":\"接头\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1001\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":1,\"ROWSTAMP\":\"698858400\"},{\"LINENUM\":3,\"HASLD\":0,\"DESCRIPTION\":\"中间\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1003\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":3,\"ROWSTAMP\":\"698858402\"},{\"LINENUM\":2,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1002\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":2,\"ROWSTAMP\":\"698858401\"},{\"LINENUM\":4,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1004\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":4,\"ROWSTAMP\":\"698858403\"},{\"LINENUM\":5,\"HASLD\":0,\"DESCRIPTION\":\"接头\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1005\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":5,\"ROWSTAMP\":\"698858413\"},{\"LINENUM\":6,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1006\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":6,\"ROWSTAMP\":\"698858414\"},{\"LINENUM\":7,\"HASLD\":0,\"DESCRIPTION\":\"中间\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1007\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":7,\"ROWSTAMP\":\"698858415\"},{\"LINENUM\":8,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1008\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":8,\"ROWSTAMP\":\"698858416\"},{\"LINENUM\":1,\"HASLD\":0,\"DESCRIPTION\":\"基本轨接头\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1009\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":9,\"ROWSTAMP\":\"698858417\"},{\"LINENUM\":2,\"HASLD\":0,\"DESCRIPTION\":\"尖轨尖端\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1010\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":10,\"ROWSTAMP\":\"698858418\"},{\"LINENUM\":3,\"HASLD\":0,\"DESCRIPTION\":\"尖轨中\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1011\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":11,\"ROWSTAMP\":\"698858419\"},{\"LINENUM\":4,\"HASLD\":0,\"DESCRIPTION\":\"尖轨跟端（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1012\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":12,\"ROWSTAMP\":\"698858420\"},{\"LINENUM\":5,\"HASLD\":0,\"DESCRIPTION\":\"尖轨跟端（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1013\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":13,\"ROWSTAMP\":\"698858421\"},{\"LINENUM\":6,\"HASLD\":0,\"DESCRIPTION\":\"直线（前）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1014\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":14,\"ROWSTAMP\":\"698858422\"},{\"LINENUM\":7,\"HASLD\":0,\"DESCRIPTION\":\"直线（中）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1015\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":15,\"ROWSTAMP\":\"698858423\"},{\"LINENUM\":8,\"HASLD\":0,\"DESCRIPTION\":\"直线（后）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1016\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":16,\"ROWSTAMP\":\"698858424\"},{\"LINENUM\":9,\"HASLD\":0,\"DESCRIPTION\":\"导曲线(前)\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1017\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":17,\"ROWSTAMP\":\"698858425\"},{\"LINENUM\":10,\"HASLD\":0,\"DESCRIPTION\":\"导曲线(中)\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1018\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":18,\"ROWSTAMP\":\"698858426\"},{\"LINENUM\":11,\"HASLD\":0,\"DESCRIPTION\":\"导曲线(后)\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1019\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":19,\"ROWSTAMP\":\"698858427\"},{\"LINENUM\":12,\"HASLD\":0,\"DESCRIPTION\":\"叉心前（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1020\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":20,\"ROWSTAMP\":\"698858428\"},{\"LINENUM\":13,\"HASLD\":0,\"DESCRIPTION\":\"叉心前（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1021\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":21,\"ROWSTAMP\":\"698858429\"},{\"LINENUM\":14,\"HASLD\":0,\"DESCRIPTION\":\"叉心中（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1022\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":22,\"ROWSTAMP\":\"698858430\"},{\"LINENUM\":15,\"HASLD\":0,\"DESCRIPTION\":\"叉心中（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1023\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":23,\"ROWSTAMP\":\"698858431\"},{\"LINENUM\":16,\"HASLD\":0,\"DESCRIPTION\":\"叉心后（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1024\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":24,\"ROWSTAMP\":\"698858432\"},{\"LINENUM\":17,\"HASLD\":0,\"DESCRIPTION\":\"叉心后（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1025\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":25,\"ROWSTAMP\":\"698858433\"},{\"LINENUM\":18,\"HASLD\":0,\"DESCRIPTION\":\"查照间隔（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1026\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":26,\"ROWSTAMP\":\"698858434\"},{\"LINENUM\":19,\"HASLD\":0,\"DESCRIPTION\":\"查照间隔（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1027\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":27,\"ROWSTAMP\":\"698858435\"},{\"LINENUM\":20,\"HASLD\":0,\"DESCRIPTION\":\"护背距离（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1028\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":28,\"ROWSTAMP\":\"698858436\"},{\"LINENUM\":21,\"HASLD\":0,\"DESCRIPTION\":\"护背距离（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1029\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":29,\"ROWSTAMP\":\"698858437\"},{\"LINENUM\":22,\"HASLD\":0,\"DESCRIPTION\":\"支距\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1030\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"支距\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":30,\"ROWSTAMP\":\"698858438\"}]";
		getNetworkConfigDatas(json);
	}
	
	private boolean once=false;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(once&&hasFocus){
			once=false;
			showLoadingPop();
		}
	}
	
	public void showLoadingPop() {
		View loadingView=View.inflate(this, R.layout.page_load_loading, null);
		loadinPop = new PopupWindow(loadingView, 100, 100);
		loadinPop.setFocusable(true);
		loadinPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		loadinPop.showAtLocation(contentView, Gravity.CENTER, 0, 0);
	}
	
	public void dismissLoadingPop() {
		once=false;
		if(loadinPop!=null&&loadinPop.isShowing()){
			loadinPop.dismiss();
			loadinPop=null;
		}
	}
	
	private ArrayList<Boolean> getRowEditable(boolean c1, boolean c2) {
		ArrayList<Boolean> row=new ArrayList<Boolean>();
		row.add(c1);
		row.add(c2);
		return row;
	}
	
	private boolean hasMoved=false;
	private PopupWindow loadinPop;
	private View contentView;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode=event.getKeyCode();
		if(keyCode==KeyEvent.KEYCODE_ENTER){
			if(hasMoved){
				hasMoved=false;
				return true;
			}
			View fView = this.getWindow().getDecorView().findFocus();
			
			if(!(fView.getParent() instanceof TableRow)){
				return super.dispatchKeyEvent(event); 
			}
			
			ViewGroup vg=(ViewGroup) fView.getParent();
			for (int i = 0; i < vg.getChildCount(); i++) {
				View pView = vg.getChildAt(i);
				if(pView==fView){
					if(i==(vg.getChildCount()-2)){
						View qView=vg.getChildAt(i+1);
						if(qView instanceof EditText){
							EditText et=(EditText) qView;
							if(et.isFocusable()){
								focusEditText(et);
								hasMoved=true;
								return true;
							}else{
								return super.dispatchKeyEvent(event);
							}
						}else{
							return super.dispatchKeyEvent(event);
						}
					}else if(i==(vg.getChildCount()-1)){
						ViewGroup pvg=(ViewGroup) vg.getParent();
						for (int j = 0; j <pvg.getChildCount(); j++) {
							if(vg==pvg.getChildAt(j)){
								if(j!=pvg.getChildCount()-1){
									ViewGroup vgg=(ViewGroup) pvg.getChildAt(j+1);
									if(vgg.getVisibility()==View.GONE){
										if(j!=pvg.getChildCount()-2){
											vgg=(ViewGroup) pvg.getChildAt(j+2);
										}else{
											return super.dispatchKeyEvent(event);
										}
									}
									View v=vgg.getChildAt(vgg.getChildCount()-2);
									if(v instanceof EditText){
										EditText et=(EditText) v;
										if(et.isFocusable()){
											focusEditText(et);
											hasMoved=true;
											return true;
										}else{
											return super.dispatchKeyEvent(event);
										}
									}else{
										return super.dispatchKeyEvent(event);
									}
								}else{
									return super.dispatchKeyEvent(event);
								}
							}
						}
					}
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}
	private void focusEditText(EditText et) {
		if(et.isFocusable()){
			et.setFocusable(true);
			et.setFocusableInTouchMode(true);
			et.requestFocus();
			et.findFocus();
		}
	}
}
