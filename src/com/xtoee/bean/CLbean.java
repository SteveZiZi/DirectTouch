package com.xtoee.bean;

/*
 * ���в˵��Ļ�·��
 * 
 */
public class CLbean {

	private double mv;// ��ѹ
	private double mi;// ����
	private int mt;// �¶�
	private int mdim;// dim
	private String mstate;// ״̬

	// �޲ι��캯��
	public CLbean() {
		super();
		this.mstate = CPRbean.mABNORMAL;
	}

	// ���ι��캯��
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

	// ����Ϊtostring����������ת�����ַ�������������
	@Override
	public String toString() {
		return "CLbean [mv=" + mv + ", mi=" + mi + ", mt=" + mt + ", mdim="
				+ mdim + ", mstate=" + mstate + "]";
	}

	public String toString2() {
		return "11," + mv + "," + mi + "," + mt + "," + mdim + "," + mstate;
	}

}
