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
	private int getno = -1;// �����Ӳ˵�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activitysystem);
		// ��������
		systemIntent = new Intent(this, SystemMenuService.class);
		systemIntent.putExtra("taskNo", 4);// ��ѯerror
		startService(systemIntent);

		slidingmenu = (SlidingMenu) findViewById(R.id.id_menuOfSyst);
		((RelativeLayout) findViewById(R.id.headmenuofall))
				.setBackgroundColor(getResources().getColor(
						R.color.leftmenuofsystem));
		InitBt();// ��ʼ�����ֵ���¼�

		// ���õ��FrameLayout�����ջز����
		LinearLayout allright = (LinearLayout) findViewById(R.id.allright_syst);
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
		// ��ʼ��fragment
		getno = getIntent().getIntExtra("no", 3);// ��ǰ���activity�õ���Ҫ������fragment���
		if ((getno > -1) && (getno < 4)) {
			Fragment[] fragmentID = { new CommonFragment(),
					new ManagerFragment(), new SetFragment(),
					new RecordFragment() };
			String[] fragmentText = { "ͨѶ", "����", "����", "��־" };
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, fragmentID[getno]).commit();// �滻fragment
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
		// ֹͣ����
		stopService(systemIntent);
		super.onDestroy();
	}

	/**
	 * ��ʼ�����ֵ���¼�
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
	 * ����¼��ľ���ִ������
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// �л�activity
		case R.id.contOfSyst:
			startActivity(new Intent(getBaseContext(), ControlActivity.class));
			finish();
			break;

		case R.id.runOfSyst:
			startActivity(new Intent(getBaseContext(), RunActivity.class));
			finish();
			break;

		// �л�fragment
		case R.id.commom_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new CommonFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("ͨѶ");
			setMenuToWhite(0);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.manager_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new ManagerFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("����");
			setMenuToWhite(1);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.set_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new SetFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("����");
			setMenuToWhite(2);
			if (slidingmenu.getstate()) {
				slidingmenu.toggle();
			}
			break;

		case R.id.record_syst:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_syst, new RecordFragment()).commit();
			((TextView) findViewById(R.id.topl2_run)).setText("��־");
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
	 * @param i   ѡ�еĶ����˵����0-3
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
