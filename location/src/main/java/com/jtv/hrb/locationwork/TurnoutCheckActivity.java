package com.jtv.hrb.locationwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.DaoQuXianBean;
import com.jtv.hrb.locationwork.domain.TurnoutBean;
import com.jtv.hrb.locationwork.domain.ZheChaBean;
import com.jtv.hrb.locationwork.domain.ZhiJuBean;
import com.jtv.hrb.locationwork.domain.ZhuanZheBean;
import com.jtv.locationwork.activity.ClassSelectActivity;

public class TurnoutCheckActivity extends Activity {
	public static int QUEST_REQUEST_CODE=300;
	public static int TURNOUT_REQUEST_CODE=301;
	private StaticCheckDbService service=new StaticCheckDbService(this);
	private String assetnum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_turnout_check);
		assetnum=getIntent().getStringExtra("assetnum");
		lineName = getIntent().getStringExtra("description");
		service.getLastTurnoutMeasureData();
		initView();
	}
	
	private void initView() {
		setButtonClick(R.id.previous_part, mOnClickListener);
		setButtonClick(R.id.next_part, mOnClickListener);
		initFrameLayout();
		initTurnOutNum();
	}

//	private void initQuestBtn() {
//		View btnQuestion=findViewById(R.id.btn_other_quest);
//		if(btnQuestion!=null){
//			btnQuestion.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent questIntent=new Intent(TurnoutCheckActivity.this, TurnoutListActivity.class);
//					startActivityForResult(questIntent, QUEST_REQUEST_CODE);
//				}
//			});
//		}
//	}

	private void initTurnOutNum() {
		tvTunroutNum = (TextView) findViewById(R.id.tv_turnout_num);
		tvTunroutNum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent turnoutIntent=new Intent(TurnoutCheckActivity.this, TurnoutListActivity.class);
				startActivityForResult(turnoutIntent, TURNOUT_REQUEST_CODE);
			}
		});
	}

	private void initFrameLayout() {
		changeView = (FrameLayout) findViewById(R.id.change_view);
		changeView(0);
	}
	
	private String[] partNames={"转辙部分","导曲线部分","辙叉部分","支距"};
	private int[] ids=new int[]{R.layout.sublayout_turnout_1zz,R.layout.sublayout_turnout_2dq,//
			R.layout.sublayout_turnout_3zc,R.layout.sublayout_turnout_4zj};
	private int currentPosition=-1;
	private void changeView(int position){
		if(position>=0&&position<4&&position!=currentPosition){
			changeView.removeAllViews();
			View subview=View.inflate(TurnoutCheckActivity.this, ids[position], null);
			View btnOtherQuest=subview.findViewById(R.id.btn_other_quest);
			tableLayout = (TableLayout) subview.findViewById(R.id.tablelayout);
			questionLayout = (LinearLayout) subview.findViewById(R.id.ll_questions);
			if(btnOtherQuest!=null){
				initOtherQuestButton(btnOtherQuest);
			}
			changeView.addView(subview);
			setText(R.id.part_name, partNames[position]);
			currentPosition=position;
//			showUiData();
		}
	}
	
//	private void showUiData() {
//		if(currentPosition==0){
//			showPage0();
//		}else if(currentPosition==1){
//			showPage1();
//		}else if(currentPosition==2){
//			showPage2();
//		}else if(currentPosition==3){
//			showPage3();
//		}
//	}

