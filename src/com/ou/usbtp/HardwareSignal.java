package com.ou.usbtp;

import com.ou.common.Common;
import com.ou.common.Enums;

public class HardwareSignal {
	byte[][] mXledSignal;
	byte[][] mYledSignal;

	LedGroup mLedGroup[];
	BoardConfig mConfig;
	int mXLedStartPos;
	int mYLedStartPos;

	class LedGroup {
		LedGroup(int grp, byte[] sig0, byte[] sig1, byte[] sig2) {
			setGroup(grp);
			setSignal(sig0, sig1, sig2);

		}

		void setGroup(int grp) {
			mGrpNum = grp;
		}

		void setSignal(byte[] sig0, byte[] sig1, byte[] sig2) {
			signal = new byte[3][];
			signal[0] = sig0;
			signal[1] = sig1;
			signal[2] = sig2;
		}

		int mGrpNum;
		byte[][] signal;
	}

	public HardwareSignal(BoardConfig config) {
		mXledSignal = new byte[Enums.LED_EMIT_DIRECTION_TOTAL_NUM][config.getXLedNumber()];
		mYledSignal = new byte[Enums.LED_EMIT_DIRECTION_TOTAL_NUM][config.getYLedNumber()];
		mConfig = config;
		// mLedGroup = new byte[Enums.LED_EMIT_DIRECTION_TOTAL_NUM * 2][];
		initLedGroup();
		initBoardMap();
	}

	private void initBoardMap() {
		mXLedStartPos = 0;
		int lights = 0;
		for (int i = 0; i < mConfig.mBoardNum; i++) {
			lights += mConfig.getOneBoardLedNumber(i);
			if (lights >= mConfig.getYBoardNum()) {
				mYLedStartPos = lights;
			}
		}
	}


	public HardwareSignal(BoardConfig config, byte[][] totalSignal) {
		mXledSignal = new byte[Enums.LED_EMIT_DIRECTION_TOTAL_NUM][config.getXLedNumber()];
		mYledSignal = new byte[Enums.LED_EMIT_DIRECTION_TOTAL_NUM][config.getYLedNumber()];
		for (int dir = 0; dir < Enums.LED_EMIT_DIRECTION_TOTAL_NUM; dir++) {
			Common.memcpy(mXledSignal[dir], totalSignal[dir], 0, config.getYLedNumber(), config.getXLedNumber());
			Common.memcpy(mYledSignal[dir], totalSignal[dir], 0, 0, config.getYLedNumber());
		}
		mConfig = config;

		initLedGroup();
		initBoardMap();
	}

	public void setXLedSignal(int dir, byte[] sig, boolean need_parse) {
		if (need_parse)
			Common.memcpy(mXledSignal[dir], sig, 0, mConfig.getYLedNumber(), mConfig.getXLedNumber());
		else
			mXledSignal[dir] = sig;
	}

	public void setYLedSignal(int dir, byte[] sig, boolean need_parse) {
		if (need_parse)
			Common.memcpy(mYledSignal[dir], sig, 0, 0, mConfig.getYLedNumber());
		else
			mYledSignal[dir] = sig;
	}

	public byte[][] getXLedSignal() {
		return mXledSignal;
	}

	public byte[][] getYLedSignal() {
		return mYledSignal;
	}



	/*
	 * grp_num:0~6
	 * signal_num:0~3
	 * */

	public byte[] getGroupSignal(int grp_num, int signal_num) {
		return mLedGroup[grp_num].signal[signal_num];
	}
	
	public byte []getOneBoardGroupSignal(int grp_num, int signal_num, int board_id) {
		byte [] data = mLedGroup[grp_num].signal[signal_num];
		int start = 0;
		int board_index = 0;
		/*x*/
		if (grp_num < 3) {
			board_index = mConfig.getYBoardNum();
			start = 0;
			
			for (int i = 0; i < board_id ; i++) {
/*
D/QcConnectivityService( 3557): getMobileDataEnabled returning true
I/MLog    ( 8288): board map id:0 member:64
I/MLog    ( 8288): board map id:1 member:58

I/MLog    ( 8288): board map id:2 member:64 //0
I/MLog    ( 8288): board map id:3 member:64 //1
I/MLog    ( 8288): board map id:4 member:44
I/MLog    ( 8288): size:74
I/MLog    ( 8244): getOneBoardLedNumber:64 circle i:0
I/MLog    ( 8244): getOneBoardLedNumber:64 circle i:1
I/MLog    ( 8244): start:128 len:64
				 * */
				 board_index = i + mConfig.getYBoardNum();
				start += mConfig.getOneBoardLedNumber(board_index);
				//Common.log("getOneBoardLedNumber:" + mConfig.getOneBoardLedNumber(board_index) + " circle i:" + i );
			}
			board_index =  mConfig.getYBoardNum() + board_id;
			
		}
		/*y*/
		else {
			board_index = board_id;
			for (int i = 0; i < board_id;i++) {
				start += mConfig.getOneBoardLedNumber(i);
			}
		
		}
		//Common.log("start:" + start + " len:" +  mConfig.getOneBoardLedNumber(board_index));
		return Common.memcut(data,  start, mConfig.getOneBoardLedNumber(board_index) );
		
	}
	
	
	private void initLedGroup() {
		mLedGroup = new LedGroup[6];
		/* ========== x===================== */
		byte[] xStraight = mXledSignal[0];
		byte[] xLeft0 = mXledSignal[1];
		byte[] xRight0 = mXledSignal[2];
		mLedGroup[0] = new LedGroup(0, xStraight, xLeft0, xRight0);

		byte[] xLeft1 = mXledSignal[3];
		byte[] xLeft2 = mXledSignal[5];
		byte[] xLeft3 = mXledSignal[7];
		mLedGroup[1] = new LedGroup(1, xLeft1, xLeft2, xLeft3);

		byte[] xRight1 = mXledSignal[4];
		byte[] xRight2 = mXledSignal[6];
		byte[] xRight3 = mXledSignal[8];
		mLedGroup[2] = new LedGroup(1, xRight1, xRight2, xRight3);

		/* ========== y===================== */
		byte[] yStraight = mYledSignal[0];
		byte[] yLeft0 = mYledSignal[1];
		byte[] yRight0 = mYledSignal[2];
		mLedGroup[3] = new LedGroup(1, yStraight, yLeft0, yRight0);

		byte[] yLeft1 = mYledSignal[3];
		byte[] yLeft2 = mYledSignal[5];
		byte[] yLeft3 = mYledSignal[7];
		mLedGroup[4] = new LedGroup(1, yLeft1, yLeft2, yLeft3);

		byte[] yRight1 = mYledSignal[4];
		byte[] yRight2 = mYledSignal[6];
		byte[] yRight3 = mYledSignal[8];
		mLedGroup[5] = new LedGroup(1, yRight1, yRight2, yRight3);
	}
}
