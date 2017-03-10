package com.xtoee.tools;

import java.util.ArrayList;

import com.xtoee.main.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SwitchTitle extends LinearLayout {
	private Context context;
	private HorizontalScrollView scroll;
	private GridView gridTitle;
	private int gridviewWidth, itemWidth;
	private float density;
	private int CLNUM = 1;
	private LayoutInflater inf;
	private ImageButton bt1, bt2;
	public int mPosition = 0;

	public SwitchTitle(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public SwitchTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View thisView = inf.inflate(R.layout.switch_title_layout, this);
		bt1 = (ImageButton) thisView.findViewById(R.id.front_cpr);
		bt2 = (ImageButton) thisView.findViewById(R.id.after_cpr);
		createButtonListener();
		scroll = (HorizontalScrollView) thisView
				.findViewById(R.id.scrollviewofH);
		gridTitle = (GridView) thisView.findViewById(R.id.topCL);
		setGridView();

	}

	public void setBackground(int colorId) {
		LinearLayout l = ((LinearLayout) findViewById(R.id.switch_title_background));
		l.setBackgroundColor(colorId);
	}

	private void setGridView() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		density = dm.density;
		itemWidth = dm.widthPixels / 4;
		resetGridView();
	}

	private void resetGridView() {
		gridviewWidth = (int) (CLNUM * (itemWidth) * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
		gridTitle.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
		gridTitle.setColumnWidth(itemWidth); // 设置列表项宽
		gridTitle.setStretchMode(GridView.NO_STRETCH);
		gridTitle.setNumColumns(CLNUM); // 设置列数量=列表集合数
	}

	public void setData(ArrayList<String> listTitle) {
		if (listTitle == null || listTitle.size() == 0) {
			// throw new IllegalArgumentException("listTitle is null");
		}
		CLNUM = listTitle.size();
		resetGridView();
	}

	public void setAdapter(BaseAdapter ba) {
		gridTitle.setAdapter(ba);
	}

	/**
	 * 前后翻页
	 */
	private void createButtonListener() {
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scroll.smoothScrollBy(itemWidth * (-3), 0);
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scroll.smoothScrollBy(itemWidth * 3, 0);
			}
		});
	}
}
