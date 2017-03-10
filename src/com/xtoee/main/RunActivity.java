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
	private boolean delays = false;// �ж��Ƿ�Ҫ���´洢��·Dimֵ����ֵ����adjust fragment��������Dim
	private int getno = -1;// �����Ӳ˵�

	TimerTask task = new TimerTask() {
		public void run() {
			delays = true;// Ϊtrueʱˢ��Dimֵ
		}
	};

	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activityrun);
		// �󶨷���
		conn = new MyServiceContection();
		connectServiceIntent = new Intent(this, ConnectService.class);
		bindService(connectServiceIntent, conn, BIND_AUTO_CREATE);

		slidingmenu = (SlidingMenu) findViewById(R.id.id_menuOfRun);
		((RelativeLayout) findViewById(R.id.headmenuofall))
				.setBackgroundColor(getResources().getColor(
						R.color.leftmenuofrun));

		// ��ʼ��FrameLayout
		getno = getIntent().getIntExtra("no", 0);// ��ǰ���activity�õ���Ҫ������fragment���
		if ((getno > -1) && (getno < 4)) {
			Fragment[] fragmentID = { new CPCFragment(), new ClFragment(),
					new CPRFragment(), new SubClFragment() };
			String[] fragmentText = { "���й�", "��·", "����ģ��", "�ӻ�·" };
			getFragmentManager().beginTransaction()
					.replace(R.id.content_run, fragmentID[getno]).commit();// �滻fragment
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
		timer.schedule(task, 1500, 1000); // ������ʱ��

		// ���õ��FrameLayout�����ջز����
		LinearLayout allright = ((LinearLayout) findViewById(R.id.allright_run));
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
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		// �洢ÿ����·��Dimֵ
		if (delays) {
			SharedPreferences spSetDim = getSharedPreferences("adjustSetDim",
					Context.MODE_PRIVATE);
			Editor edit = spSetDim.edit();
			// ��ȡ��ǰ��·����
			CLbean[] curdata = getCLbean();
			// �������洢Dimֵ
			for (int i = 1; i < 7; ++i) {
				int[][] dim = ((XtoeeApp) getApplication()).getDimDate(i);// ��ȡDim���ñ�ֵ
				for (int j = 1; j < 17; ++j) {
					edit.putInt(
							"dim" + i + j / 10 + j % 10,
							getDim(curdata[i * 16 + j - 17].getMv(), j - 1, dim));
				}
			}
			edit.commit();
			delays = false;
		}
		// �˳�������
		while (!task.cancel()) {
			timer.cancel();
		}
		// ������
		unbindService(conn);
		super.onDestroy();

	}

	/**
	 * ��ȡDImֵ
	 * @param v      ��ѹֵ
	 * @param clno   ��·��
	 * @param curdim Dim���ñ�ֵ
	 * @return Dim�ٷ�ֵ��ţ�0-10�ֱ��ʾ100%-0%,11��ʾ�ر�
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
	 * ��ʼ�����ֵ���¼�
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
	 * ����¼��ľ���ִ������
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// �л�activity
		case R.id.contOfRun:
			startActivity(new Intent(getBaseContext(), ControlActivity.class));
			finish();
			break;

		case R.id.systOfRun:
			startActivity(new Intent(getBaseContext(), SystemActivity.class));
			finish();
			break;

		// �л�fragment
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
			((TextView) findViewById(R.id.topl2_run)).setText("��·");
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
			((TextView) findViewById(R.id.topl2_run)).setText("�ӻ�·");
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

	// Ϊfragment�ṩ�Ļ�ȡ��̨���ݵĽӿ�
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
