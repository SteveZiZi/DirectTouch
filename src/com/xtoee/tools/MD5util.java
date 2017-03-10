package com.xtoee.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 *
 */
public class MD5util {
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static byte[] GetMD5Code(String digest) {
		if (digest == null)
			return null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(digest.getBytes());
			return bytes;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将加密结果bytes数组转化为字符串
	 * @param bytes
	 * @return
	 */
	public static String toString(byte[] bytes) {
		if (bytes == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (byte e : bytes) {
			sb.append(byteToArrayString(e));
//			加空格显示，方便查看
			sb.append(" ");
		}
		return sb.toString();
	}

	private static String byteToArrayString(byte bByte) {
		int iRet = bByte >= 0 ? bByte : bByte + 256;
		int iD1 = iRet >> 4;
		int iD2 = iRet & 0x0F;
		return strDigits[iD1] + strDigits[iD2];
	}
}
