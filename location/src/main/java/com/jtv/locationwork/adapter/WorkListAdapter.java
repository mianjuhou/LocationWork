package com.jtv.locationwork.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.entity.LimitOfMenu;
import com.jtv.locationwork.fragment.SingleHomeFragment;
import com.jtv.locationwork.fragment.WorkFragment;
import com.jtv.locationwork.listener.OnItemDelClickListenter;
import com.jtv.locationwork.listener.OnItemNextClickListener;
import com.jtv.locationwork.util.TextUtil;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkListAdapter extends BaseAdapter {

	protected Context con;

	protected List<ItemWonum> list;

	protected int curPosition;

	protected OnItemNextClickListener mNextListener;
	protected OnItemDelClickListenter mDelListener;

	private int checkQuestionState = -1;// 线路问题检查状态

	private boolean currDay;

	public WorkListAdapter(Context con, List<ItemWonum> list, OnItemNextClickListener mNextListener,
			OnItemDelClickListenter mDelListener) {
		this.con = con;
		this.list = list;
		this.mNextListener = mNextListener;
		this.mDelListener = mDelListener;
	}

	@Override
	public int getCount() {
		if (list == null && list.size() == 0) {
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		curPosition = position;

		if (convertView == null) {
			convertView = View.inflate(con, R.layout.location_jtv_wolist_lvitem, null);
		}
		defaultData(position, convertView, parent);

		return convertView;
	}

	protected void defaultData(int position, View convertView, ViewGroup parent) {

		final ItemWonum itemWoList = list.get(position);

		TextView tv_dis_plandate = CommonViewHolder.get(convertView, R.id.tv_dis_plandate);
		TextView tv_dis_lead = CommonViewHolder.get(convertView, R.id.tv_dis_lead);
		TextView tv_dis_wonum = CommonViewHolder.get(convertView, R.id.tv_dis_wonum);
		TextView tv_dis_planpro = CommonViewHolder.get(convertView, R.id.tv_dis_planpro);
		TextView iv_itemnext = CommonViewHolder.get(convertView, R.id.iv_itemnext);
		TextView tv_desceibute = CommonViewHolder.get(convertView, R.id.tv_desceibute);
		ImageView iv_itemdel = CommonViewHolder.get(convertView, R.id.iv_itemdel);
		TextView iv_wonum_other = CommonViewHolder.get(convertView, R.id.iv_wonum_other);

		iv_itemdel.setBackgroundResource(R.drawable.selector_pressblue_upgray_camera);

		if (mDelListener == null) {
			iv_itemdel.setVisibility(View.GONE);
		}

		if (position == 0) {// 节约资源,是否有权限
			checkQuestionState = LimitOfMenu.getLimitState(11);
			WorkFragment work = (WorkFragment) SingleHomeFragment.creatWorkNumFragment();
			if (work != null) {
				currDay = work.isCurrDay();
			}
		}

		if (currDay) {
			iv_itemdel.setVisibility(View.VISIBLE);
			iv_wonum_other.setVisibility(View.VISIBLE);
		} else {
			iv_itemdel.setVisibility(View.GONE);
			iv_wonum_other.setVisibility(View.GONE);
		}

		if (checkQuestionState == LimitOfMenu.STATE_CAN_USE && currDay) {
			iv_wonum_other.setVisibility(View.VISIBLE);
			iv_wonum_other.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mNextListener.onItemNextListener(itemWoList, -1);
				}
			});
		} else {
			iv_wonum_other.setVisibility(View.GONE);
		}

		iv_itemnext.setVisibility(View.GONE);
		tv_dis_plandate.setText("");
		tv_dis_lead.setText("");
		tv_dis_wonum.setText("");
		tv_dis_planpro.setText("");

		HashMap<String, ItemWoListAttribute> hashMap = itemWoList.get();

		Set<String> keySet2 = hashMap.keySet();

		int i = 0;
		TextView[] item = { tv_dis_wonum, tv_dis_plandate, tv_dis_planpro, tv_dis_lead };

		for (String key : keySet2) {

			if (i >= item.length) {
				break;
			}

			ItemWoListAttribute itemWoListAttribute = hashMap.get(key);

			String disPlayname = itemWoListAttribute.getDisPlayname();

			if (TextUtil.isEmpty(disPlayname)) {
				continue;
			}

			TextView textView = item[i];
			textView.setText(disPlayname + ":" + itemWoListAttribute.getDisValue());

			i++;
		}

		item = null;

		// iv_itemnext.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mNextListener.onItemNextListener(itemWoList, curPosition);
		// }
		// });
		iv_itemdel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDelListener.onItemDelClickListener(itemWoList, curPosition);
			}
		});

	}

	public void onRefresh(List<ItemWonum> list) {
		this.list = list;
		this.notifyDataSetInvalidated();
		// this.notifyDataSetChanged();
	}

}