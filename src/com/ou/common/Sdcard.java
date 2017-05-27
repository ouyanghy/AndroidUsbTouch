package com.ou.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class Sdcard {

	private FileOutputStream mOut;
	private FileInputStream mIn;
	private File mF;
	private String mPath;

	public boolean create(String dir, String name) {
		File f;
		boolean ret = true;
		File d = new File(dir);
		if (!d.exists())
			ret = d.mkdir();

		f = new File(dir + "/" + name);
		if (!f.exists())
			try {
				ret = f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = false;
			}

		mF = f;
		mPath = dir + "/" + name;
		return ret;
	}

	/*
	 * file name is time-now
	 */
	public String getTimeFileHead() {
		String s = "";
		Calendar can = Calendar.getInstance();
		s = can.get(Calendar.YEAR) + "-" + String.format("%02d", can.get(Calendar.MONTH)) + "-"
				+ String.format("%02d", can.get(Calendar.DATE));
		return s;
	}

	public boolean create(String file) {
		boolean ret = false;
		File f = new File(file);
		if (!f.exists())
			try {
				ret = f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = false;
			}

		mPath = file;
		return ret;
	}

	public String getPath() {
		return mPath;
	}

	public void write(String s) {
		try {
			mOut = new FileOutputStream(mF, true);
			mOut.write(s.getBytes());
			mOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(String s, boolean append) {
		try {
			mOut = new FileOutputStream(mF, append);
			mOut.write(s.getBytes());
			mOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String read() {
		String s = "";

		try {
			int r;
			mIn = new FileInputStream(mF);
			do {
				byte[] buffer = new byte[1024];
				r = mIn.read(buffer);
				s += new String(buffer);

			} while (r > 0);
			mIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;

	}



}
