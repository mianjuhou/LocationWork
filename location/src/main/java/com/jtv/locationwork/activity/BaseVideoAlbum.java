package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.List;

import com.jtv.base.ui.VideoImageView;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.util.Constants;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

public abstract class BaseVideoAlbum extends BaseGridPhotoAty implements OnItemClickListener {
	protected ArrayList<DBFileState> mData = new ArrayList<DBFileState>();

	protected BaseAdapter mAdapter = null;

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
		if (mData != null) {
			mData.clear();
			DBFileState empty = new DBFileState();
			mData.add(empty);
		}
		if (mAdapter == null) {
			mAdapter = new PhotoAlbumState(this, mData);
		}
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
	}

	public abstract void onItemClick(Boolean oneClick, AdapterView<?> parent, View view, int position, int faceposition,
			ViewGroup child);

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

		Object obj = parent.getItemAtPosition(position);
		int arg4 = position - parent.getFirstVisiblePosition();
		ViewGroup child = (ViewGroup) parent.getChildAt(arg4);
		if (position == 0 || obj == null) {// 代表的是拍照
			onItemClick(true, parent, arg1, position, arg4, child);
		} else {
			onItemClick(false, parent, arg1, position, arg4, child);
		}
	}

	public void refershDate() {
		refershDate(null);
	}

	public void refershDate(ArrayList<DBFileState> list) {
		if (list != null) {
			mData = list;
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	public class PhotoAlbumState extends BaseAdapter {
		private int width;

		private LayoutInflater mInflater;

		private List<DBFileState> list;

		private ViewHolder viewHolder;

		private DBFileState photoInfo;

		public PhotoAlbumState(Context context, List<DBFileState> list) {
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
			width = dm.widthPixels / 3;
			mInflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list == null) {
				return 0;
			}
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return null;
			}
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.location_jtv_videogrid_item, null);
				VideoImageView imageView = (VideoImageView) convertView.findViewById(R.id.iv_thumbnail);
				TextView tv_xuanzhong = (TextView) convertView.findViewById(R.id.tv_xuanzhong);
				TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);
				viewHolder.image = imageView;
				viewHolder.tv_xuanzhong = tv_xuanzhong;
				viewHolder.tv_status = tv_status;
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position != 0) {
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
					Bitmap mBit = (Bitmap) photoInfo.getObj2();
					if (mBit != null) {
						viewHolder.image.setDisPlayOnDraw(true);
						viewHolder.image.setImageBitmap(mBit);
					}
					//
					// UniversalImageLoadTool.disPlay(
					// ThumbnailsUtil.MapgetHashValue(photoInfo.getId(),
					// "file://" + photoInfo.getPath()),
					// new RotateImageViewAware(viewHolder.image,
					// photoInfo.getPath()), R.drawable.icon_jtv_eror);
					// 当路径不存在的时候会报can not open 路径
				}
			} else if (position == 0) {
				LayoutParams layoutParams = (LayoutParams) viewHolder.image.getLayoutParams();
				layoutParams.width = width;
				layoutParams.height = width;
				viewHolder.image.setDisPlayOnDraw(false);
				viewHolder.image.setLayoutParams(layoutParams);
				viewHolder.image.setImageResource(R.drawable.asx);
				viewHolder.tv_status.setVisibility(View.GONE);
				viewHolder.tv_xuanzhong.setVisibility(View.GONE);
			}
			return convertView;
		}

		public class ViewHolder {
			public TextView tv_status;

			public VideoImageView image;

			public TextView tv_xuanzhong;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else {

			}
			return super.getItemViewType(position);
		}

		public List<DBFileState> getCurrList() {
			return list;
		}

	}

	@Override
	public void onClick(View v) {

	}

}
