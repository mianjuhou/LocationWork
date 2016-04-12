package com.jtv.locationwork.activity;

import java.util.ArrayList;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.entity.ModulItem;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuGridView extends BaseGridView implements OnItemClickListener {
	protected static ArrayList<ModulItem> menu = new ArrayList<ModulItem>();

	protected BaseAdapter mAdapter = null;

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
		if (menu != null && menu.size() > 0) {
			mAdapter = new MenuGridAdapter(this);
			mGridView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onClick(View v) {
	}

	public ModulItem add(ModulItem mItem) {
		int position = mItem.getPosition();
		menu.add( mItem);
		return mItem;
	}

	public void adds(ArrayList<ModulItem> mArr) {
		menu = mArr;
	}

	public void remove(int position) {
		int p = -1;
		for (int i = 0; i < menu.size(); i++) {
			int mposition = menu.get(i).getPosition();
			if (mposition == position) {
				p = i;
			}
		}
		if (menu.size() > p && p != -1) {
			menu.remove(p);
		}
	}

	public void remove(ModulItem mmenu) {
		int p = -1;
		for (int i = 0; i < menu.size(); i++) {
			if (mmenu.hashCode() == menu.get(i).hashCode()) {
				p = i;
			}
		}
		if (menu.size() > p && p != -1) {
			menu.remove(p);
		}
		refersh();
	}

	protected void refersh() {

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		} else if (mGridView != null) {
			mAdapter = new MenuGridAdapter(this);
			mGridView.setAdapter(mAdapter);
		}

	}

	class MenuGridAdapter extends BaseAdapter {
		protected Context mContext;

		protected TranslateAnimation anim = new TranslateAnimation(0, 0, 100, 0);

		LayoutInflater mInflater;

		public MenuGridAdapter(Context con) {
			this.mContext = con;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			if (menu == null) {
				return 0;
			}
			return menu.size();
		}

		@Override
		public Object getItem(int position) {
			return menu.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ModulItem moudlItem = menu.get(position);
			ViewHolder mHolder;
			if (convertView == null) {

				mHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.location_jtv_homegrid_item, null);
				mHolder.mlinear = (LinearLayout) convertView.findViewById(R.id.lin_item_center);
				mHolder.mImage = (ImageView) convertView.findViewById(R.id.iv_item_center);
				mHolder.mText = (TextView) convertView.findViewById(R.id.tv_title);
				convertView.setTag(mHolder);

			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			bundle(position, convertView, parent, moudlItem, mHolder);

			return convertView;
		}

		private void bundle(int position, View convertView, ViewGroup parent, ModulItem moudlItem, ViewHolder mHolder) {
			int icon = moudlItem.getIcon();
			int stateIcon = moudlItem.getStateIcon();
			String title = moudlItem.getTitle();
			if (icon > 0) {
				mHolder.mImage.setImageResource(icon);
			}
			if (stateIcon > 0) {
				mHolder.mlinear.setBackgroundResource(stateIcon);
			}
			mHolder.mText.setText(title);

		}

		class ViewHolder {
			public LinearLayout mlinear;

			public TextView mText;

			public ImageView mImage;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int arg2, long arg3) {
		// int arg4 = arg2 - parent.getFirstVisiblePosition();
		// ViewGroup at = (ViewGroup) parent.getChildAt(arg4);
		// if (at == null) {
		// return;
		// }
		// int childCount = at.getChildCount();
		ModulItem mItem = (ModulItem) parent.getItemAtPosition(arg2);
		if (mItem != null && mItem.getBack() != null) {
			mItem.getBack().doSomeThing(mItem);
		}
	}
}
