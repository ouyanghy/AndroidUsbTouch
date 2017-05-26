package com.ou.ui;

import com.ou.common.Enums;
import com.ou.usbtp.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class UIMessageHandler extends Handler {

	private void notice(Context context, int id, boolean long_time) {
		String s = context.getResources().getString(id);
		notice(context, s, long_time);
	}

	private void notice(Context context, String note, boolean long_time) {
		if (long_time)
			Toast.makeText(context, note, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, note, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void handleMessage(Message msg) {
		int what = msg.what;
		Context context = (Context) msg.obj;
		if (context == null)
			return;
		switch (what) {
		case Enums.MSG_ERASE_BOARD_INFO_ERR:
			notice(context, R.string.msg_erase_board_err, true);
			break;
		case Enums.MSG_ERASE_BOARD_INFO_SUCC:
			notice(context, R.string.msg_erase_board_succ, false);
			break;
		case Enums.MSG_WRITE_BOARD_INFO_ERR:
			notice(context, R.string.msg_write_board_succ, true);
			break;
		case Enums.MSG_WRITE_BOARD_INFO_SUCC:
			notice(context, R.string.msg_write_board_succ, false);
			break;
		case Enums.MSG_READ_BOARD_INFO_ERR:
			notice(context, R.string.msg_read_board_succ, true);
			break;
		case Enums.MSG_READ_BOARD_INFO_SUCC:
			notice(context, R.string.msg_read_board_succ, false);
			break;

		case Enums.MSG_ERASE_CAL_INFO_ERR:
			notice(context, R.string.msg_erase_cal_err, true);
			break;
		case Enums.MSG_ERASE_CAL_INFO_SUCC:
			notice(context, R.string.msg_erase_cal_succ, false);
			break;
		case Enums.MSG_WRITE_CAL_INFO_ERR:
			notice(context, R.string.msg_write_cal_succ, true);
			break;
		case Enums.MSG_WRITE_CAL_INFO_SUCC:
			notice(context, R.string.msg_write_cal_succ, false);
			break;
		case Enums.MSG_READ_CAL_INFO_ERR:
			notice(context, R.string.msg_read_cal_succ, true);
			break;
		case Enums.MSG_READ_CAL_INFO_SUCC:
			notice(context, R.string.msg_read_cal_succ, false);
			break;

		case Enums.MSG_SETTING_INIT_ERR:
			notice(context, R.string.msg_setting_init_err, true);
			break;
		case Enums.MSG_SETTING_INIT_SUCC:
			notice(context, R.string.msg_setting_init_succ, false);
			break;

		case Enums.MSG_SETTING_OK_ERR:
			notice(context, R.string.msg_setting_ok_err, true);
			break;
		case Enums.MSG_SETTING_OK_SUCC:
			notice(context, R.string.msg_setting_ok_succ, true);
			break;

		case Enums.MSG_SETTING_RESET_ERR:
			notice(context, R.string.msg_setting_reset_err, true);
			break;
		case Enums.MSG_SETTING_RESET_SUCC:
			notice(context, R.string.msg_setting_reset_succ, false);
			break;

		case Enums.MSG_SETTING_CLEAR_ERR:
			notice(context, R.string.msg_setting_clear_err, true);
			break;
		case Enums.MSG_SETTING_CLEAR_SUCC:
			notice(context, R.string.msg_setting_clear_succ, false);
			break;

		case Enums.MSG_SET_MODE_ERR:
			notice(context, R.string.msg_set_mode_err, true);
			break;
		case Enums.MSG_SET_MODE_SUCC:
			notice(context, R.string.msg_set_mode_succ, false);
			break;

		case Enums.MSG_TOUCH_MODE_ERR:
			notice(context, R.string.msg_touch_mode_err, true);
			break;
		case Enums.MSG_TOUCH_MODE_SUCC:
			notice(context, R.string.msg_touch_mode_succ, false);
			break;

		case Enums.MSG_SETTING_INITING:
			notice(context, R.string.msg_setting_initing, false);
			break;
		case Enums.MSG_UPGRADE_ERR:
			notice(context, R.string.msg_upgrade_err, true);
			break;

		case Enums.MSG_UPGRADE_SUCC:
			notice(context, R.string.msg_upgrade_succ, false);
			break;

		case Enums.MSG_NEED_USB_PERMISSION:
			notice(context, R.string.msg_device_need_permission, false);
			break;
		case Enums.MSG_REQUEST_USB_PERMISSION_ERR:
			notice(context, R.string.msg_device_request_permission_err, false);
			break;
		case Enums.MSG_REQUEST_USB_PERMISSION_SUCC:
			notice(context, R.string.msg_device_request_permission_succ, false);
			break;
			
		case Enums.MSG_OPEN_DEVICE_BOOT_MODE_ERR:
			notice(context, R.string.msg_open_boot_err, true);
			break;
		case Enums.MSG_OPEN_DEVICE_BOOT_MODE_SUCC:
			notice(context, R.string.msg_open_boot_succ, true);
			break;
			
		case Enums.MSG_OPEN_DEVICE_NORMAL_MODE_ERR:
			notice(context, R.string.msg_open_normal_err, true);
			break;
		case Enums.MSG_OPEN_DEVICE_NORMAL_MODE_SUCC:
			notice(context, R.string.msg_open_normal_succ, true);
			break;
			
		case Enums.MSG_FILE_INVAILD:
			notice(context, R.string.msg_file_invaild, true);
			break;
		}
	}
}
