package com.xtoee.bean;

/*
 * 运行菜单的CPR类
 * 
 */
public class CPRbean {

	private boolean fill;// 是否填充::后面新增
	private int devno;// 设备号：：新增
	private double mv;// 电压
	private double mi;// 电流
	private int mt;// 温度
	private String mstate;// 状态
	private int msatateint;// 状态
	public static final String mNORMAL = "正常";
	public static final String mABNORMAL = "异常";

	// 无参构造函数
	public CPRbean() {
		super();
		this.fill = false;
		this.msatateint = -1;
		this.mstate = CPRbean.mABNORMAL;
	}

	// 带参构造函数
	public CPRbean(double mv, double mi, int mt, String mstate, int msatateint) {
		super();
		this.fill = false;
		this.mv = mv;
		this.mi = mi;
		this.mt = mt;
		this.mstate = mstate;
		this.msatateint = msatateint;
		if (!(mstate.equals(CPRbean.mNORMAL))) {
			this.mstate = CPRbean.mABNORMAL;
		}
	}

	// 带参构造函数
	public CPRbean(boolean fill, int devno, double mv, double mi, int mt,
			String mstate, int msatateint) {
		super();
		this.fill = fill;
		this.devno = devno;
		this.mv = mv;
		this.mi = mi;
		this.mt = mt;
		this.mstate = mstate;
		this.msatateint = msatateint;
	}

	// 以下为相应的get和set方法
	public double getMv() {
		return mv;
	}

	public void setMv(double mv) {
		this.mv = mv;
	}

	public double getMi() {
		return mi;
	}

	public void setMi(double mi) {
		this.mi = mi;
	}

	public int getMt() {
		return mt;
	}

	public void setMt(int mt) {
		this.mt = mt;
	}

	public String getMstate() {
		return mstate;
	}

	public void setMstate(String mstate) {
		this.mstate = mstate;
	}

	public int getMsatateint() {
		return msatateint;
	}

	public void setMsatateint(int msatateint) {
		this.msatateint = msatateint;
	}

	public boolean isFill() {
		return fill;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}

	public int getDevno() {
		return devno;
	}

	public void setDevno(int devno) {
		this.devno = devno;
	}

	// 以下为tostring方法，用于转化成字符串，测试所用
	@Override
	public String toString() {
		return "CPRbean [mv=" + mv + ", mi=" + mi + ", mt=" + mt + ", mstate="
				+ mstate + ", msatateint=" + msatateint + "]";
	}

	public String toString2() {
		return "11," + mv + "," + mi + "," + mt + "," + mstate;
	}

}
