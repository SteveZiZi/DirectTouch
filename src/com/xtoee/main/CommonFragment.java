package com.xtoee.main;

import java.util.ArrayList;
import java.util.List;

import com.xtoee.bean.AddressBean;
import com.xtoee.services.SystemMenuService;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommonFragment extends Fragment {

	private Button msave;

	private int total;// address有效个数（6+n)
	private TextView addaddr;
	private List<AddressBean> mlist;
	private ListView listview;
	private LayoutInflater lif;

	private AddressBean dialogab;

	private SharedPreferences sp;

	private MyAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.common2, container, false);
		sp = getActivity().getSharedPreferences("addr", Context.MODE_PRIVATE);
		lif = LayoutInflater.from(getActivity());
		mlist = new ArrayList<AddressBean>();
		InitView(v);// 初始化各类显示
		// list设置adapter
		mAdapter = new MyAdapter();
		listview.setAdapter(mAdapter);
		SetChange();// 初始化增加地址监听事件
		SaveIP();// 保存地址数据
		return v;
	}

	/**
	 * 初始化各类显示
	 */
	private void InitView(View v) {
		// 地址个数
		total = sp.getInt("total", 6);
		// 读取并加载前6个地址数据至list
		for (int i = 1; i < 7; ++i) {
			AddressBean ab = new AddressBean();
			ab.setEnable(sp.getBoolean("addr" + i, false));
			ab.setIp(sp.getString("ip" + i, ""));
			ab.setPort(sp.getInt("port" + i, 0));
			ab.setRemote(sp.getBoolean("remote" + i, false));
			ab.setLogicAddr(sp.getInt("logicAddr" + i, -1));
			mlist.add(ab);
		}
		// 读取并加载后面的地址数据至list
		for (int i = 7; i <= total; ++i) {
			AddressBean ab = new AddressBean();
			ab.setEnable(sp.getBoolean("addr" + i, false));
			ab.setIp(sp.getString("ip" + i, ""));
			ab.setPort(sp.getInt("port" + i, 0));
			ab.setRemote(sp.getBoolean("remote" + i, false));
			ab.setLogicAddr(sp.getInt("logicAddr" + i, -1));
			mlist.add(ab);
		}
		// findviewByID
		msave = (Button) v.findViewById(R.id.commom2_save);
		addaddr = (TextView) v.findViewById(R.id.addressAdd_common2);
		listview = (ListView) v.findViewById(R.id.common2_listview);
	}

	/**
	 * 增加地址个数
	 */
	private void SetChange() {
		addaddr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mlist.add(new AddressBean());
				Log.i("number", mlist.size() + "");
				// listview.setAdapter(new MyAdapter());
				mAdapter.notifyDataSetChanged();
				++total;
			}
		});
	}

	/**
	 * 保存地址数据
	 */
	private void SaveIP() {
		msave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				msave.setFocusable(true);
				msave.setFocusableInTouchMode(true);
				msave.requestFocus();
				msave.requestFocusFromTouch();
				if(!isSameList()) return ;
				// 先清除旧的数据
				Editor edit = sp.edit();
				edit.clear();
				edit.commit();
				// 保存前4个地址数据
				for (int i = 1; i < 7; ++i) {
					AddressBean ab = mlist.get(i - 1);
					edit.putBoolean("addr" + i, ab.isEnable());
					edit.putString("ip" + i, ab.getIp());
					edit.putInt("port" + i, ab.getPort());
					edit.putBoolean("remote" + i, ab.isRemote());
					edit.putInt("logicAddr" + i, ab.getLogicAddr());
				}
				int j = 6;
				// 如果后面的数据有效（使能），则保存，否则不保存
				for (int i = 7; i <= total; ++i) {
					AddressBean ab = mlist.get(i - 1);
					if (ab.isEnable()) {
						++j;
						edit.putBoolean("addr" + j, ab.isEnable());
						edit.putString("ip" + j, ab.getIp());
						edit.putInt("port" + j, ab.getPort());
						edit.putBoolean("remote" + j, ab.isRemote());
						edit.putInt("logicAddr" + j, ab.getLogicAddr());
					}
				}
				edit.putInt("total", j);
				edit.commit();
				// 启动服务轮询
				Intent intent = new Intent(getActivity(),
						SystemMenuService.class);
				intent.putExtra("taskNo", 1);// 轮询任务号1
				getActivity().startService(intent);
				Toast.makeText(getActivity(), "设置已经保存", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
	
	private boolean isSameList(){
		for(int i=0;i<6;++i){
			AddressBean ab = mlist.get(i);
			if(!ab.isEnable() && ab.isRemote()) continue;
			for(int j=i+1;j<6;++j){
				AddressBean tmpab = mlist.get(j);
				if(tmpab.isEnable() && (!tmpab.isRemote()) && ab.equals(tmpab)){
					Toast.makeText(getActivity(),
							"地址"+(i+1)+"与地址"+(j+1)+"不能相同，请重新设置！",Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		}
		
		return true;
	}


	/**
	 * list 的 adapter 配置
	 * 
	 */
	private class MyAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return mlist.size();
		}

		@Override
		public Object getItem(int position) {
			return mlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			MyListener myListener = null;
			if (convertView == null) {
				holder = new ViewHolder();
				holder.mPosition = position;
				
				convertView = lif.inflate(R.layout.address_element, null);
				holder.enableon = (ImageView) convertView
						.findViewById(R.id.elememt_addr_check);
				holder.enableoff = (ImageView) convertView
						.findViewById(R.id.elememt_addr_checkoff);
				holder.remoteon = (ImageView) convertView
						.findViewById(R.id.elememt_addr_remote);
				holder.remoteoff = (ImageView) convertView
						.findViewById(R.id.elememt_addr_remoteoff);
				holder.etip = (EditText) convertView
						.findViewById(R.id.elememt_ip);
				holder.etport = (EditText) convertView
						.findViewById(R.id.elememt_port);
				holder.etlogic = (EditText) convertView
						.findViewById(R.id.logic_addr);
				holder.tvcpc = (TextView) convertView
						.findViewById(R.id.elememt_cpcName);
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			myListener = new MyListener(position, holder);
			setListener(myListener.holder, myListener.mPosition);
			
			AddressBean d = mlist.get(position);
			String s = "CPC#";
			if (position < 9)
				s += "0";
			holder.tvcpc.setText(s + (position + 1));
			if (d.isEnable())
				holder.enableon.setVisibility(View.VISIBLE);
			else
				holder.enableon.setVisibility(View.INVISIBLE);
			if (d.isRemote())
				holder.remoteon.setVisibility(View.VISIBLE);
			else
				holder.remoteon.setVisibility(View.INVISIBLE);
			holder.etip.setText(d.getIp());
			holder.etip.setEnabled(true);
			int tmp = d.getPort();
			if(tmp!=0)
				holder.etport.setText(tmp + "");
			else{
				//避免复用时有数据无法显示hint
				holder.etport.setText(null);
				holder.etport.setHint("10000");
			}
			tmp = d.getLogicAddr();
			if(tmp!=-1)
				holder.etlogic.setText(tmp + "");
			else{
				holder.etlogic.setText(null);
				holder.etlogic.setHint("1");
			}

			return convertView;
		}
	}

	// 提取出来方便点
	public static class ViewHolder {
		public ImageView enableon, enableoff, remoteon, remoteoff;
		public EditText etip;
		public EditText etport;
		public EditText etlogic;
		public TextView tvcpc;
		public int mPosition;
		public int getmPosition() {
			return mPosition;
		}
		
	}

	// listView监听事件
	public class MyListener implements OnClickListener {
		int mPosition;
		ViewHolder holder;

		public MyListener(int inPosition) {
			mPosition = inPosition;
		}

		public MyListener(int inPosition, ViewHolder holder) {
			this(inPosition);
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
		}

	}

	public void setListener(ViewHolder holder, int position) {
		final ViewHolder mholder = holder;
		final int mPosition = position;
		holder.enableoff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mholder.enableon.setVisibility(View.VISIBLE);
				dialogab = mlist.get(mPosition);
				dialogab.setEnable(true);
			}
		});

		holder.enableon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mholder.enableon.setVisibility(View.INVISIBLE);
				dialogab = mlist.get(mPosition);
				dialogab.setEnable(false);
			}
		});
		holder.remoteoff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mholder.remoteon.setVisibility(View.VISIBLE);
				dialogab = mlist.get(mPosition);
				dialogab.setRemote(true);
			}
		});

		holder.remoteon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mholder.remoteon.setVisibility(View.INVISIBLE);
				dialogab = mlist.get(mPosition);
				dialogab.setRemote(false);
			}
		});
		holder.etip.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String s = ((EditText) v).getText().toString().toString();
				mholder.etip.setText(s);
				dialogab = mlist.get(mPosition);
				dialogab.setIp(s);
			}
		});
		holder.etport.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				String s = ((EditText) v).getText().toString().trim();
				mholder.etport.setText(s);
				int portint;
				if (!("".equals(s)) && s.length()<6)
					portint = Integer.parseInt(s);
				else
					portint = 0;
				if (portint > 65535)
					portint = 65535;
				dialogab = mlist.get(mPosition);
				dialogab.setPort(portint);
			}
		});
		holder.etlogic.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String s = ((EditText) v).getText().toString().trim();
				mholder.etlogic.setText(s);
				int portint;
				if (!("".equals(s)) && s.length()<6)
					portint = Integer.parseInt(s);
				else
					portint = 0;
				if (portint > 9999)
					portint = 9999;
				dialogab = mlist.get(mPosition);
				dialogab.setLogicAddr(portint);
			}
		});
	};

}
