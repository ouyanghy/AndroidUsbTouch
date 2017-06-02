package com.ou.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.ou.common.ComFunc;
import com.ou.common.Constant;
import com.ou.usbtp.R;

import android.content.Context;
import android.graphics.PointF;

public class Function extends Constant {
	private Device mUsb;
	private static Function mThis = null;
	private int mProgress = Constant.PROGRESS_UNSTART;
	public Function(Device t) {
		mUsb = t;
		mThis = this;
	}

	public static Function getTpUsbFunction() {
		return mThis;
	}

	public String getDescrption() {
		return mUsb.getDescrption();
	}

	public int getPid() {
		return mUsb.getPid();
	}

	public int getVid() {
		return mUsb.getVid();
	}

	public String getShortDesc(Context context) {
		if (getTpUsbFunction() == null)
			return ComFunc.getString(context, R.string.device_no_found);
		return mUsb.getShortDesc();
	}

	/* private function */
	private byte[] getReadCommandHead(int addr, int len) {
		byte[] bs = new byte[7];
		bs[0] = (byte) REPORT_ID_OUT_CMD;
		bs[1] = (byte) CMD_READ_FLASH;
		bs[2] = (byte) (addr & 0xff);
		bs[3] = (byte) ((addr & 0xff00) >> 8);
		bs[4] = (byte) ((addr & 0xff0000) >> 16);
		bs[5] = (byte) ((addr & 0xff000000) >> 24);
		bs[6] = (byte) len;
		return bs;
	}

	private byte[] getWriteCommandHead(int addr, int data_len, byte[] data) {
		byte[] bs = new byte[7 + data_len];
		bs[0] = (byte) REPORT_ID_OUT_CMD;
		bs[1] = (byte) CMD_WRITE_FLASH;
		bs[2] = (byte) (addr & 0xff);
		bs[3] = (byte) ((addr & 0xff00) >> 8);
		bs[4] = (byte) ((addr & 0xff0000) >> 16);
		bs[5] = (byte) ((addr & 0xff000000) >> 24);
		bs[6] = (byte) data_len;
		for (int i = 0; i < data_len; i++)
			bs[7 + i] = data[i];
		return bs;
	}

	private byte[] getEraseHead(int addr, int num) {
		byte[] bs = new byte[10];
		bs[0] = (byte) REPORT_ID_OUT_CMD;
		bs[1] = (byte) CMD_ERASE_FLASH;
		bs[2] = (byte) (addr & 0xff);
		bs[3] = (byte) ((addr & 0xff00) >> 8);
		bs[4] = (byte) ((addr & 0xff0000) >> 16);
		bs[5] = (byte) ((addr & 0xff000000) >> 24);
		addr += num * 1024;

		bs[6] = (byte) (addr & 0xff);
		bs[7] = (byte) ((addr & 0xff00) >> 8);
		bs[8] = (byte) ((addr & 0xff0000) >> 16);
		bs[9] = (byte) ((addr & 0xff000000) >> 24);
		return bs;

	}

	private byte[] getCalCommandHead() {
		byte[] bs = new byte[2];
		bs[0] = (byte) REPORT_ID_OUT_CMD;
		bs[1] = (byte) CMD_CALIBRATE;

		return bs;

	}

	private byte[] getImageCommandHead(int mode, int len) {
		byte[] bs = new byte[8];
		byte addr = 0;
		ComFunc.memset(bs, 0x00, bs.length);
		bs[0] = (byte) REPORT_ID_OUT_CMD;
		bs[1] = (byte) CMD_GET_IMAGE;

		bs[2] = (byte) mode;
		bs[3] = 0x1;

		bs[4] = (byte) (addr & 0xff);
		bs[5] = (byte) ((addr & 0xff00) >> 8);

		bs[6] = (byte) len;
		bs[7] = (byte) ((len & 0xff00) >> 8);
		return bs;
	}

	/* common function */

