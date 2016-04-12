package com.plutus.libraryui.ppw;

import java.util.List;

import com.plutus.libraryui.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopUpWindowsForIos extends PopupWindow implements OnClickListener {
	private FrameLayout parent = null;
	private LinearLayout mPanel = null;
	private Context con;
	private int textSize;
	private int textColor;

	private Animation createAlphaInAnimation() {
		AlphaAnimation an = new AlphaAnimation(0, 1);
		an.setDuration(450);
		return an;
	}

	public PopUpWindowsForIos(Context con, FrameLayout frame) {
		super(frame);
		this.con = con;
		// 修改文字尺寸，等于0代表系统默认
		textSize = 0;
		// 修改文字颜色，等于0代表系统默认
		textColor = 0xff1E82FF;
		parent = frame;
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setOutsideTouchable(false);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		frame.setOnClickListener(this);
		// 设置背景色
		parent.setBackgroundColor(Color.argb(136, 0, 0, 0));
		// 填充父窗体
		parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// 线性布局中装条目
		mPanel = new LinearLayout(con);
		// 包裹内容
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 在fragment底部
		params.gravity = Gravity.BOTTOM;
		mPanel.setOrientation(LinearLayout.VERTICAL);
		mPanel.setLayoutParams(params);
		parent.addView(mPanel);
		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (listener != null) {
					listener.onDismiss();
				}
			}
		});
	}

	public void addItem(List<? extends PopUpItemBean> item,
			final PopUpItemBean cancel) {
		mPanel.removeAllViews();
		for (int i = 0; i < item.size(); i++) {
			Button bt = new Button(con);
			bt.setId(i);
			PopUpItemBean popUpItemBean = item.get(i);
			bt.setOnClickListener(new OnItemClickListener(popUpItemBean) {

				@Override
				public void onClick(View v, PopUpItemBean pop) {
					if (listener != null) {
						listener.itemonclick(v, pop);
					}
				}
			});
			if (item.size() == 1) {// 只有一个条目
				bt.setBackgroundResource(R.drawable.select_as_ios7_cancel_bt);
			} else {
				if (i == 0) {
					bt.setBackgroundResource(R.drawable.select_as_ios7_other_bt_top);
				} else if (i == item.size() - 1) {
					bt.setBackgroundResource(R.drawable.select_as_ios7_other_bt_bottom);
				} else {
					bt.setBackgroundResource(R.drawable.select_as_ios7_other_bt_middle);
				}
			}
			LinearLayout.LayoutParams parambtn = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			parambtn.leftMargin = 20;
			parambtn.rightMargin = 20;
			if (textSize != 0) {
				bt.setTextSize(textSize);
			}
			// 设置颜色
			if (textColor != 0) {
				bt.setTextColor(textColor);
			}
			bt.setText(popUpItemBean.getText());
			bt.setLayoutParams(parambtn);
			// 线性布局中添加button
			mPanel.addView(bt);
		}
		if (cancel == null) {
			return;
		}
		// 创建取消按钮
		Button bt = new Button(con);
		if (textSize != 0) {
			bt.setTextSize(textSize);
		}
		// 设置颜色
		if (textColor != 0) {
			bt.setTextColor(textColor);
		}
		bt.setText(cancel.getText());
		// 设置取消按钮的边距
		LinearLayout.LayoutParams parambtn2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		parambtn2.topMargin = 20;
		parambtn2.leftMargin = 20;
		parambtn2.rightMargin = 20;
		parambtn2.bottomMargin = 20;
		bt.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (listener != null) {
					listener.cancelonclick(arg0, cancel);
				}
			}
		});
		// 设置按钮的状态选择器
		bt.setBackgroundResource(R.drawable.select_as_ios7_cancel_bt);
		bt.setLayoutParams(parambtn2);
		// 添加取消按钮
		mPanel.addView(bt);
	}

	public void show(View id) {
		boolean showing = isShowing();
		if (showing)
			return;
		parent.startAnimation(createAlphaInAnimation());
		// 开启一个位移动画
		mPanel.startAnimation(createTranslationInAnimation());
		showAtLocation(id, Gravity.BOTTOM, 0, 0);
	}

	public void closePopUp() {
		if (isShowing()) {
			dismiss();
		}
	}

	private Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				1, type, 0);
		an.setDuration(200);
		return an;
	}

	private onPopupClickCallBack listener = null;

	/** 设置popup的回调接口 */
	public void setOnPopUpClickListener(onPopupClickCallBack listener) {
		this.listener = listener;
	}

	public interface onPopupClickCallBack {
		/** 外置触摸关闭popup的事件 */
		public void outSideonclick(View arg0);

		/** 取消按钮的点击事件 */
		public void cancelonclick(View arg0, PopUpItemBean pop);

		/** 可以通过ID来区分序号是数组的循序从0开始 */
		public void itemonclick(View arg0, PopUpItemBean pop);

		/** 当关闭的时候的回调 */
		public void onDismiss();
	}

	/**
	 * 外置触摸的点击事件
	 */
	@Override
	public void onClick(View v) {
		closePopUp();
		if (listener != null) {
			listener.outSideonclick(v);
		}
	}

}

abstract class OnItemClickListener implements OnClickListener {

	private PopUpItemBean pop;

	private OnItemClickListener() {
	}

	public OnItemClickListener(PopUpItemBean pop) {
		this.pop = pop;
	}

	@Override
	final public void onClick(View v) {
		onClick(v, pop);
	}

	public abstract void onClick(View v, PopUpItemBean pop);

}
