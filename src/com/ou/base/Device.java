package com.ou.base;

import java.util.HashMap;

import com.ou.common.ComFunc;
import com.ou.common.Constant;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class Device {
	private Context mContext;
	private UsbManager mManager;
	private UsbDevice mDevice;
	private UsbDeviceConnection mDeviceConnect;
	private UsbEndpoint mDeviceInPoint, mDeviceOutPoint, mDeviceIntPoint;
	private UsbInterface mDeviceIntf;
	private int mInterface;
	

	
	public Device(Context c) {
		mContext = c;
		mManager = (UsbManager) c.getSystemService(Context.USB_SERVICE);
		mDevice = findDevice();
		if (mDevice == null)
			return;
		checkUsbPermission();
		

	}

	public boolean isAvail() {
		if (mDevice == null)
			return false;

		return true;
	}
	
	public boolean isBootDevice() {
		if (mDevice.getProductId() == Constant.PID_NORMAL) {
			return false;
		}
		
		return true;
	}

	protected static final String TAG = "OUsbDevice";

	/*
	 * private final BroadcastReceiver mDeviceReceiver = new BroadcastReceiver() {
	 * public void onReceive(Context context, Intent intent) { String action =
	 * intent.getAction(); Log.i(TAG, "action:" + action); // getDeviceList(); }
	 * };
	 */

	public boolean open() {
		if (mDevice == null) {
			Log.e(TAG, "usb device is null");
			return false;
		}
		

		Log.i(TAG, "usb get interface count:" + mDevice.getInterfaceCount());
		mDeviceIntf = mDevice.getInterface(0);
		if (mDeviceIntf == null) {
			Log.e(TAG, "usb mDeviceIntf is null");
			return false;
		}
		
	/*	  mDeviceInPoint = mDeviceIntf.getEndpoint(0); mDeviceOutPoint =
		 mDeviceIntf.getEndpoint(1);
		 */
		// Log.i(TAG, "usb point count:" + mDeviceIntf.getEndpointCount());

		for (int t = 0; t < mDevice.getInterfaceCount(); t++) {
			mDeviceIntf = mDevice.getInterface(t);
			Log.i(TAG, "usb point count:" + mDeviceIntf.getEndpointCount() + " interface:" + t);

			for (int i = 0; i < mDeviceIntf.getEndpointCount(); i++) {

				int type = mDeviceIntf.getEndpoint(i).getType();
				Log.i(TAG, "usb end point type:" + type);

				switch (type) {
				case UsbConstants.USB_ENDPOINT_XFER_BULK: {
					if (mDeviceIntf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
						mDeviceInPoint = mDeviceIntf.getEndpoint(i);
					} else if (mDeviceIntf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_OUT) {
						mDeviceOutPoint = mDeviceIntf.getEndpoint(i);
					}
					break;
				}

				case UsbConstants.USB_ENDPOINT_XFER_INT:
					mDeviceIntPoint = mDeviceIntf.getEndpoint(i);
					break;

				case UsbConstants.USB_ENDPOINT_XFER_CONTROL: {

					break;
				}
				}
			}

		}

		Log.i(TAG, "usb in point:" + mDeviceInPoint);
		Log.i(TAG, "usb out point:" + mDeviceOutPoint);
		Log.i(TAG, "usb int point:" + mDeviceIntPoint);
		Log.i(TAG, "usb type:" + mDeviceIntf.getEndpoint(0).getType());
		// return false;

		mDeviceConnect = mManager.openDevice(mDevice);
		if (mDeviceConnect == null) {
			Log.e(TAG, "usb connect fail");
			return false;
		}
		boolean ret = mDeviceConnect.claimInterface(mDeviceIntf, true);
		if (ret == false) {
			Log.e(TAG, "usb claimInterface fail");
		} else {
			Log.i(TAG, "usb open succ");
		}
		return ret;
	}




	public byte[] sendCommand(byte[] buffer, int len) {
		int requestType, request, value, index, ret, length;
		requestType = 0x21;
		request = 0x09;
		value = (0x03 << 8) | 0x05;
		index = mInterface;
		length = len;
		
		byte [] data = new byte[0x40];
		ComFunc.memset(data, 0x00, data.length);
	//	bs = new byte[0x40];
		if (len < 0x40) {
			ComFunc.memcpy(data, buffer, 0, 0, len);
			length = 0x40;
		}else 
			data = buffer;
	
	//	Common.log("send:", data, data.length);
		ret = mDeviceConnect.controlTransfer(requestType, request, value, index, data, length, 1000);
		if (ret < 0) {
			ComFunc.log("send ret null,ret:" + ret);
			return null;
		}
		return data;
	}

	public byte[] recvResult() {
		return recvResult(0x40);
	}
	
	public byte[] recvResult(int len) {
		int requestType, request, value, index, ret, length;
		byte[] bs;

		requestType = 0xa1;
		request = 0x01;
		value = 0x03 << 8 | 0x06;
		index = mInterface;
		length = len;
		bs = new byte[len];
		ComFunc.memset(bs, 0x00, bs.length);
		// bs[0] = 6;
		// bs[1] = 3;

		ret = mDeviceConnect.controlTransfer(requestType, request, value, index, bs, length, 1000);
		if (ret < 0) {
			ComFunc.log("recv ret null");
			return null;
		}
	//	Common.log("recv:", bs, bs.length);
		return bs;
	}



	
	public String getDescrption() {
		String s = "product id:" + String.format("%x", mDevice.getProductId()) + "\n";
		s += ("vendor id:" + String.format("%x",mDevice.getVendorId()) + "\ns");

		final int STD_USB_REQUEST_GET_DESCRIPTOR = 0x06;
		final int LIBUSB_DT_STRING = 0x03;

		byte[] buffer = new byte[255];
		int indexManufacturer = 14;
		int indexProduct = 15;
		String stringManufacturer = "";
		String stringProduct = "";

		byte[] rawDescriptors = mDeviceConnect.getRawDescriptors();

		int ret = mDeviceConnect.controlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_STANDARD,
				STD_USB_REQUEST_GET_DESCRIPTOR, (LIBUSB_DT_STRING << 8) | rawDescriptors[indexManufacturer], // value
				0, // index
				buffer, // buffer
				0xFF, // length
				0); // timeout

		if (ret < 0)
			return null;
		stringManufacturer = new String(buffer);
		//Common.sleep(10);
		//recvResult();

		ret = mDeviceConnect.controlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_STANDARD,
				STD_USB_REQUEST_GET_DESCRIPTOR, (LIBUSB_DT_STRING << 8) | rawDescriptors[indexProduct], 0, buffer, 0xFF,
				0);

		stringProduct = new String(buffer);
		s += ("manufacturer:" + stringManufacturer + "\n");
		s += ("product: " + stringProduct + "\n");
		//recvResult();
		
		ComFunc.log("s");
		return s;
	}

	public String  getShortDesc() {
		String s = "";
		s += "PID:" + String.format("%04X", mDevice.getProductId()) + "\n";
		s += "VID:" + String.format("%04X", mDevice.getVendorId()) + "\n";
		return s;
	}

	public void close() {
		if (mDevice != null && mDeviceConnect != null) {
			mDeviceConnect.releaseInterface(mDeviceIntf);
			mDeviceConnect.close();
		}
		
	}

	private UsbDevice findDevice() {

		HashMap<String, UsbDevice> list = mManager.getDeviceList();
		for (UsbDevice usb : list.values()) {
			Log.i(TAG, "usb get devices:" + usb.getProductId() + " " + usb.getVendorId());
			int pid = usb.getProductId();
			int vid = usb.getVendorId();
			if (vid == Constant.VID) {
				
				if (pid == Constant.PID_NORMAL)
					mInterface = Constant.DEVICE_INTERFACE_NORMAL;
				else 
					mInterface = Constant.DEVICE_INTEFFACE_BOOT;
				
				Log.i(TAG, "usb find tp usb,interface:" + mInterface);
				return usb;
			}

		}
		Log.i(TAG, "usb get devices fail");
		return null;
	}

	public int getPid() {
		return mDevice.getProductId();
	}
	
	public int getVid() {
		return mDevice.getVendorId();
	}
	

	public boolean checkUsbPermission() {
		

		if (mManager.hasPermission(mDevice)) {
			Log.i(TAG, "check usb has permission already");
			return true;
		} else {
			Log.i(TAG, "check usb need request permission");
			
			return false;
		}

	}
	
	public void requestPermission() {
		PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(Constant.ACTION_USB_PERMISSION), 0);
		mManager.requestPermission(mDevice, mPermissionIntent);
	}
	
}