	private byte[] __getFramewareId() {
		// byte[] mode_commands = { 0x5, 0x03 };
		byte[] ret;

		ret = switchMode(SET_MODE);
		if (ret == null) {
			// log("send command fail", mode_commands,mode_commands.length);
			return null;
		}

		ComFunc.sleep(20);
		int addr = (FW_VERSION_ADDR + 4) | EXTERNAL_FLASH_ADDRESS;
		byte[] command_id = getReadCommandHead(addr, 4);

		ret = mUsb.sendCommand(command_id, command_id.length);
		if (ret == null) {
			ComFunc.log("send command_id fail", command_id, command_id.length);
			return null;
		}
		ComFunc.sleep(20);
		ret = recvResult();
		if (ret == null) {
			ComFunc.log("recv command_id fail");
			return null;
		}
		return ret;
	}

	private byte[] __getCalInfo() {
		int addr = CALIB_INFO_ADDR | EXTERNAL_FLASH_ADDRESS;
		byte[] command;
		command = getReadCommandHead(addr, 46);
		byte[] ret = mUsb.sendCommand(command, command.length);
		if (ret == null)
			return null;

		ComFunc.sleep(20);
		ret = recvResult();
		if (ret == null) {
			ComFunc.log("recv command_id fail");
			return null;
		}
		return ret;
	}

	private byte[] __eraseCalInfo() {
		int addr = CALIB_INFO_ADDR | EXTERNAL_FLASH_ADDRESS;

		byte[] head = getEraseHead(addr, 1);
		if (head == null)
			return null;

		byte[] ret = mUsb.sendCommand(head, head.length);
		if (ret == null) {
			return null;
		}

		ComFunc.sleep(40);

		ret = recvResult();
		if (ret == null)
			return null;

		return ret;
	}

	private byte[] recvResult() {

		return recvResult(0x40);
	}

	private byte[] recvResult(int len) {
		if (!mUsb.isAvail())
			return null;

		int cnt = 0;
		byte[] ret;
		ComFunc.sleep(20);
		do {
			ret = mUsb.recvResult(len);
			if (ret == null) {
				if (cnt++ < 3) {
					ComFunc.sleep(20);
					continue;
				} else {
					break;
				}

			}

			// Common.log("ret1:" + ret[1]);
			if (ret[1] == 0xff || ret[1] == -1) {
				ComFunc.sleep(20);
				// Common.log("recvResult error retry");
				ComFunc.log("retry recv cnt:" + cnt);
			} else {
				cnt = 3;
			}
		} while (cnt++ < 3);

		return ret;
	}

	private byte[] __writeCalInfo(CalInfo info) {
		info.setCheckFlag(0x434F4E46);
		byte[] data = info.toByte();
		int addr = CALIB_INFO_ADDR | EXTERNAL_FLASH_ADDRESS;
		int data_len = info.getSize();
		byte[] command = getWriteCommandHead(addr, data_len, data);
		if (command == null)
			return null;

		byte[] ret = mUsb.sendCommand(command, command.length);
		if (ret == null)
			return null;
		ComFunc.sleep(40);
		ret = recvResult();

		return ret;
	}

	private byte[] __eraseBroadInfo() {
		int addr = BOARD_INFO_ADDR | EXTERNAL_FLASH_ADDRESS;

		byte[] head = getEraseHead(addr, 1);
		if (head == null) {
			ComFunc.log("erase broadinfo fail");
			return null;
		}

		byte[] ret = mUsb.sendCommand(head, head.length);
		if (ret == null) {
			ComFunc.log("erase broadinfo sendCommand fail");
			return null;
		}

		// ComFunc.sleep(40);
		// ComFunc.log("enter==>");
		ret = recvResult();
		// ComFunc.log("exit<==");
		return ret;
	}

	private byte[] __writeBroadInfo(BoardConfig con) {
		/* write config */

		byte[] config = con.getBuffer();
		int size = con.getSize();
		int addr = (BOARD_INFO_ADDR + 4) | EXTERNAL_FLASH_ADDRESS;

		byte[] ret = sendBlockData(addr, config, config.length);
		if (ret == null)
			return null;

		/* write screen size */
		byte[] bs = new byte[4];
		ComFunc.memset(bs, 0x00, bs.length);
		bs[0] = (byte) (size & 0xff);
		bs[1] = (byte) ((size & 0xff00) >> 8);
		addr = (BOARD_INFO_ADDR) | EXTERNAL_FLASH_ADDRESS;
		byte[] command = getWriteCommandHead(addr, bs.length, bs);
		ret = mUsb.sendCommand(command, command.length);
		if (ret == null)
			return null;

		ret = recvResult();

		return ret;
	}

