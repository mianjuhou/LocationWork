package com.jtv.locationwork.activity;

import java.io.File;

import com.jtv.base.activity.BaseAty;
import com.jtv.base.util.ScreenUtil;
import com.jtv.hrb.locationwork.R;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class PhotoAty extends BaseAty implements OnClickListener {
	public static final String PHOTO_PATH ="path";
	private ImageView iv_show;
	private String path;

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		init();
	}

	public void init() {
		setContentView(R.layout.location_jtv_photo_display);
		path = getIntent().getStringExtra(PHOTO_PATH);
		getHeaderBackBtn().setVisibility(View.VISIBLE);
		setBackOnClickFinish();
		getHeaderTitleTv().setText("查看图片");
		iv_show = (ImageView) findViewById(R.id.iv_show);
		iv_show.setImageURI(Uri.fromFile(new File(path)));
		iv_show.setOnTouchListener(new TouchListener());
		big();
	}

	@Override
	public void onClick(View v) {
	}

	class TouchListener implements OnTouchListener {
		private int mode = 0; // 记录是拖拉照片模式还是放大缩小照片模式
		private static final int MODE_DRAG = 1; // 拖拉照片模式
		private static final int MODE_ZOOM = 2; // 放大缩小照片模式

		private PointF startPointF = new PointF(); // 用于记录开始时候的坐标位置

		private Matrix matrix = new Matrix(); // 用于记录拖拉图片移动的坐标位置
		private Matrix currentMatrix = new Matrix(); // 用于记录图片要进行拖拉时候的坐标位置

		private float startDis; // 两个手指的开始距离
		private PointF midPointF; // 两个手指的中间点

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 手指压下屏幕 触发
			case MotionEvent.ACTION_DOWN:
				mode = MODE_DRAG;
				// 记录ImageView当前的移动位置
				currentMatrix.set(iv_show.getImageMatrix());
				startPointF.set(event.getX(), event.getY());
				break;
			// 手指在屏幕上移动，改事件会被不断触发
			case MotionEvent.ACTION_MOVE:
				if (mode == MODE_DRAG) {
					float dx = event.getX() - startPointF.x;// 得到x轴的移动距离
					float dy = event.getY() - startPointF.y;// 得到x轴的移动距离
					// 在没有移动之前的位置上进行移动
					matrix.set(currentMatrix);
					matrix.postTranslate(dx, dy);
				} else if (mode == MODE_ZOOM) { // 放大缩小图片
					float endDis = distance(event);// 结束距离
					try {
						if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
							float scale = endDis / startDis; // 得到缩放倍数
							matrix.set(currentMatrix);
							matrix.postScale(scale, scale, midPointF.x, midPointF.y);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				break;
			// 手指离开屏幕
			case MotionEvent.ACTION_UP:
				// 当触点离开屏幕，但是屏幕上还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;//什么都不做
				break;
			// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = MODE_ZOOM;
				/** 计算两个手指间的距离 */
				startDis = distance(event);
				/** 计算两个手指间的中间点 */
				if (startDis > 10f) {// 两个手指并拢在一起的时候像素大于10
					midPointF = mid(event);
					// 记录当前ImageView的缩放倍数
					currentMatrix.set(iv_show.getImageMatrix());
				}
				break;
			}
			iv_show.setImageMatrix(matrix);
			return true;
		}

		// modify not found api for FloatMath.sqrt(dx * dx + dy * dy);
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** 使用勾股定理返回两点之间的距离 */
			return (float) Math.sqrt(dx * dx + dy * dy);
		}

		/** 计算两个手指间的中间点 */
		private PointF mid(MotionEvent event) {
			float midX = (event.getX(1) + event.getX(0)) / 2;
			float midY = (event.getY(1) + event.getY(0)) / 2;
			return new PointF(midX, midY);
		}
	}

	public void big(){
		Matrix imageMatrix = iv_show.getImageMatrix();
		imageMatrix.postScale(3, 3, 0, 0);
		iv_show.setImageMatrix(imageMatrix);
	}
}
