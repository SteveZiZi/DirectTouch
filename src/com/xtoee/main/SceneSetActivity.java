package com.xtoee.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.xtoee.bean.Scenebean;
import com.xtoee.services.SceneSetService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SceneSetActivity extends Activity {

	// 设备号
	private String[] pick1Str = { "01", "02", "03", "04", "05", "06", "07",
			"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
			"19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
			"30", "31", "32", "33", "34", "35", "36" };
	// 子路号
	private String[] pick2Str = { "01", "02", "03", "04", "05", "06", "07",
			"08" };
	// 特殊功能
	private String[] pick3Str = { "无特殊要求", "继电器打开", "继电器关闭", "组合1灯光设置",
			"组合2灯光设置", "组合3灯光设置", "整流器电压设置", "整流器开", "整流器关", "回路电压设置",
			"回路整流器开", "回路整流器关" };
	// 特殊功能对应的控制码
	private byte[] bianma = { 0x00, 0x09, 0x0A, 0x1D, 0x1E, 0x1F, 0x28, 0x29,
			0x2A, 0x2B, 0x2C, 0x2D };

	private int curCPC = 0;
	private int curScene = 1;
	private Scenebean[] bean = new Scenebean[8];
	private Button bt1, bt2, bt3;
	private SharedPreferences sp;
	private SharedPreferences spReadName;
	private List<Scenebean> mylist;
	private ListView listview;
	private Spinner spinner;

	private Intent sceneSetintent;

	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sceneset);
		curCPC = getIntent().getIntExtra("curCPC", 0);// 获取CPC编号
		// 本activity对应的三service的intent
		sceneSetintent = new Intent(SceneSetActivity.this,
				SceneSetService.class);
		// 场景名的SharedPreferences
		spReadName = getSharedPreferences("scenename", MODE_PRIVATE);
		spinner = (Spinner) findViewById(R.id.spinner_sceneset);
		createSpinnerByResource();// 下拉选项监听事件
		bt1 = (Button) findViewById(R.id.scene1_set);
		bt2 = (Button) findViewById(R.id.scene2_set);
		bt3 = (Button) findViewById(R.id.scene3_set);
		resetSceneName();// 初始化\重置场景名
		createButtonlistener();// 场景Button点击事件监听
		setlight(bt1);
		listview = (ListView) findViewById(R.id.sceneset_liseview);
		InitView();// 初始化\刷新界面内容
		// 8个功能条的点击事件监听
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSetDialog(view, position);
			}
		});
		// 保存监听
		((Button) findViewById(R.id.sceneset_save))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 存储场景数据
						sp = getSharedPreferences("Scene"
								+ (3 * curCPC + curScene) + "Info",
								MODE_PRIVATE);
						for (int i = 1; i < 9; ++i) {
							Editor edit = sp.edit();
							edit.putBoolean("bean" + i + "enable",
									bean[i - 1].isEnable());
							edit.putInt("bean" + i + "tnNo",
									bean[i - 1].getTnNo());
							edit.putInt("bean" + i + "clNO",
									bean[i - 1].getClNO());
							edit.putInt("bean" + i + "specNo",
									bean[i - 1].getSpecNo());
							edit.putInt("bean" + i + "para",
									bean[i - 1].getPara());
							edit.commit();
						}
						// 发送并启动服务
						Bundle bund = new Bundle();
						bund.putInt("curCPC", curCPC + 1);
						bund.putByte("controlByte", (byte) 0x08);
						bund.putByteArray("usefulData", getUsefuldata());
						sceneSetintent.putExtras(bund);
						startService(sceneSetintent);
						Toast.makeText(getApplicationContext(), "配置已保存",
								Toast.LENGTH_SHORT).show();
					}
				});
		// 返回监听
		((RelativeLayout) findViewById(R.id.sceneset_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						SceneSetActivity.this.finish();
					}
				});
		// 广播接收器，用于接收并显示操作结果
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String mess = intent.getStringExtra("msg");
				Toast.makeText(SceneSetActivity.this, mess, Toast.LENGTH_SHORT)
						.show();
			}
		};
		// 广播接收器的过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction("action.SceneSetService");
		registerReceiver(receiver, filter);

	}

	/**
	 * 设置竖屏操作
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
		stopService(sceneSetintent);
	}

	/**
	 * 初始化或重置场景名
	 */
	private void resetSceneName() {
		bt1.setText(spReadName.getString("name" + (curCPC * 3 + 1), "场景"
				+ (curCPC * 3 + 1)));
		bt2.setText(spReadName.getString("name" + (curCPC * 3 + 2), "场景"
				+ (curCPC * 3 + 2)));
		bt3.setText(spReadName.getString("name" + (curCPC * 3 + 3), "场景"
				+ (curCPC * 3 + 3)));
	}

	/**
	 * 场景Button点击事件监听
	 */
	private void createButtonlistener() {
		final Button[] bts = { bt1, bt1, bt2, bt3 };
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reback(bts[curScene]);
				curScene = 1;
				setlight(bts[curScene]);
				InitView();// 刷新界面内容
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reback(bts[curScene]);
				curScene = 2;
				setlight(bts[curScene]);
				InitView();// 刷新界面内容
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reback(bts[curScene]);
				curScene = 3;
				setlight(bts[curScene]);
				InitView();// 刷新界面内容
			}
		});
	}

	private void InitView() {
		sp = getSharedPreferences("Scene" + (3 * curCPC + curScene) + "Info",
				MODE_PRIVATE);
		mylist = new ArrayList<Scenebean>();
		for (int i = 1; i < 9; ++i) {
			bean[i - 1] = new Scenebean(sp.getBoolean("bean" + i + "enable",
					false), sp.getInt("bean" + i + "tnNo", 0), sp.getInt("bean"
					+ i + "clNO", 0), sp.getInt("bean" + i + "specNo", 0),
					sp.getInt("bean" + i + "para", 0));
			mylist.add(bean[i - 1]);
		}
		listview.setAdapter(new MyAdapter());
	}

	private void createSpinnerByResource() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.CPC_spinner, R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setPrompt("test");
		spinner.setSelection(curCPC, true);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				curCPC = position;
				InitView();
				resetSceneName();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	/**
	 * list点击事件监听
	 * @param view      所点击的View
	 * @param position  所点击的View的position
	 */
	private void mSetDialog(View view, int position) {
		// 获取点击position的SceneBean
		Scenebean sb = mylist.get(position);
		if (sb.isEnable()) {
			// 如果为使能状态，则取消使能
			sb.setEnable(!sb.isEnable());
			((ImageView) view.findViewById(R.id.isenable_sceneset))
					.setVisibility(View.INVISIBLE);
			return;
		}
		// 否则，设为使能，并弹出对话框，设置控制情景
		final View mview = view;
		final int mposition = position;
		AlertDialog.Builder builder = new Builder(this);
		View actionDialog = LayoutInflater.from(this).inflate(
				R.layout.scene_dialog, null);
		final NumberPicker pick1 = (NumberPicker) actionDialog
				.findViewById(R.id.setTnNo_set);
		final NumberPicker pick2 = (NumberPicker) actionDialog
				.findViewById(R.id.setClNo_set);
		final NumberPicker pick3 = (NumberPicker) actionDialog
				.findViewById(R.id.setspec_set);
		final EditText edittext = (EditText) actionDialog
				.findViewById(R.id.setpara_set);

		builder.setView(actionDialog).setTitle("参数设置");
		// numberpick设置显示内容
		pick1.setDisplayedValues(pick1Str);
		pick1.setMinValue(0);
		pick1.setMaxValue(pick1Str.length - 1);
		pick1.getChildAt(0).setFocusable(false);
		pick2.setDisplayedValues(pick2Str);
		pick2.setMinValue(0);
		pick2.setMaxValue(pick2Str.length - 1);
		pick2.getChildAt(0).setFocusable(false);
		pick3.setDisplayedValues(pick3Str);
		pick3.setMinValue(0);
		pick3.setMaxValue(pick3Str.length - 1);
		pick3.getChildAt(0).setFocusable(false);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// get SCenebean
				Scenebean sb = mylist.get(mposition);
				sb.setEnable(!sb.isEnable());
				sb.setTnNo(pick1.getValue());
				sb.setClNO(pick2.getValue());
				sb.setSpecNo(pick3.getValue());
				String tp = edittext.getText().toString().trim();
				if (!("".equals(tp)))
					sb.setPara(Integer.parseInt(tp));
				mylist.set(mposition, sb);
				// set View
				((ImageView) mview.findViewById(R.id.isenable_sceneset))
						.setVisibility(View.VISIBLE);
				((TextView) mview.findViewById(R.id.TnNo_sceneset))
						.setText(pick1Str[pick1.getValue()]);
				((TextView) mview.findViewById(R.id.subclNo_sceneset))
						.setText(pick2Str[pick2.getValue()]);
				((TextView) mview.findViewById(R.id.spcefunc_sceneset))
						.setText(pick3Str[pick3.getValue()]);
				((TextView) mview.findViewById(R.id.para_sceneset)).setText(tp);
				if ("".equals(tp))
					((TextView) mview.findViewById(R.id.para_sceneset))
							.setText("0");
			}
		});
		builder.setNegativeButton("取消", null).show();
	}

	/**
	 * 去除点击加亮效果
	 * 
	 */
	private void reback(Button bt) {
		bt.setTextSize(getResources().getDimension(R.dimen.reback));
		bt.setTextColor(getResources().getColor(R.color.textColor_gray));
	}

	/**
	 * 设置点击加亮效果
	 * 
	 */
	private void setlight(Button bt) {
		bt.setTextSize(getResources().getDimension(R.dimen.light));
		bt.setTextColor(getResources().getColor(R.color.white));
	}

	/**
	 * list 的 adapter 配置
	 * @author yang
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mylist.size();
		}

		@Override
		public Object getItem(int position) {
			return mylist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 加载layout布局
			View v = LayoutInflater.from(SceneSetActivity.this).inflate(
					R.layout.sceneset_element, null);
			Scenebean sb = mylist.get(position);
			// 判断使能
			int visib = View.INVISIBLE;
			if (sb.isEnable())
				visib = View.VISIBLE;
			((ImageView) v.findViewById(R.id.isenable_sceneset))
					.setVisibility(visib);// 设置使能
			((TextView) v.findViewById(R.id.TnNo_sceneset)).setText(pick1Str[sb
					.getTnNo()]);// 设备号
			((TextView) v.findViewById(R.id.subclNo_sceneset))
					.setText(pick2Str[sb.getClNO()]);// 子路号
			((TextView) v.findViewById(R.id.spcefunc_sceneset))
					.setText(pick3Str[sb.getSpecNo()]);// 特殊功能号
			((TextView) v.findViewById(R.id.para_sceneset)).setText(sb
					.getPara() + "");// 参数
			return v;
		}

	}

	/**
	 * 获取控制报文
	 * @return
	 */
	private byte[] getUsefuldata() {
		byte[] res = new byte[80];
		int i = 0;
		int num = 0;// 方案数
		for (; i < 2; ++i) {
			res[i] = 0x00;
		}
		for (; i < 6; ++i) {
			res[i] = 0x11;
		}
		res[6] = (byte) (0x10 + curScene);
		res[7] = 0x01;
		for (int j = 0; j < 8; ++j) {
			if (bean[j].isEnable()) {
				res[8 + 7 * num + 1] = (byte) (bean[j].getTnNo() + 1);
				res[8 + 7 * num + 2] = (byte) (bean[j].getClNO() + 13);
				res[8 + 7 * num + 3] = bianma[(bean[j].getSpecNo())];
				res[8 + 7 * num + 4] = 0x00;
				res[8 + 7 * num + 5] = 0x00;
				res[8 + 7 * num + 6] = 0x00;
				res[8 + 7 * num + 7] = 0x00;
				++num;
			}
		}
		res[8] = (byte) num;
		return Arrays.copyOfRange(res, 0, 8 + 7 * num + 1);
	}

}