	private byte[] __readBroadInfo() {
		int addr = 0;
		int addr_start = (BOARD_INFO_ADDR + 4) | EXTERNAL_FLASH_ADDRESS;
		int len = 146;
		int offset = 0;
		byte[] command;
		int roundUpLen = ComFunc.roundUp(len, PACKAGE_RECV_LENGTH_DATA_LIMIT);
		byte[] rcv = new byte[roundUpLen];
		ComFunc.memset(rcv, 0x00, rcv.length);

		for (int i = 0; i < roundUpLen; i += PACKAGE_RECV_LENGTH_DATA_LIMIT) {
			addr = addr_start + i;

			command = getReadCommandHead(addr, PACKAGE_LENGTH_LIMIT);
			byte[] ret = mUsb.sendCommand(command, command.length);
			if (ret == null)
				return null;

			ComFunc.sleep(40);
			ret = recvResult();
			if (ret == null) {
				ComFunc.log("recv command_id fail");
				return null;
			}
			offset = i;
			ComFunc.log("offset:" + offset);
			ComFunc.memcpy(rcv, ret, offset, PACKAGE_RECV_DATA_START, PACKAGE_RECV_LENGTH_DATA_LIMIT);

		}

		// Common.log("rcv:", Common.memcut(rcv, 0, 142), Common.memcut(rcv, 0,
		// 142).length);
		return ComFunc.memcut(rcv, 0, 142);
		// return rcv;

	}

	private byte[] __readCalPoint() {
		byte bs[] = getCalCommandHead();
		byte[] ret = mUsb.sendCommand(bs, bs.length);
		if (ret == null) {
			ComFunc.log("readCalPoint error send command ");
			return null;
		}

		ret = recvResult();
		int point_num = ret[3];
		// Common.log("read point:" + point_num);
		if (point_num == 0)
			return null;

		ComFunc.sleep(20);

		return ComFunc.memcut(ret, 3, 3 + 4);
	}

	private byte[] sendBlockData(int addr, byte[] data_send, int data_len) {

		byte[] ret = null;
		int addr_org = addr;
		int roundDownLen = ComFunc.roundDown(data_len, PACKAGE_SEND_LENGTH_LIMIT);
		for (int j = 0; j < roundDownLen; j += PACKAGE_SEND_LENGTH_LIMIT) {
			if (!mUsb.isAvail())
				return null;

			addr = addr_org + j;
			byte[] data = ComFunc.memcut(data_send, j, PACKAGE_SEND_LENGTH_LIMIT);
			byte[] command = getWriteCommandHead(addr, PACKAGE_SEND_LENGTH_LIMIT, data);
			ret = mUsb.sendCommand(command, command.length);
			if (ret == null)
				return null;

			ret = recvResult();

		}
		if (!mUsb.isAvail())
			return null;
		/* send left */
		int remain = data_len - roundDownLen;
		if (remain > 0) {
			addr = addr_org + roundDownLen;
			byte[] data = ComFunc.memcut(data_send, roundDownLen, remain);
			byte[] command = getWriteCommandHead(addr, remain, data);
			ret = mUsb.sendCommand(command, command.length);
			if (ret == null)
				return null;

			ret = recvResult();
			if (ret == null)
				return null;
		}

		return ret;
	}

