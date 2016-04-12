package com.jtv.hrb.locationwork.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.jtv.hrb.locationwork.CurveListActivity;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.LineCurveCheckActivity;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.CurveLine;
import com.jtv.hrb.locationwork.domain.MainMeasureData;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.locationwork.util.BASE64Encoder;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.yixia.weibo.sdk.util.ToastUtils;

@SuppressLint("InflateParams")
public class CurveCheckFragment extends Fragment {
	public static final int CURVE_LIST_REQUEST=310;
	private LinearLayout questionList;
	private ArrayList<CurveLine> curves;
	private CurveLine curveLine;
	private List<Double> planvalues;
	private LineCurveCheckActivity activity;
	private StaticCheckDbService service;
	private int assetfeatureid=-100;
	private MeasureData measureData;
	private String assetnum;
	private String wonum;
	private String siteid;
	private String orgid;
	private EditText etPlan;
	private EditText etReal;
	private EditText etPointer;
	private View contentFragmentView;
	private TextView tvStartKM;
	private TextView tvStartM;
	private TextView tvEndKM;
	private TextView tvEndM;
	private TextView tvCurveRadius;
	private TextView tvCurveLength;
	private TextView tvStartHhqx;
	private TextView tvEndHhqx;
	private EditText etZhd1;
	private EditText etZhd2;
	private EditText etHyd1;
	private EditText etHyd2;
	private EditText etYhd1;
	private EditText etYhd2;
	private EditText etHzd1;
	private EditText etHzd2;
	private int syncState;
	private LinearLayout ll_center;
	private TextView tv_line_type;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (LineCurveCheckActivity) getActivity();
		curves = activity.curves;
//		assetfeatureid=1234567;
		assetnum = activity.assetnum;
		wonum = activity.wonum;
		siteid = GlobalApplication.siteid;
		orgid = GlobalApplication.orgid;
		service = new StaticCheckDbService(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentFragmentView = inflater.inflate(R.layout.activity_curve_check, null);
		initContentView();
		return contentFragmentView;
	}
	private void initContentView() {
		initView();
		
		Button btnNextPointer=(Button) contentFragmentView.findViewById(R.id.btn_next_pointer);
		btnNextPointer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveOnePointerData();
				int currentNum=getEdtiTextNum(etPointer);
				setVerine(currentNum);
			}
		});
		
		TextView tvLineName=(TextView) contentFragmentView.findViewById(R.id.tv_line_name);
		tvLineName.setText(activity.lineDescription);
		
		TextView tvCurveList=(TextView) contentFragmentView.findViewById(R.id.tv_curve_list);
		tvCurveList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(curves!=null&&curves.size()>0){
					Intent curveIntent=new Intent(getActivity(), CurveListActivity.class);
					curveIntent.putExtra("curves", JSONArray.toJSONString(curves));
					startActivityForResult(curveIntent, CURVE_LIST_REQUEST);
				}else{
					ToastUtils.showToast(getActivity(), "没有曲线数据");
				}
			}
		});
		
