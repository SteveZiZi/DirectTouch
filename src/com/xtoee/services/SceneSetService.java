package com.xtoee.services;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import com.xtoee.tools.MyTools;
import com.xtoee.tools.ProtocolData;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class SceneSetService extends Service {

	private final int BEATSTD=24;
	private int curCPC;
	private String[] mip = new String[6];
	private int[] mport = new int[6];
	private boolean[] maddr = new boolean[6];
	private boolean[] mremote = new boolean[6];
	private int[] mlogic = new int[6];
	private SharedPreferences sp;
	private boolean isenable = false;

	private byte controlByte;
	private byte[] usefulData;// ����
	private int[] needBeatcount = new int[6];

	// У�����
	byte[] jiaoyanbianma = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x11, 0x12,
			0x13 };
	// У�����ĺ���
	String[] bianmahanyi = { "��ȷ", "����û�з���", "�������ݷǷ�", "����Ȩ�޲���", "�޴�������",
			"����ʱ��ʧЧ", "Ŀ���ַ������", "����ʧ��", "����Ϣ̫֡��" };

	/**
	 * handleMessage������
	 * 
	 */
	MyHandler handler = new MyHandler(SceneSetService.this) {
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
		InitIP();
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
		if (bund != null) {
			isenable = true;
			curCPC = bund.getInt("curCPC");// ��ȡCPC��
			// ���÷��ͱ���
			setframe(bund.getByte("controlByte"),
					bund.getByteArray("usefulData"));
		}
		if (isenable) {
			switch (curCPC) {
			case 1:
				new CPC1SceneSet().start();
				break;

			case 2:
				new CPC2SceneSet().start();
				break;

			case 3:
				new CPC3SceneSet().start();
				break;

			case 4:
				new CPC4SceneSet().start();
				break;

			case 5:
				new CPC5SceneSet().start();
				break;

			case 6:
				new CPC6SceneSet().start();
				break;

			default:
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
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

	/**
	 * ��ʼ����ַ��Ϣ
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

	// CPC1�������ã�����ͬ
	public class CPC1SceneSet extends Thread {
		public void run() {
			if (maddr[0]) {
				ProtocolData pdsend = new ProtocolData(controlByte, usefulData);
				SendAndReceive3 sr = SendAndReceive3.getinstance(1, mip[0],
						mport[0], mremote[0],mlogic[0]);
				byte[] remess = sr.sendReceive(1, pdsend.getFrame(), mlogic[0]);
				if (remess != null && remess.length > 13) {
					ProtocolData reseiveframe = new ProtocolData(remess);
					byte[] useful = reseiveframe.getUsefulData(remess);// ���ջظ�����
					task(useful);// ������
				} else {
				}
			}
			isenable = false;
		}
	}

	public class CPC2SceneSet extends Thread {
		public void run() {
			if (maddr[1]) {
				ProtocolData pdsend = new ProtocolData(controlByte, usefulData);
				SendAndReceive3 sr = SendAndReceive3.getinstance(2, mip[1],
						mport[1], mremote[1],mlogic[1]);
				byte[] remess = sr.sendReceive(2, pdsend.getFrame(), mlogic[1]);
				if (remess != null && remess.length > 13) {
					ProtocolData reseiveframe = new ProtocolData(remess);
					byte[] useful = reseiveframe.getUsefulData(remess);
					task(useful);
				} else {
				}
			}
			isenable = false;
		}
	}

	public class CPC3SceneSet extends Thread {
		public void run() {
			if (maddr[2]) {
				ProtocolData pdsend = new ProtocolData(controlByte, usefulData);
				SendAndReceive3 sr = SendAndReceive3.getinstance(3, mip[2],
						mport[2], mremote[2], mlogic[2]);
				byte[] remess = sr.sendReceive(3, pdsend.getFrame(), mlogic[2]);
				if (remess != null && remess.length > 13) {
					ProtocolData reseiveframe = new ProtocolData(remess);
					byte[] useful = reseiveframe.getUsefulData(remess);
					task(useful);
				} else {
				}
			}
			isenable = false;
		}
	}

	public class CPC4SceneSet extends Thread {
		public void run() {
			if (maddr[3]) {
				ProtocolData pdsend = new ProtocolData(controlByte, usefulData);
				SendAndReceive3 sr = SendAndReceive3.getinstance(4, mip[3],
						mport[3], mremote[3], mlogic[3]);
				byte[] remess = sr.sendReceive(4, pdsend.getFrame(), mlogic[3]);
				if (remess != null && remess.length > 13) {
					ProtocolData reseiveframe = new ProtocolData(remess);
					byte[] useful = reseiveframe.getUsefulData(remess);
					task(useful);
				} else {
				}
			}
			isenable = false;
		}
	}

	public class CPC5SceneSet extends Thread {
		public void run() {
			if (maddr[4]) {
				ProtocolData pdsend = new ProtocolData(controlByte, usefulData);
				SendAndReceive3 sr = SendAndReceive3.getinstance(5, mip[4],
						mport[4], mremote[4], mlogic[4]);
				byte[] remess = sr.sendReceive(5, pdsend.getFrame(), mlogic[4]);
				if (remess != null && remess.length > 13) {
					ProtocolData reseiveframe = new ProtocolData(remess);
					byte[] useful = reseiveframe.getUsefulData(remess);
					task(useful);
				} else {
				}
			}
			isenable = false;
		}
	}

	public class CPC6SceneSet extends Thread {
		public void run() {
			if (maddr[5]) {
				ProtocolData pdsend = new ProtocolData(controlByte, usefulData);
				SendAndReceive3 sr = SendAndReceive3.getinstance(6, mip[5],
						mport[5], mremote[5], mlogic[5]);
				byte[] remess = sr.sendReceive(6, pdsend.getFrame(), mlogic[5]);
				if (remess != null && remess.length > 13) {
					ProtocolData reseiveframe = new ProtocolData(remess);
					byte[] useful = reseiveframe.getUsefulData(remess);
					task(useful);
				} else {
				}
			}
			isenable = false;
		}
	}

	/**
	 * ���÷��ͱ���
	 * @param controlByte  ������
	 * @param usefulData   ���ò���
	 */
	public void setframe(byte controlByte, byte[] usefulData) {
		this.controlByte = controlByte;
		this.usefulData = null;
		this.usefulData = usefulData;
	}

	public void task(byte[] mess) {
		// �����ظ�֡
		String msg = "���ͳɹ�";
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
		Intent intent = new Intent("action.SceneSetService");
		intent.putExtra("msg", msg);
		sendBroadcast(intent);
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
		byte[] mess = MyTools.beatframe.clone();

		--index;
		SendAndReceive3 sr = SendAndReceive3.getinstance(index + 1, mip[index],
				mport[index], mremote[index], mlogic[index]);
		byte[] remess = sr.sendReceive(index + 1, mess, mlogic[index]);
		if (remess != null && remess.length > 10) {

		}
	}

	static class MyHandler extends Handler {

		WeakReference<Service> mServiceReference;

		MyHandler(Service service) {
			mServiceReference = new WeakReference<Service>(service);
		}
	}

}
