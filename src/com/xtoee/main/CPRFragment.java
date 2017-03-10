package com.xtoee.main;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.xtoee.bean.CPRbean;
import com.xtoee.tools.CustomToast;
import com.xtoee.tools.SwitchTitle;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class CPRFragment extends Fragment {

	private GridView grid;
	private int curCPC = 0;// 当前CPC号
	private int curCL = 0;// 当前回路号
	private Spinner spinner;// 下拉控件
	private ArrayList<HashMap<String, Object>> arraylist = null;
	private int[] mstate = new int[256];
	private String[] merrer = { "过流、短路保护", "过流保护", "输出欠压", "输出过压", "母线不平衡",
			"输入过压", "输入欠压", "过温", "限流标志", "模块故障", "模块关机", "内部均流故障", "模块风扇故障",
			"交流故障", "模块保护" ,"通讯故障"};// 错误类型

	private SwitchTitle swtitle;
	// private int itemWidth;
	private TextView[] titleView = new TextView[16];
	private ArrayList<String> listTitle;

	private int CLnum;
	private SharedPreferences sp;
	private SimpleAdapter sa;
	private boolean notify = false;
	/**
	 * handleMessage任务处理
	 * 
	 */
	MyHandler handler = new MyHandler(getActivity()) {
		private int updata = 0;
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				InitData(curCL + 1);// 刷新当前CPC数据
				// 加载数据
				sa = new SimpleAdapter(getActivity(), arraylist,
						R.layout.subcpc, new String[] { "cell" },
						new int[] { R.id.cell });
				notify = true;
				if(++updata>10){
					updata=0;
					notify=false;
				}
				if (!notify) {
					createTitleData();
				}
				grid.setAdapter(sa);
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
		View v = inflater.inflate(R.layout.cpr2, container, false);
		// findviewByid
		for (int i = 0; i < 64; ++i)
			mstate[i] = -1;
		swtitle = (SwitchTitle) v.findViewById(R.id.clTitle_cpr2);
		grid = (GridView) v.findViewById(R.id.gridView_cpr);
		spinner = (Spinner) v.findViewById(R.id.spinner_cpr);
		createSpinnerByResource();
		sp = getActivity().getSharedPreferences("CPC1Info",
				Context.MODE_PRIVATE);

		int colnum = (int) (((getResources().getDisplayMetrics().widthPixels)) / 6);
		grid.setColumnWidth(colnum);

		mTimer.schedule(mTimerTask, 500, 1000);

		createTitleData();
		grid.setOnItemClickListener(new gridListener());

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
	 * 设置标题
	 */
	private void createTitleData() {
		CLnum = sp.getInt("CLn", 0);
		listTitle = new ArrayList<String>();
		for (int i = 1; i <= CLnum; ++i) {
			listTitle.add("回路" + sp.getInt("CL" + i, 1));
		}
		for (int i = 0; i < titleView.length; ++i) {
			titleView[i] = null;
		}
		swtitle.setData(listTitle);
		swtitle.setAdapter(new TitleAdapter());
	}

	/**
	 * 去除点击加亮效果
	 */
	private void reback(TextView bt) {
		if(bt==null) return ;
		bt.setTextSize(getActivity().getResources()
				.getDimension(R.dimen.reback));
		bt.setTextColor(getActivity().getResources().getColor(
				R.color.textColor_gray));
	}

	/**
	 * 设置点击加亮效果
	 */
	private void setlight(TextView bt) {
		if(bt==null) return ;
		bt.setTextSize(getActivity().getResources().getDimension(R.dimen.light));
		bt.setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	/**
	 * 下拉选择当前CPC 设置当前CPC号
	 */
	private void createSpinnerByResource() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.CPC_spinner,
				R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				curCPC = position;
				sp = getActivity().getSharedPreferences(
						"CPC" + (position + 1) + "Info", Context.MODE_PRIVATE);
				CLnum = sp.getInt("CLn", 0);
				createTitleData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	/**
	 * 刷新当前数据
	 * @param no 当前回路号
	 */
	public void InitData(int no) {
		CPRbean[] curdata = ((RunActivity) getActivity()).getCPRbean(curCPC);
		arraylist = new ArrayList<HashMap<String, Object>>();
		int t = sp.getInt("CL" + (curCL + 1), 1)-1;// 真实的回路号
		for (int i = 1; i < 17; ++i) {
			if (!curdata[(t << 4) + i - 1].isFill())
				continue;
			// 设置填充格式
			DecimalFormat dcmFmt = new DecimalFormat("0.0");
			DecimalFormat dcmFmt2 = new DecimalFormat("0.00");
			// DecimalFormat dcmFmt2 = new DecimalFormat("0,000.00");
			HashMap<String, Object> map = new HashMap<String, Object>();
			// map.put("cell", "R#0"+(curCPC+1)+t/10+t%10+"0"+i);//名字
			StringBuilder name = new StringBuilder();
			name.append("R#0").append(curCPC + 1).append((t+1) / 10).append((t+1) % 10);
			if (curdata[(t << 4) + i - 1].getDevno() < 10)
				name.append(0).append(curdata[(t << 4) + i - 1].getDevno());
			else
				name.append(curdata[(t << 4) + i - 1].getDevno());
			map.put("cell", name.toString());// 名字
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", dcmFmt.format(curdata[(t << 4) + i - 1].getMv()));// 电压(t-1)*4+i-1
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", dcmFmt.format(curdata[(t << 4) + i - 1].getMi()));// 电流
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell",dcmFmt2.format(
					(curdata[(t << 4) + i - 1].getMv() * curdata[(t << 4) + i - 1].getMi())));// 功率
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", curdata[(t << 4) + i - 1].getMstate());// 状态
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", dcmFmt.format(curdata[(t << 4) + i - 1].getMt()));
			// map.put("cell", "--");//温度
			arraylist.add(map);// 加载到list
			mstate[(curCL << 4) + i - 1] = curdata[(curCL << 4) + i - 1]
					.getMsatateint();// 设置错误码，以备查询
		}
	}

	class gridListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if ((position % 6) == 4) {// 只有状态处可以点击
				String str = "";
				int errposition = (curCL << 4) + position / 6;// 点击状态的位置
				if (mstate[errposition] == -1)// 初始值，未连接状态
					str = "连接异常";
				else if (mstate[errposition] == 0)
					str = "状态正常";
				else {
					for (int i = 0; i < 16; ++i) {
						if ((mstate[errposition] & (1 << i)) > 0) {
							if (!("").equals(str))
								str += "、";
							str += merrer[i];
						}
					}
				}
				CustomToast.showToast(getActivity(), str, 1500);

			}
		}

	}

	static class MyHandler extends Handler {

		WeakReference<Activity> mActivityReference;

		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}
	}

	public class TitleAdapter extends BaseAdapter {

		private class TitleViewHolder {
			public TextView tv;
		}

		@Override
		public int getCount() {
			return listTitle.size();
		}

		@Override
		public Object getItem(int position) {
			return listTitle.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TitleViewHolder holder = null;
			final int gg = position;
			if (convertView == null) {
				holder = new TitleViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.cltitle, null);
				holder.tv = (TextView) convertView
						.findViewById(R.id.cltitleofall);
				if (titleView[gg] == null)
					titleView[gg] = holder.tv;
				convertView.setTag(holder);
				// holder.tv.setWidth(itemWidth);
				holder.tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						reback(titleView[curCL]);
						curCL = gg;
						setlight(titleView[curCL]);
					}
				});
			} else {
				holder = (TitleViewHolder) convertView.getTag();
			}
			holder.tv.setText(listTitle.get(position));

			return convertView;
		}

	}

}
