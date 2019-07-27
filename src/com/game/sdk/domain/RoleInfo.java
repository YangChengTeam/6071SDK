package com.game.sdk.domain;


public class RoleInfo {
	
	/**
	 * 
	 * @param dataType
	 *            1.进入游戏 2.角色升级 3.进入副本 4.离开副本 5.创建角色
	 * @param cpUid
	 *            游戏的唯一标示 (就是指用户的ID)
	 * @param roleId
	 *            角色id
	 * @param roleLevel
	 *            角色等级
	 * @param serverID
	 *            服务器id
	 * @param vip
	 *            角色VIP等级 默认传入 0
	 * @param union
	 *            角色工会名称 没有的话写暂无
	 *   
	 * @param roleName 角色名称
	 */
	private int dataType;
	
	private String userId;
	
	private String roleId;
	
	private String roleLevel;
	
	private String serverID;
	
	private String serverName;
	
	private String vip;
	
	private String union;
	
	private String roleName;
	
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleLevel() {
		return roleLevel;
	}
	public void setRoleLevel(String roleLevel) {
		this.roleLevel = roleLevel;
	}
	public String getServerID() {
		return serverID;
	}
	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public String getUnion() {
		return union;
	}
	public void setUnion(String union) {
		this.union = union;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}
