package com.xtoee.bean;

/*
 * 记录界面的记录类
 * 包括记录的时间、动作和详情
 * get和set方法
 * 目前使用
 */
public class RecordBean {
	private String tim;// 时间
	private String act;// 记录动作
	private String detail;// 记录详情

	// 无参构造函数
	public RecordBean() {
		super();
		this.tim = "";
		this.act = "";
		this.detail = "";
	}

	// 带参构造函数
	public RecordBean(String tim, String act, String detail) {
		super();
		this.tim = tim;
		this.act = act;
		this.detail = detail;
	}

	// 以下为相应的get和set方法
	public String getTim() {
		return tim;
	}

	public void setTim(String tim) {
		this.tim = tim;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
