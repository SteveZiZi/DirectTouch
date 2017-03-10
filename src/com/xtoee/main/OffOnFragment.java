package com.xtoee.main;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.xtoee.services.ControlService;
import com.xtoee.tools.SwitchTitle;
import com.xtoee.tools.SwitchView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

public class OffOnFragment extends Fragment {

	private int curCPC = 1;
	private int curCL = 1;
	private Spinner spinner;
	private Button fresh;
	private TextView[] tvno = new TextView[8];
	private TextView[] tvname = new TextView[8];
	private SwitchView[] swc = new SwitchView[8];
	private SharedPreferences sp, spRead;
	private String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H" };// 名字
	private byte[] unuserful = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x02, 0x02 };// 无用的报文
	private Boolean isover = false;

	private int CLnum;
	private SwitchTitle swtitle;
	private TextView[] titleView = new TextView[16];
	private ArrayList<String> listTitle;

	private MyHandler handler = new MyHandler(getActivity()) {
		public void handleMessage(Message msg) {
			InitView();
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.offon, container, false);
		// 继电器名字sharedPreferences
		sp = getActivity().getSharedPreferences("offon", Context.MODE_PRIVATE);
		findView(v);// findViewByID寻找各类控件
		spinner = (Spinner) v.findViewById(R.id.spinner_offon);
		createSpinnerByResource();// findViewByID寻找各类控件

		InitView();// 初始化界面控件
		createTitleData();
		createListener();
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isover = true;
	}

	/**
	 * findViewByID寻找各类控件
	 * 
	 * @param v
	 *            CreateView函数返回总View
	 */
	private void findView(View v) {
		swtitle = (SwitchTitle) v.findViewById(R.id.clTitle_offon);
		swtitle.setBackground(Color.argb(0xff, 0x4b, 0xbb, 0x19));
		fresh = (Button) v.findViewById(R.id.fresh);

		tvno[0] = (TextView) v.findViewById(R.id.subcl1_offon);
		tvno[1] = (TextView) v.findViewById(R.id.subcl2_offon);
		tvno[2] = (TextView) v.findViewById(R.id.subcl3_offon);
		tvno[3] = (TextView) v.findViewById(R.id.subcl4_offon);
		tvno[4] = (TextView) v.findViewById(R.id.subcl5_offon);
		tvno[5] = (TextView) v.findViewById(R.id.subcl6_offon);
		tvno[6] = (TextView) v.findViewById(R.id.subcl7_offon);
		tvno[7] = (TextView) v.findViewById(R.id.subcl8_offon);

		tvname[0] = (TextView) v.findViewById(R.id.name1_offon);
		tvname[1] = (TextView) v.findViewById(R.id.name2_offon);
		tvname[2] = (TextView) v.findViewById(R.id.name3_offon);
		tvname[3] = (TextView) v.findViewById(R.id.name4_offon);
		tvname[4] = (TextView) v.findViewById(R.id.name5_offon);
		tvname[5] = (TextView) v.findViewById(R.id.name6_offon);
		tvname[6] = (TextView) v.findViewById(R.id.name7_offon);
		tvname[7] = (TextView) v.findViewById(R.id.name8_offon);

		swc[0] = (SwitchView) v.findViewById(R.id.switch1_offon);
		swc[1] = (SwitchView) v.findViewById(R.id.switch2_offon);
		swc[2] = (SwitchView) v.findViewById(R.id.switch3_offon);
		swc[3] = (SwitchView) v.findViewById(R.id.switch4_offon);
		swc[4] = (SwitchView) v.findViewById(R.id.switch5_offon);
		swc[5] = (SwitchView) v.findViewById(R.id.switch6_offon);
		swc[6] = (SwitchView) v.findViewById(R.id.switch7_offon);
		swc[7] = (SwitchView) v.findViewById(R.id.switch8_offon);

	}

	/**
	 * 初始化界面控件
	 */
	private void InitView() {
		spRead = getActivity().getSharedPreferences("CPC" + curCPC + "Info",
				Context.MODE_PRIVATE);
		// 回路号
		int t = spRead.getInt("CL" + curCL, 1);
		int state = sp.getInt("ison" + curCPC + t / 10 + t % 10, 0);
		for (int i = 0; i < 8; ++i) {
			tvno[i].setText("#0" + curCPC + t / 10 + t % 10 + alphabet[i]);// 子路号
			tvname[i].setText(sp.getString("name" + curCPC + t / 10 + t % 10
					+ (i + 1), "名称" + (i + 1)));// 名称
			swc[i].setSwitchStatus((state & (1 << i)) > 0);// 通断
		}
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
				curCPC = position + 1;
				curCL = 1;
				InitView();
				createTitleData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	/**
	 * 回路标题
	 */
	private void createTitleData() {
		listTitle = new ArrayList<String>();
		CLnum = spRead.getInt("CLn", 0);
		for (int i = 1; i <= CLnum; ++i) {
			listTitle.add("回路" + spRead.getInt("CL" + i, 1));
		}
		for (int i = 0; i < titleView.length; ++i) {
			titleView[i] = null;
		}
		swtitle.setData(listTitle);
		swtitle.setAdapter(new TitleAdapter());
	}

	private void createListener() {
		// 刷新
		fresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), ControlService.class);
				Bundle bund = new Bundle();
				bund.putInt("curCPC", curCPC);
				bund.putBoolean("isChangeOffon", true);
				bund.putByte("controlByte", (byte) 0x01);
				bund.putByteArray("usefulData", unuserful);
				intent.putExtras(bund);
				getActivity().startService(intent);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500);
							if (!isover) {
								handler.sendEmptyMessage(0);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});

		// 名称1-8 Listener
		for (int i = 0; i < 8; ++i) {
			final int j = i + 1;
			tvname[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final TextView mtv = (TextView) v;
					// 对话框的layout布局
					final View mydialog = LayoutInflater.from(getActivity())
							.inflate(R.layout.dialog_layout, null);
					// 对话框设置
					new AlertDialog.Builder(getActivity())
							.setTitle("请输入名称")
							.setIcon(android.R.drawable.ic_menu_edit)
							.setView(mydialog)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// 显示并保存子回路名
											String name = ((EditText) mydialog
													.findViewById(R.id.dialogcontent))
													.getText().toString()
													.trim();
											mtv.setText(name);
											int t = spRead.getInt("CL" + curCL,
													1);
											Editor edit = sp.edit();
											edit.putString("name" + curCPC + t
													/ 10 + t % 10 + j, name);
											edit.commit();
										}
									}).setNegativeButton("取消", null).show();
				}
			});

		}
		// 开关 1-8 Listener
		for (int i = 0; i < 8; ++i) {
			final int j = i;
			swc[i].setOnSwitchChangeListener(new SwitchView.OnSwitchChangeListener() {
				@Override
				public void onSwitchChanged(boolean open) {
					// 如果检测无此回路，提示
					int t = spRead.getInt("CL" + curCL, 1);
					if (spRead.getInt("subTn" + t, -1) == -1) {
						Toast toast = Toast.makeText(getActivity(),
								"检测无此回路，请勿于此页面操作", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					} else {
						// 存储状态
						int state = sp.getInt("ison" + (curCPC + 1) + t / 10
								+ t % 10, 0);
						Editor edit = sp.edit();
						if (open)
							state = state | (1 << j);
						else
							state = state & (255 - 1 << j);
						edit.putInt("ison" + curCPC + t / 10 + t % 10, state);
						edit.commit();
						// 启动控制
						Intent intent = new Intent(getActivity(),
								ControlService.class);
						Bundle bund = new Bundle();
						bund.putInt("curCPC", curCPC);
						bund.putByte("controlByte", (byte) 0x08);
						bund.putByteArray("usefulData", getUsefuldata(j));
						intent.putExtras(bund);
						getActivity().startService(intent);
					}
				}

			});
		}
	}

	/**
	 * 去除点击加亮效果
	 * 
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
	 * 
	 */
	private void setlight(TextView bt) {
		if(bt==null) return ;
		bt.setTextSize(getActivity().getResources().getDimension(R.dimen.light));
		bt.setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	private byte[] getUsefuldata(int j) {
		byte subno = 0x0D;
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
		res[9] = inttobyte(spRead.getInt(
				"subTn" + spRead.getInt("CL" + curCL, 1), curCL));
		res[10] = (byte) (subno + j);
		res[11] = 0x0A;
		if (swc[j].getSwitchStatus())
			res[11] = 0x09;
		res[12] = 0x00;
		res[13] = 0x00;
		res[14] = 0x00;
		res[15] = 0x00;
		return res;
	}

	public byte inttobyte(int n) {
		byte res = 0;
		if (n != 0) {
			for (int i = 0; i < 8; ++i) {
				if ((n & (1 << i)) > 0) {
					res = (byte) (res | (1 << i));
				}
			}
		}
		return res;
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
						reback(titleView[curCL - 1]);
						curCL = gg + 1;
						setlight(titleView[curCL - 1]);
						InitView();
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
