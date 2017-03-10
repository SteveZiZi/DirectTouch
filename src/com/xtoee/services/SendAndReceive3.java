package com.xtoee.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.xtoee.tools.MD5util;

import android.util.Log;

public class SendAndReceive3 {

	byte[] readframe = { 0x68, (byte) 0x81, 0x49, 0x1E, 0x04, 0x1E, 0x00, 0x68,
			(byte) 0xA4, 0x00, 0x00, 0x7E, 0x16 };

	private static byte[] login = { 0x68, 0x50, 0x05, 0x00, 0x00, 0x1E, 0x00,
			0x68, (byte) 0xB1, 0x03, 0x00, 0x11, 0x11, 0x11, 0x2A, 0x16 };

	private Socket[] msocket;
	private String[] mip;
	private int[] mport;
	private DataOutputStream[] mos;
	private DataInputStream[] mis;
	private int[] reConnect;
	private boolean[] islogin;
	private int[] logicAddr;

	private SendAndReceive3() {
		msocket = new Socket[6];
		mip = new String[6];
		mport = new int[6];
		mos = new DataOutputStream[6];
		mis = new DataInputStream[6];
		reConnect = new int[6];
		islogin = new boolean[6];
		logicAddr = new int[6];
	};

	private static SendAndReceive3 instance = new SendAndReceive3();

	public void resetInstance() {
		for (int i = 1; i < 7; ++i) {
			closeSocket(i);
		}
		try {
			Thread.sleep(1200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		instance = new SendAndReceive3();
	}

	public static SendAndReceive3 getinstance(int n, String ip,
			int port, boolean remote,int logicRemote) {
		--n;
		if(remote) instance.logicAddr[n]=logicRemote;
		boolean reInstance = false;
		if (instance.reConnect[n] > 6 ) {
			instance.closeSocket(n+1);
			reInstance = true;
			System.out.println("Count is " + instance.reConnect[n]);
		}
		if (reInstance || !(ip.equals(instance.mip[n])) || port != instance.mport[n]) {
			instance.mip[n] = ip;
			instance.mport[n] = port;
			instance.msocket[n] = new Socket();
			try {
				System.out
					.println("socket " + n + " is " + instance.msocket[n]+" begin to connect");
				instance.msocket[n] = new Socket(ip, port);
				instance.mos[n] = new DataOutputStream(
						instance.msocket[n].getOutputStream());
				instance.mis[n] = new DataInputStream(
						instance.msocket[n].getInputStream());
				System.out
						.println("socket " + n + " is " + instance.msocket[n]+" is connect");
				// 登录：如果是后台远端&&没登录
				if (remote && (!instance.islogin[n]))
					instance.sendReceive(n + 1, login.clone(), logicRemote);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Log.w("Socket connect", "socket " + (n + 1)
						+ " is connected failed!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return instance;
		}

		if (instance.msocket[n].isClosed()
				|| !instance.msocket[n].isConnected()) {
			try {
				instance.msocket[n] = new Socket(ip, port);
				instance.mos[n] = new DataOutputStream(
						instance.msocket[n].getOutputStream());
				instance.mis[n] = new DataInputStream(
						instance.msocket[n].getInputStream());
				// 登录：如果是后台远端&&没登录
				if (remote && (!instance.islogin[n]))
					instance.sendReceive(n + 1, login.clone(), logicRemote);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Log.w("Socket connect", "socket " + (n + 1)
						+ " is connected failed!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public byte[] sendReceive(int index, byte[] mess, int logic) {
		index--;
		if(mess.length==16 && mess[11]==(byte)0x11 && mess[12]==(byte)0x11 && 
				mess[13]==(byte)0x11 && instance.islogin[index]){
			Log.e("重复重复重复！！", "instance.islogin[index] is "+instance.islogin[index]+"重复重复重复！！");
			return null;
		}
		mess = toLogic(mess, logic);
		int n = 0;
		byte[] remess = new byte[1024 * 4];
		DataOutputStream os = instance.mos[index];
		DataInputStream is = instance.mis[index];
		if (os != null && is != null) {
			try {
				// 向服务器端发送一条消息
				os.write(mess);
				os.flush();
				// 打印调试
				// 打印调试
				System.out.print(index+" Send:");
				System.out.println(MD5util.toString(mess));

				// 读取服务器返回的消息
				int templen = 0;
				int timeover = 0;
				while (templen == 0) {
					Thread.sleep(150);
					if (timeover++ > 40)
						break;
					templen = is.available();
				}
				if (timeover <= 40) {
					n = is.read(remess);
					instance.islogin[index] = true;
				}
				Log.d("catch", index+" length = " + n);
			} catch (SocketException e0) {
				e0.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
			byte[] res = new byte[n];
			for (int i = 0; i < n; ++i) {
				res[i] = remess[i];
			}
			if(n==0){
				instance.reConnect[index]++;
			}else{
				instance.reConnect[index]=0;
			}
			// 打印调试
			// 打印调试
			System.out.print(index+" Receve:");
			System.out.println(MD5util.toString(res));
			return res;
		} else {
			return null;
		}

	}

	private byte[] toLogic(byte[] mess, int logic) {
		int a = logic/100;
		int b = logic%100;
		a = ((a/10)<<4)+(a%10);
		b = ((b/10)<<4)+(b%10);
		mess[3] = (byte) a;
		mess[4] = (byte) b;
		int n = mess.length;
		byte tmp = mess[n - 2];
		tmp = (byte) (tmp + a + b);
		mess[n - 2] = tmp;
		return mess;
	}

	public void closeSocket(int n) {
		--n;
		instance.islogin[n] = false;
		instance.reConnect[n]=0;
		if (instance.msocket[n] != null && !instance.msocket[n].isClosed()) {
			if (instance.msocket[n].isInputShutdown()) {
				try {
					instance.mos[n].close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("this error is " + n);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("this error is " + n);
				}
			}
			if (instance.msocket[n].isInputShutdown()) {
				try {
					instance.mis[n].close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("this error is " + n);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("this error is " + n);
				}
			}
			if (!instance.msocket[n].isClosed()) {
				try {
					instance.msocket[n].close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("this error is " + n);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("this error is " + n);
				}
			}
		}
		instance.msocket[n] = null;
		instance.mip[n] = "";
		instance.mport[n] = 0;
		instance.mos[n] = null;
		instance.mis[n] = null;
		instance.logicAddr[0] = 0;
	}

//	public boolean getLogin(int i) {
//		return instance.islogin[i];
//	}

}