	private boolean upgradeVersion(byte[] version) {
		if (!mUsb.isAvail())
			return false;

		int addr_start = (FW_VERSION_ADDR) | EXTERNAL_FLASH_ADDRESS;
		int addr = addr_start + 4;
		/* erase */

		byte[] command = getEraseHead(addr, 1);
		byte[] ret = mUsb.sendCommand(command, command.length);
		if (ret == null) {
			ComFunc.log("erase FW_VERSION_ADDR err 0");
			return false;
		}
		ret = recvResult();
		if (ret == null) {
			ComFunc.log("erase FW_VERSION_ADDR err 1");
			return false;
		}
		mProgress = 78;
		/* write fw */
		command = getWriteCommandHead(addr, 4, version);
		ret = mUsb.sendCommand(command, command.length);
		if (ret == null) {
			ComFunc.log("write FW_VERSION_ADDR err 0");
			return false;
		}
		ret = recvResult();
		if (ret == null) {
			ComFunc.log("write FW_VERSION_ADDR err 1");
			return false;

		}
		addr = addr_start;
		byte[] data = { 0x46, 0x49, 0x57, 0x46 };
		command = getWriteCommandHead(addr, 4, data);
		ret = mUsb.sendCommand(command, command.length);
		if (ret == null) {
			ComFunc.log("write FW_VERSION_ADDR err 2");
			return false;
		}
		ret = recvResult();
		if (ret == null) {
			ComFunc.log("write FW_VERSION_ADDR err 3");
			return false;
		}

		ret = switchMode(FIRMWARE_MODE);
		mProgress = PROGRESS_FLASH_FINISH;
		return true;
	}

	private byte[] __writeCalKey(byte[] data) {
		int addr = SHORTCUT_INFO_ADDR | EXTERNAL_FLASH_ADDRESS;
		byte[] ret = sendBlockData(addr, data, data.length);
		return ret;
	}

	/* public funciton */
	public byte[] switchMode(int mode) {
		if (!mUsb.isAvail())
			return null;

		byte[] bs = { REPORT_ID_OUT_CMD, CMD_SET_MODE, (byte) mode };
		byte[] ret = mUsb.sendCommand(bs, bs.length);
		ComFunc.sleep(20);
		ret = recvResult();

		return ret;
	}

	public String getFramewareId() {
		if (!mUsb.isAvail())
			return null;

		byte[] bs = __getFramewareId();
		if (bs == null)
			return null;

		int start = 8;
		String s = bs[start + 3] + "." + bs[start + 2] + "." + bs[start];
		return s;
	}

	public CalInfo readCalInfo() {
		if (!mUsb.isAvail())
			return null;

		byte[] bs = __getCalInfo();
		CalInfo cal = new CalInfo();
		if (bs == null)
			return null;

		byte[] data = ComFunc.memcut(bs, 8, bs.length - 8);
		cal.parse(data);
		return cal;
	}

	public boolean eraseCalInfo() {
		if (!mUsb.isAvail())
			return false;

		byte[] ret = __eraseCalInfo();
		if (ret == null)
			return false;

		return true;

	}

	public boolean writeCalInfo(CalInfo info) {
		if (!mUsb.isAvail())
			return false;

		byte[] ret = __writeCalInfo(info);
		if (ret == null)
			return false;

		return true;
	}

	public boolean eraseBroadInfo() {
		if (!mUsb.isAvail())
			return false;

		byte[] ret = __eraseBroadInfo();
		if (ret == null)
			return false;
		return true;
	}

	public boolean writeBroadInfo(BoardConfig con) {
		if (!mUsb.isAvail())
			return false;

		byte[] ret = __writeBroadInfo(con);
		if (ret == null)
			return false;

		return true;
	}

	public int readBroadInfoScreenSize() {
		if (!mUsb.isAvail())
			return -1;

		int addr = (BOARD_INFO_ADDR) | EXTERNAL_FLASH_ADDRESS;
		byte[] command = getReadCommandHead(addr, 4);
		byte[] ret = mUsb.sendCommand(command, command.length);
		if (ret == null)
			return -1;
		ComFunc.sleep(20);
		ret = recvResult();
		if (ret == null)
			return -1;

		int size = ret[PACKAGE_RECV_DATA_START + 0] | ret[PACKAGE_RECV_DATA_START + 1] >> 8
				| ret[PACKAGE_RECV_DATA_START + 2] >> 16 | ret[PACKAGE_RECV_DATA_START + 3] >> 24;

		return size;
	}

