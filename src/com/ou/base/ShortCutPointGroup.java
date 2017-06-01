package com.ou.base;

import android.graphics.PointF;

public class ShortCutPointGroup {
	private PointF mFirst, mSecond, mThird;
	int mCount = 0;
	boolean bComplete = false;
	int mZone = -1;
	private final float UNUSE = -10;

	public ShortCutPointGroup() {
		mFirst = new PointF(UNUSE, UNUSE);
		mSecond = new PointF(UNUSE, UNUSE);
		mThird = new PointF(UNUSE, UNUSE);
	}

	public boolean checkSecondPoint() {
		if (mSecond.x == UNUSE || mSecond.y == UNUSE)
			return false;

		if (mSecond.x < mFirst.x || mSecond.y < mFirst.y)
			return false;

		return true;
	}

	public void setCount(int n) {
		mCount = n;
	}
	
	public int getCount() {
		return mCount;
	}
	public boolean checkThirdPoint() {
		if (mThird.x == UNUSE || mThird.y == UNUSE)
			return false;

		if (mThird.x < mFirst.x || mThird.y < mFirst.y)
			return false;

		return true;
	}

	public void setComplete(boolean b) {
		bComplete = b;
	}

	public void PointAvail() {

	}

	public int getZone() {
		return mZone;
	}

	public void setZone(int s) {
		mZone = s;
	}

	public boolean isComplete() {
		return bComplete;
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
