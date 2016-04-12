package com.jtv.locationwork.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import java.util.List;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.activity.PhotoAty;
import com.jtv.locationwork.activity.VideoPlayerAty;
import com.jtv.locationwork.activity.WorkHandOverTabAty;
import com.jtv.locationwork.adapter.VideoGridAdapter;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.entity.FileState;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.UniversalImageLoadTool;

/**
 * 视频缩略图的fragment
 * 
 * @author lgs
 * 
 */
public class VideoFaragment extends BaseFragment {
	private GridView gridView;
	private VideoGridAdapter photoAdapter;

	@Override
	public View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.location_jtv_photogrid, null, false);
	}

	public void refershAdapter(List<DBFileState> mList) {
		if (photoAdapter != null) {
			photoAdapter.refershDate(mList);
		}
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		gridView = (GridView) getView().findViewById(R.id.gv_photos);
		gridView.setNumColumns(2);
		photoAdapter = new VideoGridAdapter(getActivity(), WorkHandOverTabAty.mVideo);
		gridView.setAdapter(photoAdapter);
		gridView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == 0) {
					UniversalImageLoadTool.resume();
				} else {
					UniversalImageLoadTool.pause();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position, long arg3) {
				// return onPhotoClickListener.onVideoClickLongListener(parent,
				// arg1, arg2, arg3,
				// list.get(arg2).getPath());
				// int arg4 = position - parent.getFirstVisiblePosition();
				// ViewGroup child = (ViewGroup) parent.getChildAt(arg4);
				Object itemAtPosition = parent.getItemAtPosition(position);
				DBFileState mFs = (DBFileState) itemAtPosition;
				String path_absolute = mFs.getPath();
				Intent intent = new Intent(getContext(), VideoPlayerAty.class);
				intent.putExtra(VideoPlayerAty.VIDEO_PATH, path_absolute);
				startActivity(intent);
				return true;
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {
				// onPhotoClickListener.onVideoClickListener(list.get(position)
				// .getPath_absolute());
				// onPhotoClickListener.onVideoClickListener(parent, view,
				// position, id, list.get(position).getPath());
				int arg4 = position - parent.getFirstVisiblePosition();
				ViewGroup child = (ViewGroup) parent.getChildAt(arg4);

				Object itemAtPosition = parent.getItemAtPosition(position);
				DBFileState mFs = (DBFileState) itemAtPosition;
				int childCount = child.getChildCount();
				int state = mFs.getState();
				boolean mchoose = (Boolean) mFs.getObj();
				View mChoose = null;
				for (int i = 0; i < childCount; i++) {
					View childAt = child.getChildAt(i);
					int id = childAt.getId();
					if (childAt instanceof TextView && id == R.id.tv_xuanzhong) {
						mChoose = childAt;
						break;
					}
				}
				if (mChoose == null)
					return;

				switch (state) {
				case Constants.READY_UPLOAD:
					if (mchoose) {// 选中
						mChoose.setVisibility(View.GONE);
						mFs.setObj(false);
					} else {
						mChoose.setVisibility(View.VISIBLE);
						mFs.setObj(true);
					}
					break;

				case Constants.UPLOADING:

					break;
				case Constants.UPLOAD_FINISH:

					break;
				}

			}
		});

	}

}
