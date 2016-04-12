package com.jtv.base.ui;

import com.plutus.libraryui.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/***
 * 画一个视频播放器
 * 
 * @author lgs
 *
 */
public class VideoImageView extends ImageView {

	private Context context;
	private int measuredWidth;
	private Bitmap decodeResource;
	private Paint paint;
	private int measuredHeight;
	private int width;
	private int high;

	public VideoImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VideoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VideoImageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		paint = new Paint();
		decodeResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_jtv_video_dis);
		width = decodeResource.getWidth();
		high = decodeResource.getHeight();
	}

	private boolean disPic = true;

	public void setDisPlayOnDraw(boolean disPic) {
		if (disPic == this.disPic) {
		} else {
			this.disPic = disPic;
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (disPic) {
			measuredWidth = this.getMeasuredWidth();
			measuredHeight = this.getMeasuredHeight();
			if (measuredWidth < 1 || measuredHeight < 1) {
				return;
			}
			// canvas.drawBitmap(decodeResource, matrix, paint);
			canvas.drawBitmap(decodeResource, (measuredWidth / 2) - (width / 2), (measuredHeight / 2 - (high / 2)),
					paint);
		}

	}
}
