package com.ou.usbtp;

import com.ou.common.Common;
import com.ou.common.Enums;
import com.ou.ui.UIMessageHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

public class DetectUsbThread extends Thread {
	private UIMessageHandler mHandler;
	private Device mUsb;
	private static Function mFunc;
	private Context mContext;
	private boolean bOpen = false;
	private boolean bWork = true;
	private boolean bExit = false;
	private Object mLock;
	private boolean bNeedRequestPermission = true;
	private int requestCount = 0;
	private CallBack mCall;
	private boolean bFirst = true;
	public DetectUsbThread(Context context, CallBack call) {
		mHandler = new UIMessageHandler();
		mContext = context;
		mLock = new Object();
		mCall = call;
		IntentFilter filter = new IntentFilter();
		filter.addAction(Enums.ACTION_USB_PERMISSION);
		filter.addAction(Enums.FIRST_BLOOD);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		context.registerReceiver(mUsbReceiver, filter);
	}

	public void release() {
		mContext.unregisterReceiver(mUsbReceiver);
		bWork = false;
		int cnt = 0;
		while (bExit == false && cnt++ < 60) {
			Common.sleep(100);
		}
		if (bOpen)
			deviceDeinit(true);
	}

	@Override
	public void run() {
		bExit = false;
		while (bWork) {
			work();
			Common.sleep(200);
			
		}

		bExit = true;
	}

	private void work() {
		if (bOpen == true) {
		//	Common.log("already open" );
			return;
		}else {
			//Common.log("want to open" );
		}
		
		synchronized (mLock) {
			deviceInit();
		}

	}

	public static boolean isUsbEnable() {
		if (mFunc == null)
			return false;

		return true;
	}

	public static Function getUsbFunction() {
		return mFunc;
	}

	private void deviceInit() {
		if (mUsb == null)
			mUsb = new Device(mContext);

		// mTv.setText("³õÊ¼»¯\n");
		if (mUsb.isAvail() == false) {
			sendMessage(R.string.device_no_found);
			//Common.log("device is invalid");
			return;
		} else
			sendMessage(R.string.device_found);

		boolean has_permission = mUsb.checkUsbPermission();
		if (has_permission) {	
			Common.log("has permission");
			boolean boot_mode = mUsb.isBootDevice();
			bOpen = mUsb.open();
			if (boot_mode) {
				if (bOpen) {
					mFunc = new Function(mUsb);
					//sendMessage(Enums.MSG_OPEN_DEVICE_BOOT_MODE_SUCC);
					if (bFirst) {
						bFirst = false;
						mContext.sendBroadcast(new Intent(Enums.FIRST_BLOOD));
					}
				}
				else {
					sendMessage(Enums.MSG_OPEN_DEVICE_BOOT_MODE_ERR);
				}
			} else {
				if (bOpen) {
					//sendMessage(Enums.MSG_OPEN_DEVICE_NORMAL_MODE_SUCC);
					mFunc = new Function(mUsb);
					if (bFirst) {
						bFirst = false;
						mContext.sendBroadcast(new Intent(Enums.FIRST_BLOOD));
					}
				}
				else
					sendMessage(Enums.MSG_OPEN_DEVICE_NORMAL_MODE_ERR);
			}
			//sendMessage(Enums.MSG_REQUEST_USB_PERMISSION_SUCC);
		} else {
			Common.log("enter request");
			if (bNeedRequestPermission) {
				Common.log("request permission now ");
				sendMessage(Enums.MSG_NEED_USB_PERMISSION);
				mUsb.requestPermission();
			}
			
			bNeedRequestPermission = false;
			if (requestCount++ > 10) {
				requestCount = 0;
				bNeedRequestPermission = true;
				Common.log("maybe android is stupid for new device or dead");
				
			}
		}
	}

	private void sendMessage(int what) {
		mHandler.obtainMessage(what, mContext).sendToTarget();
	}

	private void deviceDeinit(boolean is_exit_safe) {
		mFunc = null;
		if (is_exit_safe) {
			if (bOpen && mUsb != null)
				mUsb.close();
		} else {
		
		}
		mUsb = null;
		bOpen = false;
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Enums.ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					// UsbDevice device = (UsbDevice)
					// intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					boolean r = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
					Common.log("request 2:" + r);
					
					Common.log("set permission:" + bNeedRequestPermission);
					if (r) {
						deviceDeinit(false);
					} else {
						deviceDeinit(true);
					}
					bNeedRequestPermission = true;
				}
			} else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				bNeedRequestPermission = true;
				deviceDeinit(false);
				/*request first time*/

				Common.log(UsbManager.ACTION_USB_DEVICE_ATTACHED);
				Common.log("set permission 1:" + bNeedRequestPermission);
				if (mCall != null) {
					Common.sleep(500);
					mCall.call();
				}
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				bNeedRequestPermission = true;
				deviceDeinit(false);
				Common.log(UsbManager.ACTION_USB_DEVICE_DETACHED);
				if (mCall != null)
					mCall.call();
			
			} else if (Enums.FIRST_BLOOD.equals(action)){
				if (mCall != null) {
					mCall.call();
				}
			}
		}
	};

}
