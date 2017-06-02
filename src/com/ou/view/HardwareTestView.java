package com.ou.view;

import com.ou.base.BoardConfig;
import com.ou.base.HardwareSignal;
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
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class HardwareTestView extends View {
	int mXMode, mYMode, mXThreshold, mYThreshold;

	int mScreenWidth;
	int mScreenHeight;
	int mXLedNumber;
	int mYLedNumber;

	int mSize;
	Paint mPaint;
	boolean bReady = false;
	boolean bFirst = true;
	HardwareSignal mSignal, mPreSignal;

	float mXOneLedWdiv;
	float mYOneLedWDiv;
	float mScalWdiv;
	float SCAL_COL = 16;
	float SCAL_ROW = 21;

	double HDIV;

	float mHeightUnit;
	/*
	 * int HEIGHT_UNIT; int WIDTH_UNIT;
	 */
	float mFontSize = 16;
	float mHeightBase;
	float mWidthBase;
	BoardConfig mConfig;
	Rect[] mThresholdMap;
	boolean bXResult = true;
	boolean bYResult = true;
	public void init(BoardConfig config) {

		WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		mConfig = config;
		Point p = new Point();
		display.getSize(p);
		mScreenWidth = p.x;
		mScreenHeight = p.y;

		mSize = config.getSize();
		ComFunc.log("screen width:" + mScreenWidth + " height:" + mScreenHeight);
	
		mFontSize = mScreenWidth /50;
		mScreenWidth -= mFontSize * 2;
		mHeightBase = mHeightUnit;
		mWidthBase = mScreenWidth / 10;
		mScreenWidth -= mWidthBase;
		mHeightUnit = mScreenHeight / SCAL_ROW;
		
		// mScreenWidth;
		mXMode = -1;
		mYMode = -1;
		initSize(mXMode, mYMode);
		mScalWdiv = ((float) mScreenWidth) / (float) SCAL_COL;
		// HDIV = mScreenHeight / mYLedNumber;

		ComFunc.log("mXOneLedWdiv:" + mXOneLedWdiv);
		// HEIGHT_UNIT

		mPaint = new Paint();
		mPaint.setStrokeWidth(2);
		mPaint.setTextSize(mFontSize);
		mPaint.setColor(Color.YELLOW);
		bReady = true;

		mXThreshold = 90;
		mYThreshold = 90;
		mThresholdMap = new Rect[6];
		for (int i = 0; i < mThresholdMap.length; i++)
			mThresholdMap[i] = new Rect();
	}

	public void initSize(int xmode, int ymode) {
		mXMode = xmode;
		mYMode = ymode;
		if (xmode == -1) {
			mXLedNumber = mConfig.getXLedNumber();
			mXOneLedWdiv = ((float) mScreenWidth) / (float) mXLedNumber;
		}
		if (ymode == -1) {
			mYLedNumber = mConfig.getYLedNumber();
			mYOneLedWDiv = ((float) mScreenWidth) / (float) mYLedNumber;

		}

		if (xmode != -1) {
			mXLedNumber = mConfig.getOneBoardLedNumber(xmode + mConfig.getYBoardNum());
			mXOneLedWdiv = ((float) mScreenWidth) / (float) mXLedNumber;
		}
		if (ymode != -1) {
			mYLedNumber = mConfig.getOneBoardLedNumber(ymode);
			mYOneLedWDiv = ((float) mScreenWidth) / (float) mYLedNumber;
		}
	}

	public void setSignalAndUpdate(HardwareSignal signal) {
		mSignal = signal;
		invalidate();
	}

	public HardwareTestView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HardwareTestView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public HardwareTestView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private byte[] geXData(HardwareSignal signal, int grp_num, int sig_id) {
		byte[] data;
		if (mXMode == -1) {
			data = signal.getGroupSignal(grp_num, sig_id);
		} else {
			data = signal.getOneBoardGroupSignal(grp_num, sig_id, mXMode);
		}
		return data;
	}

	private byte[] geYData(HardwareSignal signal, int grp_num, int sig_id) {
		byte[] data;
		if (mYMode == -1) {
			data = signal.getGroupSignal(grp_num, sig_id);
		} else {
			data = signal.getOneBoardGroupSignal(grp_num, sig_id, mYMode);
		}
		return data;
	}

	private void drawSignal(Canvas canvas) {
		bYResult = true;
		bXResult = true;
		boolean need_info = false;
		int[] threshold_nums = new int[8];
		ComFunc.memset(threshold_nums, Constant.IGNORE, threshold_nums.length);
		int id = 0;
		for (int grp_num = 0; grp_num < 6; grp_num++) {

			for (int sig_id = 0; sig_id < 3; sig_id++) {
				//need_info = false;
				byte[] data = mSignal.getGroupSignal(grp_num, sig_id);
				if (mPreSignal == null)
					mPreSignal = mSignal;
				byte[] back = mPreSignal.getGroupSignal(grp_num, sig_id);

				if (grp_num < 3) {
					data = geXData(mSignal, grp_num, sig_id);
					back = geXData(mPreSignal, grp_num, sig_id);
				} else {
					data = geYData(mSignal, grp_num, sig_id);
					back = geYData(mPreSignal, grp_num, sig_id);
				}
				int len = data.length;
				for (int index = 0; index < len; index++) {
					float w = 0;
					float xstart;// = mFontSize / 2 + index * mXOneLedWdiv + 1 +
									// mWidthBase;
					float ystart = (grp_num * 3 + 1 + 1 + sig_id) * mHeightUnit + mHeightBase;
					int val = data[index] & 0xff;
					int pval = back[index] & 0xff;

					int color = Color.GREEN;

					int tmp = pval - val;
					int threshold = 0;
					if (tmp > 0 && pval != 0)
						threshold = tmp * 255 / pval;
					if (grp_num < 3) {
						if (tmp > 0 && threshold > mXThreshold) {
							color = Color.RED;
							need_info = true;
							bXResult = false;
						}
						xstart = mFontSize / 2 + index * mXOneLedWdiv + 1 + mWidthBase;
						w = mXOneLedWdiv / 2;

					} else {
						if (tmp > 0 && threshold > mYThreshold) {
							color = Color.RED;
							need_info = true;
							bYResult = false;
						}
						xstart = mFontSize / 2 + index * mYOneLedWDiv + 1 + mWidthBase;
						w = mYOneLedWDiv / 2;
					}

					drawUnit(canvas, xstart, ystart, val, w, color);
					if (!need_info)
						continue;

					need_info = false;
					
					if (id >= threshold_nums.length)
						continue;
					
					if (sig_id == 0)
						threshold_nums[id++] = index;
					//drawThresholdInfo(canvas, xstart, ystart, val, color, index);
					
					
					
				}
			
			}
			drawResult(canvas, grp_num, threshold_nums);
			ComFunc.memset(threshold_nums, Constant.IGNORE, threshold_nums.length);
			id = 0;

		}
	}

	private void drawUnit(Canvas canvas, float x, float y, int val, float w, int color) {

		mPaint.setColor(color);
		float h = mHeightUnit * (float) 0.9 * (float) ((float) val / 255);

		if (w < 1)
			w = 1;
		canvas.drawRect(x, y - h, x + w, y, mPaint);
	}

	private void drawResult(Canvas canvas, int pos, int[] ids) {
		String s = "";
		mPaint.setColor(Color.RED);
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] == Constant.IGNORE)
				break;
			s += ids[i] + ",";
		}
		if (ids[ids.length -1] != Constant.IGNORE)
			s+= "......";
		
		if ((bXResult == true && pos == 0)||
				(bYResult == true && pos == 3)) {
			mPaint.setTextSize((float) (mFontSize *1.6));
			s = ComFunc.getString(getContext(), R.string.signal_pass);
		}
		int len = (int) (mWidthBase/mFontSize) * 2;
		int i = 0;
		do {
			if (s.length() > len) {
				canvas.drawText(s.substring(0, len), mThresholdMap[pos].left, mThresholdMap[pos].top + i * mFontSize, mPaint);
				s = s.substring(len);
				
			}else {
				canvas.drawText(s, mThresholdMap[pos].left, mThresholdMap[pos].top + i * mFontSize, mPaint);
				break;
			}
			i++;
		}while(true);
		mPaint.setTextSize(mFontSize);
	}

	private void drawBlock(Canvas canvas) {

		for (float i = 0; i < 19; i++) {
			float xstart = mFontSize / 2;// mWidthBase + mFontSize / 2;
			float xstop = SCAL_COL * mScalWdiv + xstart + mWidthBase;
			float ystart = (i + 1) * mHeightUnit + mHeightBase;
			float ystop = ystart;

			if (i % 3 == 0) {
				mPaint.setColor(Color.WHITE);
				String s = "";
				if (i / 3 == 0)
					s = ComFunc.getString(getContext(), R.string.signal_x);
				if (i / 3 == 1 || i / 3 == 4)
					s = ComFunc.getString(getContext(), R.string.signal_left);
				if (i / 3 == 2 || i / 3 == 5)
					s = ComFunc.getString(getContext(), R.string.signal_right);
				if (i / 3 == 3)
					s = ComFunc.getString(getContext(), R.string.signal_y);

				float text_ystart = ystart + mFontSize;
				canvas.drawText(s, xstart, text_ystart, mPaint);

				if (i / 3 < 6) {
					mThresholdMap[(int) (i / 3)].left = (int) xstart;
					mThresholdMap[(int) (i / 3)].top = (int) ((int) ystart + 2 * mFontSize);
					mThresholdMap[(int) (i / 3)].right = (int) mWidthBase;
					mThresholdMap[(int) (i / 3)].bottom = (int) ((int) ystart + 4 * mFontSize);
				}
				// mPaint.setColor(Color.YELLOW);

			} else {
				mPaint.setColor(Color.DKGRAY);
				xstart = mWidthBase + mFontSize / 2;

			}
			/*if (i == 9) {
				mPaint.setStrokeWidth(4);
				ystart += 2;
				ystop += 2;
			} else
				mPaint.setStrokeWidth(2);*/

			canvas.drawLine(xstart, ystart, xstop, ystop, mPaint);
		}

		for (float i = 0; i <= SCAL_COL; i++) {
			float left = i * mScalWdiv + mFontSize / 2 + mWidthBase;
			float right = left + 1;
			float top = mHeightUnit + mHeightBase;
			float bottom = mHeightUnit * (SCAL_ROW - 2);
			mPaint.setColor(Color.WHITE);

			canvas.drawRect(left, top, right, bottom, mPaint);

			mPaint.setColor(Color.LTGRAY);

			String s = String.format("%02d", (int) (i * mXLedNumber / SCAL_COL));

			float xstart;
			if (i * mYLedNumber / SCAL_COL < 100)
				xstart = left - mFontSize / 2;
			else
				xstart = left - mFontSize;

			float ystart = top - mFontSize / 2;
			canvas.drawText(s, xstart, ystart, mPaint);

			s = String.format("%02d", (int) (i * mYLedNumber / SCAL_COL));

			if (i * mYLedNumber / SCAL_COL < 100)
				xstart = left - mFontSize / 2;
			else
				xstart = left - mFontSize;
			ystart = bottom + mFontSize;
			canvas.drawText(s, xstart, ystart, mPaint);
		}
	}

	public boolean getDrawStatus() {
		return bReady;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (bReady == false)
			return;

		if (mSignal == null)
			return;

		if (bFirst == true) {
			bFirst = false;
			mPreSignal = mSignal;
		}
		synchronized (canvas) {
			bReady = false;
		}
		drawSignal(canvas);
		drawBlock(canvas);

		

		synchronized (canvas) {
			bReady = true;
		}
	}

	public void setXMode(int mode) {

		mXMode = mode;
		initSize(mXMode, mYMode);
	}

	public void setYMode(int mode) {
		mYMode = mode;
		initSize(mXMode, mYMode);
	}

	public void setXThreshold(int val) {
		mXThreshold = val;
	}

	public void setYThreshold(int val) {
		mYThreshold = val;
	}

	public int getXThreshold() {
		return mXThreshold;
	}

	public int getYThreshold() {
		return mYThreshold;
	}
}
