package com.ou.ui;

import java.util.ArrayList;
import java.util.List;

import com.ou.common.Common;
import com.ou.common.Enums;
import com.ou.usbtp.BoardConfig;
import com.ou.usbtp.CalInfo;
import com.ou.usbtp.Function;
import com.ou.usbtp.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class SettingDialog extends Dialog implements OnClickListener {
	private Spinner mSpinnerOre, mSpinnerScreenSize;
	private Function mFunc;
	private UIMessageHandler mHandler;
	private Button mBtnOk, mBtnCancel, mBtnReset, mBtnCalClear;
	private boolean bInit = false;
	final DetectFinishHandler mDetectHandler;
	RadioGroup mRadioScreenDirection;

	public SettingDialog(Context context, Function func) {
		super(context);
		setContentView(R.layout.setting_dialog);
		String title = Common.getString(getContext(), R.string.setting_title);
		setTitle(title);

		mBtnOk = (Button) findViewById(R.id.buttonSettingOk);
		mBtnCancel = (Button) findViewById(R.id.buttonSettingCancel);
		mBtnReset = (Button) findViewById(R.id.buttonSettingReset);
		mBtnCalClear = (Button) findViewById(R.id.buttonSettingCalClear);
		mSpinnerOre = (Spinner) findViewById(R.id.spinnerOre);
		mSpinnerScreenSize = (Spinner) findViewById(R.id.spinnerScreenSize);
		mRadioScreenDirection = (RadioGroup) findViewById(R.id.radioGroup1);
		mBtnOk.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnReset.setOnClickListener(this);
		mBtnCalClear.setOnClickListener(this);

		mFunc = func;
		mHandler = new UIMessageHandler();
		 mDetectHandler = new DetectFinishHandler();
		//initSetting();
		//loadAllBroadInfo();
		initBackGround(Enums.MSG_SETTING_INIT_SUCC, Enums.MSG_SETTING_INIT_ERR);
	}

	private void sendMessage(int what) {
		mHandler.obtainMessage(what, getContext()).sendToTarget();
	}

	@Override
	public void dismiss() {
		byte ret[] = mFunc.switchMode(Enums.TOUCH_MODE);
		if (ret == null) {
			sendMessage(Enums.MSG_TOUCH_MODE_ERR);
		}

		super.dismiss();
	}

	private void loadAllBroadInfo() {
		List<String> list = new ArrayList<String>();
		for (String title : Enums.BOARD_CONFIG_TITLE) {
			list.add(title);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, list);
		mSpinnerScreenSize.setAdapter(adapter);
		// mSpinnerScreenSize

	}

	private BoardConfig parseBroadInfo() {
		int size = mFunc.readBroadInfoScreenSize();
		if (size < 0) {
			Common.log("get size fail");
			return null;
		}
		// Common.log("===size:" + size);
		Common.sleep(20);
		byte[] buf = mFunc.readBroadInfo();
		if (buf == null) {
			sendMessage(Enums.MSG_READ_BOARD_INFO_ERR);
			return null;
		}

		BoardConfig read_ic = new BoardConfig(Enums.READ_FROM_IC);
		read_ic.setBuffer(buf);
		read_ic.setSize(size);

		boolean b = false;
		for (int i = 0; i < Enums.BOARD_CONFIG_SIZE.length; i++) {
			BoardConfig con = new BoardConfig(i);
			b = con.equals(read_ic);
			if (b) {
				read_ic = con;
				return read_ic;
			}
		}

		return null;

	}

	final int INIT_FINISH = 1;
	
	
	private class DetectFinishHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == INIT_FINISH) {
				int ore = msg.arg1;
				int index = msg.arg2;
				if (index == Enums.IGNORE)
					index = 0;
				
				if (ore > 0 && ore < 5) {
					mRadioScreenDirection.check(R.id.radioVer);

				} else {
					mRadioScreenDirection.check(R.id.radioHor);
				}
				mSpinnerOre.setSelection(ore);
				mSpinnerScreenSize.setSelection(index);
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
		byte ret[] = mFunc.switchMode(Enums.SET_MODE);
		if (ret == null) {
			sendMessage(Enums.MSG_SET_MODE_ERR);
			return;
		}

		Common.sleep(20);
		CalInfo calInfo = mFunc.readCalInfo();
		if (calInfo == null) {
			sendMessage(Enums.MSG_READ_BOARD_INFO_ERR);
			return;
		}
		// notice(calInfo.toString());
		/* screen direction */
		int ore = calInfo.getScreenDirection();
		if (ore > 5) {
			// notice("initSetting screen direction error");
			ore = 0;
		}
		//mSpinnerOre.setSelection(ore);
		// notice("ore:" + ore);
		/*if (calInfo.getScreenDirection() > 0 && calInfo.getScreenDirection() < 5) {
			mRadioScreenDirection.check(R.id.radioVer);

		} else {
			mRadioScreenDirection.check(R.id.radioHor);
		}
*/
		
		BoardConfig config = parseBroadInfo();
		if (config != null) {
			// notice("find config");
			//mSpinnerScreenSize.setSelection(config.getIndex());
			mDetectHandler.obtainMessage(INIT_FINISH, ore, config.getIndex()).sendToTarget();
			sendMessage(notice_id_succ);
		} else {
			sendMessage(notice_id_fail);
			mDetectHandler.obtainMessage(INIT_FINISH, ore, Enums.IGNORE).sendToTarget();

		}
	
	}

	private boolean updateCalInfo(int dir, int matrix_flag) {
		CalInfo info = mFunc.readCalInfo();
		boolean r = false;
		if (info == null) {
			sendMessage(Enums.MSG_READ_CAL_INFO_ERR);
			return false;
		}

		if (matrix_flag != Enums.IGNORE)
			info.setMatrixFlag(matrix_flag);
		
		if (dir != Enums.IGNORE)
			info.setScreenDirection(dir);
		
		Common.sleep(20);
		r = mFunc.eraseCalInfo();
		if (r == false) {
			sendMessage(Enums.MSG_ERASE_CAL_INFO_ERR);
			return false;
		}

		Common.sleep(20);
		// dir = mSpinnerOre.getSelectedItemPosition();
	

		r = mFunc.writeCalInfo(info);
		if (r == false) {
			sendMessage(Enums.MSG_WRITE_CAL_INFO_ERR);
			return false;
		}

		return true;
	}

	private boolean updateBroadInfo() {
		byte[] ret = mFunc.eraseBroadInfo();
		if (ret == null) {
			sendMessage(Enums.MSG_ERASE_BOARD_INFO_ERR);
			return false;
		}

		int index = mSpinnerScreenSize.getSelectedItemPosition();

		BoardConfig config = new BoardConfig(index);
		ret = mFunc.writeBroadInfo(config);
		if (ret == null) {
			sendMessage(Enums.MSG_WRITE_BOARD_INFO_ERR);
			return false;
		}

		/*
		 * for (int i = 0; i < 3;i ++) { Common.sleep(40); byte [] inf =
		 * mFunc.readBroadInfo(); if (inf == null) return false; }
		 */
		return true;
	}

	private boolean clearCalInfo() {
		return updateCalInfo(Enums.IGNORE, 0);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (bInit == false)
			return;
		
		switch (v.getId()) {
		case R.id.buttonSettingOk: {
			boolean r = updateBroadInfo();
			if (r == false) {
				sendMessage(Enums.MSG_SETTING_OK_ERR);
				dismiss();
				break;
			}

			Common.sleep(40);
			int dir = mSpinnerOre.getSelectedItemPosition();
			r = updateCalInfo(dir,Enums.IGNORE);
			if (r == false)
				sendMessage(Enums.MSG_SETTING_OK_ERR);
			else {
				sendMessage(Enums.MSG_SETTING_OK_SUCC);

			}
			dismiss();
			break;
		}

		case R.id.buttonSettingCancel:
			dismiss();
			break;

		case R.id.buttonSettingReset: {
			initBackGround(Enums.MSG_SETTING_RESET_SUCC, Enums.MSG_SETTING_RESET_ERR);
			break;
		}
		case R.id.buttonSettingCalClear:
			boolean r = clearCalInfo();
			if (r)
				sendMessage(Enums.MSG_SETTING_CLEAR_SUCC);
			else
				sendMessage(Enums.MSG_SETTING_CLEAR_ERR);

		}
	}

}
