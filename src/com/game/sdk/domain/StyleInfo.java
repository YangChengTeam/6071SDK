package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class StyleInfo {

	public int id;

	public String title;

	@JSONField(name = "head_color")
	public String headColor;

	@JSONField(name = "bg_color")
	public String bgColor;

	@JSONField(name = "btn_color")
	public String btnColor;

	@JSONField(name = "notice_color")
	public String noticeColor;

	@JSONField(name = "font_color")
	public String fontColor;

	@JSONField(name = "module_color")
	public String moduleColor;

	@JSONField(name = "reg_img")
	public String regImage;

	@JSONField(name = "play_img")
	public String playImage;

	public String img;
	
	public int sort;
	
	public int status;
}
