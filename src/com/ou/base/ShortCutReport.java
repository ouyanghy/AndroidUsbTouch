package com.ou.base;

import android.graphics.Rect;

public class ShortCutReport {
	byte mLeftKeyCnt = 0;
	byte mBottomKeyCnt = 0;
	byte mRightKeyCnt = 0;
	byte mTopKeyCnt = 0;
	Rect mLeftKeyGroup[];// 40
	Rect mBottomKeyGroup[];// 8
	Rect mRightKeyGroup[];// 40
	Rect mTopKeyGroup[];// 8
	int mShortcutFlag;

	public ShortCutReport() {
		mLeftKeyGroup = new Rect[40];
		mBottomKeyGroup = new Rect[8];
		mRightKeyGroup = new Rect[40];
		mTopKeyGroup = new Rect[8];
		mShortcutFlag = 0x44534300;
		
	}
	
	public byte[] toBytes() {
		byte [] bs = new byte[4 + (40 + 8 + 40 + 8) * 8 + 4];
		int id = 0;
		bs[id++] = mLeftKeyCnt;
		bs[id++] = mBottomKeyCnt;
		bs[id++] = mRightKeyCnt;
		bs[id++] = mTopKeyCnt;
		for (int i = 0; i < 40;i++) {
			if (mLeftKeyGroup[i] == null) {
				mLeftKeyGroup[i] = new Rect(0, 0, 0, 0);
			}
			bs[id++] = (byte) ((byte) mLeftKeyGroup[i].left&0xff) ;
			bs[id++] = (byte) ((byte) (mLeftKeyGroup[i].left)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mLeftKeyGroup[i].top&0xff) ;
			bs[id++] = (byte) ((byte) (mLeftKeyGroup[i].top)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mLeftKeyGroup[i].right&0xff) ;
			bs[id++] = (byte) ((byte) (mLeftKeyGroup[i].right)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mLeftKeyGroup[i].bottom&0xff) ;
			bs[id++] = (byte) ((byte) (mLeftKeyGroup[i].bottom)>>8 & 0xff);
		}
		
		for (int i = 0; i < 8;i++) {
			if (mBottomKeyGroup[i] == null) {
				mBottomKeyGroup[i] = new Rect(0, 0, 0, 0);
			}
			bs[id++] = (byte) ((byte) mBottomKeyGroup[i].left&0xff) ;
			bs[id++] = (byte) ((byte) (mBottomKeyGroup[i].left)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mBottomKeyGroup[i].top&0xff) ;
			bs[id++] = (byte) ((byte) (mBottomKeyGroup[i].top)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mBottomKeyGroup[i].right&0xff) ;
			bs[id++] = (byte) ((byte) (mBottomKeyGroup[i].right)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mBottomKeyGroup[i].bottom&0xff) ;
			bs[id++] = (byte) ((byte) (mBottomKeyGroup[i].bottom)>>8 & 0xff);
		}
		
		for (int i = 0; i < 40;i++) {
			if (mRightKeyGroup[i] == null) {
				mRightKeyGroup[i] = new Rect(0, 0, 0, 0);
			}
			
			bs[id++] = (byte) ((byte) mRightKeyGroup[i].left&0xff) ;
			bs[id++] = (byte) ((byte) (mRightKeyGroup[i].left)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mRightKeyGroup[i].top&0xff) ;
			bs[id++] = (byte) ((byte) (mRightKeyGroup[i].top)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mRightKeyGroup[i].right&0xff) ;
			bs[id++] = (byte) ((byte) (mRightKeyGroup[i].right)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mRightKeyGroup[i].bottom&0xff) ;
			bs[id++] = (byte) ((byte) (mRightKeyGroup[i].bottom)>>8 & 0xff);
		}
		
		for (int i = 0; i < 8;i++) {
			if (mTopKeyGroup[i] == null) {
				mTopKeyGroup[i] = new Rect(0, 0, 0, 0);
			}
			bs[id++] = (byte) ((byte) mTopKeyGroup[i].left&0xff) ;
			bs[id++] = (byte) ((byte) (mTopKeyGroup[i].left)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mTopKeyGroup[i].top&0xff) ;
			bs[id++] = (byte) ((byte) (mTopKeyGroup[i].top)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mTopKeyGroup[i].right&0xff) ;
			bs[id++] = (byte) ((byte) (mTopKeyGroup[i].right)>>8 & 0xff);
			bs[id++] = (byte) ((byte) mTopKeyGroup[i].bottom&0xff) ;
			bs[id++] = (byte) ((byte) (mTopKeyGroup[i].bottom)>>8 & 0xff);
		}
		bs[id++] = (byte) (mShortcutFlag & 0xff);
		bs[id++] = (byte) (mShortcutFlag >> 8 & 0xff);
		bs[id++] = (byte) (mShortcutFlag >> 16 & 0xff);
		bs[id++] = (byte) (mShortcutFlag >> 24 & 0xff);
		return bs;
	}
	
	public void setLeftRectUnit( Rect r) {
		mLeftKeyGroup[mLeftKeyCnt++] = r; 
	}
	
	public void setBottomRectUnit( Rect r) {
		mBottomKeyGroup[mBottomKeyCnt++] = r; 
	}
	
	public void setRightRectUnit( Rect r) {
		mRightKeyGroup[mRightKeyCnt++] = r; 
	}
	
	public void setTopRectUnit(Rect r) {
		mTopKeyGroup[mTopKeyCnt++] = r; 
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
