package com.jtv.base.activity;

import com.plutus.libraryui.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BaseTableViewPagerFragementAty extends FragmentActivity {
	protected int offset = 0;// 动画图片偏移量
	protected int currIndex = 0;// 当前页卡编号
	protected int bmpW;// 动画图片宽度
	protected ImageView mImageViewTab = null;
	protected ViewPager mViewPager;
	protected TextView mTextTab1;
	protected TextView mTextTab2;
	protected TextView mTextTab3;
	protected int tabCount = 3;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(getContentView());
		init();
	}

	protected int getContentView() {
		return R.layout.base_lay_tab_viewpager;
	}

	protected void init() {
		initTabTitle();
		initImageView();
		mViewPager = (ViewPager) findViewById(R.id.vPager);
		MyOnPageChangeListener mOnPagerChanger = new MyOnPageChangeListener();
		mViewPager.setOnPageChangeListener(mOnPagerChanger);
	}

	protected void initTabTitle() {
		mTextTab1 = (TextView) findViewById(R.id.text1);
		mTextTab2 = (TextView) findViewById(R.id.text2);
		mTextTab3 = (TextView) findViewById(R.id.text3);

		mTextTab1.setOnClickListener(new TabTitleOnClickListener(0));
		mTextTab2.setOnClickListener(new TabTitleOnClickListener(1));
		mTextTab3.setOnClickListener(new TabTitleOnClickListener(2));
	}

	protected void initImageView() {
		mImageViewTab = (ImageView) findViewById(R.id.cursor);
		bmpW = mImageViewTab.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度'

		LayoutParams mImageLayout = mImageViewTab.getLayoutParams();
		mImageLayout.width = screenW / tabCount - 5;
		mImageViewTab.setLayoutParams(mImageLayout);
		bmpW = mImageViewTab.getWidth();// 获取图片宽度
		offset = (screenW / tabCount - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		mImageViewTab.setImageMatrix(matrix);// 设置动画初始位置
	}

	public class TabTitleOnClickListener implements OnClickListener {
		int moveIndex = 0;

		public TabTitleOnClickListener(int index) {
			moveIndex = index;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(moveIndex);
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			/*
			 * 两种方法，这个是一种，下面还有一种，显然这个比较麻烦 Animation animation = null; switch
			 * (arg0) { case 0: if (currIndex == 1) { animation = new
			 * TranslateAnimation(one, 0, 0, 0); } else if (currIndex == 2) {
			 * animation = new TranslateAnimation(two, 0, 0, 0); } break; case
			 * 1: if (currIndex == 0) { animation = new
			 * TranslateAnimation(offset, one, 0, 0); } else if (currIndex == 2)
			 * { animation = new TranslateAnimation(two, one, 0, 0); } break;
			 * case 2: if (currIndex == 0) { animation = new
			 * TranslateAnimation(offset, two, 0, 0); } else if (currIndex == 1)
			 * { animation = new TranslateAnimation(one, two, 0, 0); } break;
			 * 
			 * }
			 */
			Animation animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			mImageViewTab.startAnimation(animation);
			// Toast.makeText(WeiBoActivity.this, "您选择了" +
			// viewPager.getCurrentItem() + "页卡", Toast.LENGTH_SHORT).show();
		}
	}

	public class TabFragmentAdapter extends FragmentPagerAdapter {
		protected Context con;

		public TabFragmentAdapter(FragmentManager fm, Context con) {
			super(fm);
			this.con = con;
		}

		@Override
		public Fragment getItem(int arg0) {
			return null;
		}

		@Override
		public int getCount() {
			return 0;
		}

	}

}
