package com.ou.ui;

import com.ou.common.ComFunc;
import com.ou.common.Constant;
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
		case Constant.MSG_ERASE_BOARD_INFO_ERR:
			notice(context, R.string.msg_erase_board_err, true);
			break;
		case Constant.MSG_ERASE_BOARD_INFO_SUCC:
			notice(context, R.string.msg_erase_board_succ, false);
			break;
		case Constant.MSG_WRITE_BOARD_INFO_ERR:
			notice(context, R.string.msg_write_board_succ, true);
			break;
		case Constant.MSG_WRITE_BOARD_INFO_SUCC:
			notice(context, R.string.msg_write_board_succ, false);
			break;
		case Constant.MSG_READ_BOARD_INFO_ERR:
			notice(context, R.string.msg_read_board_succ, true);
			break;
		case Constant.MSG_READ_BOARD_INFO_SUCC:
			notice(context, R.string.msg_read_board_succ, false);
			break;

		case Constant.MSG_ERASE_CAL_INFO_ERR:
			notice(context, R.string.msg_erase_cal_err, true);
			break;
		case Constant.MSG_ERASE_CAL_INFO_SUCC:
			notice(context, R.string.msg_erase_cal_succ, false);
			break;
		case Constant.MSG_WRITE_CAL_INFO_ERR:
			notice(context, R.string.msg_write_cal_succ, true);
			break;
		case Constant.MSG_WRITE_CAL_INFO_SUCC:
			notice(context, R.string.msg_write_cal_succ, false);
			break;
		case Constant.MSG_READ_CAL_INFO_ERR:
			notice(context, R.string.msg_read_cal_succ, true);
			break;
		case Constant.MSG_READ_CAL_INFO_SUCC:
			notice(context, R.string.msg_read_cal_succ, false);
			break;

		case Constant.MSG_SETTING_INIT_ERR:
			notice(context, R.string.msg_setting_init_err, true);
			break;
		case Constant.MSG_SETTING_INIT_SUCC:
			notice(context, R.string.msg_setting_init_succ, false);
			break;

		case Constant.MSG_SETTING_OK_ERR:
			notice(context, R.string.msg_setting_ok_err, true);
			break;
		case Constant.MSG_SETTING_OK_SUCC:
			notice(context, R.string.msg_setting_ok_succ, true);
			break;

		case Constant.MSG_SETTING_RESET_ERR:
			notice(context, R.string.msg_setting_reset_err, true);
			break;
		case Constant.MSG_SETTING_RESET_SUCC:
			notice(context, R.string.msg_setting_reset_succ, false);
			break;

		case Constant.MSG_SETTING_CLEAR_ERR:
			notice(context, R.string.msg_setting_clear_err, true);
			break;
		case Constant.MSG_SETTING_CLEAR_SUCC:
			notice(context, R.string.msg_setting_clear_succ, false);
			break;

		case Constant.MSG_SET_MODE_ERR:
			notice(context, R.string.msg_set_mode_err, true);
			break;
		case Constant.MSG_SET_MODE_SUCC:
			notice(context, R.string.msg_set_mode_succ, false);
			break;

		case Constant.MSG_TOUCH_MODE_ERR:
			notice(context, R.string.msg_touch_mode_err, true);
			break;
		case Constant.MSG_TOUCH_MODE_SUCC:
			notice(context, R.string.msg_touch_mode_succ, false);
			break;

		case Constant.MSG_SETTING_INITING:
			notice(context, R.string.msg_setting_initing, false);
			break;
		case Constant.MSG_UPGRADE_ERR:
			notice(context, R.string.msg_upgrade_err, true);
			break;

		case Constant.MSG_UPGRADE_SUCC:
			notice(context, R.string.msg_upgrade_succ, false);
			break;

		case Constant.MSG_NEED_USB_PERMISSION:
			notice(context, R.string.msg_device_need_permission, false);
			break;
		case Constant.MSG_REQUEST_USB_PERMISSION_ERR:
			notice(context, R.string.msg_device_request_permission_err, false);
			break;
		case Constant.MSG_REQUEST_USB_PERMISSION_SUCC:
			notice(context, R.string.msg_device_request_permission_succ, false);
			break;
			
		case Constant.MSG_OPEN_DEVICE_BOOT_MODE_ERR:
			notice(context, R.string.msg_open_boot_err, true);
			break;
		case Constant.MSG_OPEN_DEVICE_BOOT_MODE_SUCC:
			notice(context, R.string.msg_open_boot_succ, true);
			break;
			
		case Constant.MSG_OPEN_DEVICE_NORMAL_MODE_ERR:
			notice(context, R.string.msg_open_normal_err, true);
			break;
		case Constant.MSG_OPEN_DEVICE_NORMAL_MODE_SUCC:
			notice(context, R.string.msg_open_normal_succ, true);
			break;
			
		case Constant.MSG_FILE_INVAILD:
			notice(context, R.string.msg_file_invaild, true);
			break;
			
		case Constant.MSG_SHORTCUT_POINT_ERR:
			notice(context, R.string.msg_shortcut_bound_err, true);
			break;
		case Constant.MSG_SHORTCUT_POINT_WRITE_ERR:
			notice(context, R.string.msg_shortcut_write_err, true);
			break;
			
		case Constant.MSG_SHORTCUT_NEED_FINISH:
			notice(context, R.string.msg_shortcut_should_finish, true);
			break;
			
		case Constant.MSG_SHORTCUT_FINISH_INDEX:
			int index = msg.arg1;
			notice(context, ComFunc.getString(context, R.string.msg_key_set_finish_index) + index, true);
			break;
		case Constant.MSG_SHORTCUT_WRITE_ERR:
			notice(context, R.string.msg_shortcut_write_err, true);
			break;
		case Constant.MSG_SHORTCUT_WRITE_SUCC:
			notice(context, R.string.msg_shortcut_write_succ, true);
			break;	
			
		case Constant.MSG_DEVICE_GET_FW_ID_FAIL:
			
			notice(context, R.string.msg_get_id_fail, true);
			break;	
		case Constant.MSG_DEVICE_GET_FW_ID:
			int id = msg.arg1;
			String s = ComFunc.getString(context, R.string.fw_info);
			s += ((id >> 16) & 0xff)  + "." + ((id >> 8) & 0xff) + "." + (id & 0xff);
			notice(context, s, true);
			break;	
		case Constant.MSG_DEVICE_NOT_OPEN:
			notice(context, R.string.msg_device_no_found, true);
			break;	
			
		case Constant.MSG_FILE_NO_PERMISSION:
			notice(context, R.string.msg_file_not_permission, true);
			break;	
			
		case Constant.MSG_DO_NOT_DETACH:
			notice(context, R.string.msg_note_do_notdetach, true);
			break;	
			
		case Constant.MSG_DEVICE_NOT_FOUND:
			notice(context, R.string.msg_device_no_found, true);
			break;	
		}
	}
}
