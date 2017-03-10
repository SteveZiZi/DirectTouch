package com.xtoee.main;

import com.xtoee.tools.MD5util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PwdReset {
	private Context context;
	private String oldPwd;
	private EditText etOldPwd,etNewPwd,etRepePwd;
	private SharedPreferences sp;
	
	public PwdReset(Context context){
		this.context = context;
	}
	
	private void getInfo(){
		sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
		String defaultpwd = context.getResources().getString(R.string.defaultPwd);
		oldPwd = sp.getString("pwd", MD5util.toString(MD5util.GetMD5Code(defaultpwd)));
	}
	
	public void resetPwd(){
		getInfo();
		AlertDialog.Builder builder = new Builder(context);
		View resetDialog = LayoutInflater.from(context).inflate(
				R.layout.pwdsetting, null);
		etOldPwd = (EditText) (resetDialog).findViewById(R.id.oldpwd);
		etNewPwd = (EditText) (resetDialog).findViewById(R.id.newpwd);
		etRepePwd = (EditText) (resetDialog).findViewById(R.id.repepwd);
		builder.setIcon(android.R.drawable.ic_menu_edit).setView(resetDialog).setTitle("修改密码");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String tmpOldpwd = etOldPwd.getText().toString().trim();
				tmpOldpwd = MD5util.toString(MD5util.GetMD5Code(tmpOldpwd));
				if(!tmpOldpwd.equals(oldPwd)){
					Toast.makeText(context, "密码错误！", Toast.LENGTH_SHORT).show();
					return ;
				}
				String tmpNewpwd = etNewPwd.getText().toString().trim();
				String tmpRepepwd = etRepePwd.getText().toString().trim();
				if(!tmpNewpwd.equals(tmpRepepwd)){
					Toast.makeText(context, "两次输入的新密码不相同，请重新输入！", Toast.LENGTH_SHORT).show();
					return ;
				}
				Editor edit = sp.edit();
				edit.putString("pwd", MD5util.toString(MD5util.GetMD5Code(tmpNewpwd)));
				edit.commit();
				Toast.makeText(context, "密码修改成功！", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("取消", null).show();
	}
}
