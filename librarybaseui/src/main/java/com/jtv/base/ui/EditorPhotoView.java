package com.jtv.base.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.jtv.base.ui.TouchView.SetViewListener;
import com.plutus.libraryui.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class EditorPhotoView extends FrameLayout {
	private Context con;
	private View mTouchView;
	private int mHeaderHei;
	private int startX;
	private int startY;
	private int moveStartX;
	private int moveStartY;
	private int moveEndX;
	private int moveEndY;
	private int endX;
	private int endY;
	private Paint mRect;
	private ImageView iv_photo1;// 这个是画板
	private ImageView iv_photo;
	private int color = Color.RED;
	private MODEL model = MODEL.DEFAULT;
	private int count = 1;

	private int POINT_MASK = 0;
	private int POINT_ONE = 1;
	private int POINT_TWO = 2;
	private float startDis;
	private int width;
	private int height;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public enum MODEL {
		PAINTER, TEXT, DEFAULT, EDITTEXT
	}

	public void setModule(MODEL modle) {
		this.model = modle;
	}

	public EditorPhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public EditorPhotoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public EditorPhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public EditorPhotoView(Context context, int w, int h) {
		super(context);
		init(context);
	}

	/**
	 * 获取一个焦点的文本对象
	 * 
	 * @return
	 */
	public TextView getFocusText() {
		if (mTouch != null&&mTouch.getVisibility()==View.VISIBLE) {
			int childCount2 = ((ViewGroup) mTouch).getChildCount();
			for (int i = 0; i < childCount2; i++) {
				View childAt = mTouch.getChildAt(i);

				if (childAt != null && childAt instanceof TextView && R.id.tv_count != childAt.getId()&&childAt.getVisibility()==View.VISIBLE) {
					return (TextView) childAt;
				}
			}
		}
		return null;
	}

	private void init(Context con) {
		this.con = con;
		setWillNotDraw(false);

		mRect = new Paint();
		mRect.setColor(Color.RED);// 设置红色
		mRect.setStyle(Paint.Style.STROKE);// 设置空心
		mRect.setStrokeWidth(2.5f);// 线条宽度

		iv_photo1 = new ImageView(con);

		iv_photo = new ImageView(con);
		iv_photo.setDrawingCacheEnabled(true);// iv_photo1显示画框轨迹，实际上不存放图片
		addView(iv_photo);
		addView(iv_photo1);

		iv_photo1.setDrawingCacheEnabled(true);// iv_photo1显示画框轨迹，实际上不存放图片
		FrameLayout.LayoutParams layoutParams = (LayoutParams) iv_photo.getLayoutParams();
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = LayoutParams.MATCH_PARENT;
		iv_photo.setLayoutParams(layoutParams);
	}

	public void setImageBitmap(Bitmap bm) {
		iv_photo.setImageBitmap(bm);
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

	private int oldLineLeng = 0;
	private boolean firstDown = true;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			POINT_MASK = POINT_ONE;
			startX = (int) event.getX();
			startY = (int) event.getY();
			moveStartX = (int) event.getX();
			moveStartY = (int) event.getY();
			// return super.dispatchTouchEvent(event);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			POINT_MASK = POINT_TWO;
			firstDown = true;
			startDis = distance(event);
			this.oldLineLeng = (int) startDis/2;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mTouchView != null) {
				moveEndX = (int) event.getX();
				moveEndY = (int) event.getY();
				moveView(moveEndX - moveStartX, moveEndY - moveStartY);
				moveStartX = (int) event.getX();
				moveStartY = (int) event.getY();
			} else {
				iv_photo1.setImageBitmap(null);
				Bitmap bitmap = iv_photo1.getDrawingCache();
				if (bitmap == null)
					break;

				Paint p = getRedRectPaint();

				// 实际y(相对应屏幕顶点的坐标)应该减去上方的状态栏和标题栏高度,下面两行不能提取出来公共的计算等式
				int top = startY - mHeaderHei;
				int bottom = (int) event.getY() - mHeaderHei;
				Canvas canvas = new Canvas(bitmap);
				canvas.drawRect(startX, top, (int) event.getX(), bottom, p);// 长方形
				iv_photo1.setImageBitmap(bitmap);
			}
			if (POINT_MASK == POINT_TWO && mTouchView != null) {
				float endDis = distance(event);// 结束距离
				try {
					if (!firstDown) { // 两个手指并拢在一起的时候像素大于10

						float scale = endDis / startDis; // 得到缩放倍数
						width = mTouchView.getWidth();
						height = mTouchView.getHeight();
						double xie = Math.sqrt(width * width + height * height);
						
						double newXie = xie*scale;
						int newhi =(int) (height*newXie/xie);
						int newwi =(int) (width*newXie/xie);
						
						FrameLayout.LayoutParams params = (LayoutParams) mTouchView.getLayoutParams();
						params.width = newwi;
						params.height = newhi;
						mTouchView.setLayoutParams(params);
						// iv_photo1.setImageBitmap(bitmap);
						startDis=endDis;
					}
					firstDown = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;

		case MotionEvent.ACTION_POINTER_UP:
			POINT_MASK = 0;
			break;
		case MotionEvent.ACTION_UP:
			if(POINT_MASK==POINT_ONE){
				endX = (int) event.getX();
				endY = (int) event.getY();
				if (mTouchView != null) {
					if (Math.abs(startX - endX) < 10 && Math.abs(startY - endY) < 10) {
						if ((mTouchView.findViewById(R.id.delete_view)).getVisibility() == View.VISIBLE) {
							(mTouchView.findViewById(R.id.delete_view)).setVisibility(View.INVISIBLE);
						} else {
							(mTouchView.findViewById(R.id.delete_view)).setVisibility(View.VISIBLE);
						}
					}
					FrameLayout.LayoutParams params = (LayoutParams) mTouchView.getLayoutParams();
					params.leftMargin = mTouchView.getLeft();
					params.topMargin = mTouchView.getTop();
					mTouchView.setLayoutParams(params);
					mTouchView = null;
				} else {
					iv_photo1.setImageBitmap(null);
					if (Math.abs(startX - endX) < 20 || Math.abs(startY - endY) < 20)
						break;
					if (startX < endX && startY < endY) {
						addView(startX, startY, endX, endY);
					} else if (startX > endX && startY < endY) {
						addView(endX, startY, startX, endY);
					} else if (startX < endX && startY > endY) {
						addView(startX, endY, endX, startY);
					} else if (startX > endX && startY > endY) {
						addView(endX, endY, startX, startY);
					}
				}
			}
		
			break;
		default:
			break;
		}
		return true;
	}

	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// if(ev.getAction()==MotionEvent.ACTION_MOVE||ev.getAction()==MotionEvent.ACTION_UP){
	// return true;
	// }
	// // TODO Auto-generated method stub
	// return false;
	// }

	private Paint getRedRectPaint() {
		// Paint p = new Paint();// 创建画笔
		// p.setColor(Color.RED);// 设置红色
		// p.setStyle(Paint.Style.STROKE);// 设置空心
		// p.setStrokeWidth(2.5f);// 线条宽度
		return mRect;
	}

	private TouchView mTouch = null;

	public void addView(int left, int top, int right, int bottom) {
		final View bgview = LayoutInflater.from(con).inflate(R.layout.jtv_module_basetouch_layout, null);
		final TextView deleteView = (TextView) bgview.findViewById(R.id.delete_view);
		TextView tv_count = (TextView) bgview.findViewById(R.id.tv_count);
		tv_count.setText("" + count);
		count++;
		deleteView.setVisibility(View.INVISIBLE);
		deleteView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditorPhotoView.this.removeView(bgview);
				mTouchView = null;
			}
		});
		TouchView touchView = (TouchView) bgview.findViewById(R.id.touch_view);
		touchView.setListener(new SetViewListener() {
			@Override
			public void getView(TouchView view) {
				mTouchView = bgview;
				mTouch = view;
			}

		});
		deleteView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		FrameLayout.LayoutParams params = new LayoutParams(right - left, bottom - top + deleteView.getMeasuredHeight());
		params.leftMargin = left;
		params.topMargin = top - mHeaderHei - deleteView.getMeasuredHeight();
		touchView.setTag(params.leftMargin + "," + params.topMargin);
		View view = null;
		if (getViewListener != null && model != null) {
			view = getViewListener.getView(model, null, Math.abs(right - left), Math.abs(bottom - top));
			if (view != null) {
				touchView.addView(view);
			}
		}

		EditorPhotoView.this.addView(bgview, params);

		if (getViewListener != null && model != null && view != null) {
			getViewListener.setLayouot(model, view);
		}
	};

	private void moveView(int moveX, int moveY) {
		int l = mTouchView.getLeft() + moveX;
		int t = mTouchView.getTop() + moveY;
		int r = mTouchView.getRight() + moveX;
		int b = mTouchView.getBottom() + moveY;
		mTouchView.layout(l, t, r, b);
		TouchView touchView = (TouchView) mTouchView.findViewById(R.id.touch_view);
		touchView.setTag(l + "," + t);
	}

	public interface ModelViewListener {
		public View getView(MODEL model, Object object, int w, int h);

		public void setLayouot(MODEL model, View view);
	}

	private ModelViewListener getViewListener;

	public void setModelViewListener(ModelViewListener listener) {
		this.getViewListener = listener;
	}

	public boolean saveImage(String path) {

		if ("".equals(path) || path == null) {
			return false;
		}

		this.buildDrawingCache();
		Bitmap drawingCache = this.getDrawingCache();

		if (drawingCache == null) {
			return false;
		}

		File mediaFile = new File(path);

		if (mediaFile.exists()) {
			mediaFile.delete();
		}

		if (!new File(path).exists()) {
			try {
				new File(path).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileOutputStream fos = null;
		try {
			mediaFile.createNewFile();
			fos = new FileOutputStream(mediaFile);
			drawingCache.compress(Bitmap.CompressFormat.PNG, 100, fos);
			drawingCache.recycle();
			drawingCache = null;
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;

	}
}
