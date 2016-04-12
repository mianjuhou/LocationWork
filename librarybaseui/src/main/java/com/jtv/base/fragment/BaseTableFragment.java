package com.jtv.base.fragment;

import java.util.List;

import com.jtv.base.util.CollectionActivity;
import com.plutus.libraryui.R;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
//viewpager ＋ fragment +tabletitle 界面比较炫，但不太实用
@SuppressLint("NewApi")
public abstract class BaseTableFragment extends FragmentActivity {
	protected ViewPager mViewPager;
	protected PagerTabStrip mPager_tabstrip=null;
	protected RelativeLayout mRl=null;
	protected List<Fragment> mListDate = null;
	protected List<String> mTitle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CollectionActivity.putTopActivity(this);
		setContentView(R.layout.base_viewpager_table_fragment);
		init(savedInstanceState);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPager_tabstrip = (PagerTabStrip) findViewById(R.id.pager_tabstrip);
		mRl = (RelativeLayout) findViewById(R.id.rl);
		
//		mPager_tabstrip.setTextColor(Color.RED);
		
		
//		mViewPager.setAdapter(mFragmentStatePagerAdapter);
		
		// mViewPager.setAdapter(mFragmentStatePagerAdapter);
	}

	public void init(Bundle savedInstanceState) {
		
	}

	protected FragmentStatePagerAdapter mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(
			getSupportFragmentManager()) {

//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {
//			return (arg0 == arg1);
//		}
//
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeView(mListDate.get(position));
//		}
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			container.addView(mListViews.get(position), 0);
//			return mListViews.get(position);
//		}

		@Override
		public int getCount() {
			return BaseTableFragment.this.getCount();
		}

		@Override
		public Fragment getItem(int position) {
			return BaseTableFragment.this.getItem(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return BaseTableFragment.this.getPageViewTitle(position);
		}
	};

	public abstract int getCount();

	public abstract Fragment getItem(int position);

	public abstract CharSequence getPageViewTitle(int position);

}
