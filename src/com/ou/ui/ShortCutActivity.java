package com.ou.ui;

import com.ou.base.CalMath;
import com.ou.base.Function;
import com.ou.base.ShortCutPointGroup;
import com.ou.base.ShortCutReport;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.thread.CalPointThread;
import com.ou.usbtp.R;
import com.ou.view.ShortCutView;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class ShortCutActivity extends Activity implements OnClickListener,KeyCntDialog.Result{
	ShortCutView mV;
	private Object mLock;
	boolean bTouchUp = true;
	SparseArray <ShortCutPointGroup>mGroupMap ;
	int mPointCnt = 0;
	Button mBtnUp,mBtnDown,mBtnLeft,mBtnRight,mBtnOk;
	private int mCurZone = Constant.ZONE_LEFT;
	private ShortCutPointGroup mCurGroup;
	boolean bOneFinish = true;
	int mKeyCount = 0;
	boolean bExit = false;
	boolean bGetPointWork = false;
	UIMessageHandler mHandler;
	ShortCutReport mReport;
	ShortCutActivity mApp;
	CalPointThread mPointThread;
	
	@Override
	protected void onDestroy() {
		mPointThread.release();
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.shortcut);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mBtnUp = (Button) findViewById(R.id.buttonShortCutUp);
		mBtnDown = (Button) findViewById(R.id.buttonShortCutDown);
		mBtnLeft = (Button) findViewById(R.id.buttonShortCutLeft);
		mBtnRight = (Button) findViewById(R.id.buttonShortCutRight);
		mBtnOk = (Button) findViewById(R.id.buttonShortCutOk);
		mBtnUp.setOnClickListener(this);
		mBtnDown.setOnClickListener(this);
		mBtnLeft.setOnClickListener(this);
		mBtnRight.setOnClickListener(this);
		mBtnOk.setOnClickListener(this);
		mHandler = new UIMessageHandler();
		mV = (ShortCutView) findViewById(R.id.shortCutView);
		mV.invalidate();
		mLock = new Object();
		mGroupMap = new SparseArray<ShortCutPointGroup>();
		mGroupMap.clear();
		setButtonBackGroundColor(Color.GRAY);
		mBtnLeft.callOnClick();
		mReport = new ShortCutReport();
		mApp = this;
		mPointThread = new CalPointThread(mPointHandler);
	}

	private void setButtonBackGroundColor(int color) {
		mBtnUp.setBackgroundColor(color);
		mBtnDown.setBackgroundColor(color);
		mBtnLeft.setBackgroundColor(color);
		mBtnRight.setBackgroundColor(color);
	}
	
	PointF getCalPoint() {
		Function func = Function.getTpUsbFunction();;
		return func.readCalPoint();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (mLock) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				bTouchUp = true;
				ComFunc.log("ouyang touch up");
				return super.onTouchEvent(event);
			}
			if (bExit)
				return super.onTouchEvent(event);

			if (bTouchUp == false)
				return super.onTouchEvent(event);

			if (event.getAction() != MotionEvent.ACTION_DOWN)
				return super.onTouchEvent(event);
			
			if (bGetPointWork == true)
				return super.onTouchEvent(event);
			
			
			if (mPointThread.getWorkState() == true)
				return super.onTouchEvent(event);
			
			bGetPointWork = true;
			mPointThread = new CalPointThread(mPointHandler);
			mPointThread.start();