//	private void showPage3() {
//		ZhuanZheBean zhuanZheBean = turnoutBean.getZhuanZheBean();
//		zhuanZheBean.getA_jgjd_1();
//		zhuanZheBean.getA_jgjd_2();
//		for (int i = 0; i < 5; i++) {
//			String cellValue1=null;
//			String cellValue2=null;
//			if(i==0){
//				cellValue1=zhuanZheBean.getA_jbgjt_1()+"";
//				cellValue2=zhuanZheBean.getA_jbgjt_2()+"";
//			}else if(i==1){
//				cellValue1=zhuanZheBean.getA_jgjd_1()+"";
//				cellValue2=zhuanZheBean.getA_jgjd_2()+"";
//			}else if(i==2){
//				cellValue1=zhuanZheBean.getA_jgz_1()+"";
//				cellValue2=zhuanZheBean.getA_jgz_2()+"";
//			}else if(i==3){
//				cellValue1=zhuanZheBean.getA_jggd_1()+"";
//				cellValue2=zhuanZheBean.getA_jggd_2()+"";
//			}else if(i==4){
//				cellValue1=zhuanZheBean.getA_jggd_3()+"";
//				cellValue2=zhuanZheBean.getA_jggd_4()+"";
//			}
//			TableRow tableRow=(TableRow) tableLayout.getChildAt(i);
//			EditText cell0=(EditText) tableRow.getChildAt(1);
//			EditText cell1=(EditText) tableRow.getChildAt(2);
//			cell0.setText(cellValue1);
//			cell1.setText(cellValue2);
//		}	
//	}

//	private void showPage2() {
//		ZheChaBean zheChaBean = turnoutBean.getZheChaBean();
//		for (int i = 0; i < 5; i++) {
//			String cellValue1=null;
//			String cellValue2=null;
//			if(i==0){
//				cellValue1=zheChaBean.getA_jbgjt_1()+"";
//				cellValue2=zheChaBean.getA_jbgjt_2()+"";
//			}else if(i==1){
//				cellValue1=zheChaBean.getA_jgjd_1()+"";
//				cellValue2=zheChaBean.getA_jgjd_2()+"";
//			}else if(i==2){
//				cellValue1=zheChaBean.getA_jgz_1()+"";
//				cellValue2=zheChaBean.getA_jgz_2()+"";
//			}else if(i==3){
//				cellValue1=zheChaBean.getA_jggd_1()+"";
//				cellValue2=zheChaBean.getA_jggd_2()+"";
//			}else if(i==4){
//				cellValue1=zheChaBean.getA_jggd_3()+"";
//				cellValue2=zheChaBean.getA_jggd_4()+"";
//			}
//			TableRow tableRow=(TableRow) tableLayout.getChildAt(i);
//			EditText cell0=(EditText) tableRow.getChildAt(1);
//			EditText cell1=(EditText) tableRow.getChildAt(2);
//			cell0.setText(cellValue1);
//			cell1.setText(cellValue2);
//		}	
//	}

//	private void showPage1() {
//		DaoQuXianBean daoQuXianBean = turnoutBean.getDaoQuXianBean();
//		for (int i = 0; i < 5; i++) {
//			String cellValue1=null;
//			String cellValue2=null;
//			if(i==0){
//				cellValue1=daoQuXianBean.getB_zx_q_1()+"";
//				cellValue2=daoQuXianBean.getB_zx_q_2()+"";
//			}else if(i==1){
//				cellValue1=daoQuXianBean.getB_zx_z_1()+"";
//				cellValue2=daoQuXianBean.getB_zx_q_2()+"";
//			}else if(i==2){
//				cellValue1=daoQuXianBean.getB_zx_h_1()+"";
//				cellValue2=daoQuXianBean.getB_zx_h_2()+"";
//			}else if(i==3){
//				cellValue1=daoQuXianBean.getB_dqx_q_1()+"";
//				cellValue2=daoQuXianBean.getB_dqx_q_2()+"";
//			}else if(i==4){
//				cellValue1=daoQuXianBean.getB_dqx_z_1()+"";
//				cellValue2=daoQuXianBean.getB_dqx_z_2()+"";
//			}else if(i==5){
//				cellValue1=daoQuXianBean.getB_dqx_h_1()+"";
//				cellValue2=daoQuXianBean.getB_dqx_h_2()+"";
//			}
//			TableRow tableRow=(TableRow) tableLayout.getChildAt(i);
//			EditText cell0=(EditText) tableRow.getChildAt(1);
//			EditText cell1=(EditText) tableRow.getChildAt(2);
//			cell0.setText(cellValue1);
//			cell1.setText(cellValue2);
//		}	
//	}

