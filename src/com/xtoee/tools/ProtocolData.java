package com.xtoee.tools;

import android.util.Log;

public class ProtocolData {

	@SuppressWarnings("unused")
	private byte firstByte = 0x68; // 起始符 1b
	@SuppressWarnings("unused")
	private byte[] logicAddress = { 0x5, 0x05, 0x00, 0x00, 0x1E, 0x00 }; // 逻辑地址
																			// 6b
	@SuppressWarnings("unused")
	private byte beginByte = 0x68; // 报文中的起始符 1b
	private byte lastByte = 0x16; // 结束符 1b
	private byte[] head = { 0x68, 0x50, 0x05, 0x00, 0x00, 0x1E, 0x00, 0x68 };// 头
																				// 8b

	private byte controlByte;// 控制码 1b
	private byte[] protocolSize = new byte[2]; // 长度 2b
	private byte[] usefulData;// 数据
	private byte CS;// 验证码 1b

	/**
	 * 无参构造函数
	 */
	public ProtocolData() {
		super();
	}

	/**
	 * 构造函数
	 * @param controlByte   报文控制字
	 * @param protocolSize  报文长度
	 * @param usefulData    报文正文
	 * @param cS            校验码
	 */
	public ProtocolData(byte controlByte, byte[] protocolSize,
			byte[] usefulData, byte cS) {
		super();
		this.controlByte = controlByte;
		this.protocolSize = protocolSize;
		this.usefulData = usefulData;
		CS = cS;
	}

	/**
	 * 构造函数
	 * @param controlByte  报文控制字
	 * @param protocolSize 报文长度
	 * @param usefulData   报文正文
	 */
	public ProtocolData(byte controlByte, byte[] protocolSize, byte[] usefulData) {
		super();
		this.controlByte = controlByte;
		this.protocolSize = protocolSize;
		this.usefulData = usefulData;
		CS = getCS(controlByte, protocolSize, usefulData);
	}

	/**
	 * 构造函数
	 * @param controlByte  报文控制字
	 * @param usefulData   报文正文
	 */
	public ProtocolData(byte controlByte, byte[] usefulData) {
		super();
		this.controlByte = controlByte;
		setProtocolSizeByUsefuldata(usefulData);
		this.usefulData = usefulData;
		CS = getCS(controlByte, protocolSize, usefulData);
	}

	/**
	 * 构造函数
	 * @param frame  报文
	 */
	public ProtocolData(byte[] frame) {
		controlByte = getControlByte(frame);
		protocolSize = getProtocolSize(frame);
		setUsefulData(frame);
		setCS(frame);
	}

	/**
	 * 获取控制字
	 * @param frame 完整报文
	 * @return
	 */
	public byte getControlByte(byte[] frame) {
		return frame[8];
	}

	/**
	 * 设置控制字
	 * @param controlByte  控制字
	 */
	public void setControlByte(byte controlByte) {
		this.controlByte = controlByte;
	}

	/**
	 * 获取长度
	 * @return
	 */
	public byte[] getProtocolSize() {
		return getProtocolSize(getFrame());
	}

	/**
	 * 设置长度
	 * @param frame
	 */
	public void setProtocolSize(byte[] frame) {
		byte[] res = getProtocolSize(frame);
		protocolSize[0] = res[0];
		protocolSize[1] = res[1];
	}

	/**
	 * 统计报文正文设置长度
	 * @param data  usefuldata
	 */
	public void setProtocolSizeByUsefuldata(byte[] data) {
		int n = data.length;
		int n1 = n / 256;
		int n2 = n - 256 * n1;
		protocolSize[0] = inttobyte(n2);
		protocolSize[1] = inttobyte(n1);
	}

	/**
	 * 获取报文正文
	 * @return
	 */
	public byte[] getUsefulData() {
		return getUsefulData(getFrame());
	}

	/**
	 * 设置报文正文
	 * @param frame
	 */
	public void setUsefulData(byte[] frame) {
		byte[] res = getUsefulData(frame);
		if (res == null)
			return;
		int n = res.length;
		this.usefulData = new byte[n];
		for (int i = 0; i < n; ++i) {
			usefulData[i] = res[i];
		}
	}

