package com.ou.view;

import com.ou.common.ComFunc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {
	Paint mPaint;
	RectF mRect = null;
	Point mMiddle;
	int mPercent = 0;
	float mFontSize;
	String mNote = "";

	private void init() {
		mPaint = new Paint();
		mPaint.setColor(Color.GREEN);
		mMiddle = new Point();

		ComFunc.log("(" + getWidth() + "," + getHeight() + ")");

	}

	public ProgressView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}

	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public void setNote(String note) {
		mNote = note;
	}

	public void setPercent(int percent) {
		if (percent > 100)
			percent = 100;

		if (percent < 0)
			percent = 0;
		mPercent = percent;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (mRect == null) {
			int left;
			int right;
			int top;
			int bottom;
			int cir;
			int w = getWidth();
			int h = getHeight();
			mMiddle.x = w / 2;
			mMiddle.y = h / 2;
			if (w > h) {
				cir = h * 4/ 5;
				left = mMiddle.x - cir / 2;
				right = left + cir;
				top = mMiddle.y - cir / 2;
				bottom = top + cir;

			} else {
				cir = w * 4 / 5;
				left = mMiddle.x - cir / 2;
				right = left + cir;
				top = mMiddle.y - cir / 2;
				bottom = top + cir;

			}
			mFontSize = cir / 10;
			mPaint.setTextSize(mFontSize);

			mRect = new RectF(left, top, right, bottom);
		}
		mPaint.setColor(Color.GRAY);
		canvas.drawArc(mRect, 0, 360, true, mPaint);

		mPaint.setColor(Color.rgb(0xFF, 0x34, 0x56));
		float deg = ((float) mPercent / 100) * 360;
		canvas.drawArc(mRect, 0, deg, true, mPaint);

		mPaint.setColor(Color.YELLOW);

		float size = mPaint.getTextSize();
		String s = mPercent + "%";
		canvas.drawText(s, mMiddle.x - (size * s.length())/ 2, mMiddle.y + size / 2, mPaint);

		mPaint.setColor(Color.WHITE);
		
		canvas.drawText(mNote, mMiddle.x - size * mNote.length()/2, size, mPaint);
	}

}
