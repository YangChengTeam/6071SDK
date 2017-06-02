package com.game.sdk.utils;

import java.lang.reflect.Array;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdk.domain.JsonParseInterface;

/**
 * author janecer 2014年4月9日下午5:24:08 用来处理从网络上获得json格式数据进行转换
 */
public class JsonUtil {

	/**
	 * 根据class，将json格式数据转换成相对应的实体类[前提待转换的实体具有shortName]
	 * 
	 * @param clazz
	 * @param jsonStr
	 * @return
	 */
	public static JsonParseInterface parseJson2Object(Class<?> clazz,
			String jsonStr) {
		if (null == jsonStr) {
			return null;
		}
		try {
			JSONObject jo = new JSONObject(jsonStr);
			JsonParseInterface jobj = (JsonParseInterface) clazz.newInstance();
			if (jo.isNull(jobj.getShotName())) {
				jobj.parseJson(jo.getJSONObject(jobj.getShotName()));
				return jobj;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据class 将json格式数据转化成相关实体数据[前提待转换的实体具有shortName]
	 * 
	 * @param clazz
	 * @param jsonStr
	 * @return
	 */
	public static JsonParseInterface[] parseJson2Array(Class<?> clazz,
			String jsonStr) {
		if (null == jsonStr) {
			return null;
		}
		try {
			JSONObject jo = new JSONObject(jsonStr);
			JsonParseInterface jobj = (JsonParseInterface) clazz.newInstance();
			if (jo.isNull(jobj.getShotName())) {
				JSONArray json_arry = jo.getJSONArray(jobj.getShotName());
				JsonParseInterface[] jobjs = (JsonParseInterface[]) Array
						.newInstance(clazz, json_arry.length());
				for (int i = 0; i < json_arry.length(); i++) {
					jobj = (JsonParseInterface) clazz.newInstance();
					jobj.parseJson(json_arry.getJSONObject(i));
					jobjs[i] = jobj;
				}
				return jobjs;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 直接纯相关json对应关联实体对象转换
	 * 
	 * @param clazz
	 * @param jsonStr
	 * @return
	 */
	public static JsonParseInterface parseJson2ObjectNoShotName(Class<?> clazz,
			String jsonStr) {
		if (null == jsonStr) {
			return null;
		}
		try {
			JsonParseInterface jobj = (JsonParseInterface) clazz.newInstance();
			jobj.parseJson(new JSONObject(jsonStr));
			return jobj;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 纯json array对应的实体对象的数组
	 * 
	 * @param clazz
	 * @param jsonStr
	 * @return
	 */
	public static JsonParseInterface[] parseJson2ArrayNoShotName(
			Class<?> clazz, String jsonStr) {
		if (null == jsonStr) {
			return null;
		}
		try {
			JsonParseInterface jobj = null;
			JSONArray joarray = new JSONArray(jsonStr);
			JsonParseInterface[] jobj_array = (JsonParseInterface[]) Array
					.newInstance(clazz, joarray.length());
			for (int i = 0; i < joarray.length(); i++) {
				jobj = (JsonParseInterface) clazz.newInstance();
				jobj.parseJson(joarray.getJSONObject(i));
				jobj_array[i] = jobj;
			}
			return jobj_array;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
