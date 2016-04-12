package com.jtv.base.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class LBaseAdapter<T> extends BaseAdapter {
	public List<? extends T> list;

	public Context con;

	public LayoutInflater inflate;

	public LBaseAdapter(Context con, List<? extends T> list) {
		this.list = list;
		this.con = con;
		inflate = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {
		if (list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (list != null)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	final public View getView(int position, View convertView, ViewGroup parent) {
		return getLView(position, convertView, parent);
	}

	public void refershData(List<? extends T> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public abstract View getLView(int position, View convertView, ViewGroup parent);

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
		return get(view, id, null);
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
	public static <T extends View> T get(View view, int id, InitFindID init) {
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
				init.initId(childView);
			}

		}
		return (T) childView;
	}

	interface InitFindID {
		void initId(View childView);
	}

}
