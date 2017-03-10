package com.xtoee.bean;

/*
 * 运行菜单的子回路类
 * 由于暂无电压电流，未用
 */
public class SubCLbean {

	private double mv;// 电压
	private double mi;// 电流
	private boolean ison;

	public SubCLbean() {
		super();
	}

	public SubCLbean(double mv, double mi, boolean ison) {
		super();
		this.mv = mv;
		this.mi = mi;
		this.ison = ison;
	}

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

	public boolean isIson() {
		return ison;
	}

	public void setIson(boolean ison) {
		this.ison = ison;
	}

	@Override
	public String toString() {
		return "SubCLbean [mv=" + mv + ", mi=" + mi + ", ison=" + ison + "]";
	}

	public String toString2() {
		return "11," + mv + "," + mi + "," + ison;
	}

}
