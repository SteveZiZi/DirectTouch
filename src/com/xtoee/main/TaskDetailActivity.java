package com.xtoee.main;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.xtoee.bean.Actionbean;
import com.xtoee.bean.Taskbean;
import com.xtoee.services.TaskSetService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TaskDetailActivity extends Activity implements
		View.OnClickListener {

	private SharedPreferences sp, spNo;
	// private SharedPreferences spReadDim;
	private int CPCNo, taskNo, taskno;
	private Taskbean task;
	private LayoutInflater myLayout;
	private Intent taskSetintent;
	private int MAXACTIONNUM = 12;

	private ScrollView scroll;//������Ļλ��
	private TextView vid, vname, vlevel, vbendtime, vloop, vcls;
	private RelativeLayout[] rl = new RelativeLayout[MAXACTIONNUM];
	private TextView[] timev = new TextView[MAXACTIONNUM];
	private TextView[] actv = new TextView[MAXACTIONNUM];
	private ImageButton[] deleteact = new ImageButton[MAXACTIONNUM];
	private Actionbean[] maction = new Actionbean[MAXACTIONNUM];
	private List<Actionbean> mlist = new LinkedList<Actionbean>();

	boolean[] loopWayCheckedItems = new boolean[7];
	boolean[] clsCheckedItems = new boolean[16];
	private String[] zhou = { "һ", "��", "��", "��", "��", "��", "��" };
	// String[] displayedValues =
	// {"�ر�","0%","10%","20%","30%","40%","50%","60%","70%","80%","90%","100%","��"};//��������
	String[] displayedValues = { "OFF", "0%", "10%", "20%", "30%", "40%", "50%",
			"60%", "70%", "80%", "90%", "100%" };// ��������
	// private int LEVELNUM = 11;
	String[] defauteLevel = { "��", "��", "��" };// ���ȼ�
	// byte[] bianma =
	// {0x2D,0x2B,0x2B,0x2B,0x2B,0x2B,0x2B,0x2B,0x2B,0x2B,0x2B,0x2B,0x2C};//�������Ͷ�Ӧ�Ŀ�����
	byte[] bianma = { 0x2D, 0x2B, 0x2B, 0x2B, 0x2B, 0x2B, 0x2B, 0x2B, 0x2B,
			0x2B, 0x2B, 0x2B };// �������Ͷ�Ӧ�Ŀ�����
	// private BroadcastReceiver receiver;

//	private int taskBegin, taskEnd;// ��ֹʱ�䣺Сʱ*100+����
	private String[] cls_all;
	
	private MyHandler handler = new MyHandler(TaskDetailActivity.this){
		public void handleMessage(android.os.Message msg) {
			//�����б�
			if(msg.what==1){
				scroll.scrollBy(0, 70);
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.task_detail);
		taskNo = getIntent().getIntExtra("No", 1);// �����1-12*6
		CPCNo = (taskNo + 11) / 12;// ����Ŷ�Ӧ��CPC��
		taskno = taskNo + 12 - 12 * CPCNo;// �����1-12
		taskSetintent = new Intent(this, TaskSetService.class);// ��activity��Ӧ��service��intent
		myLayout = LayoutInflater.from(this);
		sp = getSharedPreferences("task" + CPCNo + taskno, MODE_PRIVATE);// �������ݵ�SharedPreferences
		spNo = getSharedPreferences("CPC" + CPCNo + "Info", MODE_PRIVATE);// 040F��0202��ѯ��CPC��Ϣ��SharedPreferences
		int CLnum = spNo.getInt("CLn", 0);
		cls_all = new String[CLnum];
		for (int i = 0; i < CLnum; ++i) {
			cls_all[i] = "��·" + spNo.getInt("CL" + (i + 1), i + 1);
		}
		// spReadDim = getSharedPreferences("setdataofCPC",
		// Context.MODE_PRIVATE);//Dim���ñ��SharedPreferences
		task = new Taskbean(taskno, "����" + taskno, 0, 0L, 0L, (byte) 0x00,
				CPCNo, 0, 0);// ��ʼ��һ��TaskBean

		// ��ȡѭ����ʽ
		int a = sp.getInt("loopway", 0);
		byte ato = 0;
		if (a != 0) {
			for (int i = 0; i < 7; ++i) {
				if ((a & (1 << i)) > 0) {
					ato = (byte) (ato | (1 << i));
				}
			}
		}
		Calendar cal = Calendar.getInstance();
		task = new Taskbean(sp.getInt("ID", taskno), sp.getString("name", "����"
				+ taskno), sp.getInt("level", 0), sp.getLong("begintime",
				cal.getTimeInMillis()), sp.getLong("endtime",
				cal.getTimeInMillis()), ato, CPCNo, sp.getInt("cls", 0),
				sp.getInt("actionNum", 0));
		InitClick();// Ѱ�ҿؼ�����ʼ������¼�����
		InitView();// ��ʼ������ؼ���ʾ

		// receiver = new BroadcastReceiver() {
		// @Override
		// public void onReceive(Context context, Intent intent) {
		// String mess = intent.getStringExtra("msg");
		// Toast.makeText(TaskDetailActivity.this, mess,
		// Toast.LENGTH_LONG).show();
		// }
		// };
		// IntentFilter filter = new IntentFilter();
		// filter.addAction("action.TaskSetService");
		// registerReceiver(receiver, filter);

	}

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
		// unregisterReceiver(receiver);
		stopService(taskSetintent);
		// ����handle
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * findViewById ��ʼ���������¼�
	 */
	private void InitClick() {
		scroll = (ScrollView) findViewById(R.id.scroll_task_detail);
		
		vid = (TextView) findViewById(R.id.task_detail_id);
		// vid.setOnClickListener(this);
		vname = (TextView) findViewById(R.id.task_detail_name);
		vname.setOnClickListener(this);
		vlevel = (TextView) findViewById(R.id.task_detail_level);
		vlevel.setOnClickListener(this);
		vbendtime = (TextView) findViewById(R.id.task_detail_bendtime);
		vbendtime.setOnClickListener(this);
		vloop = (TextView) findViewById(R.id.task_detail_loopway);
		vloop.setOnClickListener(this);
		vcls = (TextView) findViewById(R.id.task_detail_cls);
		vcls.setOnClickListener(this);
		((Button) findViewById(R.id.task_detail_addaction))
				.setOnClickListener(this);
		((Button) findViewById(R.id.task_detail_save)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.task_detail_back))
				.setOnClickListener(this);

		rl[0] = (RelativeLayout) findViewById(R.id.task_detail_action1area);
		rl[1] = (RelativeLayout) findViewById(R.id.task_detail_action2area);
		rl[2] = (RelativeLayout) findViewById(R.id.task_detail_action3area);
		rl[3] = (RelativeLayout) findViewById(R.id.task_detail_action4area);
		rl[4] = (RelativeLayout) findViewById(R.id.task_detail_action5area);
		rl[5] = (RelativeLayout) findViewById(R.id.task_detail_action6area);
		rl[6] = (RelativeLayout) findViewById(R.id.task_detail_action7area);
		rl[7] = (RelativeLayout) findViewById(R.id.task_detail_action8area);
		rl[8] = (RelativeLayout) findViewById(R.id.task_detail_action9area);
		rl[9] = (RelativeLayout) findViewById(R.id.task_detail_action10area);
		rl[10] = (RelativeLayout) findViewById(R.id.task_detail_action11area);
		rl[11] = (RelativeLayout) findViewById(R.id.task_detail_action12area);

		timev[0] = (TextView) findViewById(R.id.task_detail_action1_time);
		timev[1] = (TextView) findViewById(R.id.task_detail_action2_time);
		timev[2] = (TextView) findViewById(R.id.task_detail_action3_time);
		timev[3] = (TextView) findViewById(R.id.task_detail_action4_time);
		timev[4] = (TextView) findViewById(R.id.task_detail_action5_time);
		timev[5] = (TextView) findViewById(R.id.task_detail_action6_time);
		timev[6] = (TextView) findViewById(R.id.task_detail_action7_time);
		timev[7] = (TextView) findViewById(R.id.task_detail_action8_time);
		timev[8] = (TextView) findViewById(R.id.task_detail_action9_time);
		timev[9] = (TextView) findViewById(R.id.task_detail_action10_time);
		timev[10] = (TextView) findViewById(R.id.task_detail_action11_time);
		timev[11] = (TextView) findViewById(R.id.task_detail_action12_time);

		actv[0] = (TextView) findViewById(R.id.task_detail_action1_action);
		actv[1] = (TextView) findViewById(R.id.task_detail_action2_action);
		actv[2] = (TextView) findViewById(R.id.task_detail_action3_action);
		actv[3] = (TextView) findViewById(R.id.task_detail_action4_action);
		actv[4] = (TextView) findViewById(R.id.task_detail_action5_action);
		actv[5] = (TextView) findViewById(R.id.task_detail_action6_action);
		actv[6] = (TextView) findViewById(R.id.task_detail_action7_action);
		actv[7] = (TextView) findViewById(R.id.task_detail_action8_action);
		actv[8] = (TextView) findViewById(R.id.task_detail_action9_action);
		actv[9] = (TextView) findViewById(R.id.task_detail_action10_action);
		actv[10] = (TextView) findViewById(R.id.task_detail_action11_action);
		actv[11] = (TextView) findViewById(R.id.task_detail_action12_action);

		deleteact[0] = (ImageButton) findViewById(R.id.task_detail_deleteaction1);
		deleteact[1] = (ImageButton) findViewById(R.id.task_detail_deleteaction2);
		deleteact[2] = (ImageButton) findViewById(R.id.task_detail_deleteaction3);
		deleteact[3] = (ImageButton) findViewById(R.id.task_detail_deleteaction4);
		deleteact[4] = (ImageButton) findViewById(R.id.task_detail_deleteaction5);
		deleteact[5] = (ImageButton) findViewById(R.id.task_detail_deleteaction6);
		deleteact[6] = (ImageButton) findViewById(R.id.task_detail_deleteaction7);
		deleteact[7] = (ImageButton) findViewById(R.id.task_detail_deleteaction8);
		deleteact[8] = (ImageButton) findViewById(R.id.task_detail_deleteaction9);
		deleteact[9] = (ImageButton) findViewById(R.id.task_detail_deleteaction10);
		deleteact[10] = (ImageButton) findViewById(R.id.task_detail_deleteaction11);
		deleteact[11] = (ImageButton) findViewById(R.id.task_detail_deleteaction12);

		for (int i = 0; i < MAXACTIONNUM; ++i) {
			deleteact[i].setOnClickListener(this);
		}

	}

	/**
	 * ��ʼ������ؼ���ʾ
	 */
	private void InitView() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm",
				Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		long lBeginTime = sp.getLong("begintime", 0);// ��ʼʱ��
		long lEndTime = sp.getLong("endtime", 0);// ����ʱ��
		// ��ʽ��ʱ����ʾ
		if (lBeginTime == 0) {
			str = str + sdf.format(cal.getTime()) + " - ";
//			taskBegin = cal.get(Calendar.HOUR_OF_DAY) * 100
//					+ cal.get(Calendar.MINUTE);
		} else {
			cal.setTimeInMillis(lBeginTime);
			str = str + sdf.format(cal.getTime()) + " - ";
//			taskBegin = cal.get(Calendar.HOUR_OF_DAY) * 100
//					+ cal.get(Calendar.MINUTE);
		}
		if (lEndTime == 0) {
			cal = Calendar.getInstance();
			str = str + sdf.format(cal.getTime());
//			taskEnd = cal.get(Calendar.HOUR_OF_DAY) * 100
//					+ cal.get(Calendar.MINUTE);
		} else {
			cal.setTimeInMillis(lEndTime);
			str = str + sdf.format(cal.getTime());
//			taskEnd = cal.get(Calendar.HOUR_OF_DAY) * 100
//					+ cal.get(Calendar.MINUTE);
		}
		vid.setText(task.getID() + "");// ����ID
		if ("".equals(task.getName()))
			vname.setHint("�������������");
		else
			vname.setText(task.getName());// ������
		vlevel.setText(defauteLevel[2 - task.getLevel()]);// ���ȼ�
		vbendtime.setText(str);// ��ֹʱ��
		// ѭ����ʽ
		str = "";
		int a = sp.getInt("loopway", 0);
		if (a != 0) {
			for (int i = 0; i < 7; ++i) {
				if ((a & (1 << i)) > 0) {
					if (!("".equals(str)))
						str += "��";
					str = str + "��" + zhou[i];
				}
			}
		}
		if ("".equals(str))
			str = "��������";
		vloop.setText(str);
		// CPC��
		((TextView) findViewById(R.id.task_detail_CPC))
				.setText("CPC" + (CPCNo));
		// ִ�л�·
		str = "";
		a = sp.getInt("cls", 0);
		if (a != 0) {
			for (int i = 0; i < 16; ++i) {
				if ((a & (1 << i)) > 0) {
					if (!("".equals(str)))
						str += "��";
					str = str + "��·" + (i + 1);
				}
			}
		}
		if ("".equals(str))
			str = "��������";
		vcls.setText(str);
		// ִ�ж���
		a = sp.getInt("actionNum", 0);
		for (int i = 0; i < MAXACTIONNUM; ++i) {
			maction[i] = new Actionbean(
					sp.getInt("actiontime" + (i + 1), 10000), sp.getInt(
							"actionact" + (i + 1), 10000));
			if (a > 0) {
				rl[i].setVisibility(View.VISIBLE);
				timev[i].setText(getShowTime(maction[i].getActime()));
				actv[i].setText(displayedValues[maction[i].getAct()]);
				mlist.add(maction[i]);
				--a;
			}
		}
	}

	/**
	 * �������¼�����
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.task_detail_id:
			setID();
			break;

		case R.id.task_detail_name:
			setName();
			break;

		case R.id.task_detail_level:
			setLevel();
			break;

		case R.id.task_detail_bendtime:
			setBendTime();
			break;

		case R.id.task_detail_loopway:
			setLoopWay();
			break;

		case R.id.task_detail_cls:
			setcls();
			break;

		case R.id.task_detail_addaction:
			addaAction();
			break;

		case R.id.task_detail_deleteaction1:
			deleteaAction(0);
			break;

		case R.id.task_detail_deleteaction2:
			deleteaAction(1);
			break;

		case R.id.task_detail_deleteaction3:
			deleteaAction(2);
			break;

		case R.id.task_detail_deleteaction4:
			deleteaAction(3);
			break;

		case R.id.task_detail_deleteaction5:
			deleteaAction(4);
			break;

		case R.id.task_detail_deleteaction6:
			deleteaAction(5);
			break;

		case R.id.task_detail_deleteaction7:
			deleteaAction(6);
			break;

		case R.id.task_detail_deleteaction8:
			deleteaAction(7);
			break;

		case R.id.task_detail_deleteaction9:
			deleteaAction(8);
			break;

		case R.id.task_detail_deleteaction10:
			deleteaAction(9);
			break;

		case R.id.task_detail_deleteaction11:
			deleteaAction(10);
			break;

		case R.id.task_detail_deleteaction12:
			deleteaAction(11);
			break;

		case R.id.task_detail_save:
			taskSave();
			break;

		case R.id.task_detail_back:
			finish();
			break;

		default:
			break;
		}

	}

	/**
	 * ����ID
	 */
	private void setID() {
		final View mydialog = myLayout.inflate(R.layout.dialog_inputnumber,
				null);
		new AlertDialog.Builder(TaskDetailActivity.this).setTitle("����������ID")
				.setIcon(android.R.drawable.ic_menu_edit).setView(mydialog)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String IDStr = ((EditText) mydialog
								.findViewById(R.id.dialogNumContent)).getText()
								.toString().trim();
						if (!("".equals(IDStr))) {
							int inputNum = Integer.parseInt(IDStr);
							if (inputNum > 127)
								inputNum = 127;
							((TextView) findViewById(R.id.task_detail_id))
									.setText(inputNum + "");
							task.setID(inputNum);
						}
					}
				}).setNegativeButton("ȡ��", null).show();
	}

	/**
	 * ����������
	 */
	private void setName() {
		final View mydialog = myLayout.inflate(R.layout.dialog_layout, null);
		new AlertDialog.Builder(TaskDetailActivity.this).setTitle("������������")
				.setIcon(android.R.drawable.ic_menu_edit).setView(mydialog)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = ((EditText) mydialog
								.findViewById(R.id.dialogcontent)).getText()
								.toString().trim();
						task.setName(name);
						if ("".equals(name)) {
							((TextView) findViewById(R.id.task_detail_name))
									.setText("");
							((TextView) findViewById(R.id.task_detail_name))
									.setHint("�������������");
						} else {
							((TextView) findViewById(R.id.task_detail_name))
									.setText(name);
						}
					}
				}).setNegativeButton("ȡ��", null).show();
	}

	/**
	 * �������ȼ�
	 */
	private void setLevel() {
		AlertDialog.Builder builder = new Builder(TaskDetailActivity.this);
		final View numberPickDialog = myLayout.inflate(
				R.layout.numberpick_dialog, null);

		final NumberPicker numberpick = (NumberPicker) numberPickDialog
				.findViewById(R.id.numberpick_dialog);

		numberpick.setMinValue(0);
		numberpick.setMaxValue(defauteLevel.length - 1);
		numberpick.setDisplayedValues(defauteLevel);
		numberpick.setWrapSelectorWheel(false);
		numberpick.getChildAt(0).setFocusable(false);
		numberpick.setValue(2 - task.getLevel());

		builder.setView(numberPickDialog).setTitle("��ѡ�����ȼ�");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				task.setLevel(2 - numberpick.getValue());
				((TextView) findViewById(R.id.task_detail_level))
						.setText(defauteLevel[2 - task.getLevel()]);
			}
		});
		builder.setNegativeButton("ȡ��", null).show();
	}

	/**
	 * ������ֹʱ��
	 */
	private void setBendTime() {
		AlertDialog.Builder builder = new Builder(TaskDetailActivity.this);
		View datetimeView = myLayout.inflate(R.layout.datetime_dialog, null);
		final DatePicker datepick1 = (DatePicker) datetimeView
				.findViewById(R.id.datepick1);
		final DatePicker datepick2 = (DatePicker) datetimeView
				.findViewById(R.id.datepick2);
		final TimePicker timepick1 = (TimePicker) datetimeView
				.findViewById(R.id.timepick1);
		final TimePicker timepick2 = (TimePicker) datetimeView
				.findViewById(R.id.timepick2);
		builder.setView(datetimeView);
		builder.setTitle("������ֹʱ��");
		
		datepick1.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);  
		timepick1.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		datepick2.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);  
		timepick2.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

		Calendar cal = Calendar.getInstance();
		// cal.setTimeInMillis(System.currentTimeMillis());
		cal.setTimeInMillis(task.getBegintiem());
		datepick1.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), null);
		timepick1.setIs24HourView(true);
		timepick1.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timepick1.setCurrentMinute(cal.get(Calendar.MINUTE));

		cal.setTimeInMillis(task.getEndtime());
		datepick2.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), null);
		timepick2.setIs24HourView(true);
		timepick2.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timepick2.setCurrentMinute(cal.get(Calendar.MINUTE));
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm",
						Locale.getDefault());
				Calendar cal2 = Calendar.getInstance();
				cal2.set(datepick1.getYear(), datepick1.getMonth(),
						datepick1.getDayOfMonth(), timepick1.getCurrentHour(),
						timepick1.getCurrentMinute(), 0);
				String showTime = sdf.format(cal2.getTime());
				long t1 = cal2.getTimeInMillis();
				cal2.set(datepick2.getYear(), datepick2.getMonth(),
						datepick2.getDayOfMonth(), timepick2.getCurrentHour(),
						timepick2.getCurrentMinute(), 0);
				showTime = showTime + " - " + sdf.format(cal2.getTime());
				long t2 = cal2.getTimeInMillis()-1;
				if(t2<t1){
					Toast.makeText(getBaseContext(), 
							"����ʱ��Ӧ���ڿ�ʼʱ�䣬���������ã�", Toast.LENGTH_SHORT).show();
					return ;
				}
				task.setBegintiem(t1);
				task.setEndtime(t2+1);
				((TextView) findViewById(R.id.task_detail_bendtime))
						.setText(showTime);
				Editor edit = sp.edit();
				edit.putLong("begintime", task.getBegintiem());
				edit.putLong("endtime", task.getEndtime());
				edit.commit();
			}
		}).setNegativeButton("ȡ��", null).show();
	}

	/**
	 * ����ѭ����ʽ
	 */
	private void setLoopWay() {

		new AlertDialog.Builder(this)
				.setTitle("����ѭ����ʽ")
				.setMultiChoiceItems(R.array.LoopWay_task, loopWayCheckedItems,
						new OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								loopWayCheckedItems[which] = isChecked;
							}
						})
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						byte b = 0;
						String str = "";
						for (int i = 0; i < 7; ++i) {
							if (loopWayCheckedItems[i]) {
								if (!("".equals(str)))
									str = str + "��";
								b = (byte) (b | (1 << i));
								str = str + "��" + zhou[i] + " ";
							}
						}
						task.setLoopway(b);
						if ("".equals(str))
							str = "��������";
						((TextView) findViewById(R.id.task_detail_loopway))
								.setText(str);
					}
				}).show();
	}

	/**
	 * ����ִ�л�·
	 */
	private void setcls() {

		new AlertDialog.Builder(this)
				.setTitle("���û�·")
				.setMultiChoiceItems(cls_all, clsCheckedItems,
						new OnMultiChoiceClickListener() {
							// R.array.CLs_task, clsCheckedItems, new
							// OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								clsCheckedItems[which] = isChecked;
							}
						})
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int b = 0;
						String str = "";
						for (int i = 0; i < 16; ++i) {
							if (clsCheckedItems[i]) {
								if (!("".equals(str)))
									str = str + "��";
								b = (b | (1 << i));
								str = str + "��·" + (i + 1);
							}
						}
						task.setCls(b);
						if ("".equals(str))
							str = "��������";
						((TextView) findViewById(R.id.task_detail_cls))
								.setText(str);
					}
				}).show();
	}

	/**
	 * ��Ӷ���
	 */
	private void addaAction() {
		if (task.getActionNum() < MAXACTIONNUM) {
			AlertDialog.Builder builder = new Builder(TaskDetailActivity.this);
			View actionDialog = myLayout.inflate(R.layout.action_dialog, null);
			final TimePicker timepick = (TimePicker) actionDialog
					.findViewById(R.id.timepick_action);
			final NumberPicker numberpick = (NumberPicker) actionDialog
					.findViewById(R.id.numberpick_action);

			builder.setView(actionDialog).setTitle("��Ӷ���");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			timepick.setIs24HourView(true);
			timepick.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
			timepick.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timepick.setCurrentMinute(Calendar.MINUTE);
			// numberpick������ʾ����
			numberpick.getChildAt(0).setFocusable(false);
			numberpick.setDisplayedValues(displayedValues);
			numberpick.setMinValue(0);
			numberpick.setMaxValue(displayedValues.length - 1);

			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							int actiontime = timepick.getCurrentHour() * 100
									+ timepick.getCurrentMinute();
							task.setActionNum(task.getActionNum() + 1);
							Actionbean tempbean = new Actionbean(actiontime,
									numberpick.getValue());
							mlist.add(tempbean);
							Collections.sort(mlist);
							int n = mlist.size();
							for (int j = 0; j < n; ++j) {
								tempbean = mlist.get(j);
								rl[j].setVisibility(View.VISIBLE);
								timev[j].setText(getShowTime(mlist.get(j)
										.getActime()));
								actv[j].setText(displayedValues[mlist.get(j)
										.getAct()]);
							}
							for (int j = n; j < MAXACTIONNUM; ++j) {
								timev[j].setText("");
								actv[j].setText("");
								rl[j].setVisibility(View.GONE);
							}
							//֪ͨscrollView����λ��
							handler.sendEmptyMessage(1);
						}

					});
			builder.setNegativeButton("ȡ��", null).show();
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param no
	 *            �������
	 */
	private void deleteaAction(int no) {
		task.setActionNum(task.getActionNum() - 1);
		mlist.remove(getNumberOfAction(no + 1));
		Collections.sort(mlist);
		timev[no].setText("");
		actv[no].setText("");
		rl[no].setVisibility(View.GONE);
		maction[no].setAct(10000);
		maction[no].setActime(10000);
	}

	/**
	 * ��������ִ��
	 */
	private void taskSave() {
		if(!enableSave()) return;
		Editor edit = sp.edit();
		edit.putInt("ID", task.getID());
		edit.putString("name", task.getName());
		edit.putInt("level", task.getLevel());
		edit.putLong("begintime", task.getBegintiem());
		edit.putLong("endtime", task.getEndtime());
		int t = task.getLoopway();
		edit.putInt("loopway", t);
		t = task.getCls();
		edit.putInt("cls", t);
		edit.putInt("actionNum", task.getActionNum());
		t = mlist.size();
		for (int i = 0; i < t; ++i) {
			edit.putInt("actiontime" + (i + 1), mlist.get(i).getActime());
			edit.putInt("actionact" + (i + 1), mlist.get(i).getAct());
		}
		for (int i = t; i < MAXACTIONNUM; ++i) {
			edit.putInt("actiontime" + (i + 1), 10000);
			edit.putInt("actionact" + (i + 1), 10000);
		}
		edit.commit();
		// ��������ͨѶ
		Bundle bund = new Bundle();
		bund.putInt("curCPC", CPCNo);
		bund.putByte("controlByte", (byte) 0x08);
		bund.putByteArray("usefulData", getUsefuldata());
		taskSetintent.putExtras(bund);
		startService(taskSetintent);
		finish();
	}

	/**
	 * ִ�еķ��ͱ���
	 * 
	 * @return
	 */
	private byte[] getUsefuldata() {
		int[][] curdim = ((XtoeeApp) getApplication()).getDimDate(CPCNo);
		byte[] res = new byte[1420];
		int i = 0;
		int[] cl4num = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int num = task.getActionNum();// ������
		int num2 = 0;// ��·��
		int mcls = task.getCls();
		if (mcls != 0x00) {
			for (i = 0; i < 16; ++i) {
				if ((mcls & (1 << i)) > 0) {
					cl4num[num2++] = i + 1;
				}
			}
		}
		// ����frame
		for (i = 0; i < 2; ++i) {
			res[i] = 0x00;
		}
		for (; i < 6; ++i) {
			res[i] = 0x11;
		}
		res[6] = 0x01;
		res[7] = 0x05;
		res[8] = 0x00;// ����ʹ��
		if (sp.getBoolean("enable", false))
			res[8] = 0x01;
		res[9] = (byte) task.getID();
		res[10] = (byte) task.getLevel();
		// ��ʼʱ��
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(task.getBegintiem());
		res[11] = inttoBCD(cal.get(Calendar.YEAR) / 100);
		res[12] = inttoBCD(cal.get(Calendar.YEAR) % 100);
		res[13] = inttoBCD(cal.get(Calendar.MONTH) + 1);
		res[14] = inttoBCD(cal.get(Calendar.DAY_OF_MONTH));
		res[15] = inttoBCD(cal.get(Calendar.HOUR_OF_DAY));
		res[16] = inttoBCD(cal.get(Calendar.MINUTE));
		// ����ʱ��
		cal.setTimeInMillis(task.getEndtime());
		res[17] = inttoBCD(cal.get(Calendar.YEAR) / 100);
		res[18] = inttoBCD(cal.get(Calendar.YEAR) % 100);
		res[19] = inttoBCD(cal.get(Calendar.MONTH) + 1);
		res[20] = inttoBCD(cal.get(Calendar.DAY_OF_MONTH));
		res[21] = inttoBCD(cal.get(Calendar.HOUR_OF_DAY));
		res[22] = inttoBCD(cal.get(Calendar.MINUTE));
		// ִ�����ڼ��
		res[23] = 0x01;
		// ִ������ʱ����ϸ
		res[24] = BCDturn7(task.getLoopway());
		res[25] = 0x00;
		res[26] = 0x00;
		res[27] = 0x00;
		res[28] = 0x00;
		res[29] = 0x00;
		// ������
		res[30] = (byte) num;
		// ����
		i = 0;
		for (int j = 0; j < MAXACTIONNUM; ++j) {
			int temp = (3 + 7 * num2) * i;
			int b = sp.getInt("actiontime" + (j + 1), 10000);
			if (b != 10000) {
				// ����ʱ��
				res[30 + temp + 1] = inttoBCD(b / 100);
				res[30 + temp + 2] = inttoBCD(b % 100);
				// ��·��Ա��
				res[30 + temp + 3] = (byte) num2;
				for (int k = 0; k < num2; ++k) {
					res[33 + temp + 7 * k + 1] = (byte) spNo.getInt("CL"
							+ cl4num[k], 0);
					res[33 + temp + 7 * k + 2] = 0x00;
					res[33 + temp + 7 * k + 3] = bianma[sp.getInt("actionact"
							+ (j + 1), 0)];
					if (res[33 + temp + 7 * k + 3] == 0x2B) {
						// String dimstr =
						// ""+CPCNo+CPCNo+cl4num[k]+(LEVELNUM-sp.getInt("actionact"+(j+1),
						// 0));
						// int para = spReadDim.getInt("setCPC"+dimstr, 0);
						int para = curdim[cl4num[k] - 1][sp.getInt("actionact"
								+ (j + 1), 1) - 1];
						res[33 + temp + 7 * k + 4] = inttoBCD(para / 10);
						res[33 + temp + 7 * k + 5] = inttoBCD((para * 10) % 100);
						res[33 + temp + 7 * k + 6] = 0x00;
					} else {
						res[33 + temp + 7 * k + 4] = 0x00;
						res[33 + temp + 7 * k + 5] = 0x00;
						res[33 + temp + 7 * k + 6] = 0x00;
					}
					res[33 + temp + 7 * k + 7] = 0x00;
				}
				++i;
			}
			if (i == num)
				break;
		}
		return Arrays.copyOfRange(res, 0, 30 + (3 + 7 * num2) * num + 1);
	}
	
	
	private boolean enableSave(){
		if(task.getBegintiem()>task.getEndtime()-1) {
			Toast.makeText(getBaseContext(), "����ʱ��Ӧ���ڿ�ʼʱ�䣬���������ã�", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(task.getLoopway()==0){
			Toast.makeText(getBaseContext(), "ѭ����ʽ����Ϊ�գ����������ã�", Toast.LENGTH_SHORT).show();
			return false;
		}else if(task.getCls()==0){
			Toast.makeText(getBaseContext(), "��·����Ϊ�գ����������ã�", Toast.LENGTH_SHORT).show();
			return false;
		}else if(task.getActionNum()<1){
			Toast.makeText(getBaseContext(), "��������Ϊ�գ����������ã�", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * �����е�ʱ����ʾת��
	 * @param mtime  �洢��ʱ�� �� 100*h+m
	 * @return ��ʾ��ʱ��
	 */
	private String getShowTime(int mtime) {
		String showtime = "";
		int temp = mtime / 100;
		if (temp > 9)
			showtime += temp;
		else
			showtime = "0" + temp;

		showtime += ":";

		temp = mtime % 100;
		if (temp > 9)
			showtime += temp;
		else
			showtime = showtime + "0" + temp;

		return showtime;
	}

	// n<100;
	private byte inttoBCD(int n) {
		byte res = 0;
		res = (byte) ((((byte) n / 10) << 4) + (byte) n % 10);
		return res;
	}

	private byte BCDturn7(byte b) {
		byte res = 0;
		res = (byte) ((b << 1) & 0x7F);
		if ((b & 0x40) > 0)
			res = (byte) (res | 0x01);
		else
			res = (byte) (res & 0x7E);
		return res;
	}

	/**
	 * ���㶯�����
	 */
	private int getNumberOfAction(int n) {
		int res = 0;
		for (int i = 0; i < n; ++i) {
			if (rl[i].getVisibility() == View.VISIBLE) {
				++res;
			}
		}
		return --res;
	}
	
	static class MyHandler extends Handler {

		WeakReference<Activity> mActivityReference;

		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}
	}

}
