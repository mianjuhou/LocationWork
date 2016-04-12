package com.jtv.locationwork.adapter;


import android.support.v4.util.SparseArrayCompat;
import android.view.View;

public class CommonViewHolder {

	/**
	 * 用于获取ItemView中的控件
	 *
	 * @param view
	 *            ItemView
	 * @param id
	 *            要获取的控件的id
	 * @param <T>
	 *            返回的控件的类型
	 * @return 返回的控件
	 */
	public static <T extends View> T get(View view, int id) {
		return get(view, id, null,0);
	}

	/**
	 * 用于获取ItemView中的控件
	 *
	 * @param view
	 *            ItemView
	 * @param id
	 *            要获取的控件的id
	 * @param <T>
	 *            返回的控件的类型
	 * @return 返回的控件
	 */
	public static <T extends View> T get(View view, int id, InitFindID init,int position) {
		SparseArrayCompat<View> viewHolder = (SparseArrayCompat<View>) view.getTag();// 类似hashmap优化
		if (viewHolder == null) {
			viewHolder = new SparseArrayCompat<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
			if (init != null) {
				init.initId(childView,position);
			}

		}
		return (T) childView;
	}

	interface InitFindID {
		void initId(View childView,int position);
	}
}
