package com.xtoee.bean;

/*
 * �������ý����scene��
 * get��set����
 */
public class Scenebean {
	boolean enable;// ʹ��
	int tnNo;// �豸��
	int clNO;// ��·��
	int specNo;// ���⹦��
	int para;// ����

	// �޲ι��캯��
	public Scenebean() {
		super();
	}

	// ���ι��캯��
	public Scenebean(boolean enable, int tnNo, int clNO, int specNo, int para) {
		super();
		this.enable = enable;
		this.tnNo = tnNo;
		this.clNO = clNO;
		this.specNo = specNo;
		this.para = para;
	}

	// ����Ϊ��Ӧ��get��set����
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

	// tostring����������ת�� ���ַ���
	@Override
	public String toString() {
		return "Scenebean [enable=" + enable + ", tnNo=" + tnNo + ", clNO="
				+ clNO + ", specNo=" + specNo + ", para=" + para + "]";
	}

}
