package com.jtv.hrb.locationwork;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;

import com.alibaba.fastjson.JSONArray;
import com.jtv.hrb.locationwork.domain.CurveLine;
import com.jtv.hrb.locationwork.fragment.CurveCheckFragment;
import com.jtv.hrb.locationwork.fragment.LineCheckFragment;
import com.jtv.locationwork.util.SpUtiles;

public class LineCurveCheckActivity extends Activity{
	public static int QUEST_REQUEST_CODE=100;
	public static int QUEST_RESULT_CODE=200;
	private View pageXl;
	private View pageQx;
	private FrameLayout pageContainer;
	public String lineDescription;
	public String assetnum;
	public String wonum;
	public ArrayList<CurveLine> curves;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentView = View.inflate(this,R.layout.activity_line_curve_check, null);
		setContentView(contentView);
		lineDescription = getIntent().getStringExtra("description");
		assetnum = getIntent().getStringExtra("assetnum");
		wonum = getIntent().getStringExtra("wonum");
		startmeasure = getIntent().getStringExtra("startmeasure");
		endmeasure = getIntent().getStringExtra("endmeasure");
		String curvesStr=getIntent().getStringExtra("curves");
		curves =(ArrayList<CurveLine>) JSONArray.parseArray(curvesStr, CurveLine.class);
//		showLoadingPop();
		initView();
	}
	private void initView() {
		pageXl = findViewById(R.id.page_xl);
		pageQx = findViewById(R.id.page_qx);
		checkPageOne(true);
		pageXl.setOnClickListener(tabClickListener);
		pageQx.setOnClickListener(tabClickListener);
	}
	boolean currentPageOne=false;
	int colorSelected=Color.rgb(0xF2, 0xF2, 0xF2);
	int colorUnSelected=Color.rgb(0x85, 0xB5, 0xE0);
	public void checkPageOne(boolean pageOne){
		double[] gps = SpUtiles.BaseInfo.getGps();
		
		if(pageOne==currentPageOne){
			return;
		}
		currentPageOne=pageOne;
		if(pageOne){
			pageXl.setBackgroundColor(colorSelected);
			pageQx.setBackgroundColor(colorUnSelected);
			setFragment(lcFragment);
		}else{
			pageXl.setBackgroundColor(colorUnSelected);
			pageQx.setBackgroundColor(colorSelected);
			setFragment(ccFragment);
		}
	}
	private LineCheckFragment lcFragment=new LineCheckFragment();
	private CurveCheckFragment ccFragment=new CurveCheckFragment();
	private FragmentManager fm = getFragmentManager();
	private void setFragment(Fragment fragment)
    {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.page_container, fragment);  
        transaction.commit();  
    }
	private OnClickListener tabClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.page_xl:
					checkPageOne(true);
					break;
				case R.id.page_qx:
					checkPageOne(false);
					break;
				default:
					break;
			}
		}
	};
	
    public boolean once=false;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus&&once){
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
	
	public String startmeasure;
	public String endmeasure;
	
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
							focusEditText(et);
							hasMoved=true;
							return true;
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
										focusEditText(et);
										hasMoved=true;
										return true;
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
		et.setFocusable(true);
		et.setFocusableInTouchMode(true);
		et.requestFocus();
		et.findFocus();
	}
}
