package com.xtoee.bean;

public class AdjBean {
	private int clnum;
	private boolean ison;
	private int dim;

	public AdjBean() {
		super();
		this.clnum = 1;
		this.ison = false;
		this.dim = 0;
	}

	public AdjBean(int clnum, boolean ison, int dim) {
		super();
		this.clnum = clnum;
		this.ison = ison;
		this.dim = dim;
	}

	public int getClnum() {
		return clnum;
	}

	public void setClnum(int clnum) {
		this.clnum = clnum;
	}

	public boolean isIson() {
		return ison;
	}

	public void setIson(boolean ison) {
		this.ison = ison;
	}

	public int getDim() {
		return dim;
	}

	public void setDim(int dim) {
		this.dim = dim;
	}

}
