package com.ou.common;

public class Constant {	
	public static final int ITEM_VERSION 		= 0;
	public static final int ITEM_SETTING 		= 1;
	public static final int ITEM_KEY 			= 2;
	public static final int ITEM_HARDWARETEST 	= 3;
	public static final int ITEM_CALIB 			= 4;
	public static final int ITEM_UPGRADE 		= 5;
	public static final String ITEM_KEY_TEXT 		= "text item key";
	public static final String ITEM_KEY_IMG 		= "img item key";
	public static final int VID = 0x0AC8;
	
	public static final int PID_NORMAL = 0xCB0B;
	public static final int REPORT_ID_OUT_CMD = 0x5;

	public static final int CMD_SET_MODE = 0x3;
	public static final int CMD_GET_IMAGE = 0;
	public static final int CMD_GET_STATUS = 1;
	public static final int CMD_CALIB_INIT = 7;
	public static final int CMD_CALIBRATE = 8;
	public static final int CMD_ERASE_FLASH = 5;
	public static final int CMD_WRITE_FLASH = 6;
	public static final int CMD_SET_GAIN = 10;
	public static final int CMD_READ_FLASH = 0x4;

	public static final int SET_MODE = 0x00;
	public static final int TOUCH_MODE = 0x02;
	public static final int EMCRYPTION_OPEN = 0x03;
	public static final int EMCRYPTION_CLOSE = 0x04;
	public static final int BOOTLOADER_MODE = 0x42;
	public static final int FIRMWARE_MODE = 0x46;

	public static final int EXTERNAL_FLASH_ADDRESS = 0x08000000;
	/* for firmware */
	public static final int FW_VERSION_ADDR = 0x0C000;
	public static final int BOARD_INFO_ADDR = 0x08000;
	public static final int CALIB_INFO_ADDR = 0x04000;
	public static final int DEVICE_INFO_ADDR = 0x0F000;
	public static final int SHORTCUT_INFO_ADDR = 0x20000;
	public static final int FW_UPDATEFLAG_ADDR = 0x10000;

	public static final int PACKAGE_LENGTH_LIMIT = 0x40;
	public static final int PACKAGE_RECV_DATA_START = 8;
	public static final int PACKAGE_RECV_LENGTH_DATA_LIMIT = PACKAGE_LENGTH_LIMIT - PACKAGE_RECV_DATA_START;
	public static final int PACKAGE_SEND_LENGTH_LIMIT = 52;
	
	
	public static final int KB = 1024;
	public static final int MB = KB * KB;
	public static final int MSG_ERASE_BOARD_INFO_ERR = 0;
	public static final int MSG_ERASE_BOARD_INFO_SUCC =1;
	
	public static final int MSG_READ_BOARD_INFO_ERR = 2;
	public static final int MSG_READ_BOARD_INFO_SUCC =3;
	
	public static final int MSG_WRITE_BOARD_INFO_ERR = 4;
	public static final int MSG_WRITE_BOARD_INFO_SUCC =5;
	
	public static final int MSG_ERASE_CAL_INFO_ERR = 6;
	public static final int MSG_ERASE_CAL_INFO_SUCC =7;
	
	public static final int MSG_READ_CAL_INFO_ERR = 8;
	public static final int MSG_READ_CAL_INFO_SUCC =9;
	
	public static final int MSG_WRITE_CAL_INFO_ERR = 10;
	public static final int MSG_WRITE_CAL_INFO_SUCC =11;
	
	public static final int MSG_SETTING_INIT_ERR =12;
	public static final int MSG_SETTING_INIT_SUCC =13;
	
	public static final int MSG_SETTING_OK_ERR =14;
	public static final int MSG_SETTING_OK_SUCC =15;
	
	public static final int MSG_SETTING_RESET_ERR =16;
	public static final int MSG_SETTING_RESET_SUCC =17;
	
	public static final int MSG_SET_MODE_ERR =18;
	public static final int MSG_SET_MODE_SUCC =19;
	
	public static final int MSG_TOUCH_MODE_ERR =20;
	public static final int MSG_TOUCH_MODE_SUCC =21;
	
	public static final int MSG_SETTING_CLEAR_ERR =22;
	public static final int MSG_SETTING_CLEAR_SUCC =23;
	
