package com.xtoee.main;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.xtoee.tools.SwitchTitle;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SubClFragment extends Fragment {

	private GridView grid;
	private int curCPC = 0;
	private int curCL = 0;
	private Spinner spinner;
	ArrayList<HashMap<String, Object>> arraylist = null;
	private SharedPreferences sp, spCL;
	private int isfirst = 0;// ���ڱ���������Ϊ�Ǳ䶯���ݣ��ʿ�����Ϊ����ʱˢ�£��ɼ���app����
	private String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H" };// ����

	private int CLnum;
	private SwitchTitle swtitle;
	// private int itemWidth;
	private TextView[] titleView = new TextView[16];
	private ArrayList<String> listTitle;
	private SimpleAdapter sa;

	/**
	 * handleMessage������
	 * 
	 */
	MyHandler handler = new MyHandler(getActivity()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				InitData();// ˢ�µ�ǰCPC����
				// ��������
				sa = new SimpleAdapter(getActivity(), arraylist,
						R.layout.subcpc, new String[] { "cell" },
						new int[] { R.id.cell });
				grid.setAdapter(sa);
			}
		}

	};

	// ��ʱ��
	private Timer mTimer = new Timer(true);
	// ������
	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1 + (isfirst++);// ���治����ʱ��what ��= 1����֤���治ˢ��
			handler.sendMessage(message);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.subcl, container, false);
		sp = getActivity().getSharedPreferences("offon", Context.MODE_PRIVATE);
		// findviewByid
		grid = (GridView) v.findViewById(R.id.gridView_sbucl);
		swtitle = (SwitchTitle) v.findViewById(R.id.clTitle_sub);
		spinner = (Spinner) v.findViewById(R.id.spinner_subcl);
		createSpinnerByResource();
		spCL = getActivity().getSharedPreferences("CPC1Info",
				Context.MODE_PRIVATE);
		CLnum = spCL.getInt("CLn", 0);

		// ����gridview���
		int colnum = (int) (((getResources().getDisplayMetrics().widthPixels)) / 6);
		grid.setColumnWidth(colnum);

		isfirst = 0;// ��ʼ������
		mTimer.schedule(mTimerTask, 500, 1000);// ������ʱ��

		createTitleData();

		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// ���ٶ�ʱ������
		while (!mTimerTask.cancel()) {
			mTimer.cancel();
		}
		// ����list
		if (arraylist != null && !(arraylist.isEmpty())) {
			arraylist.clear();
		}
		grid = null;
		arraylist = null;
		// ����handle
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * ���ñ���
	 */
	private void createTitleData() {
		listTitle = new ArrayList<String>();
		for (int i = 1; i <= CLnum; ++i) {
			listTitle.add("��·" + spCL.getInt("CL" + i, 1));
		}
		for (int i = 0; i < titleView.length; ++i) {
			titleView[i] = null;
		}
		swtitle.setData(listTitle);
		swtitle.setAdapter(new TitleAdapter());
	}

	/**
	 * ȥ���������Ч��
	 */
	private void reback(TextView bt) {
		if(bt==null) return ;
		bt.setTextSize(getActivity().getResources()
				.getDimension(R.dimen.reback));
		bt.setTextColor(getActivity().getResources().getColor(
				R.color.textColor_gray));
	}

	/**
	 * ���õ������Ч��
	 */
	private void setlight(TextView bt) {
		if(bt==null) return ;
		bt.setTextSize(getActivity().getResources().getDimension(R.dimen.light));
		bt.setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	/**
	 * ����ѡ��ǰCPC ���õ�ǰCPC��
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
				curCPC = position;
				isfirst = 0;
				spCL = getActivity().getSharedPreferences(
						"CPC" + (position + 1) + "Info", Context.MODE_PRIVATE);
				CLnum = spCL.getInt("CLn", 0);
				createTitleData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	/**
	 * ˢ�µ�ǰ����
	 * @param no  ��ǰ��·��1-4
	 */
	public void InitData() {
		byte[] curdata = ((RunActivity) getActivity()).getSubclOffon();
		arraylist = new ArrayList<HashMap<String, Object>>();
		int t = spCL.getInt("CL" + (curCL + 1), 1);// ��ʵ�Ļ�·��
		for (int i = 1; i < 9; ++i) {
			// DecimalFormat dcmFmt = new DecimalFormat("0.0");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("cell", "#0" + (curCPC + 1) + t / 10 + t % 10
					+ alphabet[i - 1]);// ���
			arraylist.add(map);
			map = new HashMap<String, Object>();
			// ����:name[cpc][[cl][cl]][i] : name1011->cpc1��·1�ĵ�һ���̵���
			map.put("cell", sp.getString("name" + (curCPC + 1) + t / 10 + t
					% 10 + i, "����" + i));
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", "0.0");// ��ѹ
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", "0.0");// ����
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", "0.00");// ����
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", getoffon(curdata[16 * curCPC + curCL], i - 1));// ͨ��
			arraylist.add(map);
		}
	}

	/**
	 * ��ȡ����״̬����1Ϊ������0Ϊ��
	 * @param b  ״̬��
	 * @param n  ����λ
	 * @return   ����״̬
	 */
	private String getoffon(byte b, int n) {
		if ((b & (1 << n)) > 0)
			return "��ͨ";
		else
			return "�Ͽ�";
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
						reback(titleView[curCL]);
						curCL = gg;
						setlight(titleView[curCL]);
						isfirst = 0;
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
