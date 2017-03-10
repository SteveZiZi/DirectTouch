package com.xtoee.main;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.xtoee.bean.RecordBean;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RecordFragment extends Fragment {
	private List<RecordBean> mlist;
	private ListView listview;
	private Button bt1, bt2, bt3, bt4, bt5, bt6;
	private int curCPC = 0;
	private boolean isover = false;

	/**
	 * handleMessage任务处理
	 * 
	 */
	MyHandler handler = new MyHandler(getActivity()) {
		@Override
		public void handleMessage(Message msg) {
			AddList();// 加载数据
			listview.setAdapter(new Myadaper());
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.record, container, false);
		bt1 = (Button) v.findViewById(R.id.cpc1_record);
		bt2 = (Button) v.findViewById(R.id.cpc2_record);
		bt3 = (Button) v.findViewById(R.id.cpc3_record);
		bt4 = (Button) v.findViewById(R.id.cpc4_record);
		bt5 = (Button) v.findViewById(R.id.cpc5_record);
		bt6 = (Button) v.findViewById(R.id.cpc6_record);
		setlight(bt1);
		createButtonListener();
		listview = (ListView) v.findViewById(R.id.record_listview);

		new Thread() {
			public void run() {
				try {
					sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!isover) {
					handler.sendEmptyMessage(0);
				}
			}
		}.start();

		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isover = true;
	}

	/**
	 * 加载数据
	 */
	private void AddList() {
		mlist = new ArrayList<RecordBean>();
		SharedPreferences sp = getActivity().getSharedPreferences(
				"log" + (curCPC + 1), Context.MODE_PRIVATE);
		// 记录数
		int num = sp.getInt("num", 0);
		System.out.println("记录数为  " + num);
		// 读取并加载
		for (int i = 0; i < num; ++i) {
			RecordBean rb = new RecordBean(sp.getString("time" + i, ""),
					sp.getString("type" + i, ""),
					sp.getString("detail" + i, ""));
			mlist.add(rb);
		}
	}

	/**
	 * CPC 点击事件监听
	 */
	private void createButtonListener() {
		final Button[] bts = { bt1, bt2, bt3, bt4, bt5, bt6 };
		for (int i = 0; i < 6; ++i) {
			final int j = i;
			bts[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reback(bts[curCPC]);
					curCPC = j;
					setlight(bts[curCPC]);
					// 加载数据并显示
					AddList();
					listview.setAdapter(new Myadaper());
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
	 * list 的 adapter 配置
	 * @author yang
	 */
	private class Myadaper extends BaseAdapter {

		@Override
		public int getCount() {
			return mlist.size();
		}

		@Override
		public Object getItem(int position) {
			return mlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = LayoutInflater.from(getActivity()).inflate(
					R.layout.record_element, null);
			// 获取位置
			RecordBean rb = mlist.get(position);
			// 记录时间
			TextView time_record = (TextView) v.findViewById(R.id.time_record);
			// 记录动作
			TextView act_record = (TextView) v.findViewById(R.id.act_record);
			// 记录细节
			TextView detail_record = (TextView) v
					.findViewById(R.id.detail_recoed);
			time_record.setText(rb.getTim());
			act_record.setText(rb.getAct());
			detail_record.setText(rb.getDetail());
			return v;
		}
	}

	static class MyHandler extends Handler {

		WeakReference<Activity> mActivityReference;

		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}
	}
}
