package com.jtv.locationwork.adapter;

import java.util.List;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.RotateImageViewAware;
import com.jtv.locationwork.util.ThumbnailsUtil;
import com.jtv.locationwork.util.UniversalImageLoadTool;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoGridAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DBFileState> list;
	private ViewHolder viewHolder;
	private int width;
	private DBFileState photoInfo;

	public List<DBFileState> getPhotoStatus() {
		return list;
	}

	public PhotoGridAdapter(Context context, List<DBFileState> list) {
		DisplayMetrics dm = new DisplayMetrics();
		((FragmentActivity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels / 3;
		mInflater = LayoutInflater.from(context);
		this.list = list;
		System.out.println("list; " + list);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int paramInt) {
		return list.get(paramInt);
	}

	@Override
	public long getItemId(int paramInt) {
		return paramInt;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup paramViewGroup) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.location_jtv_photogrid_item, null);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
			TextView tv_xuanzhong = (TextView) convertView.findViewById(R.id.tv_xuanzhong);
			TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHolder.image = imageView;
			viewHolder.tv_xuanzhong = tv_xuanzhong;
			viewHolder.tv_status = tv_status;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		photoInfo = list.get(position);
		if (photoInfo == null) {
			// return convertView;
		}
		boolean choose = false;
		if (photoInfo.getObj() == null) {
			photoInfo.setObj(false);
		}
		choose = (Boolean) photoInfo.getObj();
		if (choose) {// 如果当前的状态是要上传的就为true
			viewHolder.tv_xuanzhong.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tv_xuanzhong.setVisibility(View.GONE);
		}
		if (list.get(position).getState() < 5) {// 没有状态
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_readyupload);
			viewHolder.tv_status.setVisibility(View.VISIBLE);
		} else if (Constants.UPLOADING == list.get(position).getState()) {// 上传中
			viewHolder.tv_status.setVisibility(View.VISIBLE);
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_uploading);
		} else if (Constants.UPLOAD_FINISH == (list.get(position).getState())) {// 上传完成
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_uploafinish);
			viewHolder.tv_status.setVisibility(View.VISIBLE);
			viewHolder.tv_xuanzhong.setVisibility(View.GONE);
			list.get(position).setObj(false);
		} else if (Constants.READY_UPLOAD == list.get(position).getState()) {// 准备上传
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_readyupload);
			viewHolder.tv_status.setVisibility(View.VISIBLE);
		}
		LayoutParams layoutParams = (LayoutParams) viewHolder.image.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = width;
		viewHolder.image.setLayoutParams(layoutParams);
		if (photoInfo != null) {// 显示
			UniversalImageLoadTool.disPlay(
					ThumbnailsUtil.MapgetHashValue(photoInfo.getId(), "file://" + photoInfo.getPath()),
					new RotateImageViewAware(viewHolder.image, photoInfo.getPath()), R.drawable.icon_jtv_eror);
			// 当路径不存在的时候会报can not open 路径
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView tv_status;
		public ImageView image;
		public TextView tv_xuanzhong;
	}

	/**
	 * 刷新当前的数据
	 */
	public void refershDate(List<DBFileState> list) {
		if (list != null) {
			this.list = list;
			this.notifyDataSetChanged();
		}
	}
}
