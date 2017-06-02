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
	
	private void sendMessage(int what) {
		mHandler.obtainMessage(what, mContext).sendToTarget();
	}

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
			sendMessage(Constant.MSG_FILE_INVAILD);
			bWorkState = false;
			return;
		}

		Uri u = mData.getData();
		if (u == null) {
			sendMessage(Constant.MSG_FILE_INVAILD);
			bWorkState = false;
			return;
		}
		String path = u.getPath();
		if (path == null) {
			bWorkState = false;
			sendMessage(Constant.MSG_FILE_INVAILD);
			return;
		}

		
		File f = new File(path);
		if (f.exists() == false || f.canRead() == false) {
			sendMessage(Constant.MSG_FILE_INVAILD);
			bWorkState = false;
			return;
		}

		ComFunc.log("open file:" + path);
		r = mFunc.prepareUpgrade();
		if (r) {
			ComFunc.sleep(2000);
			int cnt = 0;
			
			do {
				ComFunc.sleep(100);
				if (cnt++ > 100) {
					ComFunc.log("switch fail test");
					return;
				}
				
			} while(DetectUsbThread.isUsbEnable() == false);
			
		} else {
			ComFunc.log("prepareUpgrade fail");
		}
			
		mFunc = DetectUsbThread.getUsbFunction();
		
		boolean ret = false;

		ret = mFunc.upgradeFirmware(f);

		if (ret == false) {
			sendMessage(Constant.MSG_UPGRADE_ERR);
		} else {
			//sendMessage(Enums.MSG_UPGRADE_SUCC);
		}
		ComFunc.log("path:" + path);
		bWorkState = false;
	}

	public UpgradeThread(Context context, Intent data) {
		mData = data;
		mFunc = DetectUsbThread.getUsbFunction();
		mContext = context;
		mHandler = new UIMessageHandler();
	}

}
