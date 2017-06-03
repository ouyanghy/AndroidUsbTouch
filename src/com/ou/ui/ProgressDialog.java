package com.ou.ui;

import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.thread.DetectUsbThread;
import com.ou.thread.UpgradeThread;
import com.ou.usbtp.R;
import com.ou.view.ProgressView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.WindowManager;

public class ProgressDialog extends Dialog {
	private UpgradeThread mWorkThread;
	private ProgressView mV;
	private int mPercent = 0;
	private int mCurPercent = 0;
	long mSleep = 10;
	String mNote = "";
	public static final int PROGRESS_UPDATE = 1;
	public static final int PROGRESS_FINISH = 2;

	public ProgressDialog(Context context, UpgradeThread update, Intent data) {
		super(context);
		setContentView(R.layout.progress_dialog);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		mV = (ProgressView) findViewById(R.id.progressView);
		mWorkThread = update;
		listenPercent();
		setCancelable(false);
	}

	public void listenPercent() {

		new Thread(new Runnable() {
			int sleep_all = 0;
			int obse_time = 0;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					mPercent = mWorkThread.getPercent();
					ComFunc.log("percent:" + mPercent);
					if (mPercent == Constant.PROGRESS_UNSTART) {

						obse_time++;
						if (obse_time > 8) {
							mCurPercent = Constant.PROGRESS_ERR;
							mPercent = Constant.PROGRESS_ERR;

						} else {
							ComFunc.sleep(100);
							continue;
						}
					}
					else if (mPercent <= Constant.PROGRESS_ERR) {
						mCurPercent-= 4;
					}
					else if (mPercent >= Constant.PROGRESS_FLASH_FINISH) {
						mCurPercent+=4;
					}
					else if (mPercent < Constant.PROGRESS_FLASH_FINISH
							&& mCurPercent < Constant.PROGRESS_FLASH_FINISH) {
						mCurPercent++;
					} 
					
					if (mCurPercent > mPercent)
						mCurPercent = mPercent;

					if (mPercent == Constant.PROGRESS_READ_FILE)
						mNote = ComFunc.getString(getContext(), R.string.upgrade_check_file);
					else if (mPercent >= Constant.PROGRESS_WRITE_DATA && mPercent < Constant.PROGRESS_FLASH_FINISH)
						mNote = ComFunc.getString(getContext(), R.string.upgrade_write_data);
					else if (mPercent == Constant.PROGRESS_FLASH_FINISH)
						mNote = ComFunc.getString(getContext(), R.string.upgrade_write_finish);
					else if (mPercent > Constant.PROGRESS_FLASH_FINISH)
						mNote = ComFunc.getString(getContext(), R.string.upgrade_switch_nomal);
					else if (mPercent == Constant.PROGRESS_SWITCH_BOOT)
						mNote = ComFunc.getString(getContext(), R.string.upgrade_switch_boot);

					float t = (float)Math.abs(mPercent - mCurPercent) / 100;
					mSleep = (long) (Math.abs(1 -  t) * 400);
					mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
					ComFunc.sleep(mSleep);
					sleep_all += mSleep;

					if (sleep_all > 100 * 1000) {
						// some truoble ocur
						mCurPercent = Constant.PROGRESS_ERR;
						mPercent = Constant.PROGRESS_ERR;
						mNote = ComFunc.getString(getContext(), R.string.upgrade_timeout);
						mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
						mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
					}

					if (mCurPercent >= (Constant.PROGRESS_FINISH - 1)) {
						boolean r = false;
						int cnt = 0;
						do {
							r = DetectUsbThread.isUsbEnable();
							ComFunc.sleep(20);
							ComFunc.log("wait usb enable:" + cnt);
						} while (r == false && cnt++ < 1000);
						
						if (cnt >= 1000) {
							mCurPercent = Constant.PROGRESS_ERR;
							mPercent = Constant.PROGRESS_ERR;
							mNote = ComFunc.getString(getContext(), R.string.upgrade_timeout);
							mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
							mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
							break;
						}
							
						mCurPercent = Constant.PROGRESS_FINISH;
						mNote = ComFunc.getString(getContext(), R.string.upgrade_finish);
						mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
						mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
						break;
					} 
					
					if (mCurPercent <= Constant.PROGRESS_ERR) {
						mCurPercent = Constant.PROGRESS_ERR;
						mNote = ComFunc.getString(getContext(), R.string.upgrade_err);
						mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
						mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
						break;
					} else
						;
				}
				ComFunc.log("sleep all:" + sleep_all);
			}
		}).start();
	}

	private Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == PROGRESS_UPDATE) {

				// Common.log("percent:" + mCurPercent);
				mV.setPercent(mCurPercent);
				mV.setNote(mNote);
			} else if (msg.what == PROGRESS_FINISH) {
				setCancelable(true);
			}
			return true;
		}
	});

}
