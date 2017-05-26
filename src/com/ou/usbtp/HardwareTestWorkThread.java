package com.ou.usbtp;

import com.ou.common.Common;
import com.ou.common.Enums;

import android.os.Handler;

public class HardwareTestWorkThread extends Thread {
	private boolean bWork = true;
	private Handler mHandler;
	private Function mFunc;
	private BoardConfig mBoardConfig;

	
	

	public HardwareTestWorkThread(Handler handler, BoardConfig conf) {
		// TODO Auto-generated constructor stub
		mHandler = handler;
		mFunc = DetectUsbThread.getUsbFunction();
		if (mFunc == null)
			;// send error

		mBoardConfig = conf;
	}

	private HardwareSignal fillAllSignal() {
		HardwareSignal signal = new HardwareSignal(mBoardConfig);
		for (int dir = 0; dir < Enums.LED_EMIT_DIRECTION_TOTAL_NUM; dir++) {
			byte [] bs = mFunc.readImage(dir, mBoardConfig.getTotalLedNumber());

			signal.setXLedSignal(dir, bs,true);
			signal.setYLedSignal(dir, bs,true);
		}
		return signal;
	}

	private void sendImage(HardwareSignal sig) {
		mHandler.obtainMessage(Enums.MSG_UPDATE_IMAGE, sig).sendToTarget();
	}
	@Override
	public void run() {
		while (bWork) {
			HardwareSignal sig= fillAllSignal();
			sendImage(sig);
			Common.sleep(1);
		}
	}

	public void startWork() {
		bWork = true;
		start();
	}

	public void stopWork() {
		bWork = false;
	}
}
