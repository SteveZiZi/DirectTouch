package com.xtoee.bean;

/*
 * ��¼����ļ�¼��
 * ������¼��ʱ�䡢����������
 * get��set����
 * Ŀǰʹ��
 */
public class RecordBean {
	private String tim;// ʱ��
	private String act;// ��¼����
	private String detail;// ��¼����

	// �޲ι��캯��
	public RecordBean() {
		super();
		this.tim = "";
		this.act = "";
		this.detail = "";
	}

	// ���ι��캯��
	public RecordBean(String tim, String act, String detail) {
		super();
		this.tim = tim;
		this.act = act;
		this.detail = detail;
	}

	// ����Ϊ��Ӧ��get��set����
	public String getTim() {
		return tim;
	}

	public void setTim(String tim) {
		this.tim = tim;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
