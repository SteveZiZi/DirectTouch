package com.xtoee.bean;

/*
 * ������
 * 
 */
public class Taskbean {

	private int ID;// ����ID
	private String name;// ������
	private int level;// ���ȼ�
	private long begintiem;// ��ʼʱ��
	private long endtime;// ����ʱ��
	private byte loopway;// ѭ����ʽ
	private int curCPC;// ��ǰCPC
	private int cls;// ��·��
	private int actionNum;// ������

	// �޲ι��캯��
	public Taskbean() {
		super();
	}

	// ���ι��캯��
	public Taskbean(int iD, String name, int level, long begintiem,
			long endtime, byte loopway, int curCPC, int cls, int actionNum) {
		super();
		ID = iD;
		this.name = name;
		this.level = level;
		this.begintiem = begintiem;
		this.endtime = endtime;
		this.loopway = loopway;
		this.curCPC = curCPC;
		this.cls = cls;
		this.actionNum = actionNum;
	}

	// ����Ϊ��Ӧ��get��set����
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getBegintiem() {
		return begintiem;
	}

	public void setBegintiem(long begintiem) {
		this.begintiem = begintiem;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	public byte getLoopway() {
		return loopway;
	}

	public void setLoopway(byte loopway) {
		this.loopway = loopway;
	}

	public int getCurCPC() {
		return curCPC;
	}

	public void setCurCPC(int curCPC) {
		this.curCPC = curCPC;
	}

	public int getCls() {
		return cls;
	}

	public void setCls(int cls) {
		this.cls = cls;
	}

	public int getActionNum() {
		return actionNum;
	}

	public void setActionNum(int actionNum) {
		this.actionNum = actionNum;
	}

	// ����Ϊtostring����������ת�����ַ�������������
	@Override
	public String toString() {
		return "Taskbean [ID=" + ID + ", name=" + name + ", level=" + level
				+ ", begintiem=" + begintiem + ", endtime=" + endtime
				+ ", loopway=" + loopway + ", curCPC=" + curCPC + ", cls="
				+ cls + ", actionNum=" + actionNum + "]";
	}

}
