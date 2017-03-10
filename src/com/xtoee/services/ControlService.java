package com.xtoee.services;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.xtoee.main.XtoeeApp;
import com.xtoee.tools.MyTools;
import com.xtoee.tools.ProtocolData;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ControlService extends Service {

	private final int BEATSTD=24;
	private int curCPC;// 当前CPC号
	private String[] mip = new String[6];
	private int[] mport = new int[6];
	private boolean[] maddr = new boolean[6];
	private boolean[] mremote = new boolean[6];
	private int[] mlogic = new int[6];
	private SharedPreferences sp;// 地址读写SharedPreferences
	private boolean isenable = false;// 是否有控制内容
	private boolean isread = false;
	private boolean isChangeOffon = false;// 子回路中通断是否有改变，场景控制中开关的通断传回
	private boolean needChangeOffon = false;// 是否需要重新查询（隔一段时间查询一次）
	private int[] needBeatcount = new int[6];

	private byte controlByte;// 报文控制码
	private byte[] usefulData;// 报文数据

	byte[] jiaoyanbianma = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x11, 0x12,
			0x13 };// 错误编码
	String[] bianmahanyi = { "正确", "命令没有返回", "设置内容非法", "密码权限不足", "无此项数据",
			"命令时间失效", "目标地址不存在", "发送失败", "短消息帧太长" };// 错误编码含义

	private byte[] subcloffon = new byte[96];// 子回路通断

	/**
	 * handleMessage任务处理
	 * 
	 */
	MyHandler handler = new MyHandler(ControlService.this) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				needChangeOffon = true;
				// 心跳控制
				for (int i = 0; i < 6; ++i) {
					if (maddr[i]) {
						if (needBeatcount[i]++ > BEATSTD) {
							needBeatcount[i] = 0;
							new Thread(new HearBeatRun(i + 1)).start();
						}
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
		InitIP();// 初始化通讯地址
		InitBeatTime();
		mTimer.schedule(mTimerTask, 5000, 5000);
	}

	private void InitBeatTime() {
		for (int i = 0; i < 6; ++i) {
			needBeatcount[i] = 10;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bund = intent.getExtras();
//		int stop = intent.getFlags();
//		System.out.println("stop is " + stop);
		if (bund != null) {
			// bundle非空，有控制任务，使能，设置CPC号，生成控制报文
			isenable = true;
			isChangeOffon = bund.getBoolean("isChangeOffon", false);
			isread = bund.getBoolean("isread", false);
			isenable &= (!isread);
			curCPC = bund.getInt("curCPC");
			setframe(bund.getByte("controlByte"),
					bund.getByteArray("usefulData"));
		}
		if (isenable) {
			// 根据CPC号选择启动线程
			switch (curCPC) {
			case 1:
				new CPC1Control().start();
				break;

			case 2:
				new CPC2Control().start();
				break;

			case 3:
				new CPC3Control().start();
				break;

			case 4:
				new CPC4Control().start();
				break;

			case 5:
				new CPC5Control().start();
				break;

			case 6:
				new CPC6Control().start();
				break;

			default:
				break;
			}
		} else if (isread) {
			// 根据CPC号选择启动线程
			new ReadTask(curCPC).start();
			isread = false;
		}
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		while (!mTimerTask.cancel()) {
			mTimer.cancel();
		}
		// 销毁handle
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * 初始化通讯地址
	 */
	private void InitIP() {
		sp = getSharedPreferences("addr", Context.MODE_PRIVATE);
		for (int i = 0; i < 6; ++i) {
			maddr[i] = sp.getBoolean("addr" + (i + 1), false);
			mip[i] = sp.getString("ip" + (i + 1), "");
			mport[i] = sp.getInt("port" + (i + 1), 0);
			mremote[i] = sp.getBoolean("remote" + (i + 1), false);
			mlogic[i] = sp.getInt("logicAddr" + (i + 1), 4);
		}
	}

	public class CPC1Control extends Thread {
		public void run() {
			if (maddr[0]) {
//				needBeatcount[0] = 0;
				mainTask(1);
				// 如果子回路的通断有改变，查询并记录，以便在通断界面有所显示
				// 如果需要查询
				if (isChangeOffon | needChangeOffon) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task0202(1);
				}
			}
			isenable = false;
		}
	}

	public class CPC2Control extends Thread {
		public void run() {
			if (maddr[1]) {
//				needBeatcount[1] = 0;
				mainTask(2);
				if (isChangeOffon | needChangeOffon) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task0202(2);
				}
			}
			isenable = false;
		}
	}

	public class CPC3Control extends Thread {
		public void run() {
			if (maddr[2]) {
//				needBeatcount[2] = 0;
				mainTask(3);
				if (isChangeOffon | needChangeOffon) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task0202(3);
				}
			}
			isenable = false;
		}
	}

	public class CPC4Control extends Thread {
		public void run() {
			if (maddr[3]) {
//				needBeatcount[3] = 0;
				mainTask(4);
				if (isChangeOffon | needChangeOffon) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task0202(4);
				}
			}
			isenable = false;
		}
	}

	public class CPC5Control extends Thread {
		public void run() {
			if (maddr[4]) {
//				needBeatcount[4] = 0;
				mainTask(5);
				if (isChangeOffon | needChangeOffon) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task0202(5);
				}
			}
			isenable = false;
		}
	}

	public class CPC6Control extends Thread {
		public void run() {
			if (maddr[5]) {
//				needBeatcount[5] = 0;
				mainTask(6);
				if (isChangeOffon | needChangeOffon) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task0202(6);
				}
			}
			isenable = false;
		}
	}

	public class ReadTask extends Thread {
		private int cpc = 1;

		public ReadTask(int cpc) {
			this.cpc = cpc;
		}

		public void run() {
			Intent intent = new Intent("action.Control_BROADCAST");
			if(maddr[cpc-1]) {
				intent.putExtra("msg", "抄读任务较多，请稍等！");
				intent.putExtra("updata", true);
				sendBroadcast(intent);
				boolean isconnect = true;
				for (int i = 1; i < 13; ++i) {
					isconnect &= taskRead(cpc, i);
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (isconnect)
					intent.putExtra("msg", "任务读取完毕");
				else
					intent.putExtra("msg", "任务读取失败");
			}else{
				intent.putExtra("msg", "当前CPC地址无效");
			}
			intent.putExtra("updata", true);
			sendBroadcast(intent);
		}
	}

	public class HearBeatRun implements Runnable {
		private int index;

		public HearBeatRun(int index) {
			this.index = index;
		}

		@Override
		public void run() {
			heartbeat(index);
		}
	}

	private void heartbeat(int index) {
		// 构造心跳包
		byte[] mess = MyTools.beatframe.clone();
		--index;
		SendAndReceive3 sr = SendAndReceive3.getinstance(index + 1, mip[index],
				mport[index], mremote[index], mlogic[index]);
		byte[] remess = sr.sendReceive(index + 1, mess, mlogic[index]);
		if (remess != null && remess.length > 10) {

		}
	}

	/**
	 * 操作控制报文
	 * 
	 * @param index
	 */
	private void mainTask(int index) {
		--index;
		// 生成控制报文
		ProtocolData pdsend = new ProtocolData(controlByte, usefulData);
		// 发送并接收
		SendAndReceive3 sr = SendAndReceive3.getinstance(index + 1, mip[index],
				mport[index], mremote[index],mlogic[index]);
		byte[] remess = sr.sendReceive(index + 1, pdsend.getFrame(),
				mlogic[index]);
		if (remess != null && remess.length > 13) {
			// 是否有有用报文
			ProtocolData reseiveframe = new ProtocolData();
			// 获取有用信息
			byte[] useful = reseiveframe.getUsefulData(remess);
			if (useful != null)
				analysis(useful);
		}
	}

	private void task0202(int index) {
		--index;
		SendAndReceive3 sr = SendAndReceive3.getinstance(index + 1, mip[index],
				mport[index], mremote[index],mlogic[index]);
		byte[] remess = sr.sendReceive(index + 1, MyTools.subclframe.clone(),
				mlogic[index]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null) {
				int n = getIntofaByte(useful[10]);
				for (int i = 1; i <= n; ++i) {
					if (useful[10 * n + 2] == 0x02) {
						// subcloffon对应的下标：16*CPC号+回路号
						int ti = 16 * index + useful[10 * n + 7] - 1;
						subcloffon[ti] = getoffonbyte(useful[10 * n + 4],
								useful[10 * n + 5]);
					}
				}
			}
			isChangeOffon = false;
			needChangeOffon = false;
			SharedPreferences spison = getSharedPreferences("offon",
					MODE_PRIVATE);
			Editor edit = spison.edit();
			for (int i = 1; i < 17; ++i) {
				edit.putInt("ison" + curCPC + i / 10 + i % 10, subcloffon[16
						* curCPC + i - 17] & 0xff);
			}
			edit.commit();
		}
	}

	/**
	 * 单个任务抄读
	 * @param cpc CPCno1-6
	 * @param no  读取的任务号1-12
	 */
	private boolean taskRead(int cpc, int no) {
		// 报文的标识编码
		byte[] readframe = MyTools.readframe.clone();
		byte tmp = readframe[19];
		tmp = (byte) (tmp + no);
		readframe[19] = tmp;
		// 校验码
		tmp = readframe[21];
		tmp = (byte) (tmp + no);
		readframe[21] = tmp;
		SendAndReceive3 sr = SendAndReceive3.getinstance(cpc, mip[cpc - 1],
				mport[cpc - 1], mremote[cpc - 1], mlogic[cpc - 1]);
		byte[] remess = sr.sendReceive(cpc, readframe, mlogic[cpc - 1]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null && useful.length > 10) {
				// sp命名：task1[1-12] -- task6[1-12]
				SharedPreferences sp = getSharedPreferences("task" + cpc + no,
						Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				// 任务使能
				if (useful[10] == 1)
					edit.putBoolean("enable", true);
				else
					edit.putBoolean("enable", false);
				// 优先级
				edit.putInt("level", useful[12]);
				// 起始时间
				Calendar cal = Calendar.getInstance();
				cal.set(BCDtoInt(useful[13]) * 100 + BCDtoInt(useful[14]),
						BCDtoInt(useful[15]) - 1, BCDtoInt(useful[16]),
						BCDtoInt(useful[17]), BCDtoInt(useful[18]));
				edit.putLong("begintime", cal.getTimeInMillis());
				// 结束时间
				cal.set(BCDtoInt(useful[19]) * 100 + BCDtoInt(useful[20]),
						BCDtoInt(useful[21]) - 1, BCDtoInt(useful[22]),
						BCDtoInt(useful[23]), BCDtoInt(useful[24]));
				edit.putLong("endtime", cal.getTimeInMillis());
				// 循环方式：默认为周方式
				byte b = useful[26];
				if ((b & (0x01)) > 0)
					b |= 0x80;
				edit.putInt("loopway", b >>> 1);
				// 执行方案数，即 动作数
				int num = useful[32];
				edit.putInt("actionNum", num);
				// 方案内容
				int templen = 32;
				for (int i = 1; i <= num; ++i) {
					// actiontime
					edit.putInt("actiontime" + i, BCDtoInt(useful[templen + 1])
							* 100 + BCDtoInt(useful[templen + 2]));
					// 回路数
					int clnum = useful[templen + 3];
					// TaskBean的回路
					int cls = 0;
					for (int j = 0; j < clnum; ++j) {
						// 设备号，1-16
						int tn = useful[templen + 7 * j + 4] - 1;
						cls |= (1 << tn);
						// actionact
						if (j == 0) {
							// 电压值
							int actionact = BCDtoInt(useful[templen + 7]) * 100
									+ BCDtoInt(useful[templen + 8]);
							// 电压值对应的dim值1-11代表0%-100%
							actionact = getDim(actionact / 10, tn, cpc);
//							actionact = getDim(actionact / 10, tn + 1, cpc);
							if (useful[templen + 6] == (byte) 0x2D)
								actionact = 0;
							edit.putInt("actionact" + i, actionact);
						}
					}
					if (i == 1)
						edit.putInt("cls", cls);
					templen += (3 + 7 * clnum);
				}
				for (int i = num + 1; i <= 12; ++i) {
					edit.putInt("actiontime" + i, 10000);
					edit.putInt("actionact" + i, 10000);
				}
				edit.commit();
			} else {
				SharedPreferences sp = getSharedPreferences("task" + cpc + no,
						Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.clear();
				edit.commit();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取DIm值
	 * @param v      电压值
	 * @param clno   回路号
	 * @param curdim Dim配置表值
	 * @return Dim百分值标号，1-11分别表示0%-100%
	 */
	private int getDim(double v, int clno, int cpcno) {
		if(clno<0 || clno>15) {
			Log.e("getDim中 回路号有误!", "getDim中 回路号有误! clno="+clno);
			clno=clno>8?15:0;
		}
		int[][] curdim = ((XtoeeApp) getApplication()).getDimDate(cpcno);
		if (v < curdim[clno][0])
			return 1;
		for (int i = 1; i < 11; ++i) {
			if (v < curdim[clno][i]) {
				if ((v - curdim[clno][i - 1]) > (curdim[clno][i] - v)) {
					return i + 1;
				} else {
					return i;
				}
			}
		}
		return 11;
	}

	/**
	 * 设置报文
	 * @param controlByte  控制码
	 * @param usefulData   报文有用部分
	 */
	public void setframe(byte controlByte, byte[] usefulData) {
		this.controlByte = controlByte;
		this.usefulData = null;
		this.usefulData = usefulData;
	}

	/**
	 * 回复报文解析
	 * @param mess  报文内容
	 */
	public void analysis(byte[] mess) {
		// 解析回复帧
		String msg = "发送成功";
		int len = bianmahanyi.length;
		byte result = mess[mess.length - 1];
		if (result != 0x00) {
			for (int i = 1; i < len; ++i) {
				if (result == jiaoyanbianma[i]) {
					msg = bianmahanyi[i];
					break;
				}
			}
		}
		// 广播解析结果
		Intent intent = new Intent("action.Control_BROADCAST");
		intent.putExtra("msg", msg);
		sendBroadcast(intent);
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
	 * 求hex对应的int值，如0x50—>80,0x80->128
	 * @param b
	 * @return
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
	 * 求BCD码对应的int值， 如0x50->50,0x80->80
	 * @param a
	 * @return
	 */
	private int BCDtoInt(byte a) {
		int b = a & (0x0F);
		int c = (a & (0xF0)) >> 4;
		return c * 10 + b;
	}

	static class MyHandler extends Handler {

		WeakReference<Service> mServiceReference;

		MyHandler(Service service) {
			mServiceReference = new WeakReference<Service>(service);
		}
	}
}
