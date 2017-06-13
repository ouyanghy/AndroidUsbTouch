package com.ou.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.ou.common.ComFunc;
import com.ou.common.Constant;

import android.os.PowerManager;

public class BoardConfigMap {
	final String START_ALL_KEY = "[Config]";
	final String CONFIG_KEY = "Config_";
	final String CONFIG_MAP_KEY = "[Config_";
	final String EMIT_TITLE = "emit=";
	final String RCV_TITLE = "rcv=";
	
	String mPath;
	BoardConfig [] mList = null;
	int mTotalCount = 0;	
	
	public BoardConfigMap(String path) {
		mPath = path;
		
	}

	public BoardConfig []  parse() throws IOException {
		if (mPath == null)
			return null;
		
		File f = new File(mPath);
		if (f.exists() == false || f.canRead() == false) {
			ComFunc.log("read file fail:" + mPath);
			return null;
		}
		ComFunc.log("reading:" + mPath);
		FileInputStream in = new FileInputStream(f);
		int val = 0;
		int i = 0;
		
		byte t[] ;
		byte bs[] = new byte[Constant.KB];
		final int PROCESS_TITLE = 0;
		final int PROCESS_ONE_CONFIG =1;
		final int PROCESS_EMIT = 2;
		final int PROCESS_RCV= 3;
		final int PROCESS_TOTAL_NUM = 4;
		final int PROCESS_START = -1;
		final int PROCESS_ONE_CONFIG_SIZE = 5;
		final int PROCESS_IDLE = 6;
		int process_state = PROCESS_START;
		int control = 0;
		String curKey ="";
		byte [] r;
		BoardConfig conf;
		do {
			val = in.read();
			if (val < 0)
				break;
			
			bs[i++] = (byte) (val & 0xFF);
			
			if (val != '\n') 
				continue;
			
			t = START_ALL_KEY.getBytes();
			if (ComFunc.memcmp(t, bs, t.length)) {
				process_state = PROCESS_TOTAL_NUM;
				i = 0;
				continue;
			} 
			
			t = CONFIG_MAP_KEY.getBytes();
			if (ComFunc.memcmp(t, bs, t.length)) {
				process_state = PROCESS_ONE_CONFIG;
				
			}
		
			t = EMIT_TITLE.getBytes();
			if (ComFunc.memcmp(t, bs, t.length)) {
				process_state = PROCESS_EMIT;
			}
			
			t = RCV_TITLE.getBytes();
			if (ComFunc.memcmp(t, bs, t.length)) {
				process_state = PROCESS_RCV;
			}
			
			
			switch (process_state) {
			case PROCESS_TOTAL_NUM:
				if (i <= 11) {
					in.close();
					return null;
				}
				
				mTotalCount = getNum(bs, i);
				ComFunc.log("total count:" + mTotalCount);
				if (mTotalCount > 100 || mTotalCount < 0) {
					in.close();
					return null;
				}
				
				mList = new BoardConfig[mTotalCount];
				for (int j = 0; j < mList.length;j++) {
					mList[j] = new BoardConfig(Constant.IGNORE);
				}
				i = 0;
				process_state = PROCESS_TITLE;
				break;
				//in.close();
				//ComFunc.log("read n is:" + n);
				//return null;
			case PROCESS_TITLE:
				if (i < 9 ) {
					in.close();
					ComFunc.log("existing 0");
					return null;
				}
				if (control >= mTotalCount) { 
					in.close();
					ComFunc.log("existing 1");
					return null;
				}
				
				String title = getTitle(bs, i);
				String key = getKey(bs, i);
				mList[control].setTitle(title);
				mList[control].setKey(key);
				mList[control].setIndex(control);
				control++;
				if (control == mTotalCount)
					process_state = PROCESS_IDLE;
				
				i = 0;
				
				break;
			case PROCESS_ONE_CONFIG:
				curKey = getOneKey(bs, i);
				if (curKey == null) {
					ComFunc.log("find One Key null");
					in.close();
					return null;
				}
				
				i = 0;
		
				process_state = PROCESS_ONE_CONFIG_SIZE;
				break;
			
			case PROCESS_ONE_CONFIG_SIZE:
		
				int size = getSize(bs, i);
				ComFunc.log("size:" + size);
				conf = findBoard(curKey);
				ComFunc.log("index:" + conf.getIndex());
				conf.setSize(size);
				//in.close();
				//return null;
				i = 0;
				break;
			case PROCESS_IDLE:
				i = 0;
				break;
				
			case PROCESS_EMIT:
				r = getEmitBuffer(bs, i);
				//ComFunc.log("emit:", r, r.length);
				i = 0;
				//in.close();
				//return null;
				conf = findBoard(curKey);
				conf.setEmitBuffer(r);
				process_state = PROCESS_IDLE;
				break;
				
			case PROCESS_RCV:
				r = getRcvBuffer(bs, i);
				conf = findBoard(curKey);
				conf.setRcvBuffer(r);
				//ComFunc.log("rcv:", r, r.length);
				i = 0;
				//r = conf.getBuffer();
				//ComFunc.log("buffer:", r, r.length);
				process_state = PROCESS_IDLE;
			//	in.close();
				//return null;
				//process_state = PROCESS_IDLE;
				//break;
			}
		}while(val > 0);
		
		in.close();
		return mList;
	}

