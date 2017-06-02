package com.game.sdk.domain;

import org.json.JSONObject;

/**
 * author janecer 2014年4月9日下午5:21:12
 */
public interface JsonParseInterface {

	// 获取实体对象的简写名字
	String getShotName();

	// 将实体数据转换成json格式
	JSONObject buildJson();

	// 将json格式数据转换成对象的实体对象
	void parseJson(JSONObject json);

}
