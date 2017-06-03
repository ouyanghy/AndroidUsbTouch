package com.ou.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ou.usbtp.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class FileSelectorActivity extends Activity implements OnClickListener {
	private ListView mV;
	private Button mBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_selector);
		mV = (ListView) findViewById(R.id.listViewFile);
		mBtn = (Button) findViewById(R.id.buttonFilePath);
		mBtn.setOnClickListener(this);
		float s = mBtn.getTextSize();
		mBtn.setTextSize((float) (s * 1.2));
		setResult(0, null);
		loadFile("/");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// TODO
			if (mBtn.getText().toString().equals("/") == false)
				mBtn.callOnClick();
			else
				break;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void loadFile(String path) {
		File f = new File(path);
		if (f.isDirectory()) {
			mBtn.setText(path);
			File[] ft = f.listFiles();
			final File[] fs = sort(ft);
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			String[] s = new String[fs.length];
			int[] ids = new int[fs.length];
			android.util.Log.i("FileLog", "fs len:" + fs.length);
			for (int i = 0; i < fs.length; i++) {
				Map<String, Object> items = new HashMap<String, Object>();
				s[i] = fs[i].getName();
				if (fs[i].isDirectory())
					ids[i] = R.drawable.directory;
				else
					ids[i] = R.drawable.file;
				items.put("img", ids[i]);
				items.put("text", s[i]);
				listItems.add(items);

			}

			SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.file_adapter,
					new String[] { "img", "text" }, new int[] { R.id.imageViewFileIcon, R.id.textFileName });
			mV.setAdapter(adapter);
			mV.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					android.util.Log.i("FileLog", "click:" + fs[position].getPath());
					if (fs[position].isDirectory() == false) {
						// mContext.sendBroadcast(new Intent(GET_FILE));
						Intent i = new Intent();
						Uri u = Uri.fromFile(fs[position]);
						i.setData(u);
						setResult(1, i);
						finish();
						return;
					}

					if (fs[position].canRead() == false) {
						// send some permission flag
						return;
					}

					loadFile(fs[position].getPath());

				}
			});

		} else {
			android.util.Log.i("FileLog", "is file");
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() != mBtn.getId())
			return;

		String path = mBtn.getText().toString();
		File f = new File(path);
		f = f.getParentFile();
		if (f == null)
			return;

		loadFile(f.getPath());
	}

	private File[] sort(File[] fs) {
		File[] dst = new File[fs.length];
		int index = 0;
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory())
				dst[index++] = fs[i];
		}

		for (int i = 0; i < fs.length; i++) {
			if (!fs[i].isDirectory())
				dst[index++] = fs[i];
		}
		return dst;

	}
}