	/**
	 * 获取完整报文
	 * @return
	 */
	public byte[] getFrame() {
		byte[] res = new byte[13 + usefulData.length];
		System.arraycopy(head, 0, res, 0, 8);
		res[8] = controlByte;
		System.arraycopy(protocolSize, 0, res, 9, 2);
		System.arraycopy(usefulData, 0, res, 11, usefulData.length);
		res[res.length - 2] = CS;
		res[res.length - 1] = lastByte;
		return res;
	}

	/**
	 * 设置校验码
	 * @param frame
	 */
	public void setCS(byte[] frame) {
		this.CS = frame[frame.length - 2];
	}

	/**
	 * 获取校验码
	 * @param frame  完整报文
	 * @return
	 */
	public byte getCS(byte[] frame) {
		byte res = 0;
		int len = frame.length - 2;
		for (int i = 0; i < len; ++i) {
			res = (byte) (res + frame[i]);
		}
		return res;
	}

	/**
	 * 获取校验码
	 * @return
	 */
	public byte getCS() {
		return getCS(getFrame());
	}

	/**
	 * 通过报文计算获取校验码
	 * @param controlByte   控制字
	 * @param protocolSize  报文长度
	 * @param usefulData    报文正文
	 * @return 校验码
	 */
	public byte getCS(byte controlByte, byte[] protocolSize, byte[] usefulData) {
		byte res = 0;
		for (int i = 0; i < head.length; ++i) {
			res = (byte) (res + head[i]);
		}
		res = (byte) (res + controlByte);
		for (int i = 0; i < protocolSize.length; ++i) {
			res = (byte) (res + protocolSize[i]);
		}
		for (int i = 0; i < usefulData.length; ++i) {
			res = (byte) (res + usefulData[i]);
		}
		return res;
	}

	/**
	 * 分析报文并获取报文正文
	 * @param frame 完整报文
	 * @return
	 */
	public byte[] getUsefulData(byte[] frame) {
		int len = frame.length;
		if (len < 14) {// 无效报文
			Log.d("frame warning!", "报文中不含任何参数！");
			return null;
		}
		if (frame[len - 2] != getCS(frame)) {// 校验出错
			Log.d("frame warning!", "校验出错！正确验证码应该为" + getCS(frame));
			return null;
		}
		// 正文部分
		byte[] res = new byte[len - 13];
		for (int i = 0; i < len - 13; ++i) {
			res[i] = frame[i + 11];
		}
		return res;
	}

	/**
	 * 获取报文长度BCD码形式
	 * @param frame
	 * @return
	 */
	public byte[] getProtocolSize(byte[] frame) {
		byte[] res = new byte[2];
		res[0] = frame[9];
		res[1] = frame[10];
		return res;
	}

	/**
	 * 获取报文长度 十进制形式
	 * @param frame
	 * @return
	 */
	public int getProtocolSizeofInt(byte[] frame) {
		setProtocolSize(frame);
		int len = 0;
		int temp = 0;
		if (protocolSize[1] < 0) {
			len += (1 << 15);
		}
		temp = (protocolSize[1] & 0x7F);
		len += temp << 8;
		if (protocolSize[0] < 0) {
			len += (1 << 7);
		}
		temp = (protocolSize[0] & 0x7F);
		len += temp;
		return len;
	}

	/**
	 * 获取报文长度 十进制形式
	 * @param frame
	 * @return
	 */
	public int getProtocolSizeofInt() {
		return getProtocolSizeofInt(getFrame());
	}

	/**
	 * 十进制转二进制
	 * @param n
	 * @return
	 */
	public byte inttobyte(int n) {
		byte res = 0;
		if (n != 0) {
			for (int i = 0; i < 8; ++i) {
				if ((n & (1 << i)) > 0) {
					res = (byte) (res | (1 << i));
				}
			}
		}
		return res;
	}

}
