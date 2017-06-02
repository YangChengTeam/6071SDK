package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class PayWay {
	
	@JSONField(name = "a")
	public int channelId;

	@JSONField(name = "b")
	public String channelMsg;
	
	@JSONField(name = "c")
	public String channelDes;
	
	public PayWay(){}
	
	public PayWay(int channelId,String channelMsg,String channelDes){
		this.channelId=channelId;
		this.channelMsg=channelMsg;
		this.channelDes=channelDes;
	}
}