	byte [] getEmitBuffer(byte []bs, int len) {
	
		int d = 1;

		/*maybe is windows txt*/
		if (bs[len - 2] == '\r')
			d++;
		bs = ComFunc.memcut(bs, 5, len - 5 - d);
		String s = new String(bs);
		s = s.replace(" ", "");
		s = s.replace("\r", "");
		String [] ss = s.split(",");
		
		byte [] rs = new byte[ss.length + 3];
		int id = 0;
		for (int i = 0; i < ss.length; i++) {
			int v = Integer.parseInt(ss[i]);
			if (i < 3) {
				
				rs[id++] = (byte) (v & 0xff);
				rs[id++] = (byte) ((v >> 8) & 0xff);	
			}else
				rs[id++] = (byte) (v & 0xff);
			
			
		}
		
		return rs;
	}
	
	byte [] getRcvBuffer(byte []bs, int len) {
		
		int d = 1;

		/*maybe is windows txt*/
		if (bs[len - 2] == '\r')
			d++;
		bs = ComFunc.memcut(bs, 4, len - 4 - d);
		String s = new String(bs);
		s = s.replace(" ", "");
		s = s.replace("\r", "");
		String [] ss = s.split(",");
		
		byte [] rs = new byte[ss.length + 3];
		int id = 0;
		for (int i = 0; i < ss.length; i++) {
			int v = Integer.parseInt(ss[i]);
			if (i < 3) {
				
				rs[id++] = (byte) (v & 0xff);
				rs[id++] = (byte) ((v >> 8) & 0xff);	
			}else
				rs[id++] = (byte) (v & 0xff);
			
			
		}
		
		return rs;
	}
	
	BoardConfig findBoard(String key) {
		for (int i = 0; i < mList.length; i++) {
			String k = mList[i].getKey();
			if (k.equals(key))
				return mList[i];
		}
		return null;
	}
	
	int getSize(byte bs[], int len) {
		int d = 1;

		/*maybe is windows txt*/
		if (bs[len - 2] == '\r')
			d++;
		
		ComFunc.log("size:" , bs, len);
		byte[] t = ComFunc.memcut(bs, 5, len - 5 - d);
		ComFunc.log("t buff:" , t, t.length);
		return getBytesNum(t);
	}
	
	String getKey(byte bs[], int len) {
		int start = 8;
		if (bs[8] != '=') {
			start++;
		}
		
		byte [] t = ComFunc.memcut(bs, 0, start);
		String key = new String(t);
		ComFunc.log("key:" + key);
		return key;
		
	}
	
	String getOneKey(byte bs[], int len) {
		len -= 1;
		bs = ComFunc.memcut(bs, 1, len);
		int start = 8;
		if (bs[8] != ']') {
			start++;
		}
		
		byte [] t = ComFunc.memcut(bs, 0, start);
		String key = new String(t);
		ComFunc.log("one key:" + key);
		return key;
		
	}
	String getTitle(byte bs[], int len) {
		int start = 9;
		if (bs[8] != '=') {
			start++;
		}
		
		int d = 1;

		/*maybe is windows txt*/
		if (bs[len - 2] == '\r')
			d++;
		
		
		byte [] t = ComFunc.memcut(bs, start, len - start - d);
		String title = new String(t);
		ComFunc.log("title:" + title);
		return title;
		
	}
	
	int getNum(byte bs[], int len) {
		int d = 1;
		byte [] t;
		/*maybe is windows txt*/
		if (bs[len - 2] == '\r')
			d++;
		
		t = ComFunc.memcut(bs, 10, len - 10 -d);
		//ComFunc.log("t len:" + t.length);
		//ComFunc.log("buf", t, t.length);
		/*int n = 0;
		int p = 0;
		for (int j = t.length - 1; j >= 0; j--) {
			t[j] -= '0';
			n += t[j] * ComFunc.powerOfNum(10, p++);
			//ComFunc.log("n" + t[j]);
		}*/
		int n = getBytesNum(t);
		return n;
	}
	
	int getBytesNum(byte []t) {
		int p = 0;
		int n = 0;
		for (int j = t.length - 1; j >= 0; j--) {
			t[j] -= '0';
			n += t[j] * ComFunc.powerOfNum(10, p++);
			//ComFunc.log("n" + t[j]);
		}
		return n;
	}
}
