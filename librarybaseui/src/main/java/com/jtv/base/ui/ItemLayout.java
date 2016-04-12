package com.jtv.base.ui;
import com.jtv.base.util.ScreenUtil;
import com.plutus.libraryui.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemLayout extends RelativeLayout {

	private Context con;

	private TextView mTitle;

	private TextView mDescribe;

	private int mTitleSize = 8;

	private int mDescribeSize = 7;

	private int mTitleColor = Color.BLACK;

	private int mDescribeColor = getResources().getColor(R.color.color_danhei);

	private LinearLayout linearLayout;

	private OnItemClikListener mItemListener;

	public ItemLayout(Context context) {
		super(context);
		this.con = context;
		init();
	}

	public ItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.con = context;
		init();
	}

	public ItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.con = context;
		init();
	}

	public void init() {
		setGravity(CENTER_VERTICAL);
		linearLayout = new LinearLayout(con);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		addView(linearLayout);
		setPadding(0, 5, 0, 5);
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mItemListener != null) {
					mItemListener.onItemListener(ItemLayout.this);
				}
			}
		});
	}

	public void setOnItemlistener(OnItemClikListener listener) {
		this.mItemListener = listener;
	}

	public TextView addTitle(String text) {

		if (mTitle == null) {
			mTitle = new TextView(con);
			linearLayout.addView(mTitle);
		}
		mTitle.setText(text);
		mTitle.setTextSize(ScreenUtil.dip2px(con, mTitleSize));
		mTitle.setTextColor(mTitleColor);
		return mTitle;
	}

	public TextView addDescribe(String text) {

		if (mDescribe == null) {
			mDescribe = new TextView(con);
			linearLayout.addView(mDescribe);
		}
		mDescribe.setText(text);
		mDescribe.setTextSize(ScreenUtil.dip2px(con, mDescribeSize));
		mDescribe.setTextColor(mDescribeColor);

		return mDescribe;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (linearLayout != null) {
			LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
			if (layoutParams != null) {
				layoutParams.setMargins(20, 0, 0, 0);
				layoutParams.width = w;
			}
			linearLayout.setLayoutParams(layoutParams);
		}
	}

	public interface OnItemClikListener {
		public void onItemListener(View view);
	}

}
