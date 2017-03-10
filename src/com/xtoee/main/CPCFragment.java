package com.xtoee.main;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class CPCFragment extends Fragment {

	private GridView grid;
	private ArrayList<HashMap<String, Object>> arraylist = null;
	private SimpleAdapter sa;
//	private boolean notify = false;

	/**
	 * handleMessage������
	 * 
	 */
	MyHandler handler = new MyHandler(getActivity()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				InitData();// ˢ�µ�ǰCPC����
				// ��������
				sa = new SimpleAdapter(getActivity(), arraylist,
						R.layout.subcpc, new String[] { "cell" },
						new int[] { R.id.cell });
				grid.setAdapter(sa);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.cpc, container, false);
		// findviewByid
		grid = (GridView) v.findViewById(R.id.gridView_cpc);
		// ����gridview���
		int colnum = (int) (((getResources().getDisplayMetrics().widthPixels)) / 6);
		grid.setColumnWidth(colnum);
		// ������ʱ��
		mTimer.schedule(mTimerTask, 500, 1000);

		return v;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// ���ٶ�ʱ������
		while (!mTimerTask.cancel()) {
			mTimer.cancel();
		}
		// ����list
		if (arraylist != null && !(arraylist.isEmpty())) {
			arraylist.clear();
		}
		grid = null;
		arraylist = null;
		// ����handle
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * ˢ�µ�ǰCPC����
	 */
	public void InitData() {
		double[] curdata = ((RunActivity) getActivity()).getCPCVI();// ��ȡ��ʱ�ĵ�����ѹ����
		arraylist = new ArrayList<HashMap<String, Object>>();
		for (int i = 1; i < 7; ++i) {
			// ��������ʽ
			DecimalFormat dcmFmt = new DecimalFormat("0.0");
			DecimalFormat dcmFmt2 = new DecimalFormat("0,000.00");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("cell", "C#0" + i);// ����
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", dcmFmt.format(curdata[4 * i - 4])); // 4(i-1)+0 //��ѹ
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", dcmFmt.format(curdata[4 * i - 3]));// ����
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell",
					dcmFmt2.format((curdata[4 * i - 4] * curdata[4 * i - 3])));
			arraylist.add(map);// ���ص�list
			map = new HashMap<String, Object>();
			map.put("cell", getState(curdata[4 * i - 2])); // ״̬
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", dcmFmt.format(curdata[4 * i - 1])); // �¶�
			arraylist.add(map);
		}
	}

	private String getState(double d) {
		if (d > 2.0)
			return "����";
		else
			return "�쳣";
	}

	static class MyHandler extends Handler {

		WeakReference<Activity> mActivityReference;

		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}
	}

}
