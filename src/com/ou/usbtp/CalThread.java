package com.ou.usbtp;

import com.ou.common.Common;
import com.ou.common.Enums;

import android.graphics.PointF;
import android.os.Handler;

public class CalThread extends Thread {
	boolean bWork = false;
	Function mFunc;
	private Handler mHandler;
	long mTime = 0;
	Object mLock;
	public boolean getWorkState() {
		synchronized (mLock) {
			return bWork;	
		}
		
	}

	@Override
	public void run() {
		synchronized (mLock) {
			bWork = true;
		}
		while(bWork) {
			PointF point = mFunc.readCalPoint();
			if (point != null) {
				mHandler.obtainMessage(Enums.MSG_GET_CAL_POINT, point).sendToTarget();
				break;
			}
			
			Common.sleep(100);
			mTime+=100;
			if (mTime > Enums.CAL_POINT_TIMEOUT) {
				mHandler.obtainMessage(Enums.MSG_GET_CAL_POINT_TIME_OUT).sendToTarget();
				break;
			}
		}
		synchronized (mLock) {
			bWork = false;
		}
	}

	public CalThread(Handler handler) {
		if (DetectUsbThread.isUsbEnable() == false) {
			bWork = false;
			return;
		}
		mFunc = Function.getTpUsbFunction();
		mHandler = handler;
		mLock = new Object();
	}

	

}
