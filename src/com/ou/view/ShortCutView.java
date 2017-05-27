package com.ou.view;

import com.ou.common.Common;
import com.ou.usbtp.R;
import com.ou.usbtp.ShortCutPointGroup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class ShortCutView extends View {
	Paint mPaint;
	PointF mMiddle;
	boolean bNeedTip = true;
	Rect mInsideRect;
	int mWidth,mHeight;
	int mUserPointCnt = 0;
	int mGrpCnt = 0;
	ShortCutPointGroup mUserPoint[];
	public void init() {
		WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		mWidth = p.x;
		mHeight = p.y;
		
		mPaint = new Paint();
	//	mPaint.setColor(Color.GREEN);
		mPaint.setStrokeWidth(2);
		mPaint.setTextSize((float) (mPaint.getTextSize() * 1.4));
		mMiddle = new PointF(mWidth/2, mHeight/2);
		int left = (int) (mMiddle.x /3);
		int right = mWidth - left;
		int top = (int) (mMiddle.y /3);
		int bottom = mHeight - top;
		mInsideRect = new Rect(left, top, right, bottom);

		Common.log("width:" + mWidth + " height:" + mHeight);
		mUserPoint = new ShortCutPointGroup[3];
	}
	public ShortCutView(Context context) {
		super(context);
		init();
	}

	public ShortCutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShortCutView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public void drawInsideBlock(Canvas canvas) {
		mPaint.setStrokeWidth(2);
		mPaint.setColor(Color.YELLOW);
		drawBlock(canvas, mInsideRect.left, mInsideRect.top, mInsideRect.right, mInsideRect.bottom, mPaint);
	
	}
	
	public void drawNotice(Canvas canvas) {
			String s = Common.getString(getContext(), R.string.shortcut_note_example);
			mPaint.setColor(Color.RED);
			float x = mMiddle.x/2 ;
			float y = mMiddle.y;
			canvas.drawText(s, x, y, mPaint);
			
			mPaint.setColor(Color.WHITE);
			x = mMiddle.x/2;
			y = mHeight - mMiddle.y/10;
			s = Common.getString(getContext(), R.string.shortcut_note_tip);
			canvas.drawText(s, x, y, mPaint);
	}
	
	void drawBlock(Canvas canvas, float xstart, float ystart, float xstop, float ystop, Paint paint){
		canvas.drawLine(xstart, ystart, xstop, ystart, paint);
		canvas.drawLine(xstart, ystop, xstop, ystop, paint);
		canvas.drawLine(xstart, ystart, xstart, ystop, paint);
		canvas.drawLine(xstop, ystart, xstop, ystop, paint);
	}
	
	public void drawExample(Canvas canvas) {
		if (bNeedTip == false)
			return;
		
		float xstart,xstop,ystart,ystop;
		float width = mMiddle.x/6;
		float height = mMiddle.y/4;
		float ybase = mMiddle.y/2;
		float xbase = mMiddle.x/12;
		for (int i = 0; i < 3; i++) {
			
			xstart = xbase;
			ystart = i * height + ybase;
			xstop = width + xbase;
			ystop = (i + 1) * height + ybase;
			mPaint.setColor(Color.WHITE);
			drawBlock(canvas, xstart, ystart, xstop, ystop, mPaint);
			/*test*/
			/*mUserPoint[0].x = xstart;
			mUserPoint[0].y = ystart;*/
			
			if (i == 0) {
				mPaint.setColor(Color.RED);
				canvas.drawCircle(xstart, ystart, 4, mPaint);
			} else if (i == 1) {
				mPaint.setColor(Color.RED);
				canvas.drawCircle(xstop, ystart, 4, mPaint);
			}else if (i == 2) {
				mPaint.setColor(Color.RED);
				canvas.drawCircle(xstop, ystart, 4, mPaint);
			}
		}
	}
	
	public void setUserPoint(int cnt, ShortCutPointGroup g) {
		mUserPoint[mGrpCnt] = g;
		mUserPointCnt = cnt;

		
		invalidate();
	}
	
	public void drawUserPoint(Canvas canvas) {
		
			if (mUserPointCnt == 0)
				return;
			
			float xstart,ystart,xstop,ystop;
			if (mUserPointCnt == 1) {
				xstart =mUserPoint[0].getFirst().x;
				ystart = mUserPoint[0].getFirst().y;
				xstop = mWidth;
				ystop = mHeight;
				canvas.drawLine(xstart, ystart, xstart, ystop, mPaint);
				canvas.drawLine(xstart, ystart, xstop, ystart, mPaint);
			} else if (mUserPointCnt == 2) {
				 xstart =mUserPoint[0].getFirst().x;
				ystart = mUserPoint[0].getFirst().y;
				xstop = mUserPoint[0].getSecond().x;
				ystop = mUserPoint[0].getSecond().y;
				drawBlock(canvas, xstart, ystart, xstop, ystop, mPaint);
			} else if (mUserPointCnt == 3) {
					Common.log("touch third point");
					float h = mUserPoint[0].getSecond().y - mUserPoint[0].getFirst().y;
					xstart =mUserPoint[0].getFirst().x;
					
					xstop = mUserPoint[0].getSecond().x;
					
					float len = mUserPoint[0].getThird().y - mUserPoint[0].getFirst().y;
					for ( float i = 0; i < len; i+=h) {
						ystart = mUserPoint[0].getFirst().y + i;
						ystop = mUserPoint[0].getSecond().y + i;
						Common.log("touch third point in," + xstart + "," + ystart + "," + xstop + "," + ystop + " h:" + h + " len:" + len);
						drawBlock(canvas, xstart, ystart, xstop, ystop, mPaint);
					}
					//mGrpCnt++;

			}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		drawInsideBlock(canvas);
		drawNotice(canvas);
		drawExample(canvas);
		drawUserPoint(canvas);
	}

}
