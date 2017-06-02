package com.ou.ui;

import com.ou.base.CallBack;
import com.ou.base.Function;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.thread.DetectUsbThread;
import com.ou.thread.UpgradeThread;
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
	boolean bNormalMode = false;
	UpgradeThread mUpdateThread;
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
		mTv.setText(ComFunc.getString(this, R.string.device_no_found));
		mDetectThread = new DetectUsbThread(getApplicationContext(),this);
		mDetectThread.start();
		ComFunc.log("demo onCreate");
		
	}

	@Override
	protected void onDestroy() {
		ComFunc.log("demo onDestroy");
		mDetectThread.release();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		boolean r  = false;
		r = DetectUsbThread.isUsbEnable();
		if (r == false) {
			mTv.setText(ComFunc.getString(getApplicationContext(), R.string.device_no_open) + "\n");
			return;
		}
		
		switch (id) {
		/*
		case R.id.buttonOpen:
			break;
		case R.id.buttonClose:
				;
				break;*/
		case R.id.buttonId:
			if (!bNormalMode)
				return;
			
			mFunc = DetectUsbThread.getUsbFunction();
			String s = mFunc.getFramewareId();
			if (s == null) {
				mTv.append(ComFunc.getString(getApplicationContext(), R.string.get_id_fail) + "\n");
				break;
			}
			mTv.append(s + "\n");

			break;
		case R.id.buttonSetting:
			if (!bNormalMode)
				return;
			
			mFunc = DetectUsbThread.getUsbFunction();
			SettingDialog d = new SettingDialog(this, mFunc);
			d.show();

			break;

		case R.id.buttonHardwareTest:
			if (!bNormalMode)
				return;
			
			mFunc = DetectUsbThread.getUsbFunction();
			byte[] bs = mFunc.readBroadInfo().getBuffer();
			int size = mFunc.readBroadInfoScreenSize();

			Intent intent = new Intent(this, HardwareTestAcitivity.class);
			intent.putExtra(Constant.INTENT_BUFF, bs);
			intent.putExtra(Constant.INTENT_SIZE, size);
			ComFunc.log("before 0:" + bs[0] + " 1:" + bs[1]);
			startActivity(intent);
			
			break;
		case R.id.buttonKeyset:
			if (!bNormalMode)
				return;
			
			startActivity(new Intent(this, ShortCutActivity.class));
			break;
			
		case R.id.buttonCal:
			if (!bNormalMode)
				return;
			
			startActivity(new Intent(mApp, CalActivity.class));
			break;
			
		case R.id.buttonUpgrde:
			startSelectFile();
			break;
			
		case R.id.tv:
		
			mTv.setText("");
			break;
		}	

	}

	private boolean ensureUpdate(final Intent data) {
		 AlertDialog.Builder build = new Builder(this);
		
		 build.setMessage(ComFunc.getString(this, R.string.upgrade_ensure));
		 build.setPositiveButton(ComFunc.getString(mApp, R.string.ok),new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// TODO Auto-generated method stub
				mUpdateThread = new UpgradeThread(mApp, data);
				mUpdateThread.start();
				ProgressDialog d = new ProgressDialog(mApp,mUpdateThread, data);
				d.show();
			}
			

		
		});
		 
		 build.setNegativeButton(ComFunc.getString(mApp, R.string.cancel),new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
			

		
		});
		build.create().show();
		 return true;
	}
	public void startSelectFile() {
		Intent i = new Intent(this, FileSelectorActivity.class);
		startActivityForResult(i, FILE_SELECT_CODE);
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (requestCode == FILE_SELECT_CODE) {	
				if (resultCode == 0)
					return;
				
				ensureUpdate(data);
				ComFunc.log("resultCode:" + resultCode);
			

			}
			super.onActivityResult(requestCode, resultCode, data);
	}

	
	@Override
	public void call() {
		// TODO Auto-generated method stub
		
		if (DetectUsbThread.isUsbEnable()) {
			
			mFunc = Function.getTpUsbFunction();
			mTv.setText(mFunc.getShortDesc(this));
			int pid = mFunc.getPid();
			if (pid != Constant.PID_NORMAL) {
				bNormalMode = false;
				if (mUpdateThread != null && mUpdateThread.getWorkState() == false) {
					mTv.setText(ComFunc.getString(mApp, R.string.bad_mode));
					return;
				}
				
			}
			else {
				bNormalMode = true;
			}
		}
		else {
			mTv.setText(ComFunc.getString(this, R.string.device_no_found));
		}
		
	}

	

	
}
