package com.ou.view;

import com.ou.base.ShortCutPointGroup;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.usbtp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class ShortCutView extends View {
	Paint mPaint;
	PointF mMiddle;
	boolean bNeedTip = true;
	Rect mInsideRect;
	int mWidth, mHeight;
	int mCurUserPointGroupCnt = -1;
	int mGrpCnt = 0;
	ShortCutPointGroup mCurUserPointGroup;
	SparseArray<ShortCutPointGroup> mMapGroup;

	public void init() {
		WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		mWidth = p.x;
		mHeight = p.y;

		mPaint = new Paint();
		// mPaint.setColor(Color.GREEN);
		mPaint.setStrokeWidth(2);
		mPaint.setTextSize((float) (mPaint.getTextSize() * 1.4));
		mMiddle = new PointF(mWidth / 2, mHeight / 2);
		int left = (int) (mMiddle.x / 3);
		int right = mWidth - left;
		int top = (int) (mMiddle.y / 3);
		int bottom = mHeight - top;
		mInsideRect = new Rect(left, top, right, bottom);

		ComFunc.log("width:" + mWidth + " height:" + mHeight);

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
		if (bNeedTip == false)
			return;

		String s = ComFunc.getString(getContext(), R.string.shortcut_note_example);
		mPaint.setColor(Color.RED);
		float x = mMiddle.x / 2;
		float y = mMiddle.y / 10;
		canvas.drawText(s, x, y, mPaint);

		mPaint.setColor(Color.WHITE);
		x = mMiddle.x / 2;
		y = mHeight - mMiddle.y / 10;
		s = ComFunc.getString(getContext(), R.string.shortcut_note_tip);
		canvas.drawText(s, x, y, mPaint);
	}

	void drawBlock(Canvas canvas, float xstart, float ystart, float xstop, float ystop, Paint paint) {
		canvas.drawLine(xstart, ystart, xstop, ystart, paint);
		canvas.drawLine(xstart, ystop, xstop, ystop, paint);
		canvas.drawLine(xstart, ystart, xstart, ystop, paint);
		canvas.drawLine(xstop, ystart, xstop, ystop, paint);
	}

	public void drawExample(Canvas canvas) {
		if (bNeedTip == false)
			return;

		float xstart, xstop, ystart, ystop;
		float width = mMiddle.x / 6;
		float height = mMiddle.y / 4;
		float ybase = mMiddle.y / 2;
		float xbase = mMiddle.x / 12;
		for (int i = 0; i < 3; i++) {

			xstart = xbase;
			ystart = i * height + ybase;
			xstop = width + xbase;
			ystop = (i + 1) * height + ybase;
			mPaint.setColor(Color.WHITE);
			drawBlock(canvas, xstart, ystart, xstop, ystop, mPaint);
			/* test */
			/*
			 * mCurUserPointGroup[0].x = xstart; mCurUserPointGroup[0].y =
			 * ystart;
			 */

			if (i == 0) {
				mPaint.setColor(Color.RED);
				canvas.drawCircle(xstart, ystart, 4, mPaint);
			} else if (i == 1) {
				mPaint.setColor(Color.RED);
				canvas.drawCircle(xstop, ystart, 4, mPaint);
			} else if (i == 2) {
				mPaint.setColor(Color.RED);
				canvas.drawCircle(xstop, ystart, 4, mPaint);
			}
		}
	}

	public void setUserPoint(int cnt, ShortCutPointGroup g, SparseArray<ShortCutPointGroup> grp_prev) {
		mCurUserPointGroup = g;
		mCurUserPointGroupCnt = cnt;
		mMapGroup = grp_prev;
		invalidate();
	}

	public void setNeedTip(boolean b) {
		bNeedTip = b;
	}

	private void drawOneFinishGroup(Canvas canvas, ShortCutPointGroup value) {
		int zone = value.getZone();
		if (zone == Constant.ZONE_LEFT || zone == Constant.ZONE_RIGHT) {
			float h = value.getSecond().y - value.getFirst().y;
			float xstart = value.getFirst().x;

			float xstop = value.getSecond().x;

			float len = value.getThird().y - value.getFirst().y;
			for (float i = 0; i < len; i += h) {
				float ystart = value.getFirst().y + i;
				float ystop = value.getSecond().y + i;
				// ComFunc.log("touch third point in," + xstart + "," + ystart +
				// "," + xstop + "," + ystop + " h:" + h + " len:" + len);
				drawBlock(canvas, xstart, ystart, xstop, ystop, mPaint);
			}
		} else {
			float h = value.getSecond().x - value.getFirst().x;
			float ystart = value.getFirst().y;
			float ystop = value.getSecond().y;

			float len = value.getThird().x - value.getFirst().x;

			for (float i = 0; i < len; i += h) {
				float xstart = value.getFirst().x + i;
				float xstop = value.getSecond().x + i;
				// ComFunc.log("touch third point in," + xstart + "," + ystart +
				// "," + xstop + "," + ystop + " h:" + h + " len:" + len);
				drawBlock(canvas, xstart, ystart, xstop, ystop, mPaint);
			}

		}
	}

	public void drawUserPointGroup(Canvas canvas) {
		if (mMapGroup == null)
			return;

		for (int grp_id = 0; grp_id < mMapGroup.size(); grp_id++) {
			ShortCutPointGroup value = mMapGroup.valueAt(grp_id);
			if (value == null || value.isComplete() == false) {
				ComFunc.log("drawUserPointGroup error ocur:" + value);
				continue;
			}

			drawOneFinishGroup(canvas, value);
		}

	}

	public void drawUserPoint(Canvas canvas) {

		if (mCurUserPointGroup == null) {
			ComFunc.log("drawUserPoint null ptr");
			return;
		}

		ComFunc.log("cnt:" + mCurUserPointGroupCnt);
		float xstart, ystart, xstop, ystop;
		if (mCurUserPointGroupCnt == 0) {
			xstart = mCurUserPointGroup.getFirst().x;
			ystart = mCurUserPointGroup.getFirst().y;

			xstop = mWidth;
			ystop = mHeight;

			canvas.drawLine(xstart, ystart, xstart, ystop, mPaint);
			canvas.drawLine(xstart, ystart, xstop, ystart, mPaint);

		} else if (mCurUserPointGroupCnt == 1) {
			xstart = mCurUserPointGroup.getFirst().x;
			ystart = mCurUserPointGroup.getFirst().y;
			xstop = mCurUserPointGroup.getSecond().x;
			ystop = mCurUserPointGroup.getSecond().y;
			drawBlock(canvas, xstart, ystart, xstop, ystop, mPaint);
		} else if (mCurUserPointGroupCnt == 2) {
			ComFunc.log("touch third point");
			drawOneFinishGroup(canvas, mCurUserPointGroup);
			// mGrpCnt++;

		}
	}

	@Override
	public void onDraw(Canvas canvas) {

		drawInsideBlock(canvas);
		drawNotice(canvas);
		drawExample(canvas);
		drawUserPoint(canvas);
		drawUserPointGroup(canvas);
	}

}