//	private void showPage0() {
//		ZhuanZheBean zhuanZheBean = turnoutBean.getZhuanZheBean();
//		for (int i = 0; i < 5; i++) {
//			String cellValue1=null;
//			String cellValue2=null;
//			if(i==0){
//				cellValue1=zhuanZheBean.getA_jbgjt_1()+"";
//				cellValue2=zhuanZheBean.getA_jbgjt_2()+"";
//			}else if(i==1){
//				cellValue1=zhuanZheBean.getA_jgjd_1()+"";
//				cellValue2=zhuanZheBean.getA_jgjd_2()+"";
//			}else if(i==2){
//				cellValue1=zhuanZheBean.getA_jgz_1()+"";
//				cellValue2=zhuanZheBean.getA_jgz_2()+"";
//			}else if(i==3){
//				cellValue1=zhuanZheBean.getA_jggd_1()+"";
//				cellValue2=zhuanZheBean.getA_jggd_2()+"";
//			}else if(i==4){
//				cellValue1=zhuanZheBean.getA_jggd_3()+"";
//				cellValue2=zhuanZheBean.getA_jggd_4()+"";
//			}
//			TableRow tableRow=(TableRow) tableLayout.getChildAt(i);
//			EditText cell0=(EditText) tableRow.getChildAt(1);
//			EditText cell1=(EditText) tableRow.getChildAt(2);
//			cell0.setText(cellValue1);
//			cell1.setText(cellValue2);
//		}		
//	}

	private void initOtherQuestButton(View btn){
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent questIntent=new Intent(TurnoutCheckActivity.this, ClassSelectActivity.class);
				startActivityForResult(questIntent, QUEST_REQUEST_CODE);
			}
		});
	}
	
	private OnClickListener mOnClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.previous_part:
					changeView(currentPosition-1);
					break;
				case R.id.next_part:
					//保存数据
//					saveUiData();
					//更改界面
					changeView(currentPosition+1);
					//显示历史数据，查询数据
					//如果没有数据则不显示
					//如果有数据则显示
					break;
				default:
					break;
			}
		}
	};
	
//	protected void saveUiData() {
//		if(currentPosition==0){
//			savePageType0();
//		}else if(currentPosition==1){
//			savePageType1();
//		}else if(currentPosition==2){
//			savePageType2();
//		}else if(currentPosition==3){
//			savePageType3();
//		}
//	}

	
	
//	private void savePageType0() {
//		ZhuanZheBean zhuanZheBean = turnoutBean.getZhuanZheBean();
//		zhuanZheBean.setAssetnum(assetnum);
//		TableRow tableRow0=(TableRow) tableLayout.getChildAt(0);
//		zhuanZheBean.setA_jbgjt_1(getEdtiTextNum2((EditText)tableRow0.getChildAt(1)));
//		zhuanZheBean.setA_jbgjt_2(getEdtiTextNum2((EditText)tableRow0.getChildAt(2)));
//		TableRow tableRow1=(TableRow) tableLayout.getChildAt(1);
//		zhuanZheBean.setA_jgjd_1(getEdtiTextNum2((EditText)tableRow1.getChildAt(1)));
//		zhuanZheBean.setA_jgjd_2(getEdtiTextNum2((EditText)tableRow1.getChildAt(2)));
//		TableRow tableRow2=(TableRow) tableLayout.getChildAt(2);
//		zhuanZheBean.setA_jgz_1(getEdtiTextNum2((EditText)tableRow2.getChildAt(1)));
//		zhuanZheBean.setA_jgz_2(getEdtiTextNum2((EditText)tableRow2.getChildAt(2)));
//		TableRow tableRow3=(TableRow) tableLayout.getChildAt(3);
//		zhuanZheBean.setA_jggd_1(getEdtiTextNum2((EditText)tableRow3.getChildAt(1)));
//		zhuanZheBean.setA_jggd_2(getEdtiTextNum2((EditText)tableRow3.getChildAt(2)));
//		TableRow tableRow4=(TableRow) tableLayout.getChildAt(3);
//		zhuanZheBean.setA_jggd_3(getEdtiTextNum2((EditText)tableRow4.getChildAt(1)));
//		zhuanZheBean.setA_jggd_4(getEdtiTextNum2((EditText)tableRow4.getChildAt(2)));
//	}

