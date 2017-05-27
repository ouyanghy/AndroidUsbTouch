package com.ou.ui;

import com.ou.common.Common;
import com.ou.common.Enums;
import com.ou.usbtp.CallBack;
import com.ou.usbtp.DetectUsbThread;
import com.ou.usbtp.Function;
import com.ou.usbtp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DemoActivity extends Activity implements OnClickListener,CallBack {

	protected static final String TAG = "TpActivity";
	final int FILE_SELECT_CODE = 1;
	Function mFunc;
	TextView mTv;
	UIMessageHandler mHandler;
	Button mBtnOpen, mBtnClose, mBtnId, mBtnSetting, mBtnTest, mBtnHardwareTest, mBtnCal,mBtnUpgrade;
	boolean bUpgrade = false;
	boolean bAttach = false;
	DetectUsbThread mDetectThread;
	DemoActivity mApp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usb_tp);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mApp = this;
		mTv = (TextView) findViewById(R.id.tv);
		mBtnOpen = (Button) findViewById(R.id.buttonOpen);
		mBtnClose = (Button) findViewById(R.id.buttonClose);
		mBtnId = (Button) findViewById(R.id.buttonId);
		mBtnSetting = (Button) findViewById(R.id.buttonSetting);
		mBtnTest = (Button) findViewById(R.id.buttonKeyset);
		mBtnHardwareTest = (Button) findViewById(R.id.buttonHardwareTest);
		mBtnCal = (Button) findViewById(R.id.buttonCal);
		mBtnUpgrade = (Button) findViewById(R.id.buttonUpgrde);
		mBtnOpen.setOnClickListener(this);
		mBtnClose.setOnClickListener(this);
		mBtnId.setOnClickListener(this);
		mBtnSetting.setOnClickListener(this);
		mBtnTest.setOnClickListener(this);
		mBtnHardwareTest.setOnClickListener(this);
		mBtnCal.setOnClickListener(this);
		mBtnUpgrade.setOnClickListener(this);
		mTv.setOnClickListener(this);
		mHandler = new UIMessageHandler();
		mTv.setText(Common.getString(this, R.string.device_no_found));
		mDetectThread = new DetectUsbThread(getApplicationContext(), this);
		mDetectThread.start();
		Common.log("demo onCreate");
		
	}

	
/*	private void sendMessage(int what) {
		mHandler.obtainMessage(what, getApplicationContext());
	}*/

	@Override
	protected void onDestroy() {
		Common.log("demo onDestroy");
		mDetectThread.release();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		boolean r  = false;
		switch (id) {
		case R.id.buttonOpen:


			break;
		case R.id.buttonClose:
				;
				break;
		case R.id.buttonId:
			r = DetectUsbThread.isUsbEnable();
			if (r == false) {
				mTv.append(Common.getString(getApplicationContext(), R.string.device_no_open) + "\n");
				break;
			}

			mFunc = DetectUsbThread.getUsbFunction();
			String s = mFunc.getFramewareId();
			if (s == null) {
				mTv.append(Common.getString(getApplicationContext(), R.string.get_id_fail) + "\n");
				break;
			}
			mTv.append(s + "\n");

			break;
		case R.id.buttonSetting:
			r = DetectUsbThread.isUsbEnable();
			if (r == false) {
				mTv.append(Common.getString(getApplicationContext(), R.string.device_no_open) + "\n");
				break;
			}
			
			mFunc = DetectUsbThread.getUsbFunction();
			SettingDialog d = new SettingDialog(this, mFunc);
			d.show();

			break;

		case R.id.buttonHardwareTest:
			r = DetectUsbThread.isUsbEnable();
			if (r == false) {
				mTv.append(Common.getString(getApplicationContext(), R.string.device_no_open) + "\n");
				break;
			}
			
			mFunc = DetectUsbThread.getUsbFunction();
			byte[] bs = mFunc.readBroadInfo();
			int size = mFunc.readBroadInfoScreenSize();

			Intent intent = new Intent(this, HardwareTestAcitivity.class);
			intent.putExtra(Enums.INTENT_BUFF, bs);
			intent.putExtra(Enums.INTENT_SIZE, size);
			Common.log("before 0:" + bs[0] + " 1:" + bs[1]);
			startActivity(intent);
			
			break;
		case R.id.buttonKeyset:
			startActivity(new Intent(this, ShortCutActivity.class));
			break;
			
		case R.id.buttonCal:
			r = DetectUsbThread.isUsbEnable();
			if (r == false) {
				mTv.append(Common.getString(getApplicationContext(), R.string.device_no_open) + "\n");
				break;
			}
			
			startActivity(new Intent(mApp, CalActivity.class));
			break;
			
		case R.id.buttonUpgrde:
			r = DetectUsbThread.isUsbEnable();
			if (r == false) {
				mTv.append(Common.getString(getApplicationContext(), R.string.device_no_open) + "\n");
				break;
			}

			
			startSelectFile();
			
			break;
			
		case R.id.tv:
		
			mTv.setText("");
			break;
		}	

	}

	private boolean ensureUpdate(final Intent data) {
		 AlertDialog.Builder build = new Builder(this);
		
		 build.setMessage(Common.getString(this, R.string.upgrade_ensure));
		 build.setPositiveButton(Common.getString(mApp, R.string.ok),new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mFunc = DetectUsbThread.getUsbFunction();
				ProgressDialog d = new ProgressDialog(mApp, mFunc, data);
				d.show();
			}
			

		
		});
		 
		 build.setNegativeButton(Common.getString(mApp, R.string.cancel),new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
			

		
		});
		build.create().show();
		 return true;
	}
	public void startSelectFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		

		try {
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {

		}
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (requestCode == FILE_SELECT_CODE) {	
				if (resultCode == 0)
					return;
				
				ensureUpdate(data);
				Common.log("resultCode:" + resultCode);
			

			}
			super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void call() {
		// TODO Auto-generated method stub
		
		if (DetectUsbThread.isUsbEnable()) {
			mFunc = Function.getTpUsbFunction();	
			mTv.setText(mFunc.getShortDesc(this));
		}
		else {
			mTv.setText(Common.getString(this, R.string.device_no_found));
		}
		
	}

	

	
}
