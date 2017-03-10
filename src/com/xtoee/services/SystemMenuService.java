package com.xtoee.services;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashSet;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class SystemMenuService extends Service {

	private final int BEATSTD=24;
	private SharedPreferences sp;
	private SharedPreferences[] msp = new SharedPreferences[6];
	private String[] mip = new String[6];
	private int[] mport = new int[6];
	private boolean[] maddr = new boolean[6];
	private boolean[] mremote = new boolean[6];
	private int[] mlogic = new int[6];

	//ʱ��У��
	byte[] timeframe = { 0x68, 0x50, 0x05, 0x00, 0x00, 0x1E, 0x00, 0x68, 0x08,
			0x0E, 0x00, 0x00, 0x00, 0x11, 0x11, 0x11, 0x11, 0x30, (byte) 0x80,
			0x47, 0x21, 0x20, 0x30, 0x07, 0x15, 0x21, 0x16 };
	// У��绰����
	byte[] phoneframe = { 0x68, 0x50, 0x05, 0x00, 0x00, 0x1E, 0x00, 0x68, 0x08,
			0x0E, 0x00, 0x00, 0x00, 0x11, 0x11, 0x11, 0x11, 0x0D, 0x01, 0x34,
			0x23, 0x22, 0x50, (byte) 0x89, 0x01, (byte) 0xFE, 0x16 };
	// ��־��ѯ����
	byte[] errorecord = { 0x68, 0x50, 0x05, 0x00, 0x00, (byte) 0x9E, 0x00,
			0x68, 0x19, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff,
			(byte) 0xE1, 0x16 };

	byte[] beatframe = { 0x68, 0x50, 0x05, 0x00, 0x00, 0x1E, 0x00, 0x68,
			(byte) 0xA4, 0x00, 0x0, (byte) 0xE7, 0x16 };
	
	private String[] typeOferror = { "��������", "��������", "�ֶ�����", "�ֶ�����", "", "",
			"", "", "�ֶ�����", "", "�ֶ�����", "", "", "�ֶ�����", "", "CPRͨѶ", "", "",
			"", "", "", "�������", "�������" };
	// �澯�����Ӧ����������
	private String[] detailOfError = { "ɾ������", "���ص���", "����R#x ������x%",
			"����L#x ������x%", "", "", "", "", "����R#xx", "", "����L#xx", "", "",
			"�����ӻ�·xx", "", "R#xx", "", "", "", "", "", "����L#x ������x%",
			"����x���ƻ�·L#xx" };
	// private int[][] dim1 = new int[4][11];
	// private int[][] dim2 = new int[4][11];
	// private int[][] dim3 = new int[4][11];
	// private int[][] dim4 = new int[4][11];
	private int[] needBeatcount = new int[6];

	private int curCPC;
	private int taskNo;// ����ţ�1Ϊ��ѯ��2ΪУʱ��3Ϊ�޸ĺ���,4Ϊ��־��ѯ
	private byte[] subcloffon = new byte[64];// �ӻ�·ͨ��

	/**
	 * handleMessage������
	 * 
	 */
	MyHandler handler = new MyHandler(SystemMenuService.this) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				// ��������
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
	// ��ʱ��
	private Timer mTimer = new Timer(true);
	// ������
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
		// CPC��ϢSharedPreferences
		for (int i = 0; i < 6; ++i) {
			msp[i] = getSharedPreferences("CPC" + (i + 1) + "Info",
					MODE_PRIVATE);
		}
		sp = getSharedPreferences("addr", Context.MODE_PRIVATE);
		InitIP();
		InitBeatTime();
		mTimer.schedule(mTimerTask, 5000, 5000);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		taskNo = intent.getIntExtra("taskNo", 0);
		curCPC = intent.getIntExtra("CPCno", 0);
		if (taskNo == 1) {
			InitIP();
			// ��ѯ�����Ȳ����ɵ���Ϣ
			System.out.println("��ѯ�����Ȳ����ɵ���Ϣ!");
			SendAndReceive3 sr = SendAndReceive3.getinstance(1, mip[0],
					mport[0], false,mlogic[0]);
			sr.resetInstance();
			System.out.println("������Ϣ���!");
			Editor edit;
			for (int i = 0; i < 6; ++i) {
				edit = msp[i].edit();
				edit.clear();
				edit.commit();
			}
			// ����ѯ�
			new GetCPC1Info().start();
			new GetCPC2Info().start();
			new GetCPC3Info().start();
			new GetCPC4Info().start();
			new GetCPC5Info().start();
			new GetCPC6Info().start();
		} else if (taskNo == 2) {
			// set timeframe
			// ����У׼ʱ��Ϊ��ǰʱ��
			timeframe = MyTools.timeframe.clone();
			Calendar cal = Calendar.getInstance();
			timeframe[24] = inttoBCD(cal.get(Calendar.YEAR) % 100);
			timeframe[23] = inttoBCD(cal.get(Calendar.MONTH) + 1);
			timeframe[22] = inttoBCD(cal.get(Calendar.DAY_OF_MONTH));
			timeframe[21] = inttoBCD(cal.get(Calendar.HOUR_OF_DAY));
			timeframe[20] = inttoBCD(cal.get(Calendar.MINUTE));
			timeframe[19] = inttoBCD(cal.get(Calendar.SECOND));
			// �����б䣬���¼���У����
			byte cs = 0;
			for (int i = 0; i < 25; ++i) {
				cs = (byte) (cs + timeframe[i]);
			}
			timeframe[25] = cs;
			// ʱ��У׼
			new Task23().start();
		} else if (taskNo == 3) {
			// set phoneframe
			// ��ȡ�绰
			SharedPreferences sp = getSharedPreferences("config",
					Context.MODE_PRIVATE);
//			String phone = sp.getString("phone", "18959222334") + "0";
			String phone = "0"+sp.getString("phone", "18959222334");
			
			phoneframe = MyTools.phoneframe.clone();
			// ���ñ��ĵ绰����
			for (int i = 0; i < 6; ++i) {
				int a = Integer.parseInt(phone.substring(2 * i, 2 * i + 2));
//				// phoneframe[24-i] = inttoBCD(a);
//				if (i == 0)
//					phoneframe[19 + i] = inttoBCD(a / 10);
//				else
//					phoneframe[19 + i] = inttoBCD(a);
				 phoneframe[19+i] = inttoBCD(a);
			}
			phoneframe[19]|=(byte)(0xA0);
			// ����У����
			byte cs = 0;
			for (int i = 0; i < 25; ++i) {
				cs = (byte) (cs + phoneframe[i]);
			}
			phoneframe[25] = cs;
			new Task23().start();
		} else if (taskNo == 4) {
			new ErrerofCPC1().start();
			new ErrerofCPC2().start();
			new ErrerofCPC3().start();
			new ErrerofCPC4().start();
			new ErrerofCPC5().start();
			new ErrerofCPC6().start();
		}
		return Service.START_NOT_STICKY;
		// return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		while (!mTimerTask.cancel()) {
			mTimer.cancel();
		}
		// ����handle
		handler.removeCallbacksAndMessages(null);
	}

	private void InitBeatTime() {
		for (int i = 0; i < 6; ++i) {
			needBeatcount[i] = 10;
		}
	}

	public class GetCPC1Info extends Thread {
		public void run() {
			if (maddr[0]) {
//				needBeatcount[0] = 0;
				task1_1(1);
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				task1_2(1);
			}
		}
	}

	public class GetCPC2Info extends Thread {
		public void run() {
			if (maddr[1]) {
//				needBeatcount[1] = 0;
				task1_1(2);
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				task1_2(2);
			}
		}
	}

	public class GetCPC3Info extends Thread {
		public void run() {
			if (maddr[2]) {
//				needBeatcount[2] = 0;
				task1_1(3);
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				task1_2(3);
			}
		}
	}

	public class GetCPC4Info extends Thread {
		public void run() {
			if (maddr[3]) {
//				needBeatcount[3] = 0;
				task1_1(4);
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				task1_2(4);
			}
		}
	}

	public class GetCPC5Info extends Thread {
		public void run() {
			if (maddr[4]) {
//				needBeatcount[4] = 0;
				task1_1(5);
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				task1_2(5);
			}
		}
	}

	public class GetCPC6Info extends Thread {
		public void run() {
			if (maddr[5]) {
//				needBeatcount[5] = 0;
				task1_1(6);
				try {
					sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				task1_2(6);
			}
		}
	}

	public class Task23 extends Thread {
		public void run() {
			if (maddr[curCPC - 1]) {
//				needBeatcount[curCPC - 1] = 0;
				SendAndReceive3 sr = SendAndReceive3
						.getinstance(curCPC, mip[curCPC - 1],
								mport[curCPC - 1], mremote[curCPC - 1], mlogic[curCPC - 1]);
				if (taskNo == 2) {
					sr.sendReceive(curCPC, timeframe, mlogic[curCPC - 1]);
				} else if (taskNo == 3) {
					sr.sendReceive(curCPC, phoneframe, mlogic[curCPC - 1]);
				}
			}
		}
	}

	public class Task23ofCPC1 extends Thread {
		public void run() {
			if (maddr[0]) {
//				needBeatcount[0] = 0;
				SendAndReceive3 sr = SendAndReceive3.getinstance(1, mip[0],
						mport[0], mremote[0], mlogic[0]);
				if (taskNo == 2) {
					sr.sendReceive(1, timeframe, mlogic[0]);
				} else if (taskNo == 3) {
					sr.sendReceive(1, phoneframe, mlogic[0]);
				}
			}
		}
	}

	public class Task23ofCPC2 extends Thread {
		public void run() {
			if (maddr[1]) {
//				needBeatcount[0] = 0;
				SendAndReceive3 sr = SendAndReceive3.getinstance(2, mip[1],
						mport[1], mremote[1], mlogic[1]);
				if (taskNo == 2) {
					sr.sendReceive(2, timeframe, mlogic[1]);
				} else if (taskNo == 3) {
					sr.sendReceive(2, phoneframe, mlogic[1]);
				}
			}
		}
	}

	public class Task23ofCPC3 extends Thread {
		public void run() {
			if (maddr[2]) {
//				needBeatcount[2] = 0;
				SendAndReceive3 sr = SendAndReceive3.getinstance(3, mip[2],
						mport[2], mremote[2], mlogic[2]);
				if (taskNo == 2) {
					sr.sendReceive(3, timeframe, mlogic[2]);
				} else if (taskNo == 3) {
					sr.sendReceive(3, phoneframe, mlogic[2]);
				}
			}
		}
	}

	public class Task23ofCPC4 extends Thread {
		public void run() {
			if (maddr[3]) {
//				needBeatcount[3] = 0;
				SendAndReceive3 sr = SendAndReceive3.getinstance(4, mip[3],
						mport[3], mremote[3], mlogic[3]);
				if (taskNo == 2) {
					sr.sendReceive(4, timeframe, mlogic[3]);
				} else if (taskNo == 3) {
					sr.sendReceive(4, phoneframe, mlogic[3]);
				}
			}
		}
	}

	public class ErrerofCPC1 extends Thread {
		public void run() {
			if (maddr[0]) {
				task4(1);
			}
		}
	}

	public class ErrerofCPC2 extends Thread {
		public void run() {
			if (maddr[1]) {
				task4(2);
			}
		}
	}

	public class ErrerofCPC3 extends Thread {
		public void run() {
			if (maddr[2]) {
				task4(3);
			}
		}
	}

	public class ErrerofCPC4 extends Thread {
		public void run() {
			if (maddr[3]) {
				task4(4);
			}
		}
	}

	public class ErrerofCPC5 extends Thread {
		public void run() {
			if (maddr[4]) {
				task4(5);
			}
		}
	}

	public class ErrerofCPC6 extends Thread {
		public void run() {
			if (maddr[5]) {
				task4(6);
			}
		}
	}

	/**
	 * ����1.1:040f��ѯ��·������Ϣ
	 * @param index
	 */
	private void task1_1(int index) {
		SendAndReceive3 sr = SendAndReceive3.getinstance(index, mip[index - 1],
				mport[index - 1], mremote[index - 1], mlogic[index - 1]);
		byte[] remess = sr.sendReceive(index, MyTools.CPRAllframe.clone(),
				mlogic[index - 1]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null) {
				// ����������
				int n = getIntofaByte(useful[10]);
				Editor edit1 = msp[index - 1].edit();
				edit1.putInt("CPRn", n);
				edit1.commit();
				HashSet<Integer> set = new HashSet<Integer>();
				int clnum = 1;
				for (int i = 1; i <= n; ++i) {
					Editor edit = msp[index - 1].edit();
					// tn�š����������
					int tempTn = getIntof2Byte(useful[10 + 12 * (i - 1) + 2],
							useful[10 + 12 * (i - 1) + 1]);
					// ��������������Ӧ�Ļ�·��
					// int tempCn = BCDtoInt(useful[10 + 12 * (i - 1) + 3]);
					int tempCn = useful[10 + 12 * (i - 1) + 3];
					// ������µĻ�·�����¼��
					if (!set.contains(tempCn)) {
						set.add(tempCn);
						edit.putInt("CL" + (clnum++), tempCn);// ��i��·��Ӧ�ı��???????????
					}
					edit.putInt("Tn" + i, tempTn);// ��i��CPR��Ӧ�ı��
					edit.putInt("CPR" + tempTn, tempCn);// ���ΪtempTn��CPR�����Ļ�·
					if (i == n)
						edit.putInt("CLn", clnum - 1);
					edit.commit();
				}
			}
		} else {
//			boolean login = sr.getLogin(index - 1);
//			if (!login) {
//
//			}
		}
	}

	/**
	 * ����1.1:0202��ѯ�ӻ�·������Ϣ
	 * @param index
	 */
	private void task1_2(int index) {
		SendAndReceive3 sr = SendAndReceive3.getinstance(index, mip[index - 1],
				mport[index - 1], mremote[index - 1], mlogic[index - 1]);
		byte[] remess = sr.sendReceive(index, MyTools.subclframe.clone(),
				mlogic[index - 1]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null) {
				int n = getIntofaByte(useful[10]);
				Editor edit1 = msp[index - 1].edit();
				// �ӻ�·����
				edit1.putInt("subcln", n);
				edit1.commit();
				for (int i = 1; i <= n; ++i) {
					int ti = useful[10 * n + 7];// �ӻ�·������·��
					if (useful[10 * n + 2] == 0x02) {
						// subcloffon��Ӧ���±꣺16*CPC��+��·��
						// int ti = 16 * (index - 1) + useful[10 * n + 7] -1;
						// �����ӻ�·ͨ��״̬
						subcloffon[ti - 1] = getoffonbyte(useful[10 * n + 4],
								useful[10 * n + 5]);
					}
					Editor edit = msp[index - 1].edit();
					int tempTn = getIntofaByte(useful[10 + 10 * (i - 1) + 1]);
					edit.putInt("subTn" + ti, tempTn);
					edit.commit();
				}
				// �洢����״̬
				SharedPreferences spison = getSharedPreferences("offon",
						MODE_PRIVATE);
				Editor edit = spison.edit();
				for (int i = 1; i < 17; ++i) {
					edit.putInt("ison" + (curCPC + 1) + i / 10 + i % 10,
							subcloffon[16 * curCPC + i - 1] & 0xff);
				}
				edit.commit();
			}
		}
	}

	/**
	 * ����4����ѯ��־
	 * @param index
	 */
	private void task4(int mindex) {
//		needBeatcount[mindex - 1] = 0;
		SendAndReceive3 sr = SendAndReceive3.getinstance(mindex,
				mip[mindex - 1], mport[mindex - 1], mremote[mindex - 1], mlogic[mindex - 1]);
		byte[] remess = sr.sendReceive(mindex, errorecord.clone(),
				mlogic[mindex - 1]);
		if (remess != null && remess.length > 13) {
			ProtocolData CPCdata = new ProtocolData();
			byte[] useful = CPCdata.getUsefulData(remess);
			if (useful != null) {
				SharedPreferences sp = getSharedPreferences("log" + mindex,
						MODE_PRIVATE);
				Editor edit = sp.edit();
				// ��־����
				int num = useful[0];
				int index = 0;
				if (num > 120)
					num = 120;
				edit.putInt("num", num);
				for (int i = 0; i < num; ++i) {
					// ʱ��
					String str = "20" + BCDtoInt(useful[index + 7]) + "/"
							+ BCDtoInt(useful[index + 8]) + "/"
							+ BCDtoInt(useful[index + 9]) + " "
							+ BCDtoInt(useful[index + 10]) + ":"
							+ BCDtoInt(useful[index + 11]);
					edit.putString("time" + i, str);
					// ����
					int type = getType(useful[index + 12], useful[index + 13]);
					if (useful[index + 12] == 0x15) {
						edit.putString("type" + i, "����#" + useful[index + 15]);
					} else if (type == 257) {
						edit.putString("type" + i, "�������#" + useful[index + 15]);
					} else if (type == 258) {
						edit.putString("type" + i, "�������#" + useful[index + 15]);
					} else if (type == 259) {
						edit.putString("type" + i, "�������#" + useful[index + 15]);
					} else if (type == 272) {
						edit.putString("type" + i, "��������#" + useful[index + 15]);
					} else if (type == 273) {
						edit.putString("type" + i, "��������#" + useful[index + 15]);
					} else {
						edit.putString("type" + i,
								typeOferror[getIntofaByte(useful[index + 12])]);
					}
					// ����
					int temp = useful[index + 14];
					// if(temp>5) temp-=5;//�ǵ�ɾ��
					byte[] tempbyte = new byte[9];
					System.arraycopy(useful, index + 15, tempbyte, 0, temp);
					edit.putString("detail" + i,
							getdeail(type, temp, tempbyte, mindex));
					for (int j = 0; j < temp; ++j) {

					}
					index += (14 + temp);
				}
				edit.commit();
			} else {
				SharedPreferences sp = getSharedPreferences("log" + mindex,
						MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putInt("num", 0);
				edit.commit();
			}
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
		// ����������
		byte[] mess = beatframe.clone();

		--index;
		SendAndReceive3 sr = SendAndReceive3.getinstance(index + 1, mip[index],
				mport[index], mremote[index], mlogic[index]);
		byte[] remess = sr.sendReceive(index + 1, mess, mlogic[index]);
		if (remess != null && remess.length > 10) {
		}
	}

	/**
	 * ��ȡDImֵ
	 * @param v       ��ѹֵ
	 * @param clno    ��·��
	 * @param curdim  Dim���ñ�ֵ
	 * @return Dim�ٷ�ֵ��ţ�0-10�ֱ��ʾ0%-100%
	 */
	private String getDim(double v, int clno, int cpcno) {
		try {
			int[][] curdim = ((XtoeeApp) getApplication()).getDimDate(cpcno);
			if (v < curdim[clno][0])
				return "0";
			for (int i = 1; i < 11; ++i) {
				if (v < curdim[clno][i]) {
					if ((v - curdim[clno][i - 1]) > (curdim[clno][i] - v)) {
						return i + "0";
					} else {
						if (i == 1)
							return "0";
						return (i - 1) + "0";
					}
				}
			}
			return "100";
		} catch (Exception e) {
			e.printStackTrace();
			return "110";
		}

	}

	/**
	 * ��ȡ�澯�ľ�������
	 * @param type  �澯����
	 * @param num   ���ݳ���
	 * @param mess  ��������
	 * @param cpcno 1-4
	 * @return
	 */
	private String getdeail(int type, int num, byte[] mess, int cpcno) {
		String str = null;
		if (type < 256)
			str = detailOfError[type];
		// DecimalFormat dcmFmt = new DecimalFormat("0.0");
		int hex;
		double vi;
		switch (type) {
		case 0:
		case 1:
			break;
		case 2:
			hex = mess[0];
			StringBuilder sb2 = new StringBuilder();
			sb2.append("0").append(cpcno).append(hex / 10).append(hex % 10);
			hex = mess[1];
			if (hex < 10) {
				sb2.append(0);
			}
			sb2.append(hex);
			str = str.replaceFirst("x", sb2.toString());
			vi = BCDtoVI(mess[2], mess[3]);
			str = str.replaceFirst("x", getDim(vi, hex - 1, cpcno));
			break;

		case 3:
			hex = mess[0];
			str = str.replaceFirst("x", "0" + cpcno + hex / 10 + hex % 10);
			vi = BCDtoVI(mess[1], mess[2]);
			str = str.replaceFirst("x", getDim(vi, hex - 1, cpcno));
			break;

		case 8:
			hex = mess[0];
			StringBuilder sb8 = new StringBuilder();
			sb8.append("0").append(cpcno).append(hex / 10).append(hex % 10);
			hex = mess[1];
			if (hex < 10) {
				sb8.append(0);
			}
			sb8.append(hex);
			str = str.replaceFirst("x", sb8.toString());
			hex = mess[2];
			if (hex == 1) {
				str = str.replaceFirst("x", "��");
			} else {
				str = str.replaceFirst("x", "�ر�");
			}
			break;

		case 10:
			hex = mess[0];
			str = str.replaceFirst("x", "0" + cpcno + hex / 10 + hex % 10);
			hex = mess[1];
			if (hex == 1) {
				str = str.replaceFirst("x", "��");
			} else {
				str = str.replaceFirst("x", "�ر�");
			}
			break;

		case 13:
			hex = mess[0];
			str = str.replaceFirst("x", hex + "");
			hex = mess[1];
			str = str.replaceFirst("x", "0" + cpcno + hex / 10 + hex % 10);
			hex = mess[2];
			if (hex == 1) {
				str = str.replaceFirst("x", "��");
			} else {
				str = str.replaceFirst("x", "�ر�");
			}
			break;

		case 22:
			hex = mess[0];
			str = str.replaceFirst("x", hex + "");
			hex = mess[1];
			str = str.replaceFirst("x", "0" + cpcno + hex / 10 + hex % 10);
			hex = mess[2];
			if (hex == 1) {
				str = str.replaceFirst("x", "��");
			} else {
				str = str.replaceFirst("x", "�ر�");
			}
			break;

		case 15:
			hex = mess[0];
			int t = 1 + (hex - 1) / 4;
			str = str.replaceFirst("x", "0" + cpcno + t / 10 + t % 10 + hex
					/ 10 + hex % 10);
			hex = mess[1];
			if (hex == 1) {
				str = str.replaceFirst("x", "����");
			} else {
				str = str.replaceFirst("x", "����");
			}
			break;

		case 21:
			// hex = mess[0];
			// str = str.replaceFirst("x", hex+"");
			hex = mess[1];
			str = str.replaceFirst("x", "0" + cpcno + hex / 10 + hex % 10);
			vi = BCDtoVI(mess[2], mess[3]);
			str = str.replaceFirst("x", getDim(vi, hex - 1, cpcno));
			break;

		case 257:
			str = "����L#x ������x%��ʱ��x����";
			hex = mess[1];
			str = str.replaceFirst("x", "0" + cpcno + hex / 10 + hex % 10);
			vi = BCDtoVI(mess[2], mess[3]);
			str = str.replaceFirst("x", getDim(vi, hex - 1, cpcno));
			str = str.replaceFirst("x", "" + BCD2toInt(mess[4], mess[5]));
			break;

		case 258:
			str = "����������";
			break;

		case 259:
			str = "����L#xx";
			hex = mess[1];
			str = str.replaceFirst("x", "0" + cpcno + hex / 10 + hex % 10);
			hex = mess[2];
			if (hex == 1) {
				str = str.replaceFirst("x", "��");
			} else {
				str = str.replaceFirst("x", "�ر�");
			}
			break;
			
		case 272:
			str = "�������ʼ";
			break;
			
		case 273:
			str = "����������";
			break;
			
		default:
			str = "��";
			break;
		}
		return str;
	}

	/**
	 * ��ȡ��־����
	 * @param a
	 * @param b
	 * @return
	 */
	private int getType(byte a, byte b) {
		int c = b - 2;
		return c * 256 + a;
	}

	/**
	 * @param a
	 * @param b
	 * @return a+b*256
	 */
	private int BCD2toInt(byte a, byte b) {
		int r1 = b >= 0 ? b : b + 256;
		int r0 = a >= 0 ? a : a + 256;
		return r1 + r0 * 256;
	}

	/**
	 * 13-20λ�����ӻ�·�Ŀ��أ�����ϲ�Ϊһ���ֽڣ���λ����λ��Ӧ13-20 ע������ҵ�
	 * @param a  8-15λ
	 * @param b  16-23λ
	 * @return
	 */
	private byte getoffonbyte(byte a, byte b) {
		a = (byte) ((a >>> 5) & (0x07));
		b = (byte) (b << 3);
		return (byte) (a | b);
	}

	public double BCDtoVI(byte a, byte b) {
		int c = BCDtoInt(a);
		int d = BCDtoInt(b);
		double res = c + 0.01 * d;
		return res * 10;
	}

	private void InitIP() {
		for (int i = 0; i < 6; ++i) {
			maddr[i] = sp.getBoolean("addr" + (i + 1), false);
			mremote[i] = sp.getBoolean("remote" + (i + 1), false);
			mip[i] = sp.getString("ip" + (i + 1), "");
			mport[i] = sp.getInt("port" + (i + 1), 0);
			mlogic[i] = sp.getInt("logicAddr" + (i + 1), 4);
		}
	}

	private int getIntofaByte(byte b) {
		int res = 0;
		if (b < 0) {
			res += (1 << 7);
		}
		res += (int) (b & 0x7F);
		return res;
	}

	private int getIntof2Byte(byte a, byte b) {
		int len = 0;
		int temp = 0;
		if (a < 0) { // aΪ��λ
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

	private byte inttoBCD(int n) {
		byte res = 0;
		res = (byte) ((((byte) n / 10) << 4) + (byte) n % 10);
		return res;
	}

	public int BCDtoInt(byte a) {
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
