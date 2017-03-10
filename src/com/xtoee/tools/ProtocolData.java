package com.xtoee.tools;

import android.util.Log;

public class ProtocolData {

	@SuppressWarnings("unused")
	private byte firstByte = 0x68; // ��ʼ�� 1b
	@SuppressWarnings("unused")
	private byte[] logicAddress = { 0x5, 0x05, 0x00, 0x00, 0x1E, 0x00 }; // �߼���ַ
																			// 6b
	@SuppressWarnings("unused")
	private byte beginByte = 0x68; // �����е���ʼ�� 1b
	private byte lastByte = 0x16; // ������ 1b
	private byte[] head = { 0x68, 0x50, 0x05, 0x00, 0x00, 0x1E, 0x00, 0x68 };// ͷ
																				// 8b

	private byte controlByte;// ������ 1b
	private byte[] protocolSize = new byte[2]; // ���� 2b
	private byte[] usefulData;// ����
	private byte CS;// ��֤�� 1b

	/**
	 * �޲ι��캯��
	 */
	public ProtocolData() {
		super();
	}

	/**
	 * ���캯��
	 * @param controlByte   ���Ŀ�����
	 * @param protocolSize  ���ĳ���
	 * @param usefulData    ��������
	 * @param cS            У����
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
	 * ���캯��
	 * @param controlByte  ���Ŀ�����
	 * @param protocolSize ���ĳ���
	 * @param usefulData   ��������
	 */
	public ProtocolData(byte controlByte, byte[] protocolSize, byte[] usefulData) {
		super();
		this.controlByte = controlByte;
		this.protocolSize = protocolSize;
		this.usefulData = usefulData;
		CS = getCS(controlByte, protocolSize, usefulData);
	}

	/**
	 * ���캯��
	 * @param controlByte  ���Ŀ�����
	 * @param usefulData   ��������
	 */
	public ProtocolData(byte controlByte, byte[] usefulData) {
		super();
		this.controlByte = controlByte;
		setProtocolSizeByUsefuldata(usefulData);
		this.usefulData = usefulData;
		CS = getCS(controlByte, protocolSize, usefulData);
	}

	/**
	 * ���캯��
	 * @param frame  ����
	 */
	public ProtocolData(byte[] frame) {
		controlByte = getControlByte(frame);
		protocolSize = getProtocolSize(frame);
		setUsefulData(frame);
		setCS(frame);
	}

	/**
	 * ��ȡ������
	 * @param frame ��������
	 * @return
	 */
	public byte getControlByte(byte[] frame) {
		return frame[8];
	}

	/**
	 * ���ÿ�����
	 * @param controlByte  ������
	 */
	public void setControlByte(byte controlByte) {
		this.controlByte = controlByte;
	}

	/**
	 * ��ȡ����
	 * @return
	 */
	public byte[] getProtocolSize() {
		return getProtocolSize(getFrame());
	}

	/**
	 * ���ó���
	 * @param frame
	 */
	public void setProtocolSize(byte[] frame) {
		byte[] res = getProtocolSize(frame);
		protocolSize[0] = res[0];
		protocolSize[1] = res[1];
	}

	/**
	 * ͳ�Ʊ����������ó���
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
	 * ��ȡ��������
	 * @return
	 */
	public byte[] getUsefulData() {
		return getUsefulData(getFrame());
	}

	/**
	 * ���ñ�������
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
	 * ��ȡ��������
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
	 * ����У����
	 * @param frame
	 */
	public void setCS(byte[] frame) {
		this.CS = frame[frame.length - 2];
	}

	/**
	 * ��ȡУ����
	 * @param frame  ��������
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
	 * ��ȡУ����
	 * @return
	 */
	public byte getCS() {
		return getCS(getFrame());
	}

	/**
	 * ͨ�����ļ����ȡУ����
	 * @param controlByte   ������
	 * @param protocolSize  ���ĳ���
	 * @param usefulData    ��������
	 * @return У����
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
	 * �������Ĳ���ȡ��������
	 * @param frame ��������
	 * @return
	 */
	public byte[] getUsefulData(byte[] frame) {
		int len = frame.length;
		if (len < 14) {// ��Ч����
			Log.d("frame warning!", "�����в����κβ�����");
			return null;
		}
		if (frame[len - 2] != getCS(frame)) {// У�����
			Log.d("frame warning!", "У�������ȷ��֤��Ӧ��Ϊ" + getCS(frame));
			return null;
		}
		// ���Ĳ���
		byte[] res = new byte[len - 13];
		for (int i = 0; i < len - 13; ++i) {
			res[i] = frame[i + 11];
		}
		return res;
	}

	/**
	 * ��ȡ���ĳ���BCD����ʽ
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
	 * ��ȡ���ĳ��� ʮ������ʽ
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
	 * ��ȡ���ĳ��� ʮ������ʽ
	 * @param frame
	 * @return
	 */
	public int getProtocolSizeofInt() {
		return getProtocolSizeofInt(getFrame());
	}

	/**
	 * ʮ����ת������
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
