package com.xtoee.main;

import com.xtoee.services.ControlService;
import com.xtoee.tools.SlidingMenu;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ControlActivity extends Activity implements OnClickListener {

	private SlidingMenu slidingmenu;
	private Intent controlIntent;
	private BroadcastReceiver receiver;
	private int getno = -1;// 二级子菜单

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activitycontrol);
		// 启动服务
		controlIntent = new Intent(ControlActivity.this, ControlService.class);
		startService(controlIntent);

		// 广播接收器，接收并显示操作结果
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String mess = intent.getStringExtra("msg");
				Toast toast = Toast.makeText(ControlActivity.this, mess,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

			}
		};
		// 接收器的过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction("action.Control_BROADCAST");
		registerReceiver(receiver, filter);

		// 初始化fragment
		slidingmenu = (SlidingMenu) findViewById(R.id.id_menuOfControl);
		((RelativeLayout) findViewById(R.id.headmenuofall))
				.setBackgroundColor(getResources().getColor(
						R.color.leftmenuofcontrol));
		// 从前面的activity得到需要启动的fragment标号
		getno = getIntent().getIntExtra("no", 0);
		if ((getno > -1) && (getno < 4)) {
			Fragment[] fragmentID = { new ProContFragment(),
					new SceneFragment(), new AdjustFragment(),
					new OffOnFragment() };
			String[] fragmentText = { "程控", "场景", "调光", "通断" };
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, fragmentID[getno]).commit();// 替换fragment
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

		// 设置点击FrameLayout区域收回侧边栏
		LinearLayout allright = (LinearLayout) findViewById(R.id.allright_control);
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
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收器
		unregisterReceiver(receiver);
		// 停止服务
		stopService(controlIntent);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化各种点击事件
	 */
	private void InitBt() {
		((Button) findViewById(R.id.procont_cont)).setOnClickListener(this);
		((Button) findViewById(R.id.scene_cont)).setOnClickListener(this);
		((Button) findViewById(R.id.adjust_cont)).setOnClickListener(this);
		((Button) findViewById(R.id.offon_cont)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.toprib)).setOnClickListener(this);

	}

	/**
	 * 点击事件的具体执行内容
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 切换activity
		case R.id.runOfCont:
			startActivity(new Intent(getBaseContext(), RunActivity.class));
			finish();
			break;

		case R.id.systOfCont:
			startActivity(new Intent(getBaseContext(), SystemActivity.class));
			finish();
			break;

		// 切换fragment
		case R.id.procont_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new ProContFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("程控");
			setMenuToWhite(0);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.scene_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new SceneFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("场景");
			setMenuToWhite(1);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.adjust_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new AdjustFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("调光");
			setMenuToWhite(2);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.offon_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new OffOnFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("通断");
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
		Button[] menu = { (Button) findViewById(R.id.procont_cont),
				(Button) findViewById(R.id.scene_cont),
				(Button) findViewById(R.id.adjust_cont),
				(Button) findViewById(R.id.offon_cont) };
		if ((getno > -1) && (getno < 4)) {
			menu[getno].setTextColor(getResources().getColor(R.color.black));
			getno = i;
			menu[getno]
					.setTextColor(getResources().getColor(R.color.menuwhite));
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Intent intent = new Intent(getBaseContext(), ControlService.class);
		intent.addFlags(123);
		startService(intent);
	}

}
