package com.ou.ui;

import com.ou.common.Common;
import com.ou.usbtp.BoardConfig;
import com.ou.usbtp.HardwareSignal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

	float X_LED_WDIV;
	float Y_LED_WDIV;
	float SCAL_WDIV;
	float SCAL_COL = 16;
	float SCAL_ROW = 21;

	double HDIV;

	float HEIGHT_BLOCK_UNIT;
	int HEIGHT_UNIT;
	int WIDTH_UNIT;
	int FONT_SIZE = 16;
	float HEIGHT_START_BASE;
	BoardConfig mConfig;

	public void init(BoardConfig config) {

		WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		mConfig = config;
		Point p = new Point();
		display.getSize(p);
		mScreenWidth = p.x;
		mScreenHeight = p.y;

		mSize = config.getSize();
		Common.log("screen width:" + mScreenWidth + " height:" + mScreenHeight);

		mScreenWidth -= FONT_SIZE * 2;
		// mScreenWidth;
		mXMode = -1;
		mYMode = -1;
		initSize(mXMode, mYMode);
		SCAL_WDIV = ((float) mScreenWidth) / (float) SCAL_COL;
		// HDIV = mScreenHeight / mYLedNumber;
		HEIGHT_BLOCK_UNIT = mScreenHeight / SCAL_ROW;
		HEIGHT_START_BASE = HEIGHT_BLOCK_UNIT;
		Common.log("X_LED_WDIV:" + X_LED_WDIV);
		// HEIGHT_UNIT

		mPaint = new Paint();
		mPaint.setStrokeWidth(2);
		mPaint.setTextSize(FONT_SIZE);
		mPaint.setColor(Color.YELLOW);
		bReady = true;

		mXThreshold = 90;
		mYThreshold = 90;
	}

	public void initSize(int xmode, int ymode) {
		mXMode = xmode;
		mYMode = ymode;
		if (xmode == -1) {
			mXLedNumber = mConfig.getXLedNumber();
			X_LED_WDIV = ((float) mScreenWidth) / (float) mXLedNumber;
		}
		if (ymode == -1) {
			mYLedNumber = mConfig.getYLedNumber();
			Y_LED_WDIV = ((float) mScreenWidth) / (float) mYLedNumber;

		}

		if (xmode != -1) {
			mXLedNumber = mConfig.getOneBoardLedNumber(xmode + mConfig.getYBoardNum());
			X_LED_WDIV = ((float) mScreenWidth) / (float) mXLedNumber;
		}
		if (ymode != -1) {
			mYLedNumber = mConfig.getOneBoardLedNumber(ymode);
			Y_LED_WDIV = ((float) mScreenWidth) / (float) mYLedNumber;
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
		boolean need_info = false;
		for (int grp_num = 0; grp_num < 6; grp_num++) {

			for (int sig_id = 0; sig_id < 3; sig_id++) {
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
					float xstart = FONT_SIZE / 2 + index * X_LED_WDIV + 1;
					float ystart = (grp_num * 3 + 1 + sig_id) * HEIGHT_BLOCK_UNIT + HEIGHT_START_BASE;
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
						}
						xstart = FONT_SIZE / 2 + index * X_LED_WDIV + 1;
						w = X_LED_WDIV / 2;

					} else {
						if (tmp > 0 && threshold > mYThreshold) {
							color = Color.RED;
							need_info = true;
						}
						xstart = FONT_SIZE / 2 + index * Y_LED_WDIV + 1;
						w = Y_LED_WDIV / 2;
					}

					if (grp_num == 0 && sig_id == 0) {
						// Common.log(String.format("id:%d (%d,%d) %d,%d",
						// index, (int)xstart,(int)ystart,val, color));
					}
					drawUnit(canvas, xstart, ystart, val, w, color);
					if (need_info)
					drawThresholdInfo( canvas, xstart, ystart,  val,color, index);
					need_info = false;
				}
			}

		}
	}

	private void drawUnit(Canvas canvas, float x, float y, int val, float w, int color) {

		mPaint.setColor(color);
		float h = HEIGHT_BLOCK_UNIT * (float) 1.2 * (float) ((float) val / 255);

		if (w < 1)
			w = 1;
		canvas.drawRect(x, y - h, x + w, y, mPaint);
	}
	
	private void drawThresholdInfo(Canvas canvas, float x, float y, int val ,int color, int led_num) {

		mPaint.setColor(color);
		float size = mPaint.getTextSize();
		float h = HEIGHT_BLOCK_UNIT * (float) 1.2 * (float) ((float) val / 255);
		mPaint.setTextSize(size/2);
		String s = String.valueOf(led_num);
		canvas.drawText(s, x, y - h - size /2, mPaint);
		mPaint.setTextSize(size);
	
	}

	private void drawBlock(Canvas canvas) {

		for (float i = 0; i < 19; i++) {
			float xstart = FONT_SIZE / 2;
			float xstop = SCAL_COL * SCAL_WDIV + FONT_SIZE / 2;
			float ystart = (i + 1) * HEIGHT_BLOCK_UNIT + HEIGHT_START_BASE;
			float ystop = ystart;

			if (i % 3 == 0)
				mPaint.setColor(Color.YELLOW);
			else
				mPaint.setColor(Color.DKGRAY);
			
			if (i == 9) {
				mPaint.setStrokeWidth(4);
				ystart += 2;
				ystop += 2;
			}else
				mPaint.setStrokeWidth(2);
			
			canvas.drawLine(xstart, ystart, xstop, ystop, mPaint);
		}

		for (float i = 0; i <= SCAL_COL; i++) {
			float left = i * SCAL_WDIV + FONT_SIZE / 2;
			float right = left + 1;
			float top = HEIGHT_BLOCK_UNIT + HEIGHT_START_BASE;
			float bottom = HEIGHT_BLOCK_UNIT * (SCAL_ROW - 1);
			mPaint.setColor(Color.WHITE);
			// Common.log("xs:" + xstart + " xs:" + xstop + " ystart:" + ystart
			// + " ysstop:" + ystop);
			if (i == 0) {
				mPaint.setColor(Color.BLUE);
				canvas.drawRect(left, top, right, top + (bottom - top)/2, mPaint);
				mPaint.setColor(Color.CYAN);
				canvas.drawRect(left, top + (bottom - top)/2, right, bottom, mPaint);
			}else {
				canvas.drawRect(left, top, right, bottom, mPaint);
			}
			mPaint.setColor(Color.LTGRAY);

			String s = String.format("%02d", (int) (i * mXLedNumber / SCAL_COL));
			if (i == 0) {
				mPaint.setColor(Color.BLUE);
				s+= "(X)";
			}
			float xstart = left - FONT_SIZE / 2;
			float ystart = top - FONT_SIZE / 2;
			canvas.drawText(s, xstart, ystart, mPaint);

			s = String.format("%02d", (int) (i * mYLedNumber / SCAL_COL));
			if (i == 0) {
				mPaint.setColor(Color.CYAN);
				s+= "(Y)";
			}
			xstart = left - FONT_SIZE / 2;
			ystart = bottom + FONT_SIZE;
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

		drawBlock(canvas);

		drawSignal(canvas);

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
