package com.ou.ui;

import com.ou.usbtp.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class KeyCntDialog extends Dialog implements OnClickListener,OnSeekBarChangeListener{
	private SeekBar mBar;
	private TextView mTv;
	private Button mBtn;
	Result mCall;
	public KeyCntDialog(Context context) {
		super(context);
		setContentView(R.layout.key_cnt_dialog);
		// TODO Auto-generated constructor stub
	}

	public KeyCntDialog(Context context, int theme) {
		super(context, theme);
		
		// TODO Auto-generated constructor stub
	}

	public KeyCntDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		setContentView(R.layout.key_cnt_dialog);
		// TODO Auto-generated constructor stub
	}
	
	public void init() {
		setCancelable(false);
		mBar = (SeekBar) findViewById(R.id.seekBarShortCutCnt);
		mTv = (TextView) findViewById(R.id.textViewShortCutCnt);
		mBtn = (Button) findViewById(R.id.buttonShortCutCntOk);
		setTitle("设置快捷键个数");
		mBar.setMax(20);
		
		mBar.setProgress(3);
		mBtn.setOnClickListener(this);
		mTv.setTextColor(Color.WHITE);
		mBar.setOnSeekBarChangeListener(this);
		mTv.setText("" + mBar.getProgress());
	}
	
	@Override
	public void show() {
		init();
		super.show();
	}
	
	public void setCallBack(Result r) {
		mCall = r;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() != R.id.buttonShortCutCntOk)
			return;
		
		int cnt = mBar.getProgress();
		if (mCall!= null)
			mCall.call(cnt);
		dismiss();
	}
	
	public interface Result {
		void call(int val);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		mTv.setText("" + mBar.getProgress());
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mTv.setText("" + mBar.getProgress());
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mTv.setText("" + mBar.getProgress());
	}

}
