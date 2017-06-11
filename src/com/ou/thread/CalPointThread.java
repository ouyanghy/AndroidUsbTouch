package com.ou.thread;

import com.ou.base.Function;
import com.ou.common.ComFunc;
import com.ou.common.Constant;

import android.graphics.PointF;
import android.os.Handler;

public class CalPointThread extends Thread {
	boolean bWork = false;
	Function mFunc;
	private Handler mHandler;
	long mTime = 0;
	Object mLock;
	int mSuccTime = 0;
	public boolean getWorkState() {
		synchronized (mLock) {
			return bWork;	
		}
		
	}
	
	public void release() {
		bWork = false;
	}

	@Override
	public void run() {
		synchronized (mLock) {
			bWork = true;
		}
		while(bWork) {
			if (DetectUsbThread.isUsbEnable() == false) {	
				mHandler.obtainMessage(Constant.MSG_DEVICE_NOT_FOUND).sendToTarget();
				break;
			}
			mFunc = Function.getTpUsbFunction();
			PointF point = mFunc.readCalPoint();
			if (point != null) {
				
				if (mSuccTime++ > 6) {
					mHandler.obtainMessage(Constant.MSG_GET_CAL_POINT, point).sendToTarget();
					break;
				}
			}
			
			ComFunc.sleep(100);
			mTime+=100;
			if (mTime > Constant.CAL_POINT_TIMEOUT) {
				mHandler.obtainMessage(Constant.MSG_GET_CAL_POINT_TIME_OUT).sendToTarget();
				break;
			}
		}
		synchronized (mLock) {
			bWork = false;
		}
	}

	public CalPointThread(Handler handler) {
		if (DetectUsbThread.isUsbEnable() == false) {
			bWork = false;
		//	return;
		}
		mFunc = Function.getTpUsbFunction();
		mHandler = handler;
		mLock = new Object();
	}

	

}
