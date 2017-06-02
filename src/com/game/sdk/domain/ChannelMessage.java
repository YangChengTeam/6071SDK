package com.game.sdk.domain;
/**
 * author janecer
 * 2014年5月20日上午11:43:12
 * 支付聚道信息
 */
public class ChannelMessage {

	public int channelId;

	public String channelMsg;
	
	public String channelDes;
	
	/**
	 * 
	 * @param channelId
	 * @param channelMsg
	 * @param channelDes
	 */
	public ChannelMessage(int channelId,String channelMsg,String channelDes){
		this.channelId=channelId;
		this.channelMsg=channelMsg;
		this.channelDes=channelDes;
	}
	
}
