package com.ou.usbtp;

import android.graphics.Rect;

public class KeyShortCut {
	byte mLeftKeyCnt;
	byte mBottomKeyCnt;
	byte mRightKeyCnt;
	byte mTopKeyCnt;
	Rect mLeftKeyGroup[];// 40
	Rect mBottomKeyGroup[];// 8
	Rect mRightKeyGroup[];// 40
	Rect mTopKeyGroup[];// 8
	int mShortcutFlag;

	public KeyShortCut() {
		mLeftKeyGroup = new Rect[40];
		mBottomKeyGroup = new Rect[8];
		mRightKeyGroup = new Rect[40];
		mTopKeyGroup = new Rect[8];
		mShortcutFlag = 0x44534300;
		
	}
	
	public void setLeftRectUnit(int index, Rect r) {
		mLeftKeyGroup[index] = r; 
	}
	
	public void setBottomRectUnit(int index, Rect r) {
		mBottomKeyGroup[index] = r; 
	}
	
	public void setRightRectUnit(int index, Rect r) {
		mRightKeyGroup[index] = r; 
	}
	
	public void setTopRectUnit(int index, Rect r) {
		mTopKeyGroup[index] = r; 
	}
	
	public Rect getLeftRectUnit(int index, Rect r) {
		return mLeftKeyGroup[index]; 
	}
	
	public Rect getBottomRectUnit(int index, Rect r) {
		return mBottomKeyGroup[index]; 
	}
	
	public Rect getRightRectUnit(int index, Rect r) {
		return mRightKeyGroup[index]; 
	}
	
	public Rect getTopRectUnit(int index, Rect r) {
		return mTopKeyGroup[index]; 
	}
	
	
	public byte []toBytes() {
		
		return null;
	}
	

	
	public void setLeftKeyCount(int cnt) {
		mLeftKeyCnt = (byte) cnt;
	}

	public void setBottomKeyCount(int cnt) {
		mBottomKeyCnt = (byte) cnt;
	}

	public void setRightKeyCount(int cnt) {
		mRightKeyCnt = (byte) cnt;
	}

	public void setTopKeyCount(int cnt) {
		mTopKeyCnt = (byte) cnt;
	}

	public int getLeftKeyCount() {
		return mLeftKeyCnt;
	}

	public int getBottomKeyCount() {
		return mBottomKeyCnt;
	}

	public int getRightKeyCount() {
		return mRightKeyCnt;
	}

	public int getTopKeyCount() {
		return mTopKeyCnt;
	}
}
