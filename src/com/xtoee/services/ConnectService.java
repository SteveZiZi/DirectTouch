package com.xtoee.services;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import com.xtoee.bean.CLbean;
import com.xtoee.bean.CPRbean;
import com.xtoee.bean.SubCLbean;
import com.xtoee.tools.MyTools;
import com.xtoee.tools.ProtocolData;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.SparseIntArray;

public class ConnectService extends Service {

	private final int BEATSTD=24;
	private final int CTOTAL = 6;
	private final int LTOTAL = 16;
	private CLbean[] clb = new CLbean[CTOTAL * LTOTAL];// 96个回路
	private CPRbean[] cpb1 = new CPRbean[LTOTAL << 4];// CPC1下CPR1-64
	private CPRbean[] cpb2 = new CPRbean[LTOTAL << 4];
	private CPRbean[] cpb3 = new CPRbean[LTOTAL << 4];
	private CPRbean[] cpb4 = new CPRbean[LTOTAL << 4];
	private CPRbean[] cpb5 = new CPRbean[LTOTAL << 4];
	private CPRbean[] cpb6 = new CPRbean[LTOTAL << 4];
	private SubCLbean scb1, scb2, scb3, scb4, scb5, scb6;
	private String[] mip = new String[6];
	private int[] mport = new int[6];
	private boolean[] maddr = new boolean[6];
	private boolean[] mremote = new boolean[6];
	private int[] mlogic = new int[6];
	private SharedPreferences sp;
	private SharedPreferences[] msp = new SharedPreferences[6];


	private ThreadCPC1 thCPC1 = new ThreadCPC1();
	private ThreadCPC2 thCPC2 = new ThreadCPC2();
	private ThreadCPC3 thCPC3 = new ThreadCPC3();
	private ThreadCPC4 thCPC4 = new ThreadCPC4();
	private ThreadCPC5 thCPC5 = new ThreadCPC5();
	private ThreadCPC6 thCPC6 = new ThreadCPC6();
	// 由于本界面的操作不会引起子回路界面状态的改变，故0202查询可只查询一次已提高效率，
	// 本开关设置用于判断是否进行0202查询
	private boolean[] mbool = new boolean[6];
	private byte[] subcloffon = new byte[LTOTAL * CTOTAL];// 子回路通断
	private int[] needBeatcount = new int[6];//心跳包

