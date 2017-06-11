package com.ou.base;

import com.ou.common.ComFunc;

public class CalInfo {
	byte [] mCalPoints = new byte[32];
    private byte mScreenDirection = 0;
    private byte mOsSupport = 0;
    private int mOsType = 0;
    private int mCheckFlag = 0;        
    private int mMatrixFlag = 0;    
    private final static int SIZE = 32 + 2+  3*4;//32 + 14 = 46

    private final int CMD_START_INDEX = 32;
    private byte [] mBuffer = null;

    public int getScreenDirection() {
    	return mScreenDirection;
    }
    
    public int getOsSupport() {
    	return mOsSupport;
    }
    
    public int getOsType() {
    	return mOsType;
    }
    
    public int getCheckFlag() {
    	return mCheckFlag;
    }
    
    public int getMatrixFlag() {
    	return mMatrixFlag;
    }
    public int getSize() {
    	      
    	return SIZE;
    }
    
    public void setMatrixFlag(int m) {
    	 mMatrixFlag = m;
    }
    
    public void setCalPoints(byte [] bs) {
    	mCalPoints = bs;
   }
    
    public void setCheckFlag(int n) {
    	mCheckFlag = n;
    }
    
    public void setScreenDirection(int dir) {
    	mScreenDirection = (byte) dir;
    }
    
    public void parse(byte bs[]) {
		ComFunc.memcpy(mCalPoints, bs, mCalPoints.length);
		
		mScreenDirection = bs [CMD_START_INDEX];
		mOsSupport = bs[CMD_START_INDEX + 1];
		mOsType = bs[CMD_START_INDEX + 2] ;
		mOsType |= bs[CMD_START_INDEX+3] << 8;
		mOsType |= bs[CMD_START_INDEX+4] << 16;
		mOsType |= bs[CMD_START_INDEX+5] << 24;
		mCheckFlag = bs[CMD_START_INDEX + 6];
		mCheckFlag |= bs[CMD_START_INDEX + 7]<< 8;
		mCheckFlag |= bs[CMD_START_INDEX + 8] << 16;
		mCheckFlag |= bs[CMD_START_INDEX + 9] << 24;
		mMatrixFlag = bs[CMD_START_INDEX + 10];
		mMatrixFlag |= bs[CMD_START_INDEX + 11]<<8;
		mMatrixFlag |= bs[CMD_START_INDEX + 12]<<16;
		mMatrixFlag |= bs[CMD_START_INDEX + 13] <<24;
		//mBuffer = bs;
    }
    
    public byte [] toByte() {
    	
    	mBuffer = new byte[SIZE];
    	ComFunc.memset(mBuffer, 0x00, SIZE);
    	ComFunc.memcpy(mBuffer, mCalPoints, mCalPoints.length);
    	mBuffer[CMD_START_INDEX] = mScreenDirection;
    	mBuffer[CMD_START_INDEX + 1] = mOsSupport;
    	mBuffer[CMD_START_INDEX + 2] = (byte) (mOsType & 0xFF);
    	mBuffer[CMD_START_INDEX + 3] = (byte) (mOsType >> 8 & 0xFF);
    	mBuffer[CMD_START_INDEX + 4] = (byte) (mOsType >> 16& 0xFF);
    	mBuffer[CMD_START_INDEX + 5] = (byte) (mOsType >> 24& 0xFF);
    	mBuffer[CMD_START_INDEX + 6] = (byte) (mCheckFlag & 0xFF);
    	mBuffer[CMD_START_INDEX + 7] = (byte) (mCheckFlag >> 8 & 0xFF);
    	mBuffer[CMD_START_INDEX + 8] = (byte) (mCheckFlag >> 16& 0xFF);
    	mBuffer[CMD_START_INDEX + 9] = (byte) (mCheckFlag >> 24& 0xFF);
    	mBuffer[CMD_START_INDEX + 10] = (byte) (mMatrixFlag & 0xFF);
    	mBuffer[CMD_START_INDEX + 11] = (byte) (mMatrixFlag >> 8 & 0xFF);
    	mBuffer[CMD_START_INDEX + 12] = (byte) (mMatrixFlag >> 16& 0xFF);
    	mBuffer[CMD_START_INDEX + 13] = (byte) (mMatrixFlag >> 24& 0xFF);
    	return mBuffer;
    }
    
    public static boolean compare(CalInfo a, CalInfo b) {
    	return ComFunc.memcmp(a.toByte(), b.toByte(), SIZE);
    }
    @Override
    public String toString() {
    	String s ="";
    	s+=String.format("%x",mScreenDirection) + "," +
    			String.format("%x",mOsSupport) + "," +
    			String.format("%x",mOsType) + "," +
    			String.format("%x",mCheckFlag) + "," +
    			String.format("%x",mMatrixFlag);
    	
    	toByte();
    	ComFunc.log(s + "\nbuffer\n", mBuffer, mBuffer.length);
    	return s;
    }
}
