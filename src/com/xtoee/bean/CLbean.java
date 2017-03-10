package com.xtoee.bean;

/*
 * 运行菜单的回路类
 * 
 */
public class CLbean {

	private double mv;// 电压
	private double mi;// 电流
	private int mt;// 温度
	private int mdim;// dim
	private String mstate;// 状态

	// 无参构造函数
	public CLbean() {
		super();
		this.mstate = CPRbean.mABNORMAL;
	}

	// 带参构造函数
	public CLbean(double mv, double mi, int mt, int mdim, String mstate) {
		super();
		this.mv = mv;
		this.mi = mi;
		this.mt = mt;
		this.mdim = mdim;
		this.mstate = mstate;
		if (!(mstate.equals(CPRbean.mNORMAL))) {
			this.mstate = CPRbean.mABNORMAL;
		}
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

	public int getMdim() {
		return mdim;
	}

	public void setMdim(int mdim) {
		this.mdim = mdim;
	}

	public String getMstate() {
		return mstate;
	}

	public void setMstate(String mstate) {
		this.mstate = mstate;
	}

	// 以下为tostring方法，用于转化成字符串，测试所用
	@Override
	public String toString() {
		return "CLbean [mv=" + mv + ", mi=" + mi + ", mt=" + mt + ", mdim="
				+ mdim + ", mstate=" + mstate + "]";
	}

	public String toString2() {
		return "11," + mv + "," + mi + "," + mt + "," + mdim + "," + mstate;
	}

}
