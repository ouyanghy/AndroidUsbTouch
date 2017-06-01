package com.ou.base;

import com.ou.common.ComFunc;
import com.ou.common.Constant;

public class BoardConfig extends Constant {
	int mSize;
	String mTitle;
	byte mBuffer[];
	/* 16 board,4 size */
	private OneBoard [] mBoardMap;
	int mIndex = 0;
	int mXLedNum = -1;
	int mYLedNum = -1;
	int mLedTotal = -1;
	int mBoardNum = -1;
	int mXBoardNum = 1;
	int mYBoardNum = 1;
	int BOARD_MAP_START_POSITION = 2 + 2 + 2 + 1;

	class OneBoard {
	    byte mBoardIndex;
	    byte mGroupMembers;
	    byte mBoardMembers;
	    byte mBoardGroups;
	}
	public int getOneBoardLedNumber(int board_index) {
		if (board_index >= 8)
			return -1;
		
		return mBoardMap[board_index].mBoardMembers;
		//return mBuffer[BOARD_MAP_START_POSITION + board_number * 4 + 2] & 0xff;
	}

	public BoardConfig(int size, byte[] buffer) {
		mSize = size;
		mBuffer = buffer;
		mIndex = Constant.IGNORE;
		
		parse();

	}

	private void parse() {
		mLedTotal = (mBuffer[0] & 0xff) | ((mBuffer[1] & 0xff) << 8);
		mYLedNum = (mBuffer[2] & 0xff) | ((mBuffer[3] & 0xff) << 8);
		mXLedNum = (mBuffer[4] & 0xff) | ((mBuffer[5] & 0xff) << 8);
		mBoardNum = mBuffer[6] & 0xff;
		
		mBoardMap = new OneBoard[mBoardNum];
		for (int board_index = 0; board_index < mBoardNum; board_index++) {
			mBoardMap[board_index] = new OneBoard();
			int pos = BOARD_MAP_START_POSITION + board_index * Constant.BOARD_INFO_SIZE;
			mBoardMap[board_index].mBoardGroups = (byte) (mBuffer[pos++] & 0xff);
			mBoardMap[board_index].mGroupMembers = (byte) (mBuffer[pos++] & 0xff);
			mBoardMap[board_index].mBoardMembers = (byte) (mBuffer[pos++] & 0xff);
			mBoardMap[board_index].mBoardGroups = (byte) (mBuffer[pos] & 0xff);
			ComFunc.log("board map id:" + board_index + " member:" + mBoardMap[board_index].mBoardMembers);
				
		}
		calcXYBoardNumber();
	}

	@Override
	public String toString() {
		String s = "size:" + mSize + "\n" + 
				"index:" + mIndex + "\n" + 
				"total led num:" + mLedTotal + "\n" + 
				"x led num:" + mXLedNum + "\n" + 
				"y led num:" + mYLedNum + "\n" +
				" boardNum" + mBoardNum + "\n"

		;

		return s;
	}

	public BoardConfig(int index) {
		if (index == READ_FROM_IC)
			return;

		mSize = BOARD_CONFIG_SIZE[index];
		mTitle = BOARD_CONFIG_TITLE[index];
		mIndex = index;
		mBoardMap = new OneBoard[Constant.BOARD_MAX];

		switch (index) {
		case 0:
			mBuffer = BOARD_CONFIG_0;
			break;
		case 1:
			mBuffer = BOARD_CONFIG_1;
			break;
		case 2:
			mBuffer = BOARD_CONFIG_2;
			break;
		case 3:
			mBuffer = BOARD_CONFIG_3;
			break;
		case 4:
			mBuffer = BOARD_CONFIG_4;
			break;
		case 5:
			mBuffer = BOARD_CONFIG_5;
			break;
		case 6:
			mBuffer = BOARD_CONFIG_6;
			break;
		case 7:
			mBuffer = BOARD_CONFIG_7;
			break;
		default:
			return;
		}
		parse();
	}

	public byte[] getBuffer() {
		return mBuffer;
	}

	public String getTitle() {
		return mTitle;
	}

	public int getSize() {
		return mSize;
	}

	public int getIndex() {
		return mIndex;
	}

	public int getXLedNumber() {
		return mXLedNum;
	}

	public int getYLedNumber() {
		return mYLedNum;
	}

	public int getTotalLedNumber() {
		return mLedTotal;
	}

	@Override
	public boolean equals(Object a) {
		BoardConfig con = (BoardConfig) a;
		return cmp(con);
	}

	private boolean cmp(BoardConfig a) {
		boolean bcmp = ComFunc.memcmp(a.mBuffer, mBuffer, mBuffer.length);
		boolean scmp = (a.mSize == mSize);
		ComFunc.log("buffer cmp:" + bcmp + " size cmp:" + scmp);
		/* debug,config is different */
		if (bcmp == false && scmp == true) {
			ComFunc.log("a buffer:", a.mBuffer, a.mBuffer.length);
			ComFunc.log("my buffer:", mBuffer, mBuffer.length);
		}
		return bcmp & scmp;
	}

	public void setBuffer(byte[] buf) {
		mBuffer = buf;
	}

	public void setSize(int size) {
		mSize = size;
	}

	public void setIndex(int i) {
		mIndex = i;
	}

	public int getBoardNum() {
		return mBoardNum;
	}

	private void calcXYBoardNumber() {
		int lightsNum = 0;
		for (int i = 0; i < mBoardNum; i++) {
			lightsNum += mBoardMap[i].mBoardMembers;
			if (lightsNum >= mYLedNum) {
				mYBoardNum = i + 1;
				mXBoardNum = mBoardNum - mYBoardNum;
				break;
			}
		}
	}
	public int getXBoardNum() {
		return mXBoardNum ;
	}

	public int getYBoardNum() {
		
		return mYBoardNum ;
	}
}
