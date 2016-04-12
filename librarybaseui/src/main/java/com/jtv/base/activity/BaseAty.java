package com.jtv.base.activity;

import com.jtv.base.util.CollectionActivity;
import com.plutus.libraryui.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author beyound
 * @email whatl@foxmail.com
 * @date 2015年7月6日
 */
public abstract class BaseAty extends Activity implements OnClickListener {
	/*** 标题 */
	private TextView tv_title;
	/*** 右上角的图片提交按钮 */
	private Button iv_ok;
	/*** 右上角的提交按钮 */
	private Button btn_ok;
	/*** 标题的所有的父布局 **/
	private RelativeLayout rl_background;
	/*** 返回按钮 */
	private TextView tv_back;

	@Override
	protected void onStart() {
		super.onStart();
		CollectionActivity.putTopActivity(this);
	}

	protected abstract void onCreatInit(Bundle savedInstanceState);

	@Override
	final protected void onCreate(Bundle savedInstanceState) {
		CollectionActivity.putTopActivity(this);
		super.onCreate(savedInstanceState);
		onCreatInit(savedInstanceState);
	}

	protected TextView getHeaderBackBtn() {
		if (tv_back == null) {
			tv_back = (TextView) findViewById(R.id.tv_back);
		}
		return (TextView) tv_back;
	}

	protected TextView getHeaderTitleTv() {
		if (tv_title == null) {
			tv_title = (TextView) findViewById(R.id.tv_title);
		}
		return tv_title;
	}

	protected Button getHeaderOkIvbtn() {
		if (iv_ok == null) {
			iv_ok = (Button) findViewById(R.id.iv_ok);
		}
		return iv_ok;
	}

	protected Button getHeaderOkBtn() {
		if (btn_ok == null) {
			btn_ok = (Button) findViewById(R.id.btn_ok);
		}
		return btn_ok;
	}

	protected RelativeLayout getHeaderRlayout() {
		if (rl_background == null) {
			rl_background = (RelativeLayout) findViewById(R.id.rl_title_background);
		}
		return (RelativeLayout) rl_background;
	}

	/**
	 * 设置返回的点击事件并且会关闭当前的页面，如果在关闭的同时需要处理其他逻辑可以在onclick中添加R.id.iv_back
	 */
	public void setBackOnClickFinish() {
		getHeaderBackBtn().setVisibility(View.VISIBLE);
		getHeaderBackBtn().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BaseAty.this.finish();
				BaseAty.this.onClick(v);
			}
		});
	}

	/**
	 * 设置右上角的button按钮的点击事件，会调用onclick(View v) id: R.id.btn_ok
	 * 
	 * @param mText
	 *            按钮的名字
	 */
	public Button setHeaderBtnOKOnclickListener(String mText) {
		Button headerOkBtn = getHeaderOkBtn();
		headerOkBtn.setVisibility(View.VISIBLE);
		if (!"".equals(mText) && mText != null) {
			headerOkBtn.setText(mText);
		}
		headerOkBtn.setOnClickListener(this);
		return headerOkBtn;
	}

	/**
	 * 设置右上角的ImageView按钮的点击事件，会调用onclick(View v) id: R.id.iv_ok
	 * 
	 * @param mText
	 *            按钮的名字
	 */
	public Button setHeaderIvOKOnclickListener() {
		Button iv_btn = getHeaderOkIvbtn();
		iv_btn.setVisibility(View.VISIBLE);
		iv_btn.setOnClickListener(this);
		return iv_btn;
	}

	/**
	 * 设置标题
	 */
	protected void setHeaderTitleText(String mTitle) {
		getHeaderTitleTv().setVisibility(View.VISIBLE);
		getHeaderTitleTv().setText(mTitle);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 短吐丝
	 * 
	 * @param mToast
	 */
	protected void mToastS(String mToast) {
		Toast.makeText(this, mToast, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 长吐丝
	 * 
	 * @param mToast
	 */
	protected void mToastL(String mToast) {
		Toast.makeText(this, mToast, Toast.LENGTH_LONG).show();
	}
}
