package com.jtv.locationwork.fragment;

import java.util.List;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.activity.PainterAty;
import com.jtv.locationwork.activity.PhotoAty;
import com.jtv.locationwork.activity.WorkHandOverTabAty;
import com.jtv.locationwork.adapter.PhotoGridAdapter;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.UniversalImageLoadTool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class PhotoGridFragment extends BaseFragment {

	private GridView gridView;
	private PhotoGridAdapter photoAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

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

		photoAdapter = new PhotoGridAdapter(getActivity(), WorkHandOverTabAty.mPhoto);

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
		/**
		 * 图片长按的点击事件选中
		 */
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position, long arg3) {

				Object itemAtPosition = parent.getItemAtPosition(position);
				DBFileState mFs = (DBFileState) itemAtPosition;
				String path_absolute = mFs.getPath();
				Intent intent = new Intent(getContext(), PainterAty.class);
				intent.putExtra("path", path_absolute);
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
