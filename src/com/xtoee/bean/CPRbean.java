package com.xtoee.bean;

/*
 * ���в˵���CPR��
 * 
 */
public class CPRbean {

	private boolean fill;// �Ƿ����::��������
	private int devno;// �豸�ţ�������
	private double mv;// ��ѹ
	private double mi;// ����
	private int mt;// �¶�
	private String mstate;// ״̬
	private int msatateint;// ״̬
	public static final String mNORMAL = "����";
	public static final String mABNORMAL = "�쳣";

	// �޲ι��캯��
	public CPRbean() {
		super();
		this.fill = false;
		this.msatateint = -1;
		this.mstate = CPRbean.mABNORMAL;
	}

	// ���ι��캯��
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

	// ���ι��캯��
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

	// ����Ϊ��Ӧ��get��set����
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

	// ����Ϊtostring����������ת�����ַ�������������
	@Override
	public String toString() {
		return "CPRbean [mv=" + mv + ", mi=" + mi + ", mt=" + mt + ", mstate="
				+ mstate + ", msatateint=" + msatateint + "]";
	}

	public String toString2() {
		return "11," + mv + "," + mi + "," + mt + "," + mstate;
	}

}
