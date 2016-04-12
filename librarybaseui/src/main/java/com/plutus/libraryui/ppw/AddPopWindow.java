package com.plutus.libraryui.ppw;

import java.util.ArrayList;

import com.plutus.libraryui.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 
 * @author beyound
 * @email  whatl@foxmail.com
 * @date   2015年9月16日
 */
public class AddPopWindow extends PopupWindow {
	private LinearLayout linearLayout;
	private View view;
	private ImageView iv;
	private TextView tv;
	private LayoutInfo line;
	private View view_line;

	// LayoutInflater inflater = (LayoutInflater) context
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	public AddPopWindow(final Activity context, ArrayList<LayoutInfo> arr, final onPopUpItemClick mItem)
			throws Exception {
		linearLayout = new LinearLayout(context);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linearLayout.setLayoutParams(layoutParams);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < arr.size(); i++) {
			line = arr.get(i);
			if (line == null) {
				throw new Exception("空值针异常");
			}
			if (TextUtils.isEmpty(line.getTitle())) {
				throw new Exception("空值针异常");
			}
			if (line.getId() < 0) {
				throw new Exception("id异常");
			}
			view = View.inflate(context, R.layout.location_add_popup_dialog, null);
			iv = (ImageView) view.findViewById(R.id.iv_left_icon);
			tv = (TextView) view.findViewById(R.id.tv_text);
			view_line = (View) view.findViewById(R.id.view_line);
			if (i == arr.size() - 1) {// 隐藏最后一个
				view_line.setVisibility(View.GONE);
			}
			view.setId(line.getId());
			iv.setBackgroundResource(line.getDrawable());
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mItem != null) {
						mItem.onItemClick(v);
					}
				}
			});
			tv.setText(line.getTitle());
			linearLayout.addView(view);
		}
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(linearLayout);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w / 2 + 50);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态 this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// 设置SelectPicPopupWindow弹出窗体动画效果
	}

	/** * 显示popupWindow * * @param parent */
	public void showPopupWindow(View parent) {
		showPopupWindow(parent, 0, 0);
	}
	
	public void showPopupWindow(View parent, int xoff, int yoff) {

		if (!this.isShowing()) {
			// 以下拉方式显示popupwindow
			this.showAsDropDown(parent, xoff, yoff);
		} else {
			this.dismiss();
		}
	}

}