	public static final int MSG_SETTING_INITING =24;
	
	public static final int MSG_UPGRADE_ERR =25;
	public static final int MSG_UPGRADE_SUCC =26;
	
	public static final int MSG_CAL_ERR =27;
	public static final int MSG_CAL_SUCC =28;
	public static final int MSG_FILE_INVAILD = 29;
	
	public static final int MSG_NEED_USB_PERMISSION = 30;
	public static final int MSG_REQUEST_USB_PERMISSION_ERR = 31;
	public static final int MSG_REQUEST_USB_PERMISSION_SUCC = 32;
	
	public static final int MSG_OPEN_DEVICE_NORMAL_MODE_ERR = 33;
	public static final int MSG_OPEN_DEVICE_NORMAL_MODE_SUCC = 34;

	public static final int MSG_OPEN_DEVICE_BOOT_MODE_ERR = 35;
	public static final int MSG_OPEN_DEVICE_BOOT_MODE_SUCC = 36;

	public static final int MSG_DEVICE_ATTACH = 37;
	public static final int MSG_DEVICE_DETACH = 38;
	
	public static final int MSG_UPDATE_IMAGE = 40;
	public static final int MSG_GET_CAL_POINT = 41;
	public static final int MSG_GET_CAL_POINT_TIME_OUT = 42;
	public static final int MSG_SHORTCUT_POINT_ERR = 43;
	public static final int MSG_SHORTCUT_POINT_SUCC = 44;
	public static final int MSG_SHORTCUT_POINT_WRITE_ERR = 45;
	public static final int MSG_SHORTCUT_POINT_WRITE_SUCC = 46;
	public static final int MSG_SHORTCUT_NEED_FINISH = 47;
	public static final int MSG_SHORTCUT_FINISH_INDEX = 48;
	
	public static final int MSG_SHORTCUT_WRITE_ERR = 49;
	public static final int MSG_SHORTCUT_WRITE_SUCC = 50;
	
	public static final int MSG_SETTING_NOT_CMP = 51;
	public static final int MSG_DEVICE_NOT_OPEN = 52;
	public static final int MSG_DEVICE_GET_FW_ID = 53;

	public static final int MSG_DEVICE_GET_FW_ID_FAIL = 54;
	public static final int MSG_FILE_NO_PERMISSION = 55;
	public static final int MSG_DO_NOT_DETACH = 56;
	public static final int [] BOARD_CONFIG_SIZE = {
		100,23,37,47,65,67,77,66
	};
	
	public static final String INTENT_SIZE = "put size";
	public static final String INTENT_BUFF = "put buffer";
	public static final String INTENT_CONFIG = "board-config";
	
	public static final int READ_FROM_IC = -1;
	public static final int IGNORE = -1;
	public static final int LED_EMIT_DIRECTION_TOTAL_NUM = 9;
	public static final int LED_EMIT_GROUNP = 6;
	public static final int BOARD_MAX = 16;
	public static final int BOARD_INFO_SIZE = 4;
	public static final int PROGRESS_UNSTART = 0;
	public static final int PROGRESS_EMCRYPTION_OPEN = 1;
	public static final int PROGRESS_READ_FILE = 2;
	public static final int PROGRESS_FLASH_FINISH = 90;
	public static final int PROGRESS_FINISH = 100;
	public static final int PROGRESS_ERR = -1;
	
	public static final int DEVICE_INTERFACE_NORMAL = 2;
	public static final int DEVICE_INTEFFACE_BOOT = 0;
	
	public static final int PROGRESS_SWITCH_BOOT = 20;
	public static final int PROGRESS_WRITE_DATA = 30;
	
	public static final String FIRST_BLOOD = "first open ";
	public static final int CAL_POINT_TIMEOUT = 10 * 1000;
	public static final int ZONE_LEFT = 0;
	public static final int ZONE_RIGHT = 1;
	public static final int ZONE_BOTTOM = 2;
	public static final int ZONE_TOP =3;
	public static final int ZONE_SIZE =4;
	
	public static final  String ACTION_USB_PERMISSION = "com.ou.usb.tp";
	public static final String [] BOARD_CONFIG_TITLE = {
					"100''(320*192)",
					"23''(64*64)",
					"37''(128*64)",
					"47''(128*128)",
					"65''(240*135)",
					"67''(256*128)",
					"77''(320*64)",
					"66''(192*192)",
					
			
	};
	
