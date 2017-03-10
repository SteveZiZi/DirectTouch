package com.xtoee.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.xtoee.tools.AutoAdjustLinearLayout;
import com.xtoee.tools.AutoAdjustLinearLayout.KeyBordStateListener;
import com.xtoee.tools.MD5util;

public class LoginActivity extends Activity implements KeyBordStateListener{
	private boolean isSave,autoLogin;// 登录状态
	private String user, pwd;// 登录ID、密码
	private EditText etuser, etpassword;
	private CheckBox cbSave,cbAuto;
	private Button btlogin;
	private SharedPreferences sp;
	private Intent intent;
	
	private AutoAdjustLinearLayout resizeLayout;
	private View loginLogo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);

		findID();
		InitPara();
		setListener();
		resizeLayout.setKeyBordStateListener(this);//设置回调方法

	}
	
	@Override
	protected void onResume() {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

	private void findID() {
		etuser = (EditText) findViewById(R.id.user);
		etpassword = (EditText) findViewById(R.id.password);
		cbSave = (CheckBox) findViewById(R.id.savepass);
		cbAuto = (CheckBox) findViewById(R.id.autologin);
		btlogin = (Button) findViewById(R.id.login);
		//获得可根据软键盘的弹出/关闭而隐藏和显示某些区域的LinearLayoutView组件
		resizeLayout = (AutoAdjustLinearLayout) findViewById(R.id.resizeLayout);
		//获得要控制隐藏和显示的区域
		loginLogo = (View) findViewById(R.id.login_logo);
	}

	private void InitPara() {
		sp = getSharedPreferences("login", MODE_PRIVATE);
		
		user = sp.getString("user", "null");
		if ("null".equals(user)){
//			String defaultUser = getResources().getString(R.string.defaultUser);
//			user = MD5util.toString(MD5util.GetMD5Code(defaultUser));
			user = getResources().getString(R.string.defaultUser);
		}
		pwd = sp.getString("pwd", "null");
		if ("null".equals(pwd)){
			String defaultpwd = getResources().getString(R.string.defaultPwd);
			pwd = MD5util.toString(MD5util.GetMD5Code(defaultpwd));
		}
		isSave = sp.getBoolean("isSave", false);
		autoLogin = sp.getBoolean("autoLogin", false);
		intent = new Intent(LoginActivity.this, TheMainActivity.class);
		if (autoLogin) {
			String tmpUser = sp.getString("tmpUser", "");
			String tmpPwd = sp.getString("tmpPwd", "");
			tmpPwd = MD5util.toString(MD5util.GetMD5Code(tmpPwd));
			if (tmpUser.equals(user) && tmpPwd.equals(pwd)) {
				startActivity(intent);
				finish();
			}else {
				Toast.makeText(LoginActivity.this, "用户名与密码不匹配，请重新输入！",
						Toast.LENGTH_SHORT).show();
			}
		}else if(isSave){
			String tmpUser = sp.getString("tmpUser", "");
			String tmpPwd = sp.getString("tmpPwd", "");
			etuser.setText(tmpUser);
			etpassword.setText(tmpPwd);
			cbSave.setChecked(true);
		}
	}

	private void setListener() {
		btlogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String tempuser = etuser.getText().toString().trim();
//				tempuser = MD5util.toString(MD5util.GetMD5Code(tempuser));
				String temppassword = etpassword.getText().toString().trim();
				String MD5temppassword = MD5util.toString(MD5util
						.GetMD5Code(temppassword));
				boolean save = cbSave.isChecked();
				boolean auto = cbAuto.isChecked();
				if (!("".equals(tempuser))) {
					if (tempuser.equals(user) && 
							MD5temppassword.equals(pwd)) {
						Editor edit = sp.edit();
						edit.putString("tmpUser", user);
						edit.putString("tmpPwd", temppassword);
						edit.putBoolean("isSave", save);
						edit.putBoolean("autoLogin", auto);
						edit.commit();
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(LoginActivity.this, "用户名与密码不匹配，请重新输入！",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(LoginActivity.this, "请输入用户名或密码！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		cbAuto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cbSave.setChecked(true);
				}
			}
		});
	}

	//实现接口中的方法，该方法在resizeLayout的onSizeChanged方法中调用
	@Override
	public void stateChange(int state) {
		switch (state) {
        case AutoAdjustLinearLayout.KEYBORAD_HIDE:
        	loginLogo.setVisibility(View.VISIBLE);
            break;
        case AutoAdjustLinearLayout.KEYBORAD_SHOW:
        	loginLogo.setVisibility(View.GONE);
            break;
        }
	}

}
