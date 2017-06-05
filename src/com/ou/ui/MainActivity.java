package com.ou.ui;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity implements  CallBack {

	final int FILE_SELECT_CODE = 1;
	Function mFunc;
	UIMessageHandler mHandler;
	boolean bUpgrade = false;
	boolean bAttach = false;
	DetectUsbThread mDetectThread;
	MainActivity mApp;
	boolean bNormalMode = false;
	UpgradeThread mUpdateThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	//	setTitleColor(Color.rgb(0xFF, 0x34, 0x56));
		
		mApp = this;
		
		mHandler = new UIMessageHandler();
		setTitle(ComFunc.getString(this, R.string.msg_device_no_found));
		addItem();
		
		mDetectThread = new DetectUsbThread(getApplicationContext(), this);
		mDetectThread.start();
		ComFunc.log("demo onCreate");
	}

	private void addItem() {
		GridView gridview = (GridView) findViewById(R.id.gridview);

		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map;
		map = new HashMap<String, Object>();
		map.put(Constant.ITEM_KEY_IMG, R.drawable.id);
		map.put(Constant.ITEM_KEY_TEXT, ComFunc.getString(mApp, R.string.button_id));
		lstImageItem.add(map);
		
		map = new HashMap<String, Object>();
		map.put(Constant.ITEM_KEY_IMG, R.drawable.setting);
		map.put(Constant.ITEM_KEY_TEXT, ComFunc.getString(mApp, R.string.button_setting));
		lstImageItem.add(map);
		
		map = new HashMap<String, Object>();
		map.put(Constant.ITEM_KEY_IMG, R.drawable.key);
		map.put(Constant.ITEM_KEY_TEXT, ComFunc.getString(mApp, R.string.button_key));
		lstImageItem.add(map);
		
		map = new HashMap<String, Object>();
		map.put(Constant.ITEM_KEY_IMG, R.drawable.hardwaretest);
		map.put(Constant.ITEM_KEY_TEXT, ComFunc.getString(mApp, R.string.button_hardware));
		lstImageItem.add(map);
		
		map = new HashMap<String, Object>();
		map.put(Constant.ITEM_KEY_IMG, R.drawable.cal);
		map.put(Constant.ITEM_KEY_TEXT,  ComFunc.getString(mApp, R.string.button_checksum));
		lstImageItem.add(map);
		
		map = new HashMap<String, Object>();
		map.put(Constant.ITEM_KEY_IMG, R.drawable.upgrade);
		map.put(Constant.ITEM_KEY_TEXT,  ComFunc.getString(mApp, R.string.button_upgrade));
		lstImageItem.add(map);

		SimpleAdapter saImageItems = new SimpleAdapter(this, 
				lstImageItem, 
				R.layout.grid_item,
				new String[] { Constant.ITEM_KEY_IMG, Constant.ITEM_KEY_TEXT},
				new int[] { R.id.ItemImage, R.id.ItemText });
		gridview.setAdapter(saImageItems);
		gridview.setOnItemClickListener(mClickListener);
	}
	
	OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			onClick(position);
		}
	};
	@Override
	protected void onDestroy() {
		ComFunc.log("demo onDestroy");
		if (DetectUsbThread.isUsbEnable() ) {
			mFunc = Function.getTpUsbFunction();
			mFunc.switchMode(Constant.TOUCH_MODE);
		}
		mDetectThread.release();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			ensureExit();
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

	
	public void onClick(int id) {
		// TODO Auto-generated method stub
/*		int id = v.getId();*/
		boolean r = false;
		r = DetectUsbThread.isUsbEnable();
		if (r == false) {
			//setTitle(ComFunc.getString(getApplicationContext(), R.string.device_no_open) + "\n");
			ComFunc.sendMessage(Constant.MSG_DEVICE_NOT_OPEN, this);
			return;
		}

		switch (id) {
		/*
		 * case R.id.buttonOpen: break; case R.id.buttonClose: ; break;
		 */
		case Constant.ITEM_VERSION:
			if (!bNormalMode)
				return;

			mFunc = DetectUsbThread.getUsbFunction();
			int v = mFunc.getFramewareIntId();
			if (v <= 0) {
				ComFunc.sendMessage(Constant.MSG_DEVICE_GET_FW_ID_FAIL, this);
				//mTv.append(ComFunc.getString(getApplicationContext(), R.string.get_id_fail) + "\n");
				break;
			}
			ComFunc.sendMessage(Constant.MSG_DEVICE_GET_FW_ID,v, this);
			//mTv.append(s + "\n");

			break;
		case Constant.ITEM_SETTING:
			if (!bNormalMode)
				return;

			mFunc = DetectUsbThread.getUsbFunction();
			SettingDialog d = new SettingDialog(this, mFunc);
			d.show();

			break;

		case Constant.ITEM_HARDWARETEST:
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
		case Constant.ITEM_KEY:
			if (!bNormalMode)
				return;

			startActivity(new Intent(this, ShortCutActivity.class));
			break;

		case Constant.ITEM_CALIB:
			if (!bNormalMode)
				return;

			startActivity(new Intent(mApp, CalActivity.class));
			break;

		case Constant.ITEM_UPGRADE:
			startSelectFile();
			break;
		}

	}

	private boolean ensureExit() {
		AlertDialog.Builder build = new Builder(this);

		build.setMessage(ComFunc.getString(this, R.string.ask_exit));
		build.setPositiveButton(ComFunc.getString(mApp, R.string.ok),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mApp.finish();

					}

				});

		build.setNegativeButton(ComFunc.getString(mApp, R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}

				});
		build.create().show();
		return true;
	}

	private boolean ensureUpdate(final Intent data) {
		AlertDialog.Builder build = new Builder(this);
		String message = ComFunc.getString(this, R.string.upgrade_ensure) + "\n";
		if (data != null && data.getData() != null && data.getData().getPath() != null)
			message += data.getData().getPath();
		
		build.setMessage(message);
		build.setPositiveButton(ComFunc.getString(mApp, R.string.ok),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// TODO Auto-generated method stub
						mUpdateThread = new UpgradeThread(mApp, data);
						mUpdateThread.start();
						ProgressDialog d = new ProgressDialog(mApp, mUpdateThread, data);
						d.show();
					}

				});

		build.setNegativeButton(ComFunc.getString(mApp, R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {

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
			setTitle(ComFunc.getString(this, R.string.device_info) + mFunc.getShortDesc(this));
			int pid = mFunc.getPid();
			if (pid != Constant.PID_NORMAL) {
				bNormalMode = false;
				if (mUpdateThread == null || (mUpdateThread != null && mUpdateThread.getWorkState() == false)) {
					setTitle(ComFunc.getString(mApp, R.string.bad_mode));
					return;
				}

			} else {
				bNormalMode = true;
			}
		} else {
			setTitle(ComFunc.getString(this, R.string.msg_device_no_found));
		}

	}

}
