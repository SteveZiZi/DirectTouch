package com.xtoee.main;

import java.util.ArrayList;

import com.xtoee.bean.AdjBean;
import com.xtoee.services.ControlService;
import com.xtoee.tools.SwitchView;
import com.xtoee.tools.SwitchView.OnSwitchChangeListener;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class AdjustFragment extends Fragment {

	private int curCPC = 1;// 当前CPC号1-4

	private Button bt1, bt2, bt3, bt4, bt5, bt6, gotocl;// CPC1-4
	private String[] displayedValues = { "100%", "90%", "80%", "70%", "60%",
			"50%", "40%", "30%", "20%", "10%", "0%", "0%" };// dim范围
	private SharedPreferences sp, spSetDim;
//	private SharedPreferences spReadDim;
	private ListView listView;
	private ArrayList<AdjBean> arrayList;
	private int CLnum;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.adjust2, container, false);
		findView(v);

		// 当前CPC的SharedPreferences
		sp = getActivity().getSharedPreferences("CPC1Info",
				Context.MODE_PRIVATE);
		// set的dim值的SharedPreferences
//		spReadDim = getActivity().getSharedPreferences("setdataofCPC",
//				Context.MODE_PRIVATE);
		// 查询到的每个调光结果的SharedPreferences
		spSetDim = getActivity().getSharedPreferences("adjustSetDim",
				Context.MODE_PRIVATE);
		setlight(bt1);
		CLnum = sp.getInt("CLn", 0);
		InitView();// 初始化各个控件
		createListener();// 初始化各类监听

		return v;
	}

	/**
	 * findViewById
	 */
	private void findView(View v) {
		listView = (ListView) v.findViewById(R.id.listView_adjust);

		bt1 = (Button) v.findViewById(R.id.cpc1_adjust);
		bt2 = (Button) v.findViewById(R.id.cpc2_adjust);
		bt3 = (Button) v.findViewById(R.id.cpc3_adjust);
		bt4 = (Button) v.findViewById(R.id.cpc4_adjust);
		bt5 = (Button) v.findViewById(R.id.cpc5_adjust);
		bt6 = (Button) v.findViewById(R.id.cpc6_adjust);

		gotocl = (Button) v.findViewById(R.id.adjust_save);

	}

	/**
	 * 初始化各个控件
	 * 
	 */
	private void InitView() {
		// Editor edit = spSetDim.edit();
		arrayList = new ArrayList<AdjBean>();
		int temp;
		for (int i = 0; i < CLnum; ++i) {
			int t = sp.getInt("CL" + (i + 1), 1);
			temp = spSetDim.getInt("dim" + curCPC + t / 10 + t % 10, 1);
			AdjBean ab = new AdjBean(t, temp != 11, temp);
			arrayList.add(ab);
		}
		listView.setAdapter(new AdjAdapter());
	}

	private void createListener() {
		// CPC1-4 Listener
		final Button[] bts = { bt1, bt2, bt3, bt4, bt5, bt6 };
		for (int i = 0; i < 6; ++i) {
			final int j = i + 1;
			bts[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reback(bts[curCPC - 1]);// 去除点击加亮效果
					curCPC = j;
					setlight(bts[curCPC - 1]);// 设置点击加亮效果
					sp = getActivity().getSharedPreferences("CPC" + j + "Info",
							Context.MODE_PRIVATE);
					CLnum = sp.getInt("CLn", 0);
					InitView();
				}
			});
		}
		
		// goto cl
		gotocl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 设置Intent并启动activity
				Intent intent = new Intent(getActivity(), RunActivity.class);
				intent.putExtra("no", 1);
				startActivity(intent);
				getActivity().finish();
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

	/**
	 * Dim对话框 设置当前Dim
	 */
	private void adjustdim(TextView ttv, int no) {
		final TextView tv = ttv;// 当前点击的textview
		final int pos = no - 1;// Dim设置值
		final int j = sp.getInt("CL" + no, 1);
		LayoutInflater myLayout = LayoutInflater.from(getActivity());
		AlertDialog.Builder builder = new Builder(getActivity());
		View numberPickDialog = myLayout.inflate(R.layout.numberpick_dialog,
				null);

		final NumberPicker numberpick = (NumberPicker) numberPickDialog
				.findViewById(R.id.numberpick_dialog);
		/*
		 * 注意，只有mHour对象的maxValue-minValue>=mSelectorIndices.length时。
		 * 执行setWrapSelectorWheel才会有效。而mSelectorIndices数组的长度为3。
		 * 再注意，设置了setWrapSelectorWheel后需要执行setDisplayedValues
		 * 。才会更新列表，此列表才不是循环的。所以才会有以上这样的代码顺序。
		 * 由于maxValue>hourStrs.length，所以，需要先执行setDisplayedValues，
		 * 再由于执行setDisplayedValues如果传入的数组等于原来的数组，就不会更新，所以需要传入一个hourStrsEmpty。
		 * hourStrsEmpty数组内容不能有null,长度等于hourStrs
		 */
		numberpick.setMinValue(0);
		numberpick.setMaxValue(displayedValues.length - 2);
		numberpick.setDisplayedValues(displayedValues);
		numberpick.setWrapSelectorWheel(false);
		numberpick.getChildAt(0).setFocusable(false);
		numberpick.setValue(spSetDim
				.getInt("dim" + curCPC + j / 10 + j % 10, 1));

		builder.setView(numberPickDialog).setTitle("添加动作");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				tv.setText(displayedValues[numberpick.getValue()]);
				// 存储当前值
				Editor edit = spSetDim.edit();
				edit.putInt("dim" + curCPC + j / 10 + j % 10,
						numberpick.getValue());
				edit.commit();
				// 更新数据
				arrayList.get(pos).setDim(numberpick.getValue());
				// 启动service并下发控制命令
				Intent intent = new Intent(getActivity(), ControlService.class);
				Bundle bund = new Bundle();
				bund.putInt("curCPC", curCPC);
				bund.putByte("controlByte", (byte) 0x08);
				bund.putByteArray("usefulData",
						getUsefuldata(j, numberpick.getValue()));
				intent.putExtras(bund);
				getActivity().startService(intent);
			}
		});
		builder.setNegativeButton("取消", null).show();
	}

	/**
	 * 开关选择
	 * 
	 * @param open
	 */
	private void switchChanged(boolean open, int pos) {
		int pickvalue = 12;
		if (open)
			pickvalue = 13;
		int j = sp.getInt("CL" + pos, 1);
		arrayList.get(pos - 1).setIson(open);
		Intent intent = new Intent(getActivity(), ControlService.class);
		Bundle bund = new Bundle();
		bund.putInt("curCPC", curCPC);// 设置CPC号
		bund.putByte("controlByte", (byte) 0x08);// 设置控制码
		bund.putByteArray("usefulData", getUsefuldata(j, pickvalue));// 得到有用数据
		intent.putExtras(bund);
		getActivity().startService(intent);// 启动服务
		// send frame
	}

	/**
	 * 
	 * @param no  真实回路号
	 * @param pickvalue  dim设置值，转化为相应的控制功能
	 * @return 报文的有效码
	 */
	private byte[] getUsefuldata(int no, int pickvalue) {
//		String num = "" + (curCPC + 1) + (curCPC + 1) + (no + 1) + pickvalue;
		byte[] res = new byte[10 + 6];
		int i = 0;
		for (; i < 2; ++i) {
			res[i] = 0x00;
		}
		for (; i < 6; ++i) {
			res[i] = 0x11;
		}
		res[6] = 0x01;
		res[7] = 0x02;
		res[8] = 0x01;
		res[9] = (byte) sp.getInt("CL" + no, no);
		res[10] = 0x00;
		res[12] = 0x00;
		res[13] = 0x00;
		res[14] = 0x00;
		res[15] = 0x00;
		if (pickvalue == 13) {
			res[11] = 0x2C;
		} else if (pickvalue == 12) {
			res[11] = 0x2D;
		} else {
			res[11] = 0x2B;
			int[][] dim = ((XtoeeApp) getActivity().getApplication())
					.getDimDate(curCPC);
			int para = 10 * dim[no - 1][10 - pickvalue];
			res[12] = inttoBCD(para / 100);
			res[13] = inttoBCD(para % 100);
			// System.out.println("dim[0] is : ");
			// for(int e:dim[0]){
			// System.out.print(e+" ");
			// }
			// System.out.println();
		}

		return res;
	}

	/**
	 * 十进制转化BCD码
	 * @param n  待转化十进制
	 * @return   对应BCD码
	 */
	private byte inttoBCD(int n) {
		byte res = 0;
		res = (byte) ((((byte) n / 10) << 4) + (byte) n % 10);
		return res;
	}

	private class AdjAdapter extends BaseAdapter {

		public class AdjHolder {
			public TextView CLname, CLdim;
			public SwitchView sw;
		}

		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AdjHolder holder = null;
			final int pos = position + 1;
			if (convertView == null) {
				holder = new AdjHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.adjust_element, null);
				holder.CLname = (TextView) convertView
						.findViewById(R.id.name_adj);
				holder.CLdim = (TextView) convertView
						.findViewById(R.id.dim_adj);
				holder.sw = (SwitchView) convertView
						.findViewById(R.id.switch_adj);
				convertView.setTag(holder);
			} else {
				holder = (AdjHolder) convertView.getTag();
			}
			final AdjHolder mhold = holder;
			int tmp = arrayList.get(position).getClnum();
			holder.CLname.setText("L#0" + curCPC + tmp / 10 + tmp % 10);
			holder.CLdim.setText(displayedValues[arrayList.get(position)
					.getDim()]);
			holder.sw.setSwitchStatus(arrayList.get(position).isIson());
			holder.CLdim.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					adjustdim(mhold.CLdim, pos);
				}
			});
			holder.sw.setOnSwitchChangeListener(new OnSwitchChangeListener() {
				@Override
				public void onSwitchChanged(boolean open) {
					switchChanged(open, pos);
				}
			});
			return convertView;
		}
	}
}
