package com.xtoee.bean;

/*
 * ͨѶ����ĵ�ַ��
 * ����ʹ�ܡ�IP�Ͷ˿�
 * get��set����
 */
public class AddressBean {
	private boolean enable;// ��ַʹ��
	private boolean remote;// �ƶ˿��ƣ�����
	private String ip;// ip
	private int port;// port
	private int logicAddr;// �߼���ַ

	// �޲ι��캯��
	public AddressBean() {
		super();
		this.enable = false;
		this.remote = false;
		this.ip = "";
		this.port = 0;
		this.logicAddr = 4;
	}

	// ���ι��캯��
	public AddressBean(boolean enable, String ip, int port) {
		super();
		this.enable = enable;
		this.ip = ip;
		this.port = port;
	}

	public AddressBean(boolean enable, boolean remote, String ip, int port,
			int logicAddr) {
		super();
		this.enable = enable;
		this.remote = remote;
		this.ip = ip;
		this.port = port;
		this.logicAddr = logicAddr;
	}

	// ����Ϊ��Ӧ��get��set����
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	public int getLogicAddr() {
		return logicAddr;
	}

	public void setLogicAddr(int logicAddr) {
		this.logicAddr = logicAddr;
	}
	
	@Override
	public boolean equals(Object o) {
		AddressBean other = (AddressBean)o;
		if(this.ip.equals(other.getIp()) && this.port==other.getPort()
				&& this.logicAddr==other.getLogicAddr()){
			return true;
		}else{
			return false;
		}
	}

	// tostring����������ת�� ���ַ���
	@Override
	public String toString() {
		return "AddressBean [enable=" + enable + ", ip=" + ip + ", port="
				+ port + ", logic=" + logicAddr + "]";
	}

}
