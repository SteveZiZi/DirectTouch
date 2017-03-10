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
	private int getno = -1;// �����Ӳ˵�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activitycontrol);
		// ��������
		controlIntent = new Intent(ControlActivity.this, ControlService.class);
		startService(controlIntent);

		// �㲥�����������ղ���ʾ�������
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
		// �������Ĺ�����
		IntentFilter filter = new IntentFilter();
		filter.addAction("action.Control_BROADCAST");
		registerReceiver(receiver, filter);

		// ��ʼ��fragment
		slidingmenu = (SlidingMenu) findViewById(R.id.id_menuOfControl);
		((RelativeLayout) findViewById(R.id.headmenuofall))
				.setBackgroundColor(getResources().getColor(
						R.color.leftmenuofcontrol));
		// ��ǰ���activity�õ���Ҫ������fragment���
		getno = getIntent().getIntExtra("no", 0);
		if ((getno > -1) && (getno < 4)) {
			Fragment[] fragmentID = { new ProContFragment(),
					new SceneFragment(), new AdjustFragment(),
					new OffOnFragment() };
			String[] fragmentText = { "�̿�", "����", "����", "ͨ��" };
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, fragmentID[getno]).commit();// �滻fragment
			((TextView) findViewById(R.id.topl2_run))
					.setText(fragmentText[getno]);// �滻���ϽǶ�Ӧ������
			setMenuToWhite(getno);
		} else {
			// ��ʼ��һ����Ϊ-1��ֵ�Ա��л�
			getno = 0;
			// û��fragment�����򿪲����
			if (!(slidingmenu.getstate())) {
				slidingmenu.toggle();
			}
		}

		InitBt();// ��ʼ�����ֵ���¼�

		// ���õ��FrameLayout�����ջز����
		LinearLayout allright = (LinearLayout) findViewById(R.id.allright_control);
		allright.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (slidingmenu.getstate()) {
					slidingmenu.toggle();
				}
			}
		});
		// ���ϲ������л�����
		((ImageButton) findViewById(R.id.toplib_run))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						slidingmenu.toggle();
					}
				});
	}

	/**
	 * ��������
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
		// ע���㲥������
		unregisterReceiver(receiver);
		// ֹͣ����
		stopService(controlIntent);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ�����ֵ���¼�
	 */
	private void InitBt() {
		((Button) findViewById(R.id.procont_cont)).setOnClickListener(this);
		((Button) findViewById(R.id.scene_cont)).setOnClickListener(this);
		((Button) findViewById(R.id.adjust_cont)).setOnClickListener(this);
		((Button) findViewById(R.id.offon_cont)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.toprib)).setOnClickListener(this);

	}

	/**
	 * ����¼��ľ���ִ������
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// �л�activity
		case R.id.runOfCont:
			startActivity(new Intent(getBaseContext(), RunActivity.class));
			finish();
			break;

		case R.id.systOfCont:
			startActivity(new Intent(getBaseContext(), SystemActivity.class));
			finish();
			break;

		// �л�fragment
		case R.id.procont_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new ProContFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("�̿�");
			setMenuToWhite(0);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.scene_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new SceneFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("����");
			setMenuToWhite(1);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.adjust_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new AdjustFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("����");
			setMenuToWhite(2);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.offon_cont:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_control, new OffOnFragment())
					.commit();
			((TextView) findViewById(R.id.topl2_run)).setText("ͨ��");
			setMenuToWhite(3);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		// ����homeҳ
		case R.id.toprib:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * ��ѡ�еĶ����˵�����Ϊ��ɫ
	 * @param i  ѡ�еĶ����˵����0-3
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
