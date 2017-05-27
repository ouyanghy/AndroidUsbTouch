package com.ou.ui;

import com.ou.common.Common;
import com.ou.usbtp.R;
import com.ou.usbtp.ShortCutPointGroup;
import com.ou.view.ShortCutView;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;

public class ShortCutActivity extends Activity {
	ShortCutView mV;
	private Object mLock;
	boolean bTouchUp = true;
	ShortCutPointGroup mPointGroup;
	int mPointCnt = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shortcut);
		mV = (ShortCutView) findViewById(R.id.shortCutView);
		mV.invalidate();
		mLock = new Object();
		mPointGroup = new ShortCutPointGroup();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (mLock) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				bTouchUp = true;
				Common.log("ouyang touch up");
				return super.onTouchEvent(event);
			}

			if (bTouchUp == false)
				return super.onTouchEvent(event);

			mPointCnt++;
			if (mPointCnt == 1) {
				Common.log("first");
				mPointGroup.setFirst(new PointF(event.getX(), event.getY()));
			} else if (mPointCnt == 2) {
				Common.log("second");
				mPointGroup.setSecond(new PointF(event.getX(), event.getY()));
			} else if (mPointCnt == 3) {
				Common.log("third");
				mPointGroup.setThird(new PointF(event.getX(), event.getY()));
			}
			mV.setUserPoint(mPointCnt, mPointGroup);
			bTouchUp = false;
			return super.onTouchEvent(event);
		}

	}
}
