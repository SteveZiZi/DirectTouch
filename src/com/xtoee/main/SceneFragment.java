package com.xtoee.main;

import com.xtoee.services.ControlService;
import com.xtoee.tools.SwitchView;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SceneFragment extends Fragment implements OnClickListener {

	private int curCPC = 0;
	private Button bt1, bt2, bt3, bt4, bt5, bt6;
	private View thisview;
	private SwitchView[] swc = new SwitchView[3];
	private SharedPreferences sp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisview = inflater.inflate(R.layout.scene, container, false);
		sp = getActivity().getSharedPreferences("scenename",
				Context.MODE_PRIVATE);
		// ��ʼ������¼�
		((ImageButton) thisview.findViewById(R.id.scene1set))
				.setOnClickListener(this);
		((ImageButton) thisview.findViewById(R.id.scene2set))
				.setOnClickListener(this);
		((ImageButton) thisview.findViewById(R.id.scene3set))
				.setOnClickListener(this);
		// findviewByid
		bt1 = (Button) thisview.findViewById(R.id.cpc1_scene);
		bt2 = (Button) thisview.findViewById(R.id.cpc2_scene);
		bt3 = (Button) thisview.findViewById(R.id.cpc3_scene);
		bt4 = (Button) thisview.findViewById(R.id.cpc4_scene);
		bt5 = (Button) thisview.findViewById(R.id.cpc5_scene);
		bt6 = (Button) thisview.findViewById(R.id.cpc6_scene);
		// ��ʼ������¼�
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
		bt4.setOnClickListener(this);
		bt5.setOnClickListener(this);
		bt6.setOnClickListener(this);
		changeCurCPC(0);// ���õ�ǰCPC��
		// �������ÿؼ� ��ʼ������¼�
		((TextView) thisview.findViewById(R.id.scenesetdetail_scene))
				.setOnClickListener(this);

		createSwitchListener();
		return thisview;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * ����¼�����
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// �л���������activity
		case R.id.scenesetdetail_scene:
			Intent sceneSetIntent = new Intent(getActivity(),
					SceneSetActivity.class);
			sceneSetIntent.putExtra("curCPC", curCPC);
			startActivity(sceneSetIntent);
			break;

		// ���ó�����
		case R.id.scene1set:
			setName(0);
			break;

		case R.id.scene2set:
			setName(1);
			break;

		case R.id.scene3set:
			setName(2);
			break;

		// �л���ǰCPC
		case R.id.cpc1_scene:
			changeCurCPC(0);
			break;

		case R.id.cpc2_scene:
			changeCurCPC(1);
			break;

		case R.id.cpc3_scene:
			changeCurCPC(2);
			break;

		case R.id.cpc4_scene:
			changeCurCPC(3);
			break;

		case R.id.cpc5_scene:
			changeCurCPC(4);
			break;

		case R.id.cpc6_scene:
			changeCurCPC(5);
			break;

		default:
			break;
		}
	}

	/**
	 * ���ó������Ի���
	 * 
	 * @param no
	 *            �������
	 */
	private void setName(int no) {
		final int IDno = no;
		final int[] mviewID = { R.id.scene1, R.id.scene2, R.id.scene3 };// View
																		// ID
		// �Ի����layout����
		final View mydialog = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_layout, null);
		// �Ի�������
		new AlertDialog.Builder(getActivity()).setTitle("���ĳ�����")
				.setIcon(android.R.drawable.ic_menu_edit).setView(mydialog)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ��ʾ�����泡����
						String name = ((EditText) mydialog
								.findViewById(R.id.dialogcontent)).getText()
								.toString().trim();
						((TextView) thisview.findViewById(mviewID[IDno]))
								.setText(name);
						Editor edit = sp.edit();
						edit.putString("name" + (curCPC * 3 + IDno + 1), name);
						edit.commit();
					}
				}).setNegativeButton("ȡ��", null).show();
	}

	/**
	 * ���ؼ����¼�
	 */
	private void createSwitchListener() {
		swc[0] = (SwitchView) thisview.findViewById(R.id.scene1_offon);
		swc[1] = (SwitchView) thisview.findViewById(R.id.scene2_offon);
		swc[2] = (SwitchView) thisview.findViewById(R.id.scene3_offon);
		for (int i = 0; i < 3; ++i) {
			final int j = i;
			swc[i].setOnSwitchChangeListener(new SwitchView.OnSwitchChangeListener() {
				@Override
				public void onSwitchChanged(boolean open) {
					Bundle bund = new Bundle();
					// ����bundle������service����
					bund.putBoolean("isChangeOffon", true);
					bund.putInt("curCPC", curCPC + 1);
					bund.putByte("controlByte", (byte) 0x08);
					bund.putByteArray("usefulData", getUsefuldata(j));
					Intent intent = new Intent(getActivity(),
							ControlService.class);
					intent.putExtras(bund);
					getActivity().startService(intent);
				}
			});
		}
	}

	/**
	 * ����ı䵱ǰCPC
	 * 
	 * @param no
	 */
	private void changeCurCPC(int no) {
		Button[] bts = { bt1, bt2, bt3, bt4, bt5, bt6 };
		reback(bts[curCPC]);
		curCPC = no;
		setlight(bts[curCPC]);
		// ���ó�����
		((TextView) thisview.findViewById(R.id.scene1)).setText(sp.getString(
				"name" + (curCPC * 3 + 1), "����" + (curCPC * 3 + 1)));
		((TextView) thisview.findViewById(R.id.scene2)).setText(sp.getString(
				"name" + (curCPC * 3 + 2), "����" + (curCPC * 3 + 2)));
		((TextView) thisview.findViewById(R.id.scene3)).setText(sp.getString(
				"name" + (curCPC * 3 + 3), "����" + (curCPC * 3 + 3)));
	}

	/**
	 * ȥ���������Ч��
	 * 
	 */
	private void reback(Button bt) {
		bt.setTextSize(getActivity().getResources()
				.getDimension(R.dimen.reback));
		bt.setTextColor(getActivity().getResources().getColor(
				R.color.textColor_gray));
	}

	/**
	 * ���õ������Ч��
	 * 
	 */
	private void setlight(Button bt) {
		bt.setTextSize(getActivity().getResources().getDimension(R.dimen.light));
		bt.setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	/**
	 * ��ȡ���Ƶ����ñ���
	 * @param j  ������
	 * @return   ���Ʊ���
	 */
	private byte[] getUsefuldata(int j) {
		byte[] res = new byte[10 + 6];
		int i = 0;
		for (; i < 2; ++i) {
			res[i] = 0x00;
		}
		for (; i < 6; ++i) {
			res[i] = 0x11;
		}
		res[6] = 0x01;
		res[7] = 0x02;
		res[8] = 0x01;
		res[9] = 0x05;
		res[10] = 0x05;
		res[11] = (byte) (0x1D + j);
		res[12] = 0x00;
		res[13] = 0x00;
		res[14] = 0x00;
		res[15] = 0x00;
		return res;
	}

}
