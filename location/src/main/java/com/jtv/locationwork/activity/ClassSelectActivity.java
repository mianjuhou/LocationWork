package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.R.id;
import com.jtv.hrb.locationwork.R.layout;
import com.jtv.locationwork.entity.ClassSelectBean;
import com.jtv.locationwork.imp.QuestionTree;
import com.jtv.locationwork.tree.ParmterMutableTreeNode;
/**
 * 问题分类列表界面
 * @author 房德安  2016-3-1
 *
 */
public class ClassSelectActivity extends Activity {
	private ClassSelectActivity thisActivity;
	private ListView lv_detail;
	private ListView lv_catalog;
	private List<ParmterMutableTreeNode> mArrQuestionSocre;
	private List<ParmterMutableTreeNode> mArrItemQuestion;
	
	private int curentSelectIndex=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_select);
		thisActivity = this;
		getData();

		lv_catalog = (ListView) findViewById(R.id.lv_catalog);
		lv_catalog.setAdapter(catalogAdapter);
		lv_catalog.setOnItemClickListener(catalogItemClickListener);

		lv_detail = (ListView) findViewById(R.id.lv_detail);
		lv_detail.setAdapter(detailAdapter);
		lv_detail.setOnItemClickListener(detailItemClickListener);
		
//		initButton();
	}

	private void initButton() {
//		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent resultIntent = new Intent();
//				resultIntent.putExtra("detailItem", (String) mArrItemQuestion.get(position).getUserObject());
//				setResult(RESULT_OK, resultIntent);
//				thisActivity.finish();
//			}
//		});
	}

	private void getData() {
		QuestionTree instance = QuestionTree.getInstance();
		ParmterMutableTreeNode creatTree = instance.creatTree(null);
		mArrQuestionSocre = (ArrayList<ParmterMutableTreeNode>) creatTree.getChilds();
		mArrQuestionSocre.add(0,new ParmterMutableTreeNode("常用"));
		curentSelectIndex=0;
		mArrItemQuestion = mArrQuestionSocre.get(0).getChilds();
	}
	
	private BaseAdapter catalogAdapter = new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ParmterMutableTreeNode itemData = mArrQuestionSocre.get(position);
			View itemView = View.inflate(thisActivity, R.layout.class_select_item, null);
			TextView tvItem = (TextView) itemView.findViewById(R.id.tv_item);
			tvItem.setText((String) itemData.getUserObject());
			if (curentSelectIndex==position) {
				itemView.setBackgroundColor(Color.WHITE);
			} else {
				itemView.setBackgroundColor(Color.rgb(0xF5, 0xF5, 0xF5));
			}
			return itemView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return mArrQuestionSocre.get(position);
		}

		@Override
		public int getCount() {
			return mArrQuestionSocre.size();
		}
	};
	private OnItemClickListener catalogItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View itemView, int position, long arg3) {
			if(curentSelectIndex!=position){
				itemView.setBackgroundColor(Color.WHITE);
				lv_catalog.getChildAt(curentSelectIndex).setBackgroundColor(Color.rgb(0xF5, 0xF5, 0xF5));
				curentSelectIndex=position;
				mArrItemQuestion = mArrQuestionSocre.get(position).getChilds();
				detailAdapter.notifyDataSetChanged();
			}
		}
	};

	private BaseAdapter detailAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView itemView = null;;
			if(convertView==null){
				itemView = (TextView) View.inflate(thisActivity, android.R.layout.simple_list_item_1, null);
			}else{
				itemView=(TextView) convertView;
			}
			itemView.setText((String) mArrItemQuestion.get(position).getUserObject());
			return itemView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return mArrItemQuestion.get(position);
		}

		@Override
		public int getCount() {
			return mArrItemQuestion.size();
		}
	};

	private OnItemClickListener detailItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("detailItem", (String) mArrItemQuestion.get(position).getUserObject());
			setResult(RESULT_OK, resultIntent);
			thisActivity.finish();
		}
	};
}