//	private void savePageType1() {
//		DaoQuXianBean daoQuXianBean = turnoutBean.getDaoQuXianBean();
//		daoQuXianBean.setAssetnum(assetnum);
//		TableRow tableRow0=(TableRow) tableLayout.getChildAt(0);
//		daoQuXianBean.setB_zx_q_1(getEdtiTextNum2((EditText)tableRow0.getChildAt(1)));
//		daoQuXianBean.setB_zx_q_2(getEdtiTextNum2((EditText)tableRow0.getChildAt(2)));
//		TableRow tableRow1=(TableRow) tableLayout.getChildAt(1);
//		daoQuXianBean.setB_zx_z_1(getEdtiTextNum2((EditText)tableRow1.getChildAt(1)));
//		daoQuXianBean.setB_zx_z_2(getEdtiTextNum2((EditText)tableRow1.getChildAt(2)));
//		TableRow tableRow2=(TableRow) tableLayout.getChildAt(2);
//		daoQuXianBean.setB_zx_h_1(getEdtiTextNum2((EditText)tableRow2.getChildAt(1)));
//		daoQuXianBean.setB_zx_h_2(getEdtiTextNum2((EditText)tableRow2.getChildAt(2)));
//		TableRow tableRow3=(TableRow) tableLayout.getChildAt(3);
//		daoQuXianBean.setB_dqx_q_1(getEdtiTextNum2((EditText)tableRow3.getChildAt(1)));
//		daoQuXianBean.setB_dqx_q_2(getEdtiTextNum2((EditText)tableRow3.getChildAt(2)));
//		TableRow tableRow4=(TableRow) tableLayout.getChildAt(4);
//		daoQuXianBean.setB_dqx_z_1(getEdtiTextNum2((EditText)tableRow4.getChildAt(1)));
//		daoQuXianBean.setB_dqx_z_2(getEdtiTextNum2((EditText)tableRow4.getChildAt(2)));
//		TableRow tableRow5=(TableRow) tableLayout.getChildAt(5);
//		daoQuXianBean.setB_dqx_h_1(getEdtiTextNum2((EditText)tableRow5.getChildAt(1)));
//		daoQuXianBean.setB_dqx_h_2(getEdtiTextNum2((EditText)tableRow5.getChildAt(2)));
//	}
	
//	private void savePageType2() {
//		ZheChaBean zheChaBean = turnoutBean.getZheChaBean();
//		zheChaBean.setAssetnum(assetnum);
//		TableRow tableRow0=(TableRow) tableLayout.getChildAt(0);
//		zheChaBean.setC_cxz_zhi_1(getEdtiTextNum2((EditText)tableRow0.getChildAt(1)));
//		zheChaBean.setC_cxz_qu_1(getEdtiTextNum2((EditText)tableRow0.getChildAt(2)));
//		TableRow tableRow1=(TableRow) tableLayout.getChildAt(1);
//		zheChaBean.setC_cxh_zhi_1(getEdtiTextNum2((EditText)tableRow1.getChildAt(1)));
//		zheChaBean.setC_cxh_zhi_2(getEdtiTextNum2((EditText)tableRow1.getChildAt(2)));
//		TableRow tableRow2=(TableRow) tableLayout.getChildAt(2);
//		zheChaBean.setC_cxh_qu_1(getEdtiTextNum2((EditText)tableRow2.getChildAt(1)));
//		zheChaBean.setC_cxh_qu_2(getEdtiTextNum2((EditText)tableRow2.getChildAt(2)));
//		TableRow tableRow3=(TableRow) tableLayout.getChildAt(3);
//		zheChaBean.setC_czjg_zhi_1(getEdtiTextNum2((EditText)tableRow3.getChildAt(1)));
//		zheChaBean.setC_czjg_qu_1(getEdtiTextNum2((EditText)tableRow3.getChildAt(2)));
//		TableRow tableRow4=(TableRow) tableLayout.getChildAt(4);
//		zheChaBean.setC_hbjl_zhi_1(getEdtiTextNum2((EditText)tableRow4.getChildAt(1)));
//		zheChaBean.setC_hbjl_qu_1(getEdtiTextNum2((EditText)tableRow4.getChildAt(2)));
//	}
	