	static byte  H(int val) {
		return (byte) ((val & 0xff00)>>8);
	}
	
	static byte L(int val) {
		return (byte) (val & 0xff);
	}
	public static final byte [] BOARD_CONFIG_0 = {
			/*emit*/
			L(512), H(512), L(192),H(192),L(320),H(320), 8, 
			5,8,64,40, 6,8,64,48, 7,8,64,56, 0,8,64,0, 
			1,8,64,8, 2,8,64,16, 3,8,64,24, 4,8,64,32, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
			/*rcv*/
			L(512), H(512),L(192),H(192),L(320),H(320),8,
			0,8,64,0, 1,8,64,8, 2,8,64,16, 3,8,64,24, 
			4,8,64,32, 5,8,64,40, 6,8,64,48, 7,8,64,56, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0
	};
	
	public static final byte [] BOARD_CONFIG_1 = {
					L(128),H(128),L(64),H(64),L(64),H(64),2, 
					1,8,64,8, 0,8,64,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
					
					L(128),H(128),L(64),H(64),L(64),H(64),2, 
					0,8,64,0, 1,8,64,8, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0
	};
	
	public static final byte [] BOARD_CONFIG_2 = {
			L(192),H(192),L(64),H(64),L(128),H(128),3, 
			2,8,64,16, 0,8,64,0, 1,8,64,8, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
			L(192),H(192),L(64),H(64),L(128),H(128),3, 
			0,8,64,0, 1,8,64,8, 2,8,64,16, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0

};
	
	public static final byte [] BOARD_CONFIG_3 = {
			        L(256),H(256),L(128),H(128),L(128),H(128),4, 
					2,8,64,16, 3,8,64,24, 0,8,64,0, 1,8,64,8, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
					 L(256),H(256),L(128),H(128),L(128),H(128),4, 
					0,8,64,0, 1,8,64,8, 2,8,64,16, 3,8,64,24, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
					0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0

};

	public static final byte [] BOARD_CONFIG_4 = {
			L(375),H(375),L(135),H(135),L(240),H(240), 7, 
			4,8,48,30, 5,8,48,36, 6,8,39,42, 0,8,64,0, 
			1,8,64,8, 2,8,64,16, 3,8,48,24, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
			
			
			L(375),H(375),L(135),H(135),L(240),H(240), 7, 
			0,8,48,0, 1,8,48,6, 2,8,39,12, 3,8,64,17, 
			4,8,64,25, 5,8,64,33, 6,8,48,41, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0


};
	
	public static final byte [] BOARD_CONFIG_5 = {
			L(384),H(384),L(128),H(128),L(256),H(256),6,//7
			4,8,64,32, 5,8,64,40, 0,8,64,0, 1,8,64,8, //
			2,8,64,16, 3,8,64,24, 0,0,0,0, 0,0,0,0, //
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, //
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,//71
			L(384),H(384),L(128),H(128),L(256),H(256),6,//78
			0,8,64,0, 1,8,64,8, 2,8,64,16, 3,8,64,24, 
			4,8,64,32, 5,8,64,40, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0

};
	
	public static final byte [] BOARD_CONFIG_6 = {
			L(384),H(384),L(64),H(64),L(320),H(320),6, 
			5,8,64,40, 0,8,64,0, 1,8,64,8, 2,8,64,16, 
			3,8,64,24, 4,8,64,32, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
			L(384),H(384),L(64),H(64),L(320),H(320),6, 
			0,8,64,0, 1,8,64,8, 2,8,64,16, 3,8,64,24, 
			4,8,64,32, 5,8,64,40, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0

};
	public static final byte [] BOARD_CONFIG_7 = {
			L(384),H(384),L(192),H(192),L(192),H(192),6, 
			3,8,64,24, 4,8,64,32, 5,8,64,40, 0,8,64,0, 
			1,8,64,8, 2,8,64,16, 3,8,64,24, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
			L(384),H(384),L(192),H(192),L(192),H(192),6, 
			0,8,64,0, 1,8,64,8, 2,8,64,16, 3,8,64,24, 
			4,8,64,32, 5,8,64,40, 6,8,64,48, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0


};
	
}
