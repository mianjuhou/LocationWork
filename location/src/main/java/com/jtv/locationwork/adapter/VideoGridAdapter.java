package com.jtv.locationwork.adapter;

import java.util.List;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.util.Constants;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoGridAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DBFileState> list;
	private ViewHolder viewHolder;
	private int width;
	private Bitmap bitmap;
	private Context context;

	public List<DBFileState> getPhotoStatus() {
		return list;
	}

	public VideoGridAdapter(Context context, List<DBFileState> list) {
		DisplayMetrics dm = new DisplayMetrics();
		((FragmentActivity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels / 2;
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		System.out.println("list; " + list);
	}

	/**
	 * 刷新数据到界面
	 * 
	 * @param list
	 */
	public void refershDate(List<DBFileState> list) {
		if (list != null && list.size() > 0) {
			this.list = list;
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
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
	public View getView(int paramInt, View convertView, ViewGroup paramViewGroup) {
		DBFileState photoInfo = list.get(paramInt);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.location_jtv_videogrid_item, null);
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
		DBFileState mFilse = list.get(paramInt);
		boolean isChoose = false;
		try {
			isChoose = (Boolean) mFilse.getObj();

		} catch (Exception e) {
			mFilse.setObj(false);
		}

		int state = mFilse.getState();
		if (isChoose) {
			viewHolder.tv_xuanzhong.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tv_xuanzhong.setVisibility(View.GONE);
		}
		if (state < 5) {// 没有状态
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_readyupload);
			viewHolder.tv_status.setVisibility(View.VISIBLE);
		} else if (Constants.UPLOADING == state) {// 上传中
			viewHolder.tv_status.setVisibility(View.VISIBLE);
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_uploading);
		} else if (Constants.UPLOAD_FINISH == state) {// 上传完成
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_uploafinish);
			viewHolder.tv_status.setVisibility(View.VISIBLE);
			viewHolder.tv_xuanzhong.setVisibility(View.GONE);
			list.get(paramInt).setObj(false);
		} else if (Constants.READY_UPLOAD == state) {// 准备上传
			viewHolder.tv_status.setBackgroundResource(R.drawable.icon_status_readyupload);
			viewHolder.tv_status.setVisibility(View.VISIBLE);
		}
		LayoutParams layoutParams = viewHolder.image.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = width;
		viewHolder.image.setLayoutParams(layoutParams);
		if (photoInfo != null) {// 显示
			// int video_id = photoInfo.getVideo_id();
			// UniversalImageLoadTool.disPlay(
			// VideoThumbnailsUtil.MapgetHashValue(video_id,
			// photoInfo.getPath_file()),
			// new RotateImageViewAware(viewHolder.image, photoInfo
			// .getPath_absolute()), R.drawable.icon_jtv_eror);
			bitmap = (Bitmap) photoInfo.getObj2();
			if (bitmap != null) {
				viewHolder.image.setImageBitmap(bitmap);
			}
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView tv_status;
		public ImageView image;
		public TextView tv_xuanzhong;
	}
}
