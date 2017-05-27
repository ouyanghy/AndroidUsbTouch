package com.ou.usbtp;

import android.graphics.PointF;

public class ShortCutPointGroup {
	private PointF mFirst,mSecond,mThird;
	public ShortCutPointGroup() {
		mFirst = new PointF();
		mSecond = new PointF();
		mThird = new PointF();
	}
	
	public PointF getFirst() {
		return mFirst;
	}
	
	public PointF getSecond() {
		return mSecond;
	}
	
	public PointF getThird() {
		return mThird;
	}
	
	
	public void setFirst(PointF p) {
		mFirst = p;
	}
	
	public void setSecond(PointF p) {
		mSecond = p;
	}
	
	public void setThird(PointF p) {
		mThird = p;
	}

}
