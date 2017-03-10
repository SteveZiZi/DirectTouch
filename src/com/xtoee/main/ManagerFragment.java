package com.xtoee.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.xtoee.services.SystemMenuService;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ManagerFragment extends Fragment {

	private TextView vtime;
	private Button vadjust, valter;
	private EditText vphone;
	private boolean phoneChanged = false, pwdChanged = false;
	private Button bt1, bt2, bt3, bt4, bt5, bt6;// CPC1-4
	private int curCPC = 1;

	private TextView vpwd;
	private Button btpwd;
	private ImageView imgon, imgoff;

	private SharedPreferences sp, splogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.manager, container, false);
		sp = getActivity().getApplicationContext().getSharedPreferences(
				"config", Context.MODE_PRIVATE);
		splogin = getActivity().getApplicationContext().getSharedPreferences(
				"login", Context.MODE_PRIVATE);
		bt1 = (Button) v.findViewById(R.id.cpc1_manager);
		bt2 = (Button) v.findViewById(R.id.cpc2_manager);
		bt3 = (Button) v.findViewById(R.id.cpc3_manager);
		bt4 = (Button) v.findViewById(R.id.cpc4_manager);
		bt5 = (Button) v.findViewById(R.id.cpc5_manager);
		bt6 = (Button) v.findViewById(R.id.cpc6_manager);
		vtime = (TextView) v.findViewById(R.id.time_manager);
		vphone = (EditText) v.findViewById(R.id.phone_manager);
		vadjust = (Button) v.findViewById(R.id.timeAdjust);
		valter = (Button) v.findViewById(R.id.phoneAlter);
		vpwd = (TextView) v.findViewById(R.id.pwd_manager);
		btpwd = (Button) v.findViewById(R.id.pwdAlter);
		imgon = (ImageView) v.findViewById(R.id.saveon);
		imgoff = (ImageView) v.findViewById(R.id.saveoff);

		setlight(bt1);
		setListener();// 校时监听
		InitView(1);// 初始化各类控件并显示
		return v;
	}

	/**
	 * 初始化控件并显示
	 * 
	 * @param v
	 */
	private void InitView(int no) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm",
				Locale.getDefault());
		String now = sdf.format(Calendar.getInstance().getTime());
		vtime.setText(now);// 时间
		vphone.setText(sp.getString("phone" + no, "18959222334"));// 管理者电话
		vphone.setEnabled(phoneChanged);// 修改 可点击
		vpwd.setEnabled(pwdChanged);
		int visib = View.INVISIBLE;
		if (splogin.getBoolean("autoLogin", false))
			visib = View.VISIBLE;
		imgon.setVisibility(visib);

	}

	private void setListener() {
		final Button[] bts = { bt1, bt2, bt3, bt4, bt5, bt6 };
		for (int i = 0; i < 6; ++i) {
			final int j = i + 1;
			bts[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reback(bts[curCPC - 1]);
					curCPC = j;
					setlight(bts[curCPC - 1]);
					InitView(j);
					// 加载数据并显示
				}
			});
		}

		vadjust.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 启动校时服务
				Intent intent = new Intent(getActivity(),
						SystemMenuService.class);
				intent.putExtra("taskNo", 2);// 校时任务号2
				intent.putExtra("CPCno", curCPC);
				getActivity().startService(intent);
				Toast.makeText(getActivity(), "已校时", Toast.LENGTH_SHORT).show();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm",
						Locale.getDefault());
				String now = sdf.format(Calendar.getInstance().getTime());
				vtime.setText(now);// 时间

			}
		});
		// 修改电话Button监听
		valter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 修改 是否 可点击
				phoneChanged = !phoneChanged;
				vphone.setEnabled(phoneChanged);
				if (!phoneChanged) {
					// 可修改，则修改电话号码
					String phone = vphone.getText().toString().trim();
					if (phone.length() != 11) {
						// 检查是否为11位电话号码
						Toast.makeText(getActivity(), "输入号码有误，请重新输入11位号码",
								Toast.LENGTH_SHORT).show();
						phoneChanged = !phoneChanged;
						vphone.setEnabled(phoneChanged);
						return;
					} else {
						// 否则启动服务，校正管理者电话
						Editor edit = sp.edit();
						edit.putString("phone" + curCPC, vphone.getText()
								.toString().trim());
						edit.commit();
						valter.setText("修改");
						Intent intent = new Intent(getActivity(),
								SystemMenuService.class);
						intent.putExtra("taskNo", 3);// 修改号码任务号3
						intent.putExtra("CPCno", curCPC);
						getActivity().startService(intent);
					}
				} else {
					valter.setText("确定");
				}
			}
		});
		// 修改密码Button监听
		btpwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PwdReset reset = new PwdReset(getActivity());
				reset.resetPwd();
			}
		});
		imgoff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imgon.setVisibility(View.VISIBLE);
				Editor edit = splogin.edit();
				edit.putBoolean("autoLogin", true);
				edit.commit();
			}
		});

		imgon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imgon.setVisibility(View.INVISIBLE);
				Editor edit = splogin.edit();
				edit.putBoolean("autoLogin", false);
				edit.commit();
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

}
