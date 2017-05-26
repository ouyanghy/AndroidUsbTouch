package com.ou.ui;

import com.ou.common.Common;
import com.ou.common.Enums;
import com.ou.usbtp.DetectUsbThread;
import com.ou.usbtp.Function;
import com.ou.usbtp.R;
import com.ou.usbtp.UpgradeThread;

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

	public ProgressDialog(Context context, Function func, Intent data) {
		super(context);
		setContentView(R.layout.progress_dialog);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		mV = (ProgressView) findViewById(R.id.progressView);

		mWorkThread = new UpgradeThread(context, data, func);
		mWorkThread.start();
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
					Common.log("percent:" + mPercent);
					if (mPercent == Enums.PROGRESS_UNSTART) {

						obse_time++;
						if (obse_time > 8) {
							mCurPercent = Enums.PROGRESS_ERR;
							mPercent = Enums.PROGRESS_ERR;

						} else {
							Common.sleep(100);
							continue;
						}
					}
					else if (mPercent <= Enums.PROGRESS_ERR) {
						mCurPercent-= 2;
					}
					
					else if (mPercent <= Enums.PROGRESS_FLASH_FINISH
							&& mCurPercent <= Enums.PROGRESS_FLASH_FINISH) {
						mCurPercent++;
					}  else if (mCurPercent <= (Enums.PROGRESS_FINISH - 2)) {
						mCurPercent++;
					}  

					if (mPercent == Enums.PROGRESS_READ_FILE)
						mNote = Common.getString(getContext(), R.string.upgrade_check_file);
					else if (mPercent >= Enums.PROGRESS_WRITE_DATA && mPercent < Enums.PROGRESS_FLASH_FINISH)
						mNote = Common.getString(getContext(), R.string.upgrade_write_data);
					else if (mPercent == Enums.PROGRESS_FLASH_FINISH)
						mNote = Common.getString(getContext(), R.string.upgrade_write_finish);
					else if (mPercent > Enums.PROGRESS_FLASH_FINISH)
						mNote = Common.getString(getContext(), R.string.upgrade_switch_nomal);
					else if (mPercent == Enums.PROGRESS_SWITCH_BOOT)
						mNote = Common.getString(getContext(), R.string.upgrade_switch_boot);

					float t = Math.abs(mPercent - mCurPercent) / 100;
					mSleep = (long) (Math.abs(1 - 4 * t) * 400);
					mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
					Common.sleep(mSleep);
					sleep_all += mSleep;

					if (sleep_all > 100 * 1000) {
						// some truoble ocur
						mCurPercent = Enums.PROGRESS_ERR;
						mPercent = Enums.PROGRESS_ERR;
						mNote = Common.getString(getContext(), R.string.upgrade_timeout);
						mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
						mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
					}

					if (mCurPercent >= (Enums.PROGRESS_FINISH - 1)) {
						boolean r = false;
						int cnt = 0;
						do {
							r = DetectUsbThread.isUsbEnable();
							Common.sleep(20);
						} while (r == false && cnt++ < 1000);
						
						if (cnt >= 1000) {
							mCurPercent = Enums.PROGRESS_ERR;
							mPercent = Enums.PROGRESS_ERR;
							mNote = Common.getString(getContext(), R.string.upgrade_timeout);
							mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
							mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
							break;
						}
							
						mCurPercent = Enums.PROGRESS_FINISH;
						mNote = Common.getString(getContext(), R.string.upgrade_finish);
						mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
						mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
						break;
					} 
					
					if (mCurPercent <= Enums.PROGRESS_ERR) {
						mCurPercent = Enums.PROGRESS_ERR;
						mNote = Common.getString(getContext(), R.string.upgrade_err);
						mHandler.obtainMessage(PROGRESS_UPDATE).sendToTarget();
						mHandler.obtainMessage(PROGRESS_FINISH).sendToTarget();
						break;
					} else
						;
				}
				Common.log("sleep all:" + sleep_all);
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
