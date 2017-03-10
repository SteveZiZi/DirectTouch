package com.xtoee.bean;

/*
 * 任务界面的动作类
 * 包括动作时间和动作内容
 * get和set方法
 */
public class Actionbean implements Comparable<Actionbean> {

	private int actime; // 动作时间
	private int act;// 动作内容

	// 无参构造函数
	public Actionbean() {
		super();
	}

	// 带参构造函数
	public Actionbean(int actime, int act) {
		super();
		this.actime = actime;
		this.act = act;
	}

	// 以下为相应的get和set方法
	public int getActime() {
		return actime;
	}

	public void setActime(int actime) {
		this.actime = actime;
	}

	public int getAct() {
		return act;
	}

	public void setAct(int act) {
		this.act = act;
	}

	// tostring方法，用于转化 成字符串
	@Override
	public String toString() {
		return "Actionbean [actime=" + actime + ", act=" + act + "]";
	}

	// compare函数，比较排序时所用
	@Override
	public int compareTo(Actionbean obj) {
		return new Integer(actime).compareTo(new Integer(obj.getActime()));
	}

}
