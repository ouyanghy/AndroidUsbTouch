package com.ou.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ou.base.BoardConfig;
import com.ou.base.BoardConfigMap;
import com.ou.base.CalInfo;
import com.ou.base.Function;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.thread.DetectUsbThread;
import com.ou.usbtp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener {
	private static final int FILE_SELECT_CODE = 0x345678;
	private Spinner mSpinnerOre, mSpinnerScreenSize;
	private Function mFunc;
	private Button mBtnOk, mBtnCancel, mBtnReset, mBtnCalClear, mBtnImport;
	private boolean bInit = false;
	DetectFinishHandler mDetectHandler = null;
	TextView mTvSize;
	RadioGroup mRadioScreenDirection;
	SettingActivity mApp;
	BoardConfig[] mMap = null;
	List<String> mSpinerlist = new ArrayList<String>();
	// list.add("*" + conf.getTitle());
	ArrayAdapter<String> mAdapter;// = new ArrayAdapter<String>(this,
									// R.layout.spinner_item, mSpinerlist);
	BoardConfig mCurConfig;
	final String DEFAULT_PATH = "/sdcard/broad_config.ini";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// public SettingDialog(Context context, Function func) {
		// super(context);
		setContentView(R.layout.setting_dialog);
		String title = ComFunc.getString(this, R.string.setting_title);
		setTitle(title);
		mApp = this;
		mBtnOk = (Button) findViewById(R.id.buttonSettingOk);
		mBtnCancel = (Button) findViewById(R.id.buttonSettingCancel);
		mBtnReset = (Button) findViewById(R.id.buttonSettingReset);
		mBtnCalClear = (Button) findViewById(R.id.buttonSettingCalClear);
		mSpinnerOre = (Spinner) findViewById(R.id.spinnerOre);
		mSpinnerScreenSize = (Spinner) findViewById(R.id.spinnerScreenSize);
		mRadioScreenDirection = (RadioGroup) findViewById(R.id.radioGroup1);
		mTvSize = (TextView) findViewById(R.id.textViewScreenSize);
		mBtnImport = (Button) findViewById(R.id.buttonImportConfig);
		mBtnImport.setOnClickListener(this);
		mBtnOk.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnReset.setOnClickListener(this);
		mBtnCalClear.setOnClickListener(this);

		// mFunc = ;
		mDetectHandler = new DetectFinishHandler();

		initBackGround(Constant.IGNORE, Constant.IGNORE);
	}

	private void loadAllBoardInfo(String path) {
		BoardConfigMap map = new BoardConfigMap(path);

		BoardConfig[] configs = null;

		try {
			configs = map.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return;
		}
		mMap = configs;
		if (configs == null && path != null) {
			ComFunc.sendMessage(Constant.MSG_IMPORT_CONFIG_FAIL, mApp);
		}
		mSpinerlist.clear();

		/*
		 * if (mCurConfig != null) mSpinerlist.add(mCurConfig.getTitle());
		 */

		for (int i = 0; configs != null && i < configs.length; i++) {
			mSpinerlist.add(configs[i].getTitle());
		}

		mAdapter = new ArrayAdapter<String>(mApp, R.layout.spinner_item, mSpinerlist);
		mSpinnerScreenSize.setAdapter(mAdapter);

		if (mCurConfig == null)
			return;

		for (int i = 0; configs != null && i < configs.length; i++) {
			// mSpinerlist.add(configs[i].getTitle());
			if (configs[i].getSize() == mCurConfig.getSize()) {
				mSpinnerScreenSize.setSelection(i);
				break;
			}
		}
		// mSpinnerScreenSize

	}

	private BoardConfig getCurrentBoardConfig() {
		if (DetectUsbThread.isUsbEnable() == false)
			return null;

		mFunc = Function.getTpUsbFunction();
		BoardConfig conf = mFunc.readBoardInfo();
		if (conf == null) {
			return null;
		}

		return conf;

	}

	final int INIT_FINISH = 1;

	private class DetectFinishHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == INIT_FINISH) {
				int ore = msg.arg1;
				int index = msg.arg2;
				if (index == Constant.IGNORE)
					index = 0;

				if (ore > 0 && ore < 5) {
					mRadioScreenDirection.check(R.id.radioVer);

				} else {
					mRadioScreenDirection.check(R.id.radioHor);
				}
				mSpinnerOre.setSelection(ore);
				mSpinnerScreenSize.setSelection(index);

				mCurConfig = (BoardConfig) msg.obj;
				if (mCurConfig != null) {

					String st = ComFunc.getString(mApp, R.string.setting_screen_info) + ":";

					String s = "";
					s += mCurConfig.getSize();
					s += "'";
					s += "(" + mCurConfig.getXLedNumber() + "*" + mCurConfig.getYLedNumber() + ")";
					mCurConfig.setTitle("*" + s);
					mTvSize.setText(st + s);
				}
				loadAllBoardInfo(DEFAULT_PATH);
			}

		}
	}

	private void initBackGround(final int notice_id_succ, final int notice_id_fail) {

		Runnable r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initSetting(notice_id_succ, notice_id_fail);
				bInit = true;
			}
		};

		new Thread(r).start();

	}

	private void initSetting(int notice_id_succ, int notice_id_fail) {

		ComFunc.sleep(20);
		if (DetectUsbThread.isUsbEnable() == false)
			return;

		mFunc = Function.getTpUsbFunction();
		CalInfo calInfo = mFunc.readCalInfo();
		if (calInfo == null) {
			ComFunc.sendMessage(Constant.MSG_READ_BOARD_INFO_ERR, this);
			return;
		}
		// notice(calInfo.toString());
		/* screen direction */
		int ore = calInfo.getScreenDirection();
		if (ore > 5) {
			// notice("initSetting screen direction error");
			ore = 0;
		}
		// mSpinnerOre.setSelection(ore);
		// notice("ore:" + ore);
		/*
		 * if (calInfo.getScreenDirection() > 0 && calInfo.getScreenDirection()
		 * < 5) { mRadioScreenDirection.check(R.id.radioVer);
		 * 
		 * } else { mRadioScreenDirection.check(R.id.radioHor); }
		 */

		BoardConfig config = getCurrentBoardConfig();
		if (config != null) {
			if (notice_id_succ != Constant.IGNORE)
				ComFunc.sendMessage(notice_id_succ, this);
			mDetectHandler.obtainMessage(INIT_FINISH, ore, config.getIndex(), config).sendToTarget();
		} else {
			if (notice_id_succ != Constant.IGNORE)
				ComFunc.sendMessage(notice_id_fail, this);
			mDetectHandler.obtainMessage(INIT_FINISH, ore, Constant.IGNORE, config).sendToTarget();

		}

	}

	private boolean updateCalInfo(int dir, int matrix_flag, boolean clean_cal) {
		if (DetectUsbThread.isUsbEnable() == false)
			return false;

		mFunc = Function.getTpUsbFunction();

		CalInfo info = mFunc.readCalInfo();
		boolean r = false;
		if (info == null) {
			ComFunc.sendMessage(Constant.MSG_READ_CAL_INFO_ERR, this);
			return false;
		}

		if (matrix_flag != Constant.IGNORE)
			info.setMatrixFlag(matrix_flag);

		if (dir != Constant.IGNORE)
			info.setScreenDirection(dir);

		if (clean_cal) {
			byte bs[] = new byte[32];
			ComFunc.memset(bs, 0x00, bs.length);
			info.setCalPoints(bs);
		}

		ComFunc.sleep(20);
		r = mFunc.eraseCalInfo();
		if (r == false) {
			// ComFunc.sendMessage(Constant.MSG_ERASE_CAL_INFO_ERR, this);
			return false;
		}

		ComFunc.sleep(20);
		// dir = mSpinnerOre.getSelectedItemPosition();

		r = mFunc.writeCalInfo(info);
		if (r == false) {
			ComFunc.sendMessage(Constant.MSG_WRITE_CAL_INFO_ERR, this);
			return false;
		}

		return true;
	}

	private boolean updateBoardInfo() {

		boolean ret = mFunc.eraseBoardInfo();
		if (ret == false) {
			// ComFunc.sendMessage(Constant.MSG_ERASE_BOARD_INFO_ERR, this);
			return false;
		}

		int index = mSpinnerScreenSize.getSelectedItemPosition();
		/**
		 * 0 is current setting
		 */
		/*
		 * if (index == 0) { return true; }
		 */

		BoardConfig config = mMap[index];
		if (config == null) {
			ComFunc.sendMessage(Constant.MSG_WRITE_BOARD_INFO_ERR, this);
			return false;
		}
		ret = mFunc.writeBoardInfo(config);
		if (ret == false) {
			ComFunc.sendMessage(Constant.MSG_WRITE_BOARD_INFO_ERR, this);
			return false;
		}

		/*
		 * for (int i = 0; i < 3;i ++) { Common.sleep(40); byte [] inf =
		 * mFunc.readBroadInfo(); if (inf == null) return false; }
		 */
		return true;
	}

	private boolean clearCalInfo() {
		return updateCalInfo(Constant.IGNORE, 0, true);
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

			String path = data.getData().getPath();
			loadAllBoardInfo(path);
			ComFunc.log("resultCode:" + resultCode);

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (bInit == false)
			return;

		if (DetectUsbThread.isUsbEnable() == false)
			return;

		mFunc = Function.getTpUsbFunction();

		switch (v.getId()) {
		case R.id.buttonSettingOk: {
			boolean r = updateBoardInfo();
			if (r == false) {
				ComFunc.sendMessage(Constant.MSG_SETTING_OK_ERR, this);
				break;
			}

			ComFunc.sleep(40);
			int dir = mSpinnerOre.getSelectedItemPosition();
			r = updateCalInfo(dir, Constant.IGNORE, false);
			if (r == false)
				ComFunc.sendMessage(Constant.MSG_SETTING_OK_ERR, this);
			else {
				ComFunc.sendMessage(Constant.MSG_SETTING_OK_SUCC, this);

			}
			finish();
			break;
		}

		case R.id.buttonSettingCancel:
			finish();
			break;

		case R.id.buttonSettingReset: {
			initBackGround(Constant.MSG_SETTING_RESET_SUCC, Constant.MSG_SETTING_RESET_ERR);
			break;
		}
		case R.id.buttonSettingCalClear:
			boolean r = clearCalInfo();
			if (r)
				ComFunc.sendMessage(Constant.MSG_SETTING_CLEAR_SUCC, this);
			else
				ComFunc.sendMessage(Constant.MSG_SETTING_CLEAR_ERR, this);
			break;

		case R.id.buttonImportConfig:
			startSelectFile();
			break;
		}
	}

}
