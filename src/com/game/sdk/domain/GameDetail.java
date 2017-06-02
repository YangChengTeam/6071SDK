package com.game.sdk.domain;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author janecer
 * 2014年4月16日下午6:41:36
 * b
 */
public class GameDetail implements JsonParseInterface,Serializable{
	
	public String name;//游戏名字
	public String image;//游戏图标  url地址
	public String count;//下载次数
	public String size;//游戏大小
	public String versions;//游戏版本
	public String type;//游戏类型
	public String jietu1,jietu2,jietu3,jietu4;//四张游戏截图
	public String description;//游戏介绍
	public String function;//活动简介
	public String androidurl;//游戏下载地址
    public String baoming;//游戏包名
	
	@Override
	public String getShotName() {
		return "b";
	}
	
	@Override
	public JSONObject buildJson() {
		
		return null;
	}
	
	@Override
	public void parseJson(JSONObject json) {
		
		try {
			name=json.isNull("f")?"":json.getString("f");
			image=json.isNull("a")?"":json.getString("a").trim();
			//count=json.isNull("c")?"":json.getString("c");
			size=json.isNull("d")?"":json.getString("d");
			//name=json.isNull("n")?"":json.getString("n");
			versions=json.isNull("c")?"":json.getString("c");
			//type=json.isNull("t")?"":json.getString("t");
			//jietu1=json.isNull("jt1")?"":json.getString("jt1");
			//jietu2=json.isNull("jt2")?"":json.getString("jt2");
			//jietu3=json.isNull("jt3")?"":json.getString("jt3");
			//jietu4=json.isNull("jt4")?"":json.getString("jt4");
			description=json.isNull("b")?"":json.getString("b");
			//function=json.isNull("f")?"":json.getString("f");
			androidurl=json.isNull("e")?"":json.getString("e").trim();
			//baoming=json.isNull("bm")?"":json.getString("bm");
		} catch (JSONException e) {
		}
	}
	
}
