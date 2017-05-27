package com.ou.ui;

import com.ou.common.Common;
import com.ou.common.Enums;
import com.ou.usbtp.BoardConfig;
import com.ou.usbtp.HardwareSignal;
import com.ou.usbtp.HardwareTestWorkThread;
import com.ou.usbtp.R;
import com.ou.view.HardwareTestView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HardwareTestAcitivity extends Activity {

	HardwareTestView mV;
	HardwareTestWorkThread mDataThread;
	HardwareTestHandler mHandler;
	BoardConfig mBoardConfig;
	boolean bSetting = false;
	Object mLock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hardware_test);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		int size = getIntent().getIntExtra(Enums.INTENT_SIZE, -1);
		byte[] buffer = getIntent().getByteArrayExtra(Enums.INTENT_BUFF);
		mLock = new Object();
		// Common.log("after 0:" +buffer[0] + " 1:" + buffer[1]);

		mBoardConfig = new BoardConfig(size, buffer);
		Common.log(mBoardConfig.toString());

		mV = (HardwareTestView) findViewById(R.id.hardwareView);
		mV.init(mBoardConfig);

		mHandler = new HardwareTestHandler();
		mDataThread = new HardwareTestWorkThread(mHandler, mBoardConfig);
		mDataThread.start();
	}

	final int MENU_GRP_X = 3;
	final int MENU_GRP_Y = 4;
	final int MENU_GRP_SET = 5;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usb_tp, menu);
		String title = "X ( " + mBoardConfig.getXBoardNum() + " )";
		SubMenu sub = menu.addSubMenu(title);
		title = "X-ALL";
		sub.add(MENU_GRP_X, -1, Menu.NONE, title);
		for (int i = 0; i < mBoardConfig.getXBoardNum(); i++) {
			title = "X-" + i;
			sub.add(MENU_GRP_X, i, Menu.NONE, title);
		}

		title = "Y ( " + mBoardConfig.getYBoardNum() + " )";
		sub = menu.addSubMenu(title);
		title = "Y-ALL";
		sub.add(MENU_GRP_Y, -1, Menu.NONE, title);
		// sub.add("Y-ALL");
		for (int i = 0; i < mBoardConfig.getYBoardNum(); i++) {
			title = "Y-" + i;
			sub.add(MENU_GRP_Y, i, Menu.NONE, title);
		}
		title = "ÉèÖÃãÐÖµ";
		//sub = menu.addSubMenu(title);
		menu.add(MENU_GRP_SET, 1, Menu.NONE, title);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		int gid = item.getGroupId();
		Common.log("++select:" + id + " gid:" + gid);
		boolean ready = false;
		int cnt = 0;
		switch (gid) {
		
		case MENU_GRP_X:
			synchronized (mLock) {
				bSetting = true;
			}
		
			do {		
				ready = mV.getDrawStatus();
				Common.sleep(10);
				Common.log("x select ready:" + ready);
			}while(ready == false && cnt++ < 100);
			mV.setXMode(id);
			synchronized (mLock) {
				bSetting = false;
			}
			break;
		case MENU_GRP_Y:
			synchronized (mLock) {
				bSetting = true;
			}
			do {		
				ready = mV.getDrawStatus();
				Common.sleep(10);
			}while(ready == false && cnt++ < 100);
			mV.setYMode(id);
			synchronized (mLock) {
				bSetting = false;
			}
			break;

		case MENU_GRP_SET:
			HardwareSetDialog d = new HardwareSetDialog(this);
			d.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		mDataThread.stopWork();
		super.onDestroy();

	}

	class HardwareTestHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/* recv image data from HardwareTestWorkThread */
			case Enums.MSG_UPDATE_IMAGE:
				HardwareSignal signal = (HardwareSignal) msg.obj;
				boolean ready = mV.getDrawStatus();
				//Common.log("handler ready:" + ready + " setting:" + bSetting );
				if (ready) {
						boolean set = false;
						synchronized (mLock) {
							set= bSetting;
						}
						if (set == false)
							mV.setSignalAndUpdate(signal);	
						
				}
			}
		}
	}

	class HardwareSetDialog extends Dialog implements android.view.View.OnClickListener {
		private Button mBtnOk;
		private EditText mXEditThreshold, mYEditThreshold;

		public HardwareSetDialog(Context context) {
			super(context);
			setContentView(R.layout.hardware_test_dialog);
			mBtnOk = (Button) findViewById(R.id.buttonThresholdOk);
			mBtnOk.setOnClickListener(this);
			mXEditThreshold = (EditText) findViewById(R.id.editXThreshold);
			mYEditThreshold = (EditText) findViewById(R.id.editYThreshold);

			int initThreshold = mV.getXThreshold();
			mXEditThreshold.setText(String.valueOf(initThreshold));

			initThreshold = mV.getYThreshold();
			mYEditThreshold.setText(String.valueOf(initThreshold));
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.buttonThresholdOk) {
				String val = mXEditThreshold.getEditableText().toString();
				int ival = Integer.parseInt(val);
				if(ival > 0)
					mV.setXThreshold(ival);

				val = mYEditThreshold.getEditableText().toString();
				ival = Integer.parseInt(val);
				if (ival > 0)
					mV.setYThreshold(ival);
				dismiss();
			}
		}

	}

}