/*			bOneFinish = false;
			float x = event.getX();
			float y = event.getY();
			
			if (mPointCnt%3 == 0) {
				ComFunc.log("first");
				mCurGroup = new ShortCutPointGroup();
				mCurGroup.setZone(mCurZone);
				mCurGroup.setFirst(new PointF(x, y));
				mV.setNeedTip(false);
				KeyCntDialog d = new KeyCntDialog(this);
				d.setCallBack(this);
				d.show();
			} else if (mPointCnt%3 == 1) {
				ComFunc.log("second");
				mCurGroup.setSecond(new PointF(x, y));
				r = mCurGroup.checkSecondPoint();
				if (r == false) {
					ComFunc.sendMessage(Constant.MSG_SHORTCUT_POINT_ERR, this);
					return super.onTouchEvent(event);
				}
				
			} else if (mPointCnt%3 == 2) {
				ComFunc.log("third");
				mCurGroup.setThird(new PointF(x, y));
				r = mCurGroup.checkThirdPoint();
				if (r == false) {
					ComFunc.sendMessage(Constant.MSG_SHORTCUT_POINT_ERR, this);
					return super.onTouchEvent(event);
				}
				
				mCurGroup.setComplete(true);
				//mCurGroup.setZone(mCurZone);
				mGroupMap.put(mPointCnt, mCurGroup);
				bOneFinish = true;
				ComFunc.sendMessage(Constant.MSG_SHORTCUT_FINISH_INDEX, mPointCnt/3, this);
				joinReport(mCurGroup);
			}
			mV.setUserPoint(mPointCnt%3, mCurGroup, mGroupMap);
			
			
			mPointCnt++;*/
			
			bTouchUp = false;
			return super.onTouchEvent(event);
		}

	}
	
	void joinReport(ShortCutPointGroup grp) {
		int zone = grp.getZone();
		CalMath math = new CalMath();
		Rect [] r = math.getCalKeyPosition(zone, grp);
		switch (zone) {
		case Constant.ZONE_LEFT:
			for (int i = 0; i < r.length; i++) {
				mReport.setLeftRectUnit(r[i]);
			}
			break;
		case Constant.ZONE_RIGHT:
			for (int i = 0; i < r.length; i++) {
				mReport.setRightRectUnit(r[i]);
			}
			break;
		case Constant.ZONE_TOP:
			for (int i = 0; i < r.length; i++) {
				mReport.setTopRectUnit(r[i]);
			}
			break;
		case Constant.ZONE_BOTTOM:
			for (int i = 0; i < r.length; i++) {
				mReport.setBottomRectUnit(r[i]);
			}
			break;
		}
	}

	boolean writeToIc() {
		Function mFunc  = Function.getTpUsbFunction();
		byte [] data = mReport.toBytes();
		if (data == null) {
			return false;
		}
		
		boolean r = mFunc.writeCalKey(data);
		
		return r;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (bOneFinish == false) {
			ComFunc.sendMessage(Constant.MSG_SHORTCUT_NEED_FINISH, this);
			return;
		}
		setButtonBackGroundColor(Color.GRAY);
		switch (v.getId()) {
		case R.id.buttonShortCutUp:
			mCurZone = Constant.ZONE_TOP;
			mBtnUp.setBackgroundColor(Color.GREEN);
			break;
		case R.id.buttonShortCutDown:
			mCurZone = Constant.ZONE_BOTTOM;
			mBtnDown.setBackgroundColor(Color.GREEN);
			break;
		case R.id.buttonShortCutLeft:
			mCurZone = Constant.ZONE_LEFT;
			mBtnLeft.setBackgroundColor(Color.GREEN);
			break;
		case R.id.buttonShortCutRight:
			mCurZone = Constant.ZONE_RIGHT;
			mBtnRight.setBackgroundColor(Color.GREEN);
			break;
			
		case R.id.buttonShortCutOk:
			bExit = true;
			boolean r = writeToIc();
			if (r) {
				ComFunc.sendMessage(Constant.MSG_SHORTCUT_WRITE_SUCC, this);
			} else {
				ComFunc.sendMessage(Constant.MSG_SHORTCUT_WRITE_ERR, this);
			}
			finish();
			break;
		}
	}

	@Override
	public void call(int val) {
		// TODO Auto-generated method stub
		mKeyCount = val;
		mCurGroup.setCount(val);
		ComFunc.log("get count:" + mKeyCount);
	}
	
	float tx = 100;
	float ty = 100;
	
	private Handler mPointHandler = new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			boolean r = false;
			if (msg.what == Constant.MSG_GET_CAL_POINT_TIME_OUT){
				bGetPointWork = false;
				return false;
			}
			
			if (msg.what ==Constant.MSG_DEVICE_NOT_FOUND) {
				ComFunc.sendMessage(Constant.MSG_DEVICE_NOT_FOUND, mApp);
				bGetPointWork = false;
				return false;
			}
			
			if (msg.what != Constant.MSG_GET_CAL_POINT)
				return false;
			
			PointF p = (PointF) msg.obj;
			float x = p.x;
			float y = p.y;
			boolean test = true;
			if (test) {
				x = tx+= 20;
				y = ty+= 20;
			}
			bGetPointWork = false;
			if (mPointCnt%3 == 0) {
				ComFunc.log("first");
				mCurGroup = new ShortCutPointGroup();
				mCurGroup.setZone(mCurZone);
				mCurGroup.setFirst(new PointF(x, y));
				mV.setNeedTip(false);
				KeyCntDialog d = new KeyCntDialog(mApp);
				d.setCallBack(mApp);
				d.show();
			} else if (mPointCnt%3 == 1) {
				ComFunc.log("second");
				mCurGroup.setSecond(new PointF(x, y));
				r = mCurGroup.checkSecondPoint();
				if (r == false) {
					ComFunc.sendMessage(Constant.MSG_SHORTCUT_POINT_ERR, mApp);
					return r;
				}
				
			} else if (mPointCnt%3 == 2) {
				ComFunc.log("third");
				mCurGroup.setThird(new PointF(x, y));
				r = mCurGroup.checkThirdPoint();
				if (r == false) {
					ComFunc.sendMessage(Constant.MSG_SHORTCUT_POINT_ERR, mApp);
					return r;
				}
				
				mCurGroup.setComplete(true);
				//mCurGroup.setZone(mCurZone);
				mGroupMap.put(mPointCnt, mCurGroup);
				bOneFinish = true;
				ComFunc.sendMessage(Constant.MSG_SHORTCUT_FINISH_INDEX, mPointCnt/3, mApp);
				joinReport(mCurGroup);
			}
			mV.setUserPoint(mPointCnt%3, mCurGroup, mGroupMap);
			
			
			mPointCnt++;
			return true;
		}
	});
}
