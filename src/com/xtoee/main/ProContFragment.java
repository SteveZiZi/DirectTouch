package com.xtoee.main;

import java.lang.ref.WeakReference;

import com.xtoee.bean.Taskbean;
import com.xtoee.services.ControlService;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProContFragment extends Fragment {

	private int TASKN = 12;
	private int curCPC = 0;// 0-3
	private Button bt1, bt2, bt3, bt4, bt5, bt6, bt_readall;
	private Button[] detail = new Button[TASKN];
	Taskbean[] CPCtask = new Taskbean[TASKN * 6];
	private SharedPreferences[] sp = new SharedPreferences[TASKN * 6];
	private TextView[] taskname = new TextView[TASKN];
	private TextView[] taskID = new TextView[TASKN];
	private TextView[] tasklevel = new TextView[TASKN];
	private TextView[] taskcl = new TextView[TASKN];
	private ImageView[] enable = new ImageView[TASKN * 2];
	String[] levelStr = { "高", "中", "低" };// 优先级
	// private String[] zhou = {"一","二","三","四","五","六","七"};
	private BroadcastReceiver receiver;

	MyHandler handler = new MyHandler(getActivity()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				InitInfo();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.procont, container, false);

		findview(v);// findViewById
		setlight(bt1);
		createButtonListener();// 初始化各类Button监听
		InitInfo();

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean updata = intent.getBooleanExtra("updata", false);
				if (updata) {
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				}

			}
		};
		// 接收器的过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction("action.Control_BROADCAST");
		getActivity().registerReceiver(receiver, filter);

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		InitInfo();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 注销广播接收器
		getActivity().unregisterReceiver(receiver);
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * findViewById
	 */
	private void findview(View v) {
		int[] tempEn = { R.id.enable11_pro, R.id.enable12_pro,
				R.id.enable13_pro, R.id.enable14_pro, R.id.enable15_pro,
				R.id.enable16_pro, R.id.enable17_pro, R.id.enable18_pro,
				R.id.enable19_pro, R.id.enable110_pro, R.id.enable111_pro,
				R.id.enable112_pro, R.id.enable21_pro, R.id.enable22_pro,
				R.id.enable23_pro, R.id.enable24_pro, R.id.enable25_pro,
				R.id.enable26_pro, R.id.enable27_pro, R.id.enable28_pro,
				R.id.enable29_pro, R.id.enable210_pro, R.id.enable211_pro,
				R.id.enable212_pro };
		int[] tempTn = { R.id.task1, R.id.task2, R.id.task3, R.id.task4,
				R.id.task5, R.id.task6, R.id.task7, R.id.task8, R.id.task9,
				R.id.task10, R.id.task11, R.id.task12 };
		int[] tempDt = { R.id.task1detail, R.id.task2detail, R.id.task3detail,
				R.id.task4detail, R.id.task5detail, R.id.task6detail,
				R.id.task7detail, R.id.task8detail, R.id.task9detail,
				R.id.task10detail, R.id.task11detail, R.id.task12detail };
		int[] tempTc = { R.id.cl1_task1, R.id.cl2_task2, R.id.cl3_task3,
				R.id.cl4_task4, R.id.cl5_task5, R.id.cl6_task6, R.id.cl7_task7,
				R.id.cl8_task8, R.id.cl9_task9, R.id.cl10_task10,
				R.id.cl11_task11, R.id.cl12_task12 };
		int[] tempTi = { R.id.ic1_task1, R.id.ic2_task2, R.id.ic3_task3,
				R.id.ic4_task4, R.id.ic5_task5, R.id.ic6_task6, R.id.ic7_task7,
				R.id.ic8_task8, R.id.ic9_task9, R.id.ic10_task10,
				R.id.ic11_task11, R.id.ic12_task12 };
		int[] tempTl = { R.id.level1_task1, R.id.level2_task2,
				R.id.level3_task3, R.id.level4_task4, R.id.level5_task5,
				R.id.level6_task6, R.id.level7_task7, R.id.level8_task8,
				R.id.level9_task9, R.id.level10_task10, R.id.level11_task11,
				R.id.level12_task12 };
		for (int i = 0; i < TASKN; ++i) {
			enable[i] = (ImageView) v.findViewById(tempEn[i]);
			enable[12 + i] = (ImageView) v.findViewById(tempEn[12 + i]);
			taskname[i] = (TextView) v.findViewById(tempTn[i]);
			detail[i] = (Button) v.findViewById(tempDt[i]);
			taskcl[i] = (TextView) v.findViewById(tempTc[i]);
			taskID[i] = (TextView) v.findViewById(tempTi[i]);
			tasklevel[i] = (TextView) v.findViewById(tempTl[i]);
		}
		for (int j = 0; j < 6; ++j) {
			for (int i = 0; i < TASKN; ++i) {
				sp[12 * j + i] = getActivity().getApplicationContext()
						.getSharedPreferences("task" + (j + 1) + (i + 1),
								Context.MODE_PRIVATE);
			}
		}

		bt1 = (Button) v.findViewById(R.id.cpc1_procont);
		bt2 = (Button) v.findViewById(R.id.cpc2_procont);
		bt3 = (Button) v.findViewById(R.id.cpc3_procont);
		bt4 = (Button) v.findViewById(R.id.cpc4_procont);
		bt5 = (Button) v.findViewById(R.id.cpc5_procont);
		bt6 = (Button) v.findViewById(R.id.cpc6_procont);
		bt_readall = (Button) v.findViewById(R.id.readall);

	}

	public void InitInfo() {
		for (int i = 0; i < TASKN; ++i) {
			// 设置使能
			int visib = View.INVISIBLE;
			if (sp[curCPC * TASKN + i].getBoolean("enable", false))
				visib = View.VISIBLE;
			enable[TASKN + i].setVisibility(visib);
			// 设置任务名
			taskname[i].setText(sp[curCPC * TASKN + i].getString("name", "任务"
					+ (i + 1)));
			// 任务ID
			taskID[i].setText(sp[curCPC * TASKN + i].getInt("ID", i + 1) + "");
			// 优先级
			tasklevel[i].setText(levelStr[2 - sp[curCPC * TASKN + i].getInt(
					"level", 2)]);
			// 任务回路
			String str = "";
			int a = sp[curCPC * TASKN + i].getInt("cls", 0);
			if (a != 0) {
				for (int j = 0; j < 16; ++j) {
					if ((a & (1 << j)) > 0) {
						if (!("".equals(str)))
							str += "、";
						str = str + "回路" + (j + 1);
					}
				}
			}
			if ("".equals(str))
				str = "无";
			taskcl[i].setText(str);
		}
	}

	// 初始化各类Button监听
	private void createButtonListener() {
		// CPC号切换
		final Button[] bts = { bt1, bt2, bt3, bt4, bt5, bt6 };
		for (int i = 0; i < 6; ++i) {
			final int mj = i;
			bts[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reback(bts[curCPC]);
					curCPC = mj;
					setlight(bts[curCPC]);
					InitInfo();
				}
			});
		}

		// 查看详情的activity跳转
		for (int i = 0; i < TASKN; ++i) {
			final int mk = i;
			detail[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							TaskDetailActivity.class);
					intent.putExtra("No", TASKN * curCPC + mk + 1);
					startActivity(intent);
				}
			});
		}

		// 使能关闭
		for (int i = 0; i < TASKN; ++i) {
			final int j = i;
			enable[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int visib = View.INVISIBLE;
					boolean b = sp[curCPC * TASKN + j].getBoolean("enable",
							false);
					b = !b;
					if (b)
						visib = View.VISIBLE;
					enable[TASKN + j].setVisibility(visib);
					Editor edit = sp[curCPC * TASKN + j].edit();
					edit.putBoolean("enable", b);
					edit.commit();
				}
			});
		}

		// 使能打开
		for (int i = TASKN; i < TASKN * 2; ++i) {
			final int j = i - TASKN;
			enable[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int visib = View.INVISIBLE;
					boolean b = sp[curCPC * TASKN + j].getBoolean("enable",
							false);
					b = !b;
					if (b)
						visib = View.VISIBLE;
					enable[TASKN + j].setVisibility(visib);
					Editor edit = sp[curCPC * TASKN + j].edit();
					edit.putBoolean("enable", b);
					edit.commit();
				}
			});
		}

		bt_readall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ControlService.class);
				Bundle bund = new Bundle();
				bund.putInt("curCPC", curCPC + 1);
				bund.putBoolean("isread", true);
				intent.putExtras(bund);
				getActivity().startService(intent);
//				Toast toast = Toast.makeText(getActivity(), "抄读任务较多，请稍等！",
//						Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
			}
		});
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

	static class MyHandler extends Handler {

		WeakReference<Activity> mActivityReference;

		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}
	}
}