//		etPointer.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if(!hasFocus){
//					EditText etNum=(EditText)v;
//					String numStr=etNum.getText().toString().trim();
//					if(TextUtils.isEmpty(numStr)){
//						setVerine(0);
//					}else{
//						int num=Integer.parseInt(numStr);
//						setVerine(num-1);
//					}
//				}
//			}
//		});
		
		etPointer.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String numStr=s.toString();
				if(!TextUtils.isEmpty(numStr)){
					int num=Integer.parseInt(numStr);
					setNowOrderData(num);
				}
			}
		});
		
		tv_line_type.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tv=(TextView) v;
				String name=tv.getText().toString().trim();
				if("隐藏".equals(name)){
					ll_center.setVisibility(View.GONE);
					tv.setText("显示");
				}else{
					ll_center.setVisibility(View.VISIBLE);
					tv.setText("隐藏");
				}
			}
		});
		
		if(measureData!=null){
			
		}else{
			
		}
	}

	private void initView() {
		etPlan = (EditText) contentFragmentView.findViewById(R.id.et_plan);
		etReal = (EditText) contentFragmentView.findViewById(R.id.et_real);
		etPointer = (EditText) contentFragmentView.findViewById(R.id.et_pointer_num);
		tvStartKM = (TextView) contentFragmentView.findViewById(R.id.tv_start_km);
		tvStartM = (TextView) contentFragmentView.findViewById(R.id.tv_start_m);
		tvEndKM = (TextView) contentFragmentView.findViewById(R.id.tv_end_km);
		tvEndM = (TextView) contentFragmentView.findViewById(R.id.tv_end_m);
		tvCurveRadius = (TextView) contentFragmentView.findViewById(R.id.tv_curve_radius);
		tvCurveLength = (TextView) contentFragmentView.findViewById(R.id.tv_curve_length);
		tvStartHhqx = (TextView) contentFragmentView.findViewById(R.id.tv_start_hhqx);
		tvEndHhqx = (TextView) contentFragmentView.findViewById(R.id.tv_end_hhqx);
		etZhd1 = (EditText) contentFragmentView.findViewById(R.id.et_zhd_1);
		etZhd2 = (EditText) contentFragmentView.findViewById(R.id.et_zhd_2);
		etHyd1 = (EditText) contentFragmentView.findViewById(R.id.et_hyd_1);
		etHyd2 = (EditText) contentFragmentView.findViewById(R.id.et_hyd_2);
		etYhd1 = (EditText) contentFragmentView.findViewById(R.id.et_yhd_1);
		etYhd2 = (EditText) contentFragmentView.findViewById(R.id.et_yhd_2);
		etHzd1 = (EditText) contentFragmentView.findViewById(R.id.et_hzd_1);
		etHzd2 = (EditText) contentFragmentView.findViewById(R.id.et_hzd_2);
		ll_center = (LinearLayout) contentFragmentView.findViewById(R.id.ll_center);
		tv_line_type = (TextView) contentFragmentView.findViewById(R.id.tv_line_type);
	}
	
	protected void saveOnePointerData() {
		MeasureData msData=new MeasureData();
		msData.setValue1(getEditTextDouble(etReal));
		msData.setValue2(getEditTextDouble(etPlan));
		int pointerNum=getEditTextInt(etPointer);
		msData.setLinenum(pointerNum);
		msData.setFlag_v("1");
		double[] gps = SpUtiles.BaseInfo.getGps();
		msData.setXvalue(gps[0]+"");
		msData.setYvalue(gps[1]+"");
		msData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));
		msData.setNametype("第"+pointerNum+"点");
		msData.setStatus("0");
		
		msData.setAssetfeatureid(assetfeatureid);
		msData.setOrgid(orgid);
		msData.setSiteid(siteid);
		msData.setAssetnum(assetnum);
		msData.setWonum(wonum);
		msData.setHasld(0);
		
		msData.setStartmeasure(curveLine.getStartmeasure());
		msData.setEndmeasure(curveLine.getEndmeasure());
		service.saveCurveMeasureData(msData);
	}


	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==getActivity().RESULT_OK){
			if(requestCode==CURVE_LIST_REQUEST){
				int position=data.getIntExtra("selectindex", 0);
				assetfeatureid=curves.get(position).getAssetfeatureid();
				setCurveLineData(position);
				//
				MainMeasureData mainMeasureData = service.getCP1RAW_ZXMAIN_Curve(wonum, assetnum, assetfeatureid);
				if(mainMeasureData!=null){
					etZhd1.setText(mainMeasureData.getZx_zhd_value1());
					etZhd2.setText(mainMeasureData.getZx_zhd_value2());
					etHyd1.setText(mainMeasureData.getZx_hyd_value1());
					etHyd2.setText(mainMeasureData.getZx_hyd_value2());
					etYhd1.setText(mainMeasureData.getZx_yhd_value1());
					etYhd2.setText(mainMeasureData.getZx_yhd_value2());
					etHzd1.setText(mainMeasureData.getZx_hzd_value1());
					etHzd2.setText(mainMeasureData.getZx_hzd_value2());
				}else{
					etZhd1.setText("");
					etZhd2.setText("");
					etHyd1.setText("");
					etHyd2.setText("");
					etYhd1.setText("");
					etYhd2.setText("");
					etHzd1.setText("");
					etHzd2.setText("");
				}
				//
				measureData = service.getLastCurveMeasureData(wonum,assetfeatureid);
				
				if(measureData!=null){
					syncState = measureData.getSyncstate();
					//显示出数据
					int linenum = measureData.getLinenum();
					double realValue = measureData.getValue1();
					double planValue = measureData.getValue2();
					etPointer.setText(linenum+"");
					etReal.setText(realValue+"");
					etPlan.setText(planValue+"");
				}else{
					etPointer.setText("1");
					etReal.setText("");
					etPlan.setText("");
					measureData=new MeasureData();
				}
				if(syncState==-2||syncState==1){
					etPlan.setFocusable(false);
					etReal.setFocusable(false);
					
					etZhd1.setFocusable(false);
					etZhd2.setFocusable(false);
					etHyd1.setFocusable(false);
					etHyd2.setFocusable(false);
					etYhd1.setFocusable(false);
					etYhd2.setFocusable(false);
					etHzd1.setFocusable(false);
					etHzd2.setFocusable(false);
				}
			}
		}
	}
	
	private void setCurveLineData(int position) {
		curveLine = curves.get(position);
		
		tvStartKM.setText(getKMStr(curveLine.getStartmeasure()));
		tvStartM.setText(getMStr(curveLine.getStartmeasure()));
		tvEndKM.setText(getKMStr(curveLine.getEndmeasure()));
		tvEndM.setText(getMStr(curveLine.getEndmeasure()));
		tvCurveRadius.setText(curveLine.getQxbj()+"");
		tvCurveLength.setText(curveLine.getXxqc()+"");
		tvStartHhqx.setText(curveLine.getQhxc()+"");
		tvEndHhqx.setText(curveLine.getZhxc()+"");
		
		planvalues = curveLine.getPlanvalue();
		setVerine(0);
	}
	
	private void setVerine(int order) {
		if(planvalues!=null&&planvalues.size()>order){
			etPlan.setText(planvalues.get(order)+"");
		}else{
			etPlan.setText("");
		}
		etReal.setText("");
		etPointer.setText((order+1)+"");
		
		MeasureData msData = service.getCurveMeasueDataByPoint(wonum,assetfeatureid,order+1);
		if(msData!=null){
			etPlan.setText(msData.getValue2()+"");
			etReal.setText(msData.getValue1()+"");
		}
	}

	
	private void setNowOrderData(int order) {
		if(planvalues!=null&&planvalues.size()>order){
			etPlan.setText(planvalues.get(order)+"");
		}else{
			etPlan.setText("");
		}
		etReal.setText("");
		
		MeasureData msData = service.getCurveMeasueDataByPoint(wonum,assetfeatureid,order);
		if(msData!=null){
			etPlan.setText(msData.getValue2()+"");
			etReal.setText(msData.getValue1()+"");
		}
	}
	
	public String getKMStr(double dkm){
		int ikm=(int) dkm;
		return ikm+"";
	}
	
	public String getMStr(double dkm){
		int ikm=(int) dkm;
		double dm=(dkm-ikm*1.0)*1000;
		int im=(int) dm;
		return im+"";
	}
	/**
	 * 获取录入控件对应的点值
	 * @param editText
	 * @return
	 */
	private double getEditTextDouble(EditText editText){
		String railNumStr=editText.getText().toString().trim();
		if(!TextUtils.isEmpty(railNumStr)){
			double railNum=Double.parseDouble(railNumStr);
			return railNum;
		}else{
			return 0;
		}
	}
	
	private int getEditTextInt(EditText editText) {
		String railNumStr=editText.getText().toString().trim();
		if(!TextUtils.isEmpty(railNumStr)){
			int railNum=Integer.parseInt(railNumStr);
			return railNum;
		}else{
			return 0;
		}
	}
	
	private int getEdtiTextNum(EditText editText) {
		String railNumStr=editText.getText().toString().trim();
		if(!TextUtils.isEmpty(railNumStr)){
			int railNum=Integer.parseInt(railNumStr);
			if(railNum<0){
				return -1;
			}
			return railNum;
		}else{
			return -2;
		}
	}
	
	private String getNextNumStr(int number){
		if(number>=0){
			return (number+1)+"";
		}else{
			return "0";
		}
	}
	
	private TextView createTextView(String text){
		View view=View.inflate(getActivity(), R.layout.layout_quest_textview, null);
		TextView tv=(TextView) view.findViewById(R.id.tv);
		tv.setText(text);
		return tv;
	}
	
    // Base64加密  
    public String getBase64(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {  
            s = new BASE64Encoder().encode(b);  
        }  
        return s;  
    }
    
    @Override
    public void onStop() {
    	if(assetfeatureid>=0){
    		saveMainTableData();
    	}
    	super.onStop();
    }
    
    @Override
    public void onDestroy() {
    	if(assetfeatureid>=0){
    		saveMainTableData();
    	}
    	super.onDestroy();
    }

	private void saveMainTableData() {
		MainMeasureData mainData=new MainMeasureData();
    	mainData.setOrgid(orgid);
    	mainData.setSiteid(siteid);
    	mainData.setWonum(wonum);
    	mainData.setAssetnum(assetnum);
    	mainData.setAssetfeatureid(assetfeatureid);
    	mainData.setZx_zhd_value1(getEditTextInt(etZhd1)+"");
    	mainData.setZx_zhd_value2(getEditTextInt(etZhd2)+"");
    	mainData.setZx_hyd_value1(getEditTextInt(etHyd1)+"");
    	mainData.setZx_hyd_value2(getEditTextInt(etHyd2)+"");
    	mainData.setZx_yhd_value1(getEditTextInt(etYhd1)+"");
    	mainData.setZx_yhd_value2(getEditTextInt(etYhd2)+"");
    	mainData.setZx_hzd_value1(getEditTextInt(etHzd1)+"");
    	mainData.setZx_hzd_value2(getEditTextInt(etHzd2)+"");
    	mainData.setSyncstate(-1);
    	mainData.setHasld(0);
    	service.saveCP1RAW_ZXMAIN_Curve(mainData);
	}
	
}