	// 存储systemservice的查询信息
	private boolean[] misave = new boolean[6];
	/**
	 * handleMessage任务处理
	 * 
	 */
	MyHandler handler = new MyHandler(ConnectService.this) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				for (int i = 0; i < 6; ++i) {
					misave[i] = true;
					// 心跳控制
					if (maddr[i]) {
						needBeatcount[i]++;
					}
				}
			}
		}

	};
	// 定时器
	private Timer mTimer = new Timer(true);
	// 任务器
	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		InitBean();// 初始化类数据
		InitIP();// 初始化地址信息
		// 初始化不可查询CPC信息
		mTimer.schedule(mTimerTask, 5000, 4000);
		// CPC信息SharedPreferences
		for (int i = 0; i < 6; ++i) {
			misave[i] = false;
			msp[i] = getSharedPreferences("CPC" + (i + 1) + "Info",
					MODE_PRIVATE);
			mbool[i] = true;
		}
		// 初始化可查询0202报文
		thCPC1.start();
		thCPC2.start();
		thCPC3.start();
		thCPC4.start();
		thCPC5.start();
		thCPC6.start();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		while (!mTimerTask.cancel()) {
			mTimer.cancel();
		}
		for (int i = 0; i < 6; ++i) {
			maddr[i] = false;
		}

		// 查询并存储子回路的开关状态
		SharedPreferences spison = getSharedPreferences("offon", MODE_PRIVATE);
		Editor edit = spison.edit();
		for (int i = 1; i < 7; ++i) {
			for (int j = 1; j < 17; ++j) {
				edit.putInt("ison" + i + j / 10 + j % 10, subcloffon[16 * i + j
						- 17] & 0xff);
			}
		}
		edit.commit();
		// 销毁handle
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * 初始化类数据
	 */
	private void InitBean() {
		for (int i = 0; i < LTOTAL * 16; ++i) {
			cpb1[i] = new CPRbean();
			cpb2[i] = new CPRbean();
			cpb3[i] = new CPRbean();
			cpb4[i] = new CPRbean();
			cpb5[i] = new CPRbean();
			cpb6[i] = new CPRbean();
		}
		for (int i = 0; i < CTOTAL * LTOTAL; ++i) {
			subcloffon[i] = 0x00;
			clb[i] = new CLbean();
		}
		scb1 = new SubCLbean();
		scb2 = new SubCLbean();
		scb3 = new SubCLbean();
		scb4 = new SubCLbean();
		scb5 = new SubCLbean();
		scb6 = new SubCLbean();
	}

	/**
	 * 初始化地址信息
	 */
	private void InitIP() {
		sp = getSharedPreferences("addr", Context.MODE_PRIVATE);
		for (int i = 1; i < 7; ++i) {
			maddr[i - 1] = sp.getBoolean("addr" + i, false);
			mremote[i - 1] = sp.getBoolean("remote" + i, false);
			mip[i - 1] = sp.getString("ip" + i, "");
			mport[i - 1] = sp.getInt("port" + i, 0);
			mlogic[i - 1] = sp.getInt("logicAddr" + i, 4);
		}
	}

	// 数据转化函数
	public int BCDtoInt(byte a) {
		int b = a & (0x0F);
		int c = (a & (0xF0)) >> 4;
		return c * 10 + b;
	}

	public double BCDtoVI(byte a, byte b) {
		int c = BCDtoInt(a);
		int d = BCDtoInt(b);
		double res = c + 0.01 * d;
		return res * 10;
	}

	public String BCDtoState(byte a, byte b) {
		if (a != (byte)0x00) {
			return CPRbean.mABNORMAL;
		} else if (b != (byte)0x00) {
			return CPRbean.mABNORMAL;
		}
		return CPRbean.mNORMAL;
	}

	private int BCDtoTemper(byte a) {
		int res = a & 0x7F;
		if ((a & (0x80)) != 0) {
			res = res + 128;
		}
		return res;
	}

	private int BCD2toInt(byte a, byte b) {
		return BCDtoTemper(a) * 256 + BCDtoTemper(b);
	}

	// CPC1通讯
	public class ThreadCPC1 extends Thread {

		public void run() {
			while (maddr[0]) {// 地址使能方可查询
				if (mbool[0]) {
					task1(1);// 是否进行0202查询
				} else if (misave[0]) {
					task2(1);
				} else {
					task3(1);
				}
				try {
					sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(needBeatcount[0]>BEATSTD){
					heartbeat(1);
					needBeatcount[0]=0;
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class ThreadCPC2 extends Thread {

		public void run() {
			while (maddr[1]) {
				if (mbool[1]) {
					task1(2);
				} else if (misave[1]) {
					task2(2);
				} else {
					task3(2);
				}
				try {
					sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(needBeatcount[1]>BEATSTD){
					heartbeat(2);
					needBeatcount[1]=0;
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class ThreadCPC3 extends Thread {

		public void run() {
			while (maddr[2]) {
				if (mbool[2]) {
					task1(3);
				} else if (misave[2]) {
					task2(3);
				} else {
					task3(3);
				}
				try {
					sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(needBeatcount[2]>BEATSTD){
					heartbeat(3);
					needBeatcount[2]=0;
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class ThreadCPC4 extends Thread {

		public void run() {
			while (maddr[3]) {
				if (mbool[3]) {
					task1(4);
				} else if (misave[3]) {
					task2(4);
				} else {
					task3(4);
				}
				try {
					sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(needBeatcount[3]>BEATSTD){
					heartbeat(4);
					needBeatcount[3]=0;
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class ThreadCPC5 extends Thread {

		public void run() {
			while (maddr[4]) {
				if (mbool[4]) {
					task1(5);
				} else if (misave[4]) {
					task2(5);
				} else {
					task3(5);
				}
				try {
					sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(needBeatcount[4]>BEATSTD){
					heartbeat(5);
					needBeatcount[4]=0;
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class ThreadCPC6 extends Thread {

		public void run() {
			while (maddr[5]) {
				if (mbool[5]) {
					task1(6);
				} else if (misave[5]) {
					task2(6);
				} else {
					task3(6);
				}
				try {
					sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(needBeatcount[5]>BEATSTD){
					heartbeat(6);
					needBeatcount[5]=0;
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void task1(int index) {
		SendAndReceive3 sr = SendAndReceive3.getinstance(index, mip[index - 1],
				mport[index - 1], mremote[index - 1],mlogic[index - 1]);
		byte[] remess = sr.sendReceive(index, MyTools.subclframe.clone(),
				mlogic[index - 1]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null) {
				int n = getIntofaByte(useful[10]);
				Editor edit1 = msp[index - 1].edit();
				edit1.putInt("subcln", n);
				edit1.commit();
				for (int i = 1; i <= n; ++i) {
					int ti = useful[10 * n + 7];
					if (useful[10 * n + 2] == 0x02) {
						// subcloffon对应的下标：16*CPC号+回路号
						subcloffon[ti - 1] = getoffonbyte(useful[10 * n + 4],
								useful[10 * n + 5]);
					}
					Editor edit = msp[index - 1].edit();
					int tempTn = getIntofaByte(useful[10 + 10 * (i - 1) + 1]);
					edit.putInt("subTn" + ti, tempTn);
					edit.commit();
				}
			}
		}
		mbool[index - 1] = false;
	}

	private void task2(int index) {
		SendAndReceive3 sr = SendAndReceive3.getinstance(index, mip[index - 1],
				mport[index - 1], mremote[index - 1],mlogic[index - 1]);
		byte[] remess = sr.sendReceive(index, MyTools.CPRAllframe.clone(),
				mlogic[index - 1]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null) {
				// 整流器个数
				int n = getIntofaByte(useful[10]);
				Editor edit1 = msp[index - 1].edit();
				edit1.putInt("CPRn", n);
				edit1.commit();
				HashSet<Integer> set = new HashSet<Integer>();
				int clnum = 1;
				for (int i = 1; i <= n; ++i) {
					Editor edit = msp[index - 1].edit();
					// tn号、整流器编号
					int tempTn = getIntofaByte(useful[10 + 12 * (i - 1) + 1]);
					// 上面整流器所对应的回路号
					// int tempCn = BCDtoInt(useful[10 + 12 * (i - 1) + 3]);
					int tempCn = useful[10 + 12 * (i - 1) + 3];
					// 如果是新的回路，则记录下
					if (!set.contains(tempCn)) {
						set.add(tempCn);
						edit.putInt("CL" + (clnum++), tempCn);// 第i回路对应的编号???????????
					}
					edit.putInt("Tn" + i, tempTn);
					edit.putInt("CPR" + tempTn, tempCn);
					if (i == n)
						edit.putInt("CLn", clnum - 1);
					edit.commit();
				}
			}
		}
//		else{
//			Editor edit = msp[index - 1].edit();
//			edit.clear();
//			edit.commit();
//		}
		misave[index - 1] = false;
		
	}

	private void task3(int index) {
		CPRbean[] cpb = cpb1;
		if (index == 2) {
			cpb = cpb2;
		} else if (index == 3) {
			cpb = cpb3;
		} else if (index == 4) {
			cpb = cpb4;
		} else if (index == 5) {
			cpb = cpb5;
		} else if (index == 6) {
			cpb = cpb6;
		}
		SendAndReceive3 sr = SendAndReceive3.getinstance(index, mip[index - 1],
				mport[index - 1], mremote[index - 1],mlogic[index - 1]);
		byte[] remess = sr.sendReceive(index, MyTools.CPRAllframe.clone(),
				mlogic[index - 1]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null) {
				//清除标记
				for(int i=0;i<(LTOTAL << 4);++i){
					cpb[i].setFill(false);
				}
				// 整流器个数
				int n = getIntofaByte(useful[10]);
				//记录每个回路对应有多少个CPR  以方便计算每个CPR的排位
				SparseIntArray sa = new SparseIntArray();
				for (int j = 0; j < n; ++j) {
					// 回路号
					int numcl = (int) useful[10 + 12 * j + 3] - 1;
					// 计算每个设备号所对应的排位
					// int temp = getIntofaByte(useful[10 + 12 * j +
					// 1])-1;//获取CPR编号
					int temp = sa.get(numcl, 0);//获取当前回路的CPR个数
					sa.put(numcl, temp + 1);//个数+1
					temp += numcl * 16;

					cpb[temp].setDevno(getIntofaByte(useful[10 + 12 * j + 1]));
					cpb[temp].setFill(true);
					// 获取电压
					cpb[temp].setMv(BCDtoVI(useful[10 + 12 * j + 4],
							useful[10 + 12 * j + 5]));
					// 获取电流
					cpb[temp].setMi(BCDtoVI(useful[10 + 12 * j + 6],
							useful[10 + 12 * j + 7]));
					// 获取温度
					cpb[temp].setMt(BCDtoTemper(useful[10 + 12 * j + 8]));
					// 获取状态
					cpb[temp].setMstate(BCDtoState(useful[10 + 12 * j + 11],
							useful[10 + 12 * j + 12]));
					// 获取状态字，用于异常类型判断
					cpb[temp].setMsatateint(BCD2toInt(useful[10 + 12 * j + 11],
							useful[10 + 12 * j + 12]));
				}
				sa = null;
			}
		} else {
			for (int j = 0; j < 256; ++j) {
				cpb[j] = new CPRbean();
			}
		}
	}
	
	private void heartbeat(int index) {
		// 构造心跳包
		byte[] mess = MyTools.beatframe.clone();
		SendAndReceive3 sr = SendAndReceive3.getinstance(index, mip[index-1],
				mport[index-1], mremote[index-1], mlogic[index-1]);
		byte[] remess = sr.sendReceive(index, mess, mlogic[index-1]);
		if (remess != null && remess.length > 10) {

		}
	}

	public class MyBinder extends Binder {

		public ConnectService getService() {
			return ConnectService.this;
		}

	}

	// 以下为本service为前台activity提供的数据获取接口
	public CLbean[] getCLbean() {
		getCurCLbean();
		return clb;
	}

	public CPRbean[] getCPRbean(int CPCno) {
		CPRbean[][] temp = new CPRbean[6][LTOTAL << 4];
		temp[0] = cpb1;
		temp[1] = cpb2;
		temp[2] = cpb3;
		temp[3] = cpb4;
		temp[4] = cpb5;
		temp[5] = cpb6;
		return temp[CPCno];
	}

	public SubCLbean[] getSubClbean() {
		SubCLbean[] temp = { scb1, scb2, scb3, scb4, scb5, scb6 };
		return temp;
	}

	public byte[] getSubclOffon() {
		if (subcloffon == null) {
			for (int i = 0; i < LTOTAL * CTOTAL; ++i) {
				subcloffon[i] = 0x00;
			}
		}
		return subcloffon;
	}

	public void getCurCLbean() {
		CPRbean[][] temp = new CPRbean[6][LTOTAL << 4];
		temp[0] = cpb1;
		temp[1] = cpb2;
		temp[2] = cpb3;
		temp[3] = cpb4;
		temp[4] = cpb5;
		temp[5] = cpb6;
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < LTOTAL; ++j) {
				double mv=0,mi=0;//电压、电流
				int mt=0;//温度
				for(int k=0;k<16;++k){
					mv=mv>temp[i][(j<<4) + k].getMv()?mv:temp[i][(j<<4) + k].getMv();
					mt=mt>temp[i][(j<<4) + k].getMt()?mt:temp[i][(j<<4) + k].getMt();
					mi+=temp[i][(j<<4) + k].getMi();
				}
				clb[16 * i + j].setMv(mv);
				clb[16 * i + j].setMi(mi);
				clb[16 * i + j].setMt(mt);
				// 状态判断
				int k = 0;
				for (; k < 16; ++k) {
					if (temp[i][(j<<4) + k].isFill() && 
							0!=temp[i][(j<<4) + k].getMsatateint()) {
						break;
					}
				}
				if (k == 16)
					clb[(i << 4) + j].setMstate(CPRbean.mNORMAL);
				else
					clb[(i << 4) + j].setMstate(CPRbean.mABNORMAL);
			}
		}
	}

	public double[] getCPCVI() {
		double[] CPCVI = new double[24];
		getCurCLbean();
		for (int i = 0; i < 6; ++i) {
			CPCVI[(i<<2)] = maxVT(i, 1);
			CPCVI[(i<<2) + 1] = totalI(i);
			CPCVI[(i<<2) + 2] = getCPCState(i);
			CPCVI[(i<<2) + 3] = maxVT(i, 4);
		}
		return CPCVI;
	}

	/**
	 * 求16个回路的状态
	 * @param CPCno
	 * @return 1为abnormal，3为normal
	 */
	private double getCPCState(int CPCno) {
		int i = 0;
		for (; i < 16; ++i) {
			if (CPRbean.mABNORMAL.equals(clb[16 * CPCno + i].getMstate())) {
				break;
			}
		}
		if (i == 16)
			return 3.0;
		else
			return 1.0;
	}

	/**
	 * 求16个回路的最大电压值或最大温度
	 * @param CPCno CPC编号0-3
	 * @type 1求电压，4求温度
	 * @return
	 */
	private double maxVT(int CPCno, int type) {
		if (type == 1) {
			double res = clb[16 * CPCno].getMv();
			for (int i = 1; i < 16; ++i) {
				res = res > clb[16 * CPCno + i].getMv() ? res : clb[16 * CPCno
						+ i].getMv();
			}
			return res;
		} else {
			double res = clb[16 * CPCno].getMt();
			for (int i = 1; i < 16; ++i) {
				res = res > clb[16 * CPCno + i].getMt() ? res : clb[16 * CPCno
						+ i].getMt();
			}
			return res;
		}
	}

	/**
	 * 求16个回路的电流之和
	 * @param CPCno  CPC编号0-3
	 * @return
	 */
	private double totalI(int CPCno) {
		double res = 0;
		for (int i = 0; i < 16; ++i) {
			res += clb[16 * CPCno + i].getMi();
		}
		return res;
	}

	/**
	 * 13-20位代表子回路的开关，将其合并为一个字节：高位到低位对应13-20 注意左高右低
	 * @param a  8-15位
	 * @param b  16-23位
	 * @return
	 */
	private byte getoffonbyte(byte a, byte b) {
		a = (byte) ((a >>> 5) & (0x07));
		b = (byte) (b << 3);
		return (byte) (a | b);
	}

	/**
	 * 求最大值
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return max value
	 */
	public double max(double a, double b, double c, double d) {
		double res = a;
		if (b > res)
			res = b;
		if (c > res)
			res = c;
		if (d > res)
			res = d;
		return res;

	}

	/**
	 * 求最大值
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return max value
	 */
	public int max(int a, int b, int c, int d) {
		int res = a;
		if (b > res)
			res = b;
		if (c > res)
			res = c;
		if (d > res)
			res = d;
		return res;

	}

	/**
	 * 返回十进制对应的数值
	 * @param b hex值
	 * @return b对应的十进制数值
	 */
	private int getIntofaByte(byte b) {
		int res = 0;
		if (b < 0) {
			res += (1 << 7);
		}
		res += (int) (b & 0x7F);
		return res;
	}

	/**
	 * 返回十进制对应的数值
	 * @param a   高位hex
	 * @param b   低位hex
	 * @return ab对应的十进制
	 */
	public int getIntof2Byte(byte a, byte b) {
		int len = 0;
		int temp = 0;
		if (a < 0) { // a为高位
			len += (1 << 15);
		}
		temp = (a & 0x7F);
		len += temp << 8;
		if (b < 0) {
			len += (1 << 7);
		}
		temp = (b & 0x7F);
		len += temp;
		return len;
	}

	static class MyHandler extends Handler {

		WeakReference<Service> mServiceReference;

		MyHandler(Service service) {
			mServiceReference = new WeakReference<Service>(service);
		}
	}
}
