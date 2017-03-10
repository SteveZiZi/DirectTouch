package com.xtoee.main;

import com.xtoee.tools.CrashHandler;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class XtoeeApp extends Application {
	// private int CLnum,CPRnum;
	private SharedPreferences spReadDim;
	private int[][][] dimData = new int[6][16][11];
	private boolean[] islogin = new boolean[6];

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());
		spReadDim = getSharedPreferences("setdataofCPC", Context.MODE_PRIVATE);
		setAndReadDimData();
	}
	

	/**
	 * 修改单个dim值
	 * @param i      cpc编号1-6
	 * @param j      回路编号1-16
	 * @param k      百分比1-11 --> 0%-100%
	 * @param data   具体数值
	 */
	public void setDimData(int i, int j, int k, int data) {
		dimData[i - 1][j - 1][k - 1] = data;
		System.out.println("dimData　" + i + " " + j + " " + k + " " + " "
				+ data);
	}

	private void setAndReadDimData() {
		// 命名规则：setCPC[i][j/10][j%10][1-11]---Dim[100%--0%]
		// dimData[i][j][0-10] = [190,201 ... 300]
		if (spReadDim.getInt("setCPC1111", 0) == 0) {
			Editor edit = spReadDim.edit();
			for (int i = 1; i < 7; ++i) {
				for (int j = 1; j < 17; ++j) {
					for (int k = 1; k < 12; ++k) {
						String str = "setCPC";
						str = str + i + j / 10 + j % 10 + k;
						edit.putInt(str, 300 - 11 * (k - 1));
						dimData[i - 1][j - 1][k - 1] = 300 - 11 * (11 - k);
					}
				}
				edit.commit();
			}
		} else {
			for (int i = 1; i < 7; ++i) {
				for (int j = 1; j < 17; ++j) {
					for (int k = 1; k < 12; ++k) {
						dimData[i - 1][j - 1][k - 1] = spReadDim.getInt(
								"setCPC" + i + j / 10 + j % 10 + (12 - k),
								300 - 11 * (11 - k));
					}
				}
			}
		}
	}

	/**
	 * 获取
	 * @param CPCno  CPC编号1-6
	 * @return
	 */
	public int[][] getDimDate(int CPCno) {
		return dimData[CPCno - 1];
	}

	public void setLogin(int i, boolean bool) {
		islogin[i] = bool;
	}

	public boolean getLogin(int i) {
		return islogin[i];
	}

	public void publicToast() {
		Toast.makeText(getApplicationContext(), "toast test",
				Toast.LENGTH_SHORT).show();
	}

}
