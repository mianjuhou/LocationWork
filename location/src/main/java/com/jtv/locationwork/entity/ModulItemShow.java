package com.jtv.locationwork.entity;

import com.jtv.locationwork.listener.ModulItemListener;
import com.plutus.libraryui.ppw.PopUpWindowsForIos;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 弹出一个popupwindows
 * 
 * @author beyound
 * @email whatl@foxmail.com
 * @date 2015年9月17日
 */
public class ModulItemShow extends ModulItem implements ModulItemListener {
	protected PopUpWindowsForIos mPop;

	protected View mView;

	private static PopUpWindowsForIos mPopIng;

	/**
	 * 
	 * @param con
	 *            上下文
	 * @param mView
	 *            需要显示在哪个view上
	 */
	public ModulItemShow(Context con, View mView) {
		this(con, mView, null);
	}

	/**
	 * 
	 * @param con
	 *            上下文
	 * @param mView
	 *            需要显示在哪个view上
	 */
	public ModulItemShow(Context con, View mView, PopUpWindowsForIos mpop) {
		back = this;
		this.mView = mView;
		if (mpop == null) {
			mpop = new PopUpWindowsForIos(con, new FrameLayout(con)); // 每个条目的popup
		}
		this.mPop = mpop;

	}

	@Override
	public void setBack(ModulItemListener back) {
	}

	public PopUpWindowsForIos getPopForIos() {
		return mPop;
	}

	@Override
	public void doSomeThing(Object... obj) {
		if (mPop == null) {
			return;
		}
		if (mView != null) {
			closedPopIng();
			mPop.show(mView);
			mPopIng = mPop;
		}
	}

	public static PopUpWindowsForIos getPopIng() {
		return mPopIng;
	}

	public static void closedPopIng() {
		if (getPopIng() != null) {
			getPopIng().closePopUp();
		}
	}

}
