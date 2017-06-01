package com.ou.thread;

import com.ou.base.CallBack;
import com.ou.base.Device;
import com.ou.base.Function;
import com.ou.base.ShortCutPointGroup;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.ui.UIMessageHandler;
import com.ou.usbtp.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.SparseArray;

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

	private boolean bFirst = true;
	SparseArray <CallBack> mCallMap ;
	int mCallId = 0;
	public void addCall( CallBack call) {
		mCallId++;
		mCallMap.put(mCallId, call);
	}
	
	public void deleteCall(CallBack call) {
		for (int i = 0; i < mCallMap.size(); i++) {
			CallBack c = mCallMap.valueAt(i);
			if (c == call) {
				int key = mCallMap.keyAt(i);
				mCallMap.delete(key);
				break;
			}
		}
		
	}
	public DetectUsbThread(Context context) {
		mHandler = new UIMessageHandler();
		mContext = context;
		mLock = new Object();
		mCallMap = new SparseArray<CallBack>();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_USB_PERMISSION);
		filter.addAction(Constant.FIRST_BLOOD);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		context.registerReceiver(mUsbReceiver, filter);
	}

	public void release() {
		mContext.unregisterReceiver(mUsbReceiver);
		bWork = false;
		int cnt = 0;
		while (bExit == false && cnt++ < 60) {
			ComFunc.sleep(100);
		}
		if (bOpen)
			deviceDeinit(true);
	}

	@Override
	public void run() {
		bExit = false;
		while (bWork) {
			work();
			ComFunc.sleep(200);
			
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
			ComFunc.log("has permission");
			boolean boot_mode = mUsb.isBootDevice();
			bOpen = mUsb.open();
			if (boot_mode) {
				if (bOpen) {
					mFunc = new Function(mUsb);
					//sendMessage(Enums.MSG_OPEN_DEVICE_BOOT_MODE_SUCC);
					if (bFirst) {
						bFirst = false;
						mContext.sendBroadcast(new Intent(Constant.FIRST_BLOOD));
					}
				}
				else {
					sendMessage(Constant.MSG_OPEN_DEVICE_BOOT_MODE_ERR);
				}
			} else {
				if (bOpen) {
					//sendMessage(Enums.MSG_OPEN_DEVICE_NORMAL_MODE_SUCC);
					mFunc = new Function(mUsb);
					if (bFirst) {
						bFirst = false;
						mContext.sendBroadcast(new Intent(Constant.FIRST_BLOOD));
					}
				}
				else
					sendMessage(Constant.MSG_OPEN_DEVICE_NORMAL_MODE_ERR);
			}
			//sendMessage(Enums.MSG_REQUEST_USB_PERMISSION_SUCC);
		} else {
			ComFunc.log("enter request");
			if (bNeedRequestPermission) {
				ComFunc.log("request permission now ");
				sendMessage(Constant.MSG_NEED_USB_PERMISSION);
				mUsb.requestPermission();
			}
			
			bNeedRequestPermission = false;
			if (requestCount++ > 10) {
				requestCount = 0;
				bNeedRequestPermission = true;
				ComFunc.log("maybe android is stupid for new device or dead");
				
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

	void call() {
		for (int i = 0; i < mCallMap.size(); i++) {
			CallBack c = mCallMap.valueAt(i);
			c.call();
		}
	}
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					// UsbDevice device = (UsbDevice)
					// intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					boolean r = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
					ComFunc.log("request 2:" + r);
					
					ComFunc.log("set permission:" + bNeedRequestPermission);
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

				ComFunc.log(UsbManager.ACTION_USB_DEVICE_ATTACHED);
				ComFunc.log("set permission 1:" + bNeedRequestPermission);
				call();
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				bNeedRequestPermission = true;
				deviceDeinit(false);
				ComFunc.log(UsbManager.ACTION_USB_DEVICE_DETACHED);
				call();
			
			} else if (Constant.FIRST_BLOOD.equals(action)){
				call();
			}
		}
	};

}
