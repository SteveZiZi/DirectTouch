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
		builder.setIcon(android.R.drawable.ic_menu_edit).setView(resetDialog).setTitle("�޸�����");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String tmpOldpwd = etOldPwd.getText().toString().trim();
				tmpOldpwd = MD5util.toString(MD5util.GetMD5Code(tmpOldpwd));
				if(!tmpOldpwd.equals(oldPwd)){
					Toast.makeText(context, "�������", Toast.LENGTH_SHORT).show();
					return ;
				}
				String tmpNewpwd = etNewPwd.getText().toString().trim();
				String tmpRepepwd = etRepePwd.getText().toString().trim();
				if(!tmpNewpwd.equals(tmpRepepwd)){
					Toast.makeText(context, "��������������벻��ͬ�����������룡", Toast.LENGTH_SHORT).show();
					return ;
				}
				Editor edit = sp.edit();
				edit.putString("pwd", MD5util.toString(MD5util.GetMD5Code(tmpNewpwd)));
				edit.commit();
				Toast.makeText(context, "�����޸ĳɹ���", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("ȡ��", null).show();
	}
}
