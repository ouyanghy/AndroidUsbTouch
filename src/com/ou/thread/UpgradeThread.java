package com.ou.thread;

import java.io.File;

import com.ou.base.Function;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.ui.UIMessageHandler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UpgradeThread extends Thread {
	Context mContext;
	UIMessageHandler mHandler;
	Function mFunc;
	Intent mData;
	boolean bWorkState = false;

	public boolean getWorkState() {
		return bWorkState;
	}
	
	public int getPercent() {
		return mFunc.getProgress();
	}
/*	
	public boolean reinitBootDevice() {
			
	}*/

	@Override
	public void run() {
		bWorkState = true;
		boolean r = false;
		if (mData == null) {
			ComFunc.sendMessage(Constant.MSG_FILE_INVAILD, mContext);
			bWorkState = false;
			return;
		}

		Uri u = mData.getData();
		if (u == null) {
			ComFunc.sendMessage(Constant.MSG_FILE_INVAILD, mContext);
			bWorkState = false;
			return;
		}
		String path = u.getPath();
		if (path == null) {
			bWorkState = false;
			ComFunc.sendMessage(Constant.MSG_FILE_INVAILD, mContext);
			return;
		}

		
		File f = new File(path);
		if (f.exists() == false || f.canRead() == false) {
			ComFunc.sendMessage(Constant.MSG_FILE_INVAILD, mContext);
			bWorkState = false;
			return;
		}

		ComFunc.log("open file:" + path);
		r = mFunc.prepareUpgrade();
		if (r) {
			ComFunc.sendMessage(Constant.MSG_DO_NOT_DETACH, mContext);
			ComFunc.sleep(1000);
			
			int cnt = 0;
			
			boolean enable = false;
			int pid = Constant.PID_NORMAL;
			do {
				ComFunc.sleep(100);
				if (cnt++ > 500) {
					ComFunc.log("switch fail test");
					return;
				}
				enable =DetectUsbThread.isUsbEnable();
				if (enable) {
					mFunc = Function.getTpUsbFunction();
					pid = mFunc.getPid();
				}
				ComFunc.log("enable:" + enable + " pid:" + String.format("0x%x", pid));
			} while(enable == false || pid == Constant.PID_NORMAL);
			
		} else {
			ComFunc.log("prepareUpgrade fail");
		}
			

		mFunc = Function.getTpUsbFunction();
		
		boolean ret = false;

		ret = mFunc.upgradeFirmware(f);

		if (ret == false) {
			ComFunc.sendMessage(Constant.MSG_UPGRADE_ERR, mContext);
		} else {
			ComFunc.sendMessage(Constant.MSG_UPGRADE_SUCC, mContext);
		}
		ComFunc.log("path:" + path);
		
		int cnt = 0;
		do {
			r = DetectUsbThread.isUsbEnable();
			ComFunc.sleep(10);
			ComFunc.log("update thread wait usb enable:" + mFunc.getPid());
		} while (r == false && cnt++ < 600);
		bWorkState = false;
		
	}

	public UpgradeThread(Context context, Intent data) {
		mData = data;
		mFunc = Function.getTpUsbFunction();
		mContext = context;
		mHandler = new UIMessageHandler();
	}

}
