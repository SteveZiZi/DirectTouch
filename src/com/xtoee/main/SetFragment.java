package com.xtoee.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SetFragment extends Fragment {

	private SharedPreferences sp;
	ArrayList<HashMap<String, Object>> arraylist = null;
	private GridView grid;
	private View curView;
	private int curPosition, curCPC = 1;
	private Button cpc1bt, cpc2bt, cpc3bt, cpc4bt, cpc5bt, cpc6bt, toleft,
			toright;
	private HorizontalScrollView scroll;
	private int mwidth;
	private static final int CNUM = 14;// ����

	private SimpleAdapter sa;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.set2, container, false);
		cpc1bt = (Button) v.findViewById(R.id.cpc1_set);
		cpc2bt = (Button) v.findViewById(R.id.cpc2_set);
		cpc3bt = (Button) v.findViewById(R.id.cpc3_set);
		cpc4bt = (Button) v.findViewById(R.id.cpc4_set);
		cpc5bt = (Button) v.findViewById(R.id.cpc5_set);
		cpc6bt = (Button) v.findViewById(R.id.cpc6_set);
		grid = (GridView) v.findViewById(R.id.gridView_scroll);
		scroll = (HorizontalScrollView) v.findViewById(R.id.scroll_set);
		toleft = (Button) v.findViewById(R.id.toleft_set);
		toright = (Button) v.findViewById(R.id.toright_set);

		// ���������
		// int colnum = (int)
		// (((getResources().getDisplayMetrics().widthPixels)) / 7);
		int colnum = (int) (((getResources().getDisplayMetrics().widthPixels)));
		grid.setColumnWidth(colnum);
		sp = getActivity().getApplicationContext().getSharedPreferences(
				"setdataofCPC", Context.MODE_PRIVATE);

		// ����CPC1
		setlight(cpc1bt);
		// ��ʼ������
		InitData(1);
		sa = new SimpleAdapter(getActivity(), arraylist, R.layout.subcpc,
				new String[] { "cell" }, new int[] { R.id.cell });
		grid.setAdapter(sa);
		grid.setOnItemClickListener(new mSetListener2());

		CPCClick();
		// ������Ļ���
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		mwidth = dm.widthPixels;

		return v;
	}

	/**
	 * ��ʼ��������ݲ�������list
	 * 
	 * @param no
	 *            CPC��
	 */
	public void InitData(int no) {
		arraylist = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		for (int i = 1; i < 17; ++i) {
			map = new HashMap<String, Object>();
			map.put("cell", "L#0" + no + i / 10 + i % 10);
			arraylist.add(map);
			map = new HashMap<String, Object>();
			map.put("cell", "Vdb");
			arraylist.add(map);
			for (int j = 1; j < 6; ++j) {
				map = new HashMap<String, Object>();
				map.put("cell",
						sp.getInt("setCPC" + no + i / 10 + i % 10 + j, 0));
				arraylist.add(map);
			}
			map = new HashMap<String, Object>();
			map.put("cell", "L#0" + no + i / 10 + i % 10);
			arraylist.add(map);
			for (int j = 6; j < 12; ++j) {
				map = new HashMap<String, Object>();
				map.put("cell",
						sp.getInt("setCPC" + no + i / 10 + i % 10 + j, 0));
				arraylist.add(map);
			}
		}
	}

	/**
	 * CPC ����޸�
	 * @param v  CPC1-6 Button
	 */
	private void CPCClick() {
		// CPC����¼�
		final Button[] bts = { cpc1bt, cpc2bt, cpc3bt, cpc4bt, cpc5bt, cpc6bt };
		for (int i = 1; i <= 6; ++i) {
			final int mj = i;
			bts[i - 1].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					reback(bts[curCPC - 1]);
					curCPC = mj;
					setlight(bts[curCPC - 1]);
					// ��ʼ��������ݲ�����
					InitData(curCPC);
					sa = new SimpleAdapter(getActivity(), arraylist,
							R.layout.subcpc, new String[] { "cell" },
							new int[] { R.id.cell });
					// ������gridView��ʾ
					grid.setAdapter(sa);
				}
			});
		}
		toleft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				scroll.smoothScrollBy(-1 * mwidth, 0);
			}
		});
		toright.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				scroll.smoothScrollBy(mwidth, 0);
			}
		});
	}

	/**
	 * ȥ���������Ч��
	 */
	private void reback(Button bt) {
		bt.setTextSize(getActivity().getResources()
				.getDimension(R.dimen.reback));
		bt.setTextColor(getActivity().getResources().getColor(
				R.color.textColor_gray));
	}

	/**
	 * ���õ������Ч��
	 */
	private void setlight(Button bt) {
		bt.setTextSize(getActivity().getResources().getDimension(R.dimen.light));
		bt.setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	/**
	 * ���� parent �������������AdapterView�� view
	 * ��AdapterView�б��������ͼ(������adapter�ṩ��һ����ͼ)�� position ��ͼ��adapter�е�λ�á� id
	 * �����Ԫ�ص���id��
	 */
	class mSetListener1 implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			curView = view;
			curPosition = position;
			// ��1��2�в��ɸı�
			// if(position == 5){
			// scroll.smoothScrollBy(mwidth, 0);
			// }else if(position == 6){
			// scroll.smoothScrollBy(-1*mwidth, 0);
			// }else
			if ((position % CNUM > 1) && ((position % CNUM) != 7)) {
				LayoutInflater myLayout = LayoutInflater.from(getActivity());
				final View mydialog = myLayout.inflate(
						R.layout.dialog_inputnumber, null);
				new AlertDialog.Builder(getActivity()).setTitle("������")
						.setIcon(android.R.drawable.ic_menu_edit)
						.setView(mydialog)
						.setPositiveButton("ȷ��", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String alterdata = ((EditText) mydialog
										.findViewById(R.id.dialogNumContent))
										.getText().toString().trim();
								if (!("".equals(alterdata))) {
									((TextView) curView.findViewById(R.id.cell))
											.setText(alterdata);
									int i = curCPC;
									int j = curPosition / CNUM + 1;
									int k = curPosition % CNUM - 1;
									if (k > 6)
										--k;
									Editor edit = sp.edit();
									edit.putInt("setCPC" + i + i + j + k,
											Integer.parseInt(alterdata));
									edit.commit();
								}
							}
						}).setNegativeButton("ȡ��", null).show();
			}
		}
	}

	class mSetListener2 implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			curView = view;
			curPosition = position;

			// ��ȡlistview�е��item������
			@SuppressWarnings("unchecked")
			final HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);

			// ��1�в��ɸı�
			if ((position % CNUM > 1) && ((position % CNUM) != 7)) {
				LayoutInflater myLayout = LayoutInflater.from(getActivity());
				final View mydialog = myLayout.inflate(
						R.layout.dialog_inputnumber, null);
				new AlertDialog.Builder(getActivity()).setTitle("������")
						.setIcon(android.R.drawable.ic_menu_edit)
						.setView(mydialog)
						.setPositiveButton("ȷ��", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String alterdata = ((EditText) mydialog
										.findViewById(R.id.dialogNumContent))
										.getText().toString().trim();
								if (!("".equals(alterdata))) {
									int i = curCPC;
									int j = curPosition / CNUM + 1;
									int k = curPosition % CNUM - 1;
									if (k > 6)
										--k;
									int alterV = parseInt(alterdata, "setCPC"
											+ i + j / 10 + j % 10 + k);
									Editor edit = sp.edit();
									edit.putInt("setCPC" + i + j / 10 + j % 10
											+ k, alterV);
									edit.commit();
									// ��������
									item.remove("cell");
									item.put("cell", alterV + "");
									sa.notifyDataSetChanged();
									((XtoeeApp) getActivity().getApplication())
											.setDimData(i, j, k, alterV);
								}
							}
						}).setNegativeButton("ȡ��", null).show();
			}
		}

	}

	private int parseInt(String s, String path) {
		int res = 0;
		try {
			res = Integer.parseInt(s);
			return res;
		} catch (Exception e) {
			res = sp.getInt(path, 0);
			return res;
		}
	}

}
