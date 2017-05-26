package com.ou.usbtp;

import java.io.File;

import com.ou.common.Common;
import com.ou.common.Enums;
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
			sendMessage(Enums.MSG_FILE_INVAILD);
			bWorkState = false;
			return;
		}

		Uri u = mData.getData();
		if (u == null) {
			sendMessage(Enums.MSG_FILE_INVAILD);
			bWorkState = false;
			return;
		}
		String path = u.getPath();
		if (path == null) {
			bWorkState = false;
			sendMessage(Enums.MSG_FILE_INVAILD);
			return;
		}

		File f = new File(path);
		if (f.exists() == false || f.canRead() == false) {
			sendMessage(Enums.MSG_FILE_INVAILD);
			bWorkState = false;
			return;
		}

		
		r = mFunc.prepareUpgrade();
		if (r) {
			Common.sleep(2000);
			int cnt = 0;
			
			do {
				Common.sleep(100);
				if (cnt++ > 100) {
					Common.log("switch fail test");
					return;
				}
				
			} while(DetectUsbThread.isUsbEnable() == false);
			
		} else {
			Common.log("prepareUpgrade fail");
		}
			
		mFunc = DetectUsbThread.getUsbFunction();
		
		boolean ret = false;

		ret = mFunc.upgradeFirmware(f);

		if (ret == false) {
			sendMessage(Enums.MSG_UPGRADE_ERR);
		} else {
			//sendMessage(Enums.MSG_UPGRADE_SUCC);
		}
		Common.log("path:" + path);
		bWorkState = false;
	}

	public UpgradeThread(Context context, Intent data, Function func) {
		mData = data;
		mFunc = func;
		mContext = context;
		mHandler = new UIMessageHandler();
	}

}
