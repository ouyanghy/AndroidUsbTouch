package com.ou.common;

import android.content.Context;
import android.util.Log;

public class Common {
	private static final String TAG = "MLog";

	public static void log(String note, byte[] buffer, int len) {
		String s = "";
		s += note;
		s+= "{";
		if (buffer != null && len > 0) {
			for (int i = 0; i < len; i++) {
				if (i % 16 == 0)
					s+="\n";
				String t = String.format("%02x,", buffer[i]);
				s += t;
				
			}
			s += "};\n";
		}
		Log.i(TAG, s);
	}

	public static void log(String note) {
		Log.i(TAG, note);
	}
	
	public static void memset(byte[] bs, int val, int len) {
		for (int i = 0; i < len; i++)
			bs[i] = (byte) val;
	}

	public static void memcpy(byte[] dst, byte[] src, int len) {
		for (int i = 0; i < len; i++)
			dst[i] = src[i];
	}
	
	public static void memcpy(byte[] dst, byte[] src, int dst_start, int src_start, int len) {
		for (int i = 0; i < len; i++)
			dst[dst_start + i] = src[src_start + i];
	}
	
	public static boolean memcmp(byte [] a, byte []b, int len) {
		for (int i = 0; i < len; i++) {
			if (a[i] != b[i]) {
		/*		Common.log("i:" + i + " a[i]:" + a[i] + " b[i]:" + b[i] + " hex a[i]:" 
						+ String.format("%02x,", a[i]) 
						+ " hex b[i]:" + String.format("%02x,", b[i]));*/
				return false;
			}
		}
		return true;
	}
	
	public static boolean memcmp(byte [] a, byte []b, int a_start, int b_start, int len) {
		for (int i = 0; i < len; i++) {
			if (a[i + a_start] != b[i + b_start])
				return false;
		}
		return true;
	}
	
	public static byte[] memcut(byte [] src, int start, int len) {
		byte [] bs = new byte[len];
		memcpy(bs, src, 0, start, len);
		return bs;
	}
	
	public static int roundUp(int value, int sp) {
		int ret = 0;
		int rem = value % sp;
		if (rem > 0)
			ret++;
		
		return (value / sp) *sp + ret *sp; 
			
	}
	
	public static int roundDown(int value, int sp) {
		return (value / sp) *sp; 
			
	}
	
	public static byte [] intsToBytes(int [] src) {
		byte [] bs = new byte[src.length * 4];
		for (int i = 0; i < src.length; i++) {
			bs[i + 0] = (byte) (src[i] & 0xff);
			bs[i + 1] = (byte) (((src[i] & 0xff00)>>8) & 0xff);
			bs[i + 2] = (byte) (((src[i] & 0xff0000)>>16) & 0xff);
			bs[i + 3] = (byte) (((src[i] & 0xff000000)>>24) & 0xff);
			
		}
		return bs;
	}
	
	public static  void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getString(Context context, int id) {
			return context.getResources().getString(id);
	}
	
}
