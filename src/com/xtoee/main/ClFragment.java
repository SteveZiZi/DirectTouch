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
	// private int[][] dim1 = new int[4][11];//CPC1的Dim
	// private int[][] dim2 = new int[4][11];//CPC2的Dim
	// private int[][] dim3 = new int[4][11];//CPC3的Dim
	// private int[][] dim4 = new int[4][11];//CPC4的Dim
	private int[][] dim = new int[16][11];// CPC的Dim
	private SharedPreferences sp;

	private Button bt1, bt2, bt3, bt4, bt5, bt6;// CPC1-6

	private SimpleAdapter sa;
	private boolean notify = false;

	// 设置填充格式
	private DecimalFormat dcmFmt = new DecimalFormat("0.0");
	private DecimalFormat dcmFmt2 = new DecimalFormat("0.00");

	/**
	 * handleMessage任务处理
	 * 
	 */
	MyHandler handler = new MyHandler(getActivity()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				InitData(curCPC + 1); // 刷新当前CPC数据
				// 加载数据
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

		// 设置gridview宽度
		int colnum = (int) (((getResources().getDisplayMetrics().widthPixels)) / 7);
		grid.setColumnWidth(colnum);

		// 启动定时器
		mTimer.schedule(mTimerTask, 500, 1000);

		setlight(bt1);// 设置gridview宽度
		sp = getActivity().getSharedPreferences("CPC" + (curCPC + 1) + "Info",
				Context.MODE_PRIVATE);
		dim = ((XtoeeApp) getActivity().getApplication()).getDimDate(1);
		createButtonListener();// 设置监听事件

		return v;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// 销毁定时器任务
		while (!mTimerTask.cancel()) {
			mTimer.cancel();
		}
		// 销毁list
		if (arraylist != null && !(arraylist.isEmpty())) {
			arraylist.clear();
		}
		grid = null;
		arraylist = null;
		// 销毁handle
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * 设置CPC1-6监听 设置当前CPC号 设置CPC点亮效果
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
	 * 去除点击加亮效果
	 * 
	 */
	private void reback(Button bt) {
		bt.setTextSize(getActivity().getResources()
				.getDimension(R.dimen.reback));
		bt.setTextColor(getActivity().getResources().getColor(
				R.color.textColor_gray));
	}

	/**
	 * 设置点击加亮效果
	 * 
	 */
	private void setlight(Button bt) {
		bt.setTextSize(getActivity().getResources().getDimension(R.dimen.light));
		bt.setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	/**
	 * //刷新当前CPC数据
	 * 
	 * @param no 当前CPC号
	 */
	public void InitData(int no) {
		CLbean[] curdata = ((RunActivity) getActivity()).getCLbean();// 获取此时的CLbean数据
		int CLnum = sp.getInt("CLn", 0);
		if (notify) {
			int listlen = arraylist.size() / 7;//7列
			if (CLnum <= listlen) {// 没有增加回路
				// 修改已有的回路
				for (int i = 1; i <= CLnum; ++i) {
					changeLine(i, no, curdata);
				}
			} else {
				// 修改已有的回路
				for (int i = 1; i <= listlen; ++i) {
					changeLine(i, no, curdata);
				}
				// 增加多余的回路
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
	 * 增加一行
	 * @param line  行编号，1-n
	 * @param no    cpc编号 1-4
	 * @param curdata
	 */
	private void changeLine(int line, int no, CLbean[] curdata) {
		int base = 7 * (line - 1);// base+i为当前元素在arraylist的下标
		int k = sp.getInt("CL" + line, 1);// 回路编号

		HashMap<String, Object> map = new HashMap<String, Object>();
		map = arraylist.get(base);
		map.put("cell", "L#0" + no + k / 10 + k % 10);// 名字

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 1);
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMv()));// 电压

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 2);
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMi()));// 电流

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 3);
		map.put("cell",
				dcmFmt2.format((curdata[curCPC * 16 + k - 1].getMv() * curdata[curCPC
						* 16 + k - 1].getMi())));// 功率

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 4);
		map.put("cell", curdata[curCPC * 16 + k - 1].getMstate());// 状态

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 5);
		map.put("cell", getDim(curdata[curCPC * 16 + k - 1].getMv(), line - 1));// Dim

		map = new HashMap<String, Object>();
		map = arraylist.get(base + 6);
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMt()));
	}

	/**
	 * 增加一行
	 * 
	 * @param line 行编号，1-n
	 * @param no   cpc编号 1-6
	 * @param curdata
	 */
	private void addLine(int line, int no, CLbean[] curdata) {
		int k = sp.getInt("CL" + line, 1);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("cell", "L#0" + no + k / 10 + k % 10);// 名字
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMv()));// 电压
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMi()));// 电流
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell",
				dcmFmt2.format((curdata[curCPC * 16 + k - 1].getMv() * curdata[curCPC
						* 16 + k - 1].getMi())));// 功率
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", curdata[curCPC * 16 + k - 1].getMstate());// 状态
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", getDim(curdata[curCPC * 16 + k - 1].getMv(), line - 1));// Dim
		arraylist.add(map);

		map = new HashMap<String, Object>();
		map.put("cell", dcmFmt.format(curdata[curCPC * 16 + k - 1].getMt()));
		arraylist.add(map);// 加载到list
	}

	/**
	 * 
	 * @param v    输入电压
	 * @param clno 回路号
	 * @return 有电压转换的Dim值
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
