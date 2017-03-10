package com.xtoee.main;

import com.xtoee.services.SystemMenuService;
import com.xtoee.tools.SlidingMenu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SystemActivity extends Activity implements OnClickListener {

	private SlidingMenu slidingmenu;
	private Intent systemIntent;
	private int getno = -1;// 二级子菜单

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activitysystem);
		// 启动服务
		systemIntent = new Intent(this, SystemMenuService.class);
		systemIntent.putExtra("taskNo", 4);// 查询error
		startService(systemIntent);

		slidingmenu = (SlidingMenu) findViewById(R.id.id_menuOfSyst);
		((RelativeLayout) findViewById(R.id.headmenuofall))
				.setBackgroundColor(getResources().getColor(
						R.color.leftmenuofsystem));
		InitBt();// 初始化各种点击事件

		// 设置点击FrameLayout区域收回侧边栏
		LinearLayout allright = (LinearLayout) findViewById(R.id.allright_syst);
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
		// 初始化fragment
		getno = getIntent().getIntExtra("no", 3);// 从前面的activity得到需要启动的fragment标号
		if ((getno > -1) && (getno < 4)) {
			Fragment[] fragmentID = { new CommonFragment(),
					new ManagerFragment(), new SetFragment(),
					new RecordFragment() };
			String[] fragmentText = { "通讯", "管理", "设置", "日志" };
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, fragmentID[getno]).commit();// 替换fragment
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
	protected void onDestroy() {
		// 停止服务
		stopService(systemIntent);
		super.onDestroy();
	}

	/**
	 * 初始化各种点击事件
	 */
	private void InitBt() {
		// ((ImageButton)findViewById(R.id.contOfSyst)).setOnClickListener(this);
		// ((ImageButton)findViewById(R.id.runOfSyst)).setOnClickListener(this);
		((Button) findViewById(R.id.commom_syst)).setOnClickListener(this);
		((Button) findViewById(R.id.manager_syst)).setOnClickListener(this);
		((Button) findViewById(R.id.set_syst)).setOnClickListener(this);
		((Button) findViewById(R.id.record_syst)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.toprib)).setOnClickListener(this);
	}

	/**
	 * 点击事件的具体执行内容
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 切换activity
		case R.id.contOfSyst:
			startActivity(new Intent(getBaseContext(), ControlActivity.class));
			finish();
			break;

		case R.id.runOfSyst:
			startActivity(new Intent(getBaseContext(), RunActivity.class));
			finish();
			break;

		// 切换fragment
		case R.id.commom_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new CommonFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("通讯");
			setMenuToWhite(0);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.manager_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new ManagerFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("管理");
			setMenuToWhite(1);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.set_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new SetFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("设置");
			setMenuToWhite(2);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.record_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new RecordFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("日志");
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
	 * @param i   选中的二级菜单编号0-3
	 */
	private void setMenuToWhite(int i) {
		Button[] menu = { (Button) findViewById(R.id.commom_syst),
				(Button) findViewById(R.id.manager_syst),
				(Button) findViewById(R.id.set_syst),
				(Button) findViewById(R.id.record_syst) };
		if ((getno > -1) && (getno < 4)) {
			menu[getno].setTextColor(getResources().getColor(R.color.black));
			getno = i;
			menu[getno]
					.setTextColor(getResources().getColor(R.color.menuwhite));
		}
	}

}
