package com.jtv.locationwork.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import com.jtv.base.activity.BaseAty;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.util.Constants;

public class VideoPlayerAty extends BaseAty implements OnClickListener {
	private VideoView vid;
	private String path;
	private String title = "播放视频";
	private RelativeLayout rl;
	public static final String VIDEO_PATH = "path";
	public static final String TITLE = "tiele";

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		init();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void getParmter() {
		path = getIntent().getStringExtra(VIDEO_PATH);
		boolean hasExtra = getIntent().hasExtra(TITLE);
		if (hasExtra) {
			title = getIntent().getStringExtra(TITLE);
		}

	}

	public void init() {
		setContentView(R.layout.location_jtv_video_player);
		getHeaderTitleTv().setText("播放视频");
		getHeaderBackBtn().setVisibility(View.VISIBLE);
		getHeaderBackBtn().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getParmter();
		setHeaderTitleText(title);
		vid = (VideoView) findViewById(R.id.video);
		rl = (RelativeLayout) findViewById(R.id.rl);
		vid.setVideoPath(path);
		vid.start();
		vid.setMediaController(new MediaController(this));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (vid == null)
			return;
		if (vid.isPlaying()) {
			vid.stopPlayback();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
	}

}
