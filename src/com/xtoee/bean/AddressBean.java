package com.xtoee.bean;

/*
 * 通讯界面的地址类
 * 包括使能、IP和端口
 * get和set方法
 */
public class AddressBean {
	private boolean enable;// 地址使能
	private boolean remote;// 云端控制，新增
	private String ip;// ip
	private int port;// port
	private int logicAddr;// 逻辑地址

	// 无参构造函数
	public AddressBean() {
		super();
		this.enable = false;
		this.remote = false;
		this.ip = "";
		this.port = 0;
		this.logicAddr = 4;
	}

	// 带参构造函数
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

	// 以下为相应的get和set方法
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

	// tostring方法，用于转化 成字符串
	@Override
	public String toString() {
		return "AddressBean [enable=" + enable + ", ip=" + ip + ", port="
				+ port + ", logic=" + logicAddr + "]";
	}

}
