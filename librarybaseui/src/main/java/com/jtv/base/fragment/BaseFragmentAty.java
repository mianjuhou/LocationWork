package com.jtv.base.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

@SuppressLint("NewApi")
public class BaseFragmentAty extends FragmentActivity implements OnBackStackChangedListener {
	private FragmentManager supportFragmentManager;

	private Fragment showIngFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		supportFragmentManager = getSupportFragmentManager();
		supportFragmentManager.addOnBackStackChangedListener(this);
	}

	@SuppressLint("NewApi")
	public void switchFragment(int id, Fragment nextfragment) {
		switchFragment(id, showIngFragment, nextfragment, null);
	}
	private List<Fragment> allFragment = new ArrayList<Fragment>();
	public List<Fragment> getFragment(){
		return allFragment;
	}

	/**
	 * 
	 */
	@SuppressLint("NewApi")
	public void switchFragment(int id, Fragment showfragment, Fragment nextfragment) {
		switchFragment(id, showfragment, nextfragment, null);
	}

	/**
	 * 
	 * @param id
	 *            替换的id
	 * @param showfragment
	 *            当前正在展示的fragment
	 * @param nextfragment
	 *            需要展示的fragment
	 * @param tag
	 *            标记
	 */
	@SuppressLint("NewApi")
	public void switchFragment(int id, Fragment showfragment, Fragment nextfragment, String tag) {
		FragmentTransaction beginTransaction;
		beginTransaction = supportFragmentManager.beginTransaction();
		if (switchListener != null) {
			beginTransaction = switchListener.setSwitchFragmentParmter(beginTransaction);
		}
//		List<Fragment> fragList = supportFragmentManager.getFragments();
		if (allFragment != null) {
			for (int i = 0; i < allFragment.size(); i++) {
				Fragment fragment = allFragment.get(i);
				if (!fragment.isHidden()) {
					beginTransaction.hide(fragment);
				}
				fragment = null;
			}
			if(allFragment.size()>15){
				allFragment.clear();
			}
//			allFragment.clear();
		}
//		fragList = null;
		if (showfragment != null && showfragment.isAdded()) {
			beginTransaction.hide(showfragment);
		}
		if (nextfragment.isAdded()) {
			beginTransaction.show(nextfragment);
		} else {// 没添加
			if (TextUtils.isEmpty(tag)) {
				beginTransaction.add(id, nextfragment);
			} else {
				beginTransaction.add(id, nextfragment, tag);
			}
		}
		beginTransaction.commit();
		if (count > 0) {
			count--;
		}
		allFragment.add(nextfragment);
		showIngFragment = nextfragment;
	}

	public void setOnSwitchFragmentParmter(OnSwitchFragmentParmter switchListener) {
		this.switchListener = switchListener;
	}

	private OnSwitchFragmentParmter switchListener;

	public interface OnSwitchFragmentParmter {
		public FragmentTransaction setSwitchFragmentParmter(FragmentTransaction beginTransaction);
	}
	// public abstract ProvderFactory{
	// public abstract Fragment creatFragment();
	// }

	@Override
	public void onBackStackChanged() {
		// setCount();
		// stack();
		// int backStackEntryCount = supportFragmentManager.getBackStackEntryCount();
		// System.out.println("hhhhhhhhhh" + backStackEntryCount);
		// if (countStack > 0) {
		// if (backStackEntryCount > countStack) {
		// for (int i = 0; i < backStackEntryCount - countStack; i++) {
		// BackStackEntry backStackEntryAt = supportFragmentManager.getBackStackEntryAt(i);
		// int id = backStackEntryAt.getId();
		// String name = backStackEntryAt.getName();
		// System.out.println("name" + name + "id" + id);
		// supportFragmentManager.popBackStack(id, 1);
		// }
		//
		// }
		// }
	}

	int count = 0;

	public void setCount() {
		count++;
	}

	private void stack() {
		if (countStack > -1) {
			if (count >= countStack) {
				// int backStackEntryCount = supportFragmentManager.getBackStackEntryCount();
				supportFragmentManager.removeOnBackStackChangedListener(this);
				// for (int i = 0; i < backStackEntryCount; i++) {
				// supportFragmentManager.popBackStack();
				// }
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		setCount();
		stack();
		super.onBackPressed();
	}

	int countStack = 0;
	/**
	 * 设置onback点击次数关闭界面
	 * @param count
	 */
	public void setOnbackFinishCount(int count) {
		countStack = count;
	}
}
