package com.xtoee.main;

import java.util.Timer;
import java.util.TimerTask;

import com.xtoee.bean.CLbean;
import com.xtoee.bean.CPRbean;
import com.xtoee.bean.SubCLbean;
import com.xtoee.services.ConnectService;
import com.xtoee.services.ConnectService.MyBinder;
import com.xtoee.tools.SlidingMenu;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RunActivity extends Activity implements OnClickListener {

	private SlidingMenu slidingmenu;
	private Intent connectServiceIntent;
	private MyServiceContection conn = null;
	private ConnectService connectService = null;
	private boolean delays = false;// 判断是否要重新存储回路Dim值，此值用于adjust fragment界面设置Dim
	private int getno = -1;// 二级子菜单

	TimerTask task = new TimerTask() {
		public void run() {
			delays = true;// 为true时刷新Dim值
		}
	};

	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activityrun);
		// 绑定服务
		conn = new MyServiceContection();
		connectServiceIntent = new Intent(this, ConnectService.class);
		bindService(connectServiceIntent, conn, BIND_AUTO_CREATE);

		slidingmenu = (SlidingMenu) findViewById(R.id.id_menuOfRun);
		((RelativeLayout) findViewById(R.id.headmenuofall))
				.setBackgroundColor(getResources().getColor(
						R.color.leftmenuofrun));

		// 初始化FrameLayout
		getno = getIntent().getIntExtra("no", 0);// 从前面的activity得到需要启动的fragment标号
		if ((getno > -1) && (getno < 4)) {
			Fragment[] fragmentID = { new CPCFragment(), new ClFragment(),
					new CPRFragment(), new SubClFragment() };
			String[] fragmentText = { "集中柜", "回路", "整流模块", "子回路" };
			getFragmentManager().beginTransaction()
					.replace(R.id.content_run, fragmentID[getno]).commit();// 替换fragment
			((TextView) findViewById(R.id.topl2_run))
					.setText(fragmentText[getno]);// 替换左上角对应的名字
			setMenuToWhite(getno);
		} else {
			// 初始化一个不为-1的值以便切换
			getno = 0;
			// 没有fragment标号则打开侧边栏
			if (!(slidingmenu.getstate())) {
				slidingmenu.toggle();
			}
		}

		InitBt();// 初始化各种点击事件
		timer.schedule(task, 1500, 1000); // 启动定时器

		// 设置点击FrameLayout区域收回侧边栏
		LinearLayout allright = ((LinearLayout) findViewById(R.id.allright_run));
		allright.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (slidingmenu.getstate()) {
					slidingmenu.toggle();
				}
			}
		});
		// 右上侧侧边栏切换开关
		((ImageButton) findViewById(R.id.toplib_run))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						slidingmenu.toggle();
					}
				});
	}

	/**
	 * 保持竖屏
	 */
	@Override
	protected void onResume() {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		// 存储每个回路的Dim值
		if (delays) {
			SharedPreferences spSetDim = getSharedPreferences("adjustSetDim",
					Context.MODE_PRIVATE);
			Editor edit = spSetDim.edit();
			// 获取当前回路数据
			CLbean[] curdata = getCLbean();
			// 分析并存储Dim值
			for (int i = 1; i < 7; ++i) {
				int[][] dim = ((XtoeeApp) getApplication()).getDimDate(i);// 读取Dim配置表值
				for (int j = 1; j < 17; ++j) {
					edit.putInt(
							"dim" + i + j / 10 + j % 10,
							getDim(curdata[i * 16 + j - 17].getMv(), j - 1, dim));
				}
			}
			edit.commit();
			delays = false;
		}
		// 退出任务器
		while (!task.cancel()) {
			timer.cancel();
		}
		// 解绑服务
		unbindService(conn);
		super.onDestroy();

	}

	/**
	 * 获取DIm值
	 * @param v      电压值
	 * @param clno   回路号
	 * @param curdim Dim配置表值
	 * @return Dim百分值标号，0-10分别表示100%-0%,11表示关闭
	 */
	private int getDim(double v, int clno, int[][] curdim) {
		if (v < 100)
			return 11;
		if (v < curdim[clno][0])
			return 10;
		for (int i = 1; i < 11; ++i) {
			if (v < curdim[clno][i]) {
				if ((v - curdim[clno][i - 1]) > (curdim[clno][i] - v)) {
					return 10 - i;
				} else {
					return 11 - i;
				}
			}
		}
		return 0;
	}

	/**
	 * 初始化各种点击事件
	 */
	private void InitBt() {
		// ((ImageButton)findViewById(R.id.contOfRun)).setOnClickListener(this);
		// ((ImageButton)findViewById(R.id.systOfRun)).setOnClickListener(this);
		((Button) findViewById(R.id.cpc_run)).setOnClickListener(this);
		((Button) findViewById(R.id.cl_run)).setOnClickListener(this);
		((Button) findViewById(R.id.cpr_run)).setOnClickListener(this);
		((Button) findViewById(R.id.subcl_run)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.toprib)).setOnClickListener(this);
	}

	/**
	 * 点击事件的具体执行内容
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 切换activity
		case R.id.contOfRun:
			startActivity(new Intent(getBaseContext(), ControlActivity.class));
			finish();
			break;

		case R.id.systOfRun:
			startActivity(new Intent(getBaseContext(), SystemActivity.class));
			finish();
			break;

		// 切换fragment
		case R.id.cpc_run:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_run, new CPCFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("CPC");
			setMenuToWhite(0);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.cl_run:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_run, new ClFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("回路");
			setMenuToWhite(1);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.cpr_run:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_run, new CPRFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("CPR");
			setMenuToWhite(2);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.subcl_run:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_run, new SubClFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("子回路");
			setMenuToWhite(3);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		// 返回home页
		case R.id.toprib:
			finish();
			break;

		default:
			break;
		}

	}

	/**
	 * 将选中的二级菜单设置为白色
	 * @param i  选中的二级菜单编号0-3
	 */
	private void setMenuToWhite(int i) {
		Button[] menu = { (Button) findViewById(R.id.cpc_run),
				(Button) findViewById(R.id.cl_run),
				(Button) findViewById(R.id.cpr_run),
				(Button) findViewById(R.id.subcl_run) };
		if ((getno > -1) && (getno < 4)) {
			menu[getno].setTextColor(getResources().getColor(R.color.black));
			getno = i;
			menu[getno]
					.setTextColor(getResources().getColor(R.color.menuwhite));
		}
	}

	// 为fragment提供的获取后台数据的接口
	public double[] getCPCVI() {
		return connectService.getCPCVI();
	}

	public CLbean[] getCLbean() {
		return connectService.getCLbean();
	}

	public CPRbean[] getCPRbean(int CPCno) {
		return connectService.getCPRbean(CPCno);
	}

	public SubCLbean[] getSubClbean() {
		return connectService.getSubClbean();
	}

	public byte[] getSubclOffon() {
		return connectService.getSubclOffon();
	}

	public class MyServiceContection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			connectService = ((MyBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	}

}
