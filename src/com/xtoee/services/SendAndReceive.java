package com.xtoee.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class SendAndReceive {

	private static Socket offonsocket = null;

	public static byte[] sendReceive(String mip, int mport, byte[] mess) {
		int n = 0;
		byte[] remess = new byte[1024 * 4];
		DataOutputStream os = null;
		DataInputStream is = null;
		try {
			// IP地址和端口号（对应服务端），我这的IP是本地路由器的IP地址
			Socket socket = new Socket(mip, mport);
			try {

				os = new DataOutputStream(socket.getOutputStream());
				// 向服务器端发送一条消息
				os.write(mess);
				os.flush();

				// 读取服务器返回的消息
				is = new DataInputStream(socket.getInputStream());

				int templen = 0;
				while (templen == 0) {
					Thread.sleep(150);
					templen = is.available();
				}
				n = is.read(remess);
				Log.d("catch", "length = " + n);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 关闭Socket
				os.close();
				is.close();
				socket.close();
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (ConnectException e2) {
			n = 0;
			Log.d("catch", "length = 0");
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] res = new byte[n];
		for (int i = 0; i < n; ++i) {
			res[i] = remess[i];
		}
		return res;
	}

	public static byte[] sendReceive2(String mip, int mport, byte[] mess,
			boolean goon) {
		int n = 0;
		boolean success = true;
		if (!goon) {
			if (offonsocket != null) {
				try {
					offonsocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				offonsocket = null;
			}
			return null;
		}
		if (offonsocket == null) {
			try {
				// IP地址和端口号（对应服务端），我这的IP是本地路由器的IP地址
				offonsocket = new Socket(mip, mport);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
				success = false;
			} catch (ConnectException e2) {
				n = 0;
				Log.d("catch", "length = 0");
				success = false;
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
			} finally {
				if (!success) {
					// try {
					// offonsocket.close();
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					offonsocket = null;
				}
			}

		}
		if (offonsocket != null) {
			byte[] remess = new byte[1024];
			DataOutputStream os = null;
			DataInputStream is = null;
			try {
				os = new DataOutputStream(offonsocket.getOutputStream());
				// 向服务器端发送一条消息
				os.write(mess);
				os.flush();

				// 读取服务器返回的消息
				is = new DataInputStream(offonsocket.getInputStream());
				n = is.read(remess);
				Log.d("catch", "length = " + n);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 关闭Socket
				try {
					os.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			byte[] res = new byte[n];
			for (int i = 0; i < n; ++i) {
				res[i] = remess[i];
			}
			return res;
		}
		return null;
	}

}
