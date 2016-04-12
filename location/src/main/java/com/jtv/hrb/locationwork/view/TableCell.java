package com.jtv.hrb.locationwork.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jtv.hrb.locationwork.R;

public class TableCell extends TextView {

	public TableCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TableCell);
		
		text1=attributes.getString(R.styleable.TableCell_text1);
		text2=attributes.getString(R.styleable.TableCell_text2);
		
		textSize1=attributes.getDimensionPixelSize(R.styleable.TableCell_textSize1, 30);
		textSize2=attributes.getDimensionPixelSize(R.styleable.TableCell_textSize2, 30);
		
		splitColor = attributes.getColor(R.styleable.TableCell_splitColor, Color.WHITE);
	}

	public TableCell(Context context) {
		this(context,null);
	}
	private Paint mPaint;
	private String text1;
	private String text2;
	private int textSize1;
	private int textSize2;
	private int splitColor;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setColor(splitColor);
		mPaint.setStrokeWidth(2);
		canvas.drawLine(0, 0, getMeasuredWidth(),getMeasuredHeight() , mPaint);
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(textSize1);
		if(!TextUtils.isEmpty(text1)){
			int textLength1=text1.length()*textSize1;
			canvas.drawText(text1,(getMeasuredWidth()/2-textLength1), getMeasuredHeight()/2+25, mPaint);
		}
		mPaint.setTextSize(textSize2);
		if(!TextUtils.isEmpty(text2)){
			int textLength2=text2.length()*textSize2;
			canvas.drawText(text2, getMeasuredWidth()/2, getMeasuredHeight()/2, mPaint);
		}
	}
}