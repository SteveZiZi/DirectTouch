package com.xtoee.bean;

/*
 * 任务类
 * 
 */
public class Taskbean {

	private int ID;// 任务ID
	private String name;// 任务名
	private int level;// 优先级
	private long begintiem;// 开始时间
	private long endtime;// 结束时间
	private byte loopway;// 循环方式
	private int curCPC;// 当前CPC
	private int cls;// 回路号
	private int actionNum;// 动作数

	// 无参构造函数
	public Taskbean() {
		super();
	}

	// 带参构造函数
	public Taskbean(int iD, String name, int level, long begintiem,
			long endtime, byte loopway, int curCPC, int cls, int actionNum) {
		super();
		ID = iD;
		this.name = name;
		this.level = level;
		this.begintiem = begintiem;
		this.endtime = endtime;
		this.loopway = loopway;
		this.curCPC = curCPC;
		this.cls = cls;
		this.actionNum = actionNum;
	}

	// 以下为相应的get和set方法
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getBegintiem() {
		return begintiem;
	}

	public void setBegintiem(long begintiem) {
		this.begintiem = begintiem;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	public byte getLoopway() {
		return loopway;
	}

	public void setLoopway(byte loopway) {
		this.loopway = loopway;
	}

	public int getCurCPC() {
		return curCPC;
	}

	public void setCurCPC(int curCPC) {
		this.curCPC = curCPC;
	}

	public int getCls() {
		return cls;
	}

	public void setCls(int cls) {
		this.cls = cls;
	}

	public int getActionNum() {
		return actionNum;
	}

	public void setActionNum(int actionNum) {
		this.actionNum = actionNum;
	}

	// 以下为tostring方法，用于转化成字符串，测试所用
	@Override
	public String toString() {
		return "Taskbean [ID=" + ID + ", name=" + name + ", level=" + level
				+ ", begintiem=" + begintiem + ", endtime=" + endtime
				+ ", loopway=" + loopway + ", curCPC=" + curCPC + ", cls="
				+ cls + ", actionNum=" + actionNum + "]";
	}

}
