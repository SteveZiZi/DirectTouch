package com.xtoee.main;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.xtoee.bean.CLbean;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class ClFragment extends Fragment {

	private GridView grid;
	private ArrayList<HashMap<String, Object>> arraylist = null;
	private int curCPC = 0;// current CPC
	// private int[][] dim1 = new int[4][11];//CPC1��Dim
	// private int[][] dim2 = new int[4][11];//CPC2��Dim
	// private int[][] dim3 = new int[4][11];//CPC3��Dim
	// private int[][] dim4 = new int[4][11];//CPC4��Dim
	private int[][] dim = new int[16][11];// CPC��Dim
	private SharedPreferences sp;

	private Button bt1, bt2, bt3, bt4, bt5, bt6;// CPC1-6

	private SimpleAdapter sa;
	private boolean notify = false;

	// ��������ʽ
	private DecimalFormat dcmFmt = new DecimalFormat("0.0");
	private DecimalFormat dcmFmt2 = new DecimalFormat("0.00");

	/**
	 * handleMessage������
	 * 
	 */
	MyHandler handler = new MyHandler(getActivity()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				InitData(curCPC + 1); // ˢ�µ�ǰCPC����
				// ��������
				if (notify)
					sa.notifyDataSetChanged();
				else {
					sa = new SimpleAdapter(getActivity(), arraylist,
							R.layout.subcpc, new String[] { "cell" },
							new int[] { R.id.cell });
					grid.setAdapter(sa);
					notify = true;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.cl, container, false);
		// findviewByid
		grid = (GridView) v.findViewById(R.id.gridView_cl);
		bt1 = (Button) v.findViewById(R.id.cpc1_cl);
		bt2 = (Button) v.findViewById(R.id.cpc2_cl);
		bt3 = (Button) v.findViewById(R.id.cpc3_cl);
		bt4 = (Button) v.findViewById(R.id.cpc4_cl);
		bt5 = (Button) v.findViewById(R.id.cpc5_cl);
		bt6 = (Button) v.findViewById(R.id.cpc6_cl);

		// ����gridview���
		int colnum = (int) (((getResources().getDisplayMetrics().widthPixels)) / 7);
		grid.setColumnWidth(colnum);

		// ������ʱ��
		mTimer.schedule(mTimerTask, 500, 1000);

		setlight(bt1);// ����gridview���
		sp = getActivity().getSharedPreferences("CPC" + (curCPC + 1) + "Info",
				Context.MODE_PRIVATE);
		dim = ((XtoeeApp) getActivity().getApplication()).getDimDate(1);
		createButtonListener();// ���ü����¼�

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
	 * ����CPC1-6���� ���õ�ǰCPC�� ����CPC����Ч��
	 */
	private void createButtonListener() {
		final Button[] bts = { bt1, bt2, bt3, bt4, bt5, bt6 };
		for (int i = 0; i < 6; ++i) {
			final int j = i;
			bts[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					notify = false;
					reback(bts[curCPC]);
					curCPC = j;
					setlight(bts[curCPC]);
					sp = getActivity()
							.getSharedPreferences(
									"CPC" + (curCPC + 1) + "Info",
									Context.MODE_PRIVATE);
					dim = ((XtoeeApp) getActivity().getApplication())
							.getDimDate(curCPC + 1);
				}
			});
		}
	}

	/**
	 * ȥ���������Ч��
	 * 
	 */
	private void reback(Button bt) {
		bt.setTextSize(getActivity().getResources()
				.getDimension(R.dimen.reback));
		bt.setTextColor(getActivity().getResources().getColor(
				R.color.textColor_gray));
	}

	/**
	 * ���õ������Ч��
	 * 
	 */
	private void setlight(Button bt) {
		bt.setTextSize(getActivity().getResources().getDimension(R.dimen.light));
		bt.setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	/**
	 * //ˢ�µ�ǰCPC����
	 * 
	 * @param no ��ǰCPC��
	 */
	public void InitData(int no) {
		CLbean[] curdata = ((RunActivity) getActivity()).getCLbean();// ��ȡ��ʱ��CLbean����
		int CLnum = sp.getInt("CLn", 0);
		if (notify) {
			int listlen = arraylist.size() / 7;//7��
			if (CLnum <= listlen) {// û�����ӻ�·
				// �޸����еĻ�·
				for (int i = 1; i <= CLnum; ++i) {
					changeLine(i, no, curdata);
				}
			} else {
				// �޸����еĻ�·
				for (int i = 1; i <= listlen; ++i) {
					changeLine(i, no, curdata);
				}
				// ���Ӷ���Ļ�·
				for (int i = listlen + 1; i <= CLnum; ++i) {
					addLine(i, no, curdata);
				}
			}
		} else {
			arraylist = new ArrayList<HashMap<String, Object>>();
			for (int i = 1; i <= CLnum; ++i) {
				addLine(i, no, curdata);
			}
		}
	}

	/**
	 * ����һ��
	 * @param line  �б�ţ�1-n
	 * @param no    cpc��� 1-4
	 * @param curdata
	 */
	private void changeLine(int line, int no, CLbean[] curdata) {
		int base = 7 * (line - 1);// base+iΪ��ǰԪ����arraylist���±�
		int k = sp.getInt("CL" + line, 1);// ��·���

		HashMap<String, Object> map = new HashMap<String, Object>();
		map = arraylist.get(base);
		map.put("cell", "L#0" + no + k / 10 + k % 10);// ����

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 1);
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMv()));// ��ѹ

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 2);
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMi()));// ����

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 3);
		map.put("cell",
				dcmFmt2.format((curdata[curCPC * 16 + k - 1].getMv() * curdata[curCPC
						* 16 + k - 1].getMi())));// ����

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 4);
		map.put("cell", curdata[curCPC * 16 + k - 1].getMstate());// ״̬

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 5);
		map.put("cell", getDim(curdata[curCPC * 16 + k - 1].getMv(), line - 1));// Dim

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 6);
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMt()));
	}

	/**
	 * ����һ��
	 * 
	 * @param line �б�ţ�1-n
	 * @param no   cpc��� 1-6
	 * @param curdata
	 */
	private void addLine(int line, int no, CLbean[] curdata) {
		int k = sp.getInt("CL" + line, 1);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("cell", "L#0" + no + k / 10 + k % 10);// ����
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMv()));// ��ѹ
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMi()));// ����
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell",
				dcmFmt2.format((curdata[curCPC * 16 + k - 1].getMv() * curdata[curCPC
						* 16 + k - 1].getMi())));// ����
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", curdata[curCPC * 16 + k - 1].getMstate());// ״̬
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", getDim(curdata[curCPC * 16 + k - 1].getMv(), line - 1));// Dim
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMt()));
		arraylist.add(map);// ���ص�list
	}

	/**
	 * 
	 * @param v    �����ѹ
	 * @param clno ��·��
	 * @return �е�ѹת����Dimֵ
	 */
	private String getDim(double v, int clno) {
		if (v < dim[clno][0])
			return "0%";
		for (int i = 1; i < 11; ++i) {
			if (v < dim[clno][i]) {
				if ((v - dim[clno][i - 1]) > (dim[clno][i] - v)) {
					return i + "0%";
				} else {
					if (i == 1)
						return "0%";
					return (i - 1) + "0%";
				}
			}
		}
		return "100%";
	}

	static class MyHandler extends Handler {

		WeakReference<Activity> mActivityReference;

		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}
	}

}