//	private void savePageType3() {
//		List<ZhiJuBean> zhiJuBeans = turnoutBean.getZhiJuBeans();
//		int index=-1;
//		do{
//			index++;
//			TableRow tableRow=(TableRow) tableLayout.getChildAt(index);
//			EditText cell0=(EditText) tableRow.getChildAt(0);
//			EditText cell1=(EditText) tableRow.getChildAt(1);
//			Integer num0=getEdtiTextNum(cell0);
//			Integer num1=getEdtiTextNum(cell1);
//			if(num0==null&&num1==null){
//				break;
//			}else if(num0==null){
//				num0=0;
//			}else if(num1==null){
//				num1=0;
//			}
//			ZhiJuBean zhiJuBean=new ZhiJuBean();
//			zhiJuBean.setAssetnum(assetnum);
//			zhiJuBean.setLinenum(index);
//			zhiJuBean.setPlanvalue(num0);
//			zhiJuBean.setR_value(num0);
//			zhiJuBeans.add(zhiJuBean);
//		}while(true);
//	}
	
	private Integer getEdtiTextNum(EditText editText) {
		String railNumStr=editText.getText().toString().trim();
		if(!TextUtils.isEmpty(railNumStr)){
			Integer railNum=Integer.parseInt(railNumStr);
			return railNum;
		}else{
			return null;
		}
	}
	
	private int getEdtiTextNum2(EditText editText) {
		String railNumStr=editText.getText().toString().trim();
		if(!TextUtils.isEmpty(railNumStr)){
			int railNum=Integer.parseInt(railNumStr);
			return railNum;
		}else{
			return 0;
		}
	}
	
	private Map<Integer, View> viewChache=new HashMap<Integer, View>();
	private FrameLayout changeView;
	private Spinner turnOutNumber;
	private TextView tvTunroutNum;
	private void setButtonClick(int resId,OnClickListener listener){
		View btn=viewChache.get(resId);
		if(btn==null){
			btn=findViewById(resId);
		}
		btn.setOnClickListener(listener);
	}
	private void setText(int resId,String text){
		View view=viewChache.get(resId);
		if(view==null){
			view=findViewById(resId);
		}
		TextView tv=(TextView) view;
		tv.setText(text);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK){
			if(requestCode==TURNOUT_REQUEST_CODE){
				String name=data.getStringExtra("turnoutName");
				tvTunroutNum.setText(name);
			}else if(requestCode==QUEST_REQUEST_CODE){
				LinearLayout questionList=(LinearLayout) findViewById(R.id.ll_questions);
				String question=data.getStringExtra("detailItem");
				questionList.addView(getQuestTextView(question), params);
			}
		}
	}
	
	private TextView getQuestTextView(String text){
		View view=View.inflate(TurnoutCheckActivity.this, R.layout.layout_quest_textview, null);
		TextView tv=(TextView) view.findViewById(R.id.tv);
		tv.setText(text);
		return tv;
	}
	private LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(//
			LinearLayout.LayoutParams.MATCH_PARENT,//
			LinearLayout.LayoutParams.WRAP_CONTENT);
	private TableLayout tableLayout;
	private LinearLayout questionLayout;
	private String lineName;
	
	@Override
	protected void onStop() {
		super.onStop();
	}
}
