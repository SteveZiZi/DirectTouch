package com.xtoee.bean;

/*
 * �������Ķ�����
 * ��������ʱ��Ͷ�������
 * get��set����
 */
public class Actionbean implements Comparable<Actionbean> {

	private int actime; // ����ʱ��
	private int act;// ��������

	// �޲ι��캯��
	public Actionbean() {
		super();
	}

	// ���ι��캯��
	public Actionbean(int actime, int act) {
		super();
		this.actime = actime;
		this.act = act;
	}

	// ����Ϊ��Ӧ��get��set����
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

	// tostring����������ת�� ���ַ���
	@Override
	public String toString() {
		return "Actionbean [actime=" + actime + ", act=" + act + "]";
	}

	// compare�������Ƚ�����ʱ����
	@Override
	public int compareTo(Actionbean obj) {
		return new Integer(actime).compareTo(new Integer(obj.getActime()));
	}

}
