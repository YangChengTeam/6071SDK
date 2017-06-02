package com.game.sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author janecer
 * 2014年4月18日下午7:57:47
 * 游戏礼包item页面
 */
public class GameGiftItem implements JsonParseInterface {
	
	
	public int infoid;
	public int gameid;
    public String title;
    public String content;
    public String game_image;
    public long starttime;
	
	
	@Override
	public String getShotName() {
		return "e";
	}

	@Override
	public JSONObject buildJson() {
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		try{
			title=json.isNull("a")?"":json.getString("a");
			game_image=json.isNull("b")?"":json.getString("b").trim();
			content=json.isNull("c")?"":json.getString("c");
			infoid=json.isNull("d")?0:json.getInt("d");
			gameid=json.isNull("e")?0:json.getInt("e");
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

}
