package com.ou.ui;

import java.util.ArrayList;
import java.util.List;

import com.ou.base.BoardConfig;
import com.ou.base.CalInfo;
import com.ou.base.Function;
import com.ou.common.ComFunc;
import com.ou.common.Constant;
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
import android.widget.TextView;

public class SettingDialog extends Dialog implements OnClickListener {
	private Spinner mSpinnerOre, mSpinnerScreenSize;
	private Function mFunc;
	private Button mBtnOk, mBtnCancel, mBtnReset, mBtnCalClear;
	private boolean bInit = false;
	final DetectFinishHandler mDetectHandler;
	TextView mTvSize;
	RadioGroup mRadioScreenDirection;

	public SettingDialog(Context context, Function func) {
		super(context);
		setContentView(R.layout.setting_dialog);
		String title = ComFunc.getString(getContext(), R.string.setting_title);
		setTitle(title);

		mBtnOk = (Button) findViewById(R.id.buttonSettingOk);
		mBtnCancel = (Button) findViewById(R.id.buttonSettingCancel);
		mBtnReset = (Button) findViewById(R.id.buttonSettingReset);
		mBtnCalClear = (Button) findViewById(R.id.buttonSettingCalClear);
		mSpinnerOre = (Spinner) findViewById(R.id.spinnerOre);
		mSpinnerScreenSize = (Spinner) findViewById(R.id.spinnerScreenSize);
		mRadioScreenDirection = (RadioGroup) findViewById(R.id.radioGroup1);
		mTvSize = (TextView) findViewById(R.id.textViewScreenSize);
		mBtnOk.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnReset.setOnClickListener(this);
		mBtnCalClear.setOnClickListener(this);

		mFunc = func;
		mDetectHandler = new DetectFinishHandler();

		loadAllBroadInfo();
		initBackGround(Constant.IGNORE, Constant.IGNORE);
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	private void loadAllBroadInfo() {
		List<String> list = new ArrayList<String>();
		for (String title : Constant.BOARD_CONFIG_TITLE) {
			list.add(title);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, list);
		mSpinnerScreenSize.setAdapter(adapter);
		// mSpinnerScreenSize

	}

	private BoardConfig parseBroadInfo() {

		BoardConfig conf = mFunc.readBroadInfo();
		if (conf == null) {
			return null;
		}

		boolean b = false;
		for (int i = 0; i < Constant.BOARD_CONFIG_SIZE.length; i++) {
			BoardConfig con = new BoardConfig(i);
			b = con.equals(conf);
			if (b) {
				conf = con;
				return conf;
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
				if (index == Constant.IGNORE)
					index = 0;

				if (ore > 0 && ore < 5) {
					mRadioScreenDirection.check(R.id.radioVer);

				} else {
					mRadioScreenDirection.check(R.id.radioHor);
				}
				mSpinnerOre.setSelection(ore);
				mSpinnerScreenSize.setSelection(index);

				BoardConfig conf = (BoardConfig) msg.obj;
				String s = ComFunc.getString(getContext(), R.string.setting_screen_info);
				s += ": ";
				s += conf.getSize();
				s += "'";
				s += "(" + conf.getXLedNumber() + "*" + conf.getYLedNumber() + ")";
				mTvSize.setText(s);
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
		CalInfo calInfo = mFunc.readCalInfo();
		if (calInfo == null) {
			ComFunc.sendMessage(Constant.MSG_READ_BOARD_INFO_ERR, getContext());
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

		BoardConfig config = parseBroadInfo();
		if (config != null) {
			if (notice_id_succ != Constant.IGNORE)
				ComFunc.sendMessage(notice_id_succ, getContext());
			mDetectHandler.obtainMessage(INIT_FINISH, ore, config.getIndex(), config).sendToTarget();
		} else {
			if (notice_id_succ != Constant.IGNORE)
				ComFunc.sendMessage(notice_id_fail, getContext());
			mDetectHandler.obtainMessage(INIT_FINISH, ore, Constant.IGNORE, config).sendToTarget();

		}

	}

	private boolean updateCalInfo(int dir, int matrix_flag) {
		CalInfo info = mFunc.readCalInfo();
		boolean r = false;
		if (info == null) {
			ComFunc.sendMessage(Constant.MSG_READ_CAL_INFO_ERR, getContext());
			return false;
		}

		if (matrix_flag != Constant.IGNORE)
			info.setMatrixFlag(matrix_flag);

		if (dir != Constant.IGNORE)
			info.setScreenDirection(dir);

		ComFunc.sleep(20);
		r = mFunc.eraseCalInfo();
		if (r == false) {
			//ComFunc.sendMessage(Constant.MSG_ERASE_CAL_INFO_ERR, getContext());
			return false;
		}

		ComFunc.sleep(20);
		// dir = mSpinnerOre.getSelectedItemPosition();

		r = mFunc.writeCalInfo(info);
		if (r == false) {
			ComFunc.sendMessage(Constant.MSG_WRITE_CAL_INFO_ERR, getContext());
			return false;
		}

		return true;
	}

	private boolean updateBroadInfo() {
		boolean ret = mFunc.eraseBroadInfo();
		if (ret == false) {
			//ComFunc.sendMessage(Constant.MSG_ERASE_BOARD_INFO_ERR, getContext());
			return false;
		}

		int index = mSpinnerScreenSize.getSelectedItemPosition();

		BoardConfig config = new BoardConfig(index);
		ret = mFunc.writeBroadInfo(config);
		if (ret == false) {
			ComFunc.sendMessage(Constant.MSG_WRITE_BOARD_INFO_ERR, getContext());
			return false;
		}

		/*
		 * for (int i = 0; i < 3;i ++) { Common.sleep(40); byte [] inf =
		 * mFunc.readBroadInfo(); if (inf == null) return false; }
		 */
		return true;
	}

	private boolean clearCalInfo() {
		return updateCalInfo(Constant.IGNORE, 0);
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
				ComFunc.sendMessage(Constant.MSG_SETTING_OK_ERR, getContext());
				dismiss();
				break;
			}

			ComFunc.sleep(40);
			int dir = mSpinnerOre.getSelectedItemPosition();
			r = updateCalInfo(dir, Constant.IGNORE);
			if (r == false)
				ComFunc.sendMessage(Constant.MSG_SETTING_OK_ERR, getContext());
			else {
				ComFunc.sendMessage(Constant.MSG_SETTING_OK_SUCC, getContext());

			}
			dismiss();
			break;
		}

		case R.id.buttonSettingCancel:
			dismiss();
			break;

		case R.id.buttonSettingReset: {
			initBackGround(Constant.MSG_SETTING_RESET_SUCC, Constant.MSG_SETTING_RESET_ERR);
			break;
		}
		case R.id.buttonSettingCalClear:
			boolean r = clearCalInfo();
			if (r)
				ComFunc.sendMessage(Constant.MSG_SETTING_CLEAR_SUCC, getContext());
			else
				ComFunc.sendMessage(Constant.MSG_SETTING_CLEAR_ERR, getContext());

		}
	}

}
