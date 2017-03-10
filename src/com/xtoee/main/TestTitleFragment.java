package com.xtoee.main;

import java.util.ArrayList;

import com.xtoee.tools.SwitchTitle;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestTitleFragment extends Fragment {
	private SwitchTitle myswitch;
	private ArrayList<String> listTitle;
	private int itemWidth;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.testtitile, container, false);
		myswitch = (SwitchTitle) v.findViewById(R.id.myswitch);
		listTitle = new ArrayList<String>();
		for (int i = 1; i < 17; ++i) {
			listTitle.add("»ØÂ·" + i);
		}
		setGridView();
		myswitch.setData(listTitle);
		myswitch.setAdapter(new TitleAdapter());
		return v;
	}

	private void setGridView() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		itemWidth = dm.widthPixels / 4;
	}

	public class TitleAdapter extends BaseAdapter {

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
				convertView.setTag(holder);
				holder.tv.setWidth(itemWidth);
				holder.tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						System.out.println("on click is " + gg);
					}
				});
			} else {
				holder = (TitleViewHolder) convertView.getTag();
			}
			holder.tv.setText(listTitle.get(position));

			return convertView;
		}

	}

	public class TitleViewHolder {
		public TextView tv;
	}

}
