package com.ou.base;

public class CalPoint {
	short mX0 = 0;
	short mY0 = 0;
	
	short mX1 = 0;
	short mY1 = 0;
	
	int mNum;
	
	public CalPoint(byte []src) {
		mNum = src[0];
		if (mNum == 0)
			return;
		
		mX0 = (short) (src[1] &0xff | ((src[2] & 0xff) << 8) & 0xff00) ;
		mY0 = (short) (src[3] &0xff | ((src[4]& 0xff) << 8) & 0xff00) ;
		
		if (mNum == 1)
			return;
		
		mX1 = (short) (src[5] &0xff | ((src[6]& 0xff) << 8) & 0xff00) ;
		mY1 = (short) (src[7] &0xff | ((src[8]& 0xff) << 8) & 0xff00) ;
	}
	
	@Override
	public String toString() {
		String s ;
		s = "Active Number:" + mNum + "\n" +
				"X0:" + mX0 + "\n" +
				"Y0:" + mY0 + "\n" +
				"X1:" + mX1 + "\n" +
				"Y1:" + mY1 + "\n" 
				;
		return s;
	}
}