	public BoardConfig readBroadInfo() {
		if (!mUsb.isAvail())
			return null;

		int size = readBroadInfoScreenSize();
		if (size < 0) {
			ComFunc.log("get size fail");
			return null;
		}
		// Common.log("===size:" + size);
		ComFunc.sleep(20);
		byte[] buf = __readBroadInfo();
		if (buf == null) {
			return null;
		}

		BoardConfig read_ic = new BoardConfig(Constant.READ_FROM_IC);
		read_ic.setBuffer(buf);
		read_ic.setSize(size);

		return read_ic;
	}

	public PointF readCalPoint() {
		if (!mUsb.isAvail())
			return null;

		byte[] ret = __readCalPoint();
		if (ret == null || ret.length < 4)
			return null;

		float x = ret[0] & 0xff | (ret[1] & 0xff) << 8;
		float y = ret[2] & 0xff | (ret[3] & 0xff) << 8;
		return new PointF(x, y);

	}

	public byte[] readImage(int mode, int len) {
		if (!mUsb.isAvail())
			return null;
		// int len = 146;
		len += 7;
		byte[] command;
		// int roundUpLen = Common.roundUp(len, PACKAGE_RECV_LENGTH_DATA_LIMIT);
		// byte[] rcv = new byte[roundUpLen];
		byte[] rcv = new byte[len];
		ComFunc.memset(rcv, 0x00, rcv.length);

		command = getImageCommandHead(mode, len);
		byte[] ret = mUsb.sendCommand(command, command.length);
		if (ret == null)
			return null;

		ComFunc.sleep(40);
		ret = recvResult(len);
		if (ret == null) {
			ComFunc.log("recv command_id fail");
			return null;
		}

		// Common.memcpy(rcv, ret, offset, PACKAGE_RECV_DATA_START - 1,
		// PACKAGE_RECV_LENGTH_DATA_LIMIT);

		// Common.log("rcv img:", Common.memcut(rcv, 0, len), len);
		// return Common.memcut(rcv, 0, len);
		return ComFunc.memcut(ret, 7, len - 7);
	}

	

	public void setProgress(int v) {
		mProgress = v;
	}

	public int getProgress() {
		return mProgress;
	}

