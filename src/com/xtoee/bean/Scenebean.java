package com.xtoee.bean;

/*
 * 场景设置界面的scene类
 * get和set方法
 */
public class Scenebean {
	boolean enable;// 使能
	int tnNo;// 设备号
	int clNO;// 子路号
	int specNo;// 特殊功能
	int para;// 参数

	// 无参构造函数
	public Scenebean() {
		super();
	}

	// 带参构造函数
	public Scenebean(boolean enable, int tnNo, int clNO, int specNo, int para) {
		super();
		this.enable = enable;
		this.tnNo = tnNo;
		this.clNO = clNO;
		this.specNo = specNo;
		this.para = para;
	}

	// 以下为相应的get和set方法
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getTnNo() {
		return tnNo;
	}

	public void setTnNo(int tnNo) {
		this.tnNo = tnNo;
	}

	public int getClNO() {
		return clNO;
	}

	public void setClNO(int clNO) {
		this.clNO = clNO;
	}

	public int getSpecNo() {
		return specNo;
	}

	public void setSpecNo(int specNo) {
		this.specNo = specNo;
	}

	public int getPara() {
		return para;
	}

	public void setPara(int para) {
		this.para = para;
	}

	// tostring方法，用于转化 成字符串
	@Override
	public String toString() {
		return "Scenebean [enable=" + enable + ", tnNo=" + tnNo + ", clNO="
				+ clNO + ", specNo=" + specNo + ", para=" + para + "]";
	}

}
