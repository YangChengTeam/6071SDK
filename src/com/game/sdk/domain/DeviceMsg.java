package com.game.sdk.domain;

/**
 * 设备保存信息
 * 
 * @author Administrator
 * 
 */
public class DeviceMsg {

	public String imeil;// 手机标识码
	public String deviceinfo;// 设备数据 包括手机号码、用户系统版本，以||隔开
	public String userip;// 用户使终端的网络的ip
	public String userua = "aa";// 用户使用的移动终端的UA信息

	public int device = 2;// 设备来源 2为android端

	public DeviceMsg() {
	};

	/**
	 * 构造实例
	 * 
	 * @param imeil
	 * @param deviceinfo
	 * @param serverid
	 * @param agent
	 * @param device
	 */
	public DeviceMsg(String imeil, String deviceinfo, int device,
			String userip, String userua) {
		this.imeil = imeil;
		this.deviceinfo = deviceinfo;
		this.device = device;
		this.userip = userip;
		this.userua = userua;
	}

}
