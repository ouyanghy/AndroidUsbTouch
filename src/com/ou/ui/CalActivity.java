package com.ou.ui;

import com.ou.base.CalInfo;
import com.ou.base.CalMath;
import com.ou.base.Function;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.thread.CalPointThread;
import com.ou.thread.DetectUsbThread;
import com.ou.usbtp.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class CalActivity extends Activity implements OnClickListener {
	ImageView mLU, mRU, mLD, mRD;
	TextView mTv;
	final int STEP_LEFT_UP = 0;
	final int STEP_RIGHT_UP = 1;
	final int STEP_LEFT_DOWN = 2;
	final int STEP_RIGHT_DOWN = 3;
	int mStep = 0;
	PointF mPointTouch[];
	PointF mPointScreen[];
	PointF mPointCal[];
	CalPointThread mWorker;
	CalActivity mApp;
	Object mLock;
	boolean bTouchUp = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cal);
		mApp = this;
		mLock = new Object();
		mLU = (ImageView) findViewById(R.id.imagePointLeftUp);
		mRU = (ImageView) findViewById(R.id.imagePointRightUp);
		mLD = (ImageView) findViewById(R.id.imagePointLeftDown);
		mRD = (ImageView) findViewById(R.id.imagePointRightDown);
	/*	mLU.setOnClickListener(this);
		mRU.setOnClickListener(this);
		mLD.setOnClickListener(this);
		mRD.setOnClickListener(this);
		*/

		mTv = (TextView) findViewById(R.id.textViewCalTv);
		mPointTouch = new PointF[4];
		mPointScreen = new PointF[4];
		mPointCal = new PointF[4];
		for (int i = 0; i < mPointTouch.length; i++) {
			mPointTouch[i] = new PointF();
			mPointScreen[i] = new PointF();
			mPointCal[i] = new PointF();
		}

		mStep = STEP_LEFT_UP;
		setAllVisiable(false);
		mLU.setVisibility(View.VISIBLE);
		mTv.setText(ComFunc.getString(this, R.string.cal_touch_notice));
		
		mWorker = new CalPointThread(mHandler);
		
		float size = mTv.getTextSize();
		mTv.setTextSize(size * (float)1.4);
	}

	void setAllVisiable(boolean b) {
		int v = 0;
		if (b)
			v = View.VISIBLE;
		else
			v = View.INVISIBLE;

		mLU.setVisibility(v);
		mRU.setVisibility(v);
		mLD.setVisibility(v);
		mRD.setVisibility(v);
	}

	private void calcutePointScreen() {
		int vals[] = new int[2];
		int w, h;
		/* coor */
		mLU.getLocationOnScreen(vals);
		w = mLU.getWidth();
		h = mLU.getHeight();
		mPointScreen[STEP_LEFT_UP].x = vals[0] + w / 2;
		mPointScreen[STEP_LEFT_UP].y = vals[1] + h / 2;

		mRU.getLocationOnScreen(vals);
		w = mRU.getWidth();
		h = mRU.getHeight();
		mPointScreen[STEP_RIGHT_UP].x = vals[0] + w / 2;
		mPointScreen[STEP_RIGHT_UP].y = vals[1] + h / 2;

		mLD.getLocationOnScreen(vals);
		w = mLD.getWidth();
		h = mLD.getHeight();
		mPointScreen[STEP_LEFT_DOWN].x = vals[0] + w / 2;
		mPointScreen[STEP_LEFT_DOWN].y = vals[1] + h / 2;

		mRD.getLocationOnScreen(vals);
		w = mRD.getWidth();
		h = mRD.getHeight();
		mPointScreen[STEP_RIGHT_DOWN].x = vals[0] + w / 2;
		mPointScreen[STEP_RIGHT_DOWN].y = vals[1] + h / 2;
		ComFunc.log("screen point:{");
		for (int i = 0; i < 4; i++) {
			ComFunc.log("(" + mPointScreen[i].x + "," + mPointScreen[i].y + ")");
		}
		ComFunc.log("}");
		
		for (int i = 0; i < 4; i++) {
			ComFunc.log("(" + mPointCal[i].x + "," + mPointCal[i].y + ")");
		}
		ComFunc.log("}");
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (mLock) {
/*			float x = event.getX();
			float y = event.getY();
			Common.log("(" + x +"," + y + ")");*/
			
			if (event.getAction() == MotionEvent.ACTION_UP) {
				bTouchUp = true;
				ComFunc.log("ouyang touch up");
				return super.onTouchEvent(event);
			}

			if (mWorker == null)
				return super.onTouchEvent(event);
			
			if (mWorker.getWorkState() == true) {
				mTv.setText(ComFunc.getString(mApp, R.string.cal_working));
				return super.onTouchEvent(event);
				
			}
			
			if (bTouchUp == false)
				return super.onTouchEvent(event);
			
			mWorker = new CalPointThread(mHandler);
			mWorker.start();
			bTouchUp = false;
			ComFunc.log("ouyang touch up start cal thread");
			return super.onTouchEvent(event);
		}
	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/*case R.id.imagePointLeftUp:
		case R.id.imagePointRightUp:
		case R.id.imagePointLeftDown:
		case R.id.imagePointRightDown:
			if (mWorker.getWorkState() == true) {
				mTv.setText(Common.getString(mApp, R.string.cal_working));
				return ;
			}
			mWorker = new CalThread(mHandler);
			mWorker.start();
			break;*/
		case R.id.textViewCalTv:
			finish();
			break;
		}

	}

	private byte []  calcuteResult() {
		calcutePointScreen();
		CalMath math = new CalMath();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		Point p = new Point();
		display.getSize(p);
		
		byte [] bs = math.getCalResult(p.x, p.y, mPointCal, mPointScreen);
		return bs;
	}
	
	private boolean updateCalInfo(byte [] ps) {
		if (DetectUsbThread.isUsbEnable())
			return false;
		boolean r;
		Function func = Function.getTpUsbFunction();
		CalInfo cal = func.readCalInfo();
		if (cal == null)
			return false;
		
		cal.setCalPoints(ps);
		
		r = func.eraseCalInfo();
		if (r == false)
			return false;
		
		r = func.writeCalInfo(cal);
		
		
		return r;
	}
	private Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.MSG_GET_CAL_POINT:
				ComFunc.log("ouyang touch up MSG_GET_CAL_POINT");
				PointF p = (PointF) msg.obj;
				
				if (mStep > 3) {
					mStep = 3;
					ComFunc.log("step is out of boundary");
				}
				mPointCal[mStep] = p;
				//mTv.setText("cal point(" + p.x + "," + p.y + "");
				setAllVisiable(false);
				mTv.setText(ComFunc.getString(mApp, R.string.cal_touch_notice));
				switch (mStep) {
				case STEP_LEFT_UP:
					mRU.setVisibility(View.VISIBLE);
					//mPointCal[mStep].x = 10;
					//mPointCal[mStep].y = 10;
					break;
				case STEP_RIGHT_UP:
					mLD.setVisibility(View.VISIBLE);
					//mPointCal[mStep].x = 800;
					//mPointCal[mStep].y = 10;
					break;
				case STEP_LEFT_DOWN:
					mRD.setVisibility(View.VISIBLE);
					//mPointCal[mStep].x = 10;
					//mPointCal[mStep].y = 480;
					break;
				case STEP_RIGHT_DOWN:
					mWorker = null;
					//mPointCal[mStep].x = 800;
					//mPointCal[mStep].y = 480;
					byte []bs = calcuteResult();
					if (bs == null) {
						mTv.setOnClickListener(mApp);
						mTv.setText(ComFunc.getString(mApp, R.string.cal_err));
						break;
					}
					boolean r = updateCalInfo(bs);
					if (r == false) {
						mTv.setText(ComFunc.getString(mApp, R.string.cal_err));
					}else
						mTv.setText(ComFunc.getString(mApp, R.string.cal_touch_exit));
					mTv.setOnClickListener(mApp);
					break;
				}
				mStep++;
				break;

			case Constant.MSG_GET_CAL_POINT_TIME_OUT:
				mTv.setText(ComFunc.getString(mApp, R.string.cal_timeout));
				break;
			default:
				break;
			}
			return true;
		}
	});

}