	public boolean upgradeFirmware(File f) {
		if (!mUsb.isAvail())
			return false;

		boolean r = false;
		/*
		 * r = prepareUpgrade(); if (r == false) { mProgress =
		 * Enums.PROGRESS_ERR; Common.log("prepareUpgrade fail"); return false;
		 * }
		 */
		try {
			r = upgrade(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			r = false;
			mProgress = Constant.PROGRESS_ERR;
		}
		// 15
		if (r)
			mProgress = Constant.PROGRESS_FINISH;
		else
			mProgress = Constant.PROGRESS_ERR;

		ComFunc.log("write upgrade result:" + mProgress);
		return r;
	}

	public boolean prepareUpgrade() {
		if (!mUsb.isAvail())
			return false;

		mProgress = Constant.PROGRESS_UNSTART;
		byte[] ret = switchMode(SET_MODE);
		if (ret == null) {
			ComFunc.log("prepareUpgrade error swich set mode");
			return false;
		}
		mProgress = 1;
		int addr = FW_VERSION_ADDR | EXTERNAL_FLASH_ADDRESS;
		byte[] command = getEraseHead(addr, 1);
		ret = mUsb.sendCommand(command, command.length);
		if (ret == null) {
			ComFunc.log("prepareUpgrade error swich set mode");
			return false;
		}
		ret = recvResult();
		if (ret == null) {
			ComFunc.log("prepareUpgrade error swich set mode");
			return false;
		}
		mProgress = 2;
		ret = switchMode(BOOTLOADER_MODE);
		if (ret == null) {
			ComFunc.log("prepareUpgrade error swich set mode");
			return false;
		}
		mProgress = 10;
		return true;

	}

	public boolean upgrade(File f) throws IOException {
		if (!mUsb.isAvail())
			return false;

		/* read bin data */
		byte[] buffer = new byte[Constant.KB];
		byte[] file_data = new byte[Constant.MB];
		int r = 0;
		int offset = 0;
		byte[] version = new byte[4];
		int file_len = 0;
		mProgress = 11;
		file_len = (int) (f.length() - 8);
		FileInputStream in = new FileInputStream(f);
		do {
			r = in.read(buffer);
			if (r < 0)
				break;
			ComFunc.memcpy(file_data, buffer, offset, 0, r);
			offset += r;
		} while (r > 0);
		in.close();
		// file_len = offset;
		// file_len -= 8;
		ComFunc.memcpy(version, file_data, 0, 4, 4);

		ComFunc.log("version:" + version);

		/* switch emcry mode */
		mProgress = 15;
		byte[] ret;
		int cnt = 0;
		do {
			ComFunc.sleep(40);
			ret = switchMode(Constant.EMCRYPTION_OPEN);
			if (ret == null) {
				ComFunc.log("switchMode(Enums.EMCRYPTION_OPEN) err 0 try:" + cnt);
				continue;
				// return false;
			}
			ret = recvResult();
			if (ret == null) {
				ComFunc.log("switchMode(Enums.EMCRYPTION_OPEN) err 1 try:" + cnt);
				continue;
				// return false;
			}
		} while (cnt++ < 3);
		mProgress = 16;
		/* erase */

		int packetCount = (file_len + (Constant.KB - 1)) / Constant.KB;
		int addr_start = FW_UPDATEFLAG_ADDR | EXTERNAL_FLASH_ADDRESS;
		byte[] command = getEraseHead(addr_start, packetCount);
		ret = mUsb.sendCommand(command, command.length);
		if (ret == null) {
			ComFunc.log("erase FW_UPDATEFLAG_ADDR err 0");
			return false;
		}
		ComFunc.sleep(20);
		ret = recvResult();
		if (ret == null) {
			ComFunc.log("erase FW_UPDATEFLAG_ADDR err 1");
			return false;
		}

		mProgress = PROGRESS_WRITE_DATA;
		final int OUT_LEN = ((file_len / 9) / 112) * 112;
		int dataLen;
		offset = 0;
		if (file_len > 1008) {
			for (int i = 0; i < 9; i++) {
				mProgress += 6;// 4~12
				int addr = addr_start + i * OUT_LEN;
				if (i == 8) {
					dataLen = file_len - 8 * OUT_LEN;
				} else {
					dataLen = OUT_LEN;
				}

				offset = i * OUT_LEN + 8;
				byte[] data_send = ComFunc.memcut(file_data, offset, dataLen);
				ret = sendBlockData(addr, data_send, data_send.length);
				if (ret == null)
					return false;

			}
		} else {
			mProgress += Constant.PROGRESS_FINISH / 2;
			offset = 8;
			byte[] data_send = ComFunc.memcut(file_data, offset, file_len);
			sendBlockData(addr_start, data_send, data_send.length);
		}

		/* switch offemcry mode */
		mProgress++;// 13
		ret = switchMode(Constant.EMCRYPTION_CLOSE);
		if (ret == null) {
			ComFunc.log("switchMode(Enums.EMCRYPTION_CLOSE) 0");
			return false;
		}
		ret = recvResult();

		if (ret == null) {
			ComFunc.log("switchMode(Enums.EMCRYPTION_CLOSE) 1");
			return false;
		}
		mProgress = 76;
		return upgradeVersion(version);
	}

	public boolean writeCalKey(byte[] data) {
		if (!mUsb.isAvail())
			return false;

		byte[] ret = switchMode(SET_MODE);
		if (ret == null)
			return false;

		int addr = SHORTCUT_INFO_ADDR | EXTERNAL_FLASH_ADDRESS;
		byte[] command = getEraseHead(addr, 1);
		ret = mUsb.sendCommand(command, command.length);
		if (ret == null)
			return false;
		ret = recvResult();
		if (ret == null)
			return false;

		ComFunc.sleep(20);
		ret = __writeCalKey(data);
		if (ret == null)
			return false;
		return true;
	}

}
