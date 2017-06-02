package com.game.sdk.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdk.TTWAppService;
import com.game.sdk.utils.Logger;



public class ResultCode implements JsonParseInterface {
	public int code;
	public String data;
	public String orderid;
	public String username;
	public String password;
	public String sign;
	public long logintime;
	public String msg;
	public String ptbkey;
	public int isBindPhone;
	public String ttb;   //平台币
	public String gttb;  //游戏币
	public String url;// 充值时 第三方返回来的url
	public String userId;
	public String publickey;
	
	public String partnerId;
	public String email;
	public String privateKey;
	public String starttime;
	@Override
	public String getShotName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject buildJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			username = json.isNull("b") ? "" : json.getString("b");
			password = json.isNull("c") ? "" : json.getString("c");
			sign =json.isNull("userId") ? "" :json.getString("userId");
			if("".equals(json.getString("e"))){
				logintime = 0;
			}else{
				logintime = json.isNull("e") ? 0 : json.getLong("e");
			}
			msg = json.isNull("f") ? "" : json.getString("f");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loginoutJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			
			msg = json.isNull("b") ? "" : json.getString("b");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void regJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			username = json.isNull("b") ? "" : json.getString("b");
			password = json.isNull("c") ? "" : json.getString("c");
			msg = json.isNull("g") ? "" : json.getString("g");
			ptbkey =json.isNull("f") ? "" :json.getString("f");
			isBindPhone =json.isNull("h") ? 0 :json.getInt("h");
			ttb =json.isNull("i") ? "0" :json.getString("i");
			gttb =json.isNull("j") ? "0" :json.getString("j");
			
			TTWAppService.ttb = ttb;
			TTWAppService.gttb =gttb;
			TTWAppService.badge =json.isNull("badge") ? 0 :json.getInt("badge");
			
			userId = json.isNull("userId") ? "" : json.getString("userId");
			sign   = json.isNull("sign") ? "" : json.getString("sign");
			
			if("".equals(json.getString("e"))){
				logintime = 0;
			}else{
				logintime = json.isNull("e") ? 0 : json.getLong("e");
			}
			
			publickey = json.isNull("publickey") ? "" :json.getString("publickey");
			
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void oneregJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			username = json.isNull("b") ? "" : json.getString("b");
			password = json.isNull("d") ? "" : json.getString("d");
			msg = json.isNull("c") ? "" : json.getString("c");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void parseTTBJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			msg = json.isNull("b") ? "" : json.getString("b");
			orderid = json.isNull("c") ? "" : json.getString("c");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 现在支付返回参数
	 * 
	 * @param json
	 */
	public void parseNowPayJson(JSONObject json){
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			msg = json.isNull("b") ? "" : json.getString("b");
			orderid = json.isNull("c") ? "" : json.getString("c");
			starttime = json.isNull("starttime") ? new SimpleDateFormat("yyyyMMddHHmmss",Locale.CHINA).format(new Date()) : json.getString("starttime");
			JSONObject params =new JSONObject(json.getString("params"));
			if(params != null){
				partnerId =params.isNull("partnerid") ? "" : params.getString("partnerid"); 
				Logger.msg(partnerId);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void parseAlipayJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			msg = json.isNull("b") ? "" : json.getString("b");
			orderid = json.isNull("c") ? "" : json.getString("c");
			JSONObject params =new JSONObject(json.getString("params"));
			if(params != null){
				partnerId =params.isNull("partnerid") ? "" : params.getString("partnerid"); 
				email =params.isNull("email") ? "" : params.getString("email"); 
				privateKey =params.isNull("privatekey") ? "" : params.getString("privatekey");
				Logger.msg(partnerId+privateKey);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseTTBTwoJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			data = json.isNull("b") ? "" : json.getString("b");
			msg = json.isNull("c") ? "" : json.getString("c");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 财付通返回参数
	 * 
	 * @param json
	 */
	public void parseCFTJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			url = json.isNull("b") ? "" : json.getString("b");
			msg = json.isNull("c") ? "" : json.getString("c");
			orderid = json.isNull("d") ? "" : json.getString("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 易联返回参数
	 * 
	 * @param json
	 */
	public void parseECOJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("code") ? 0 : json.getInt("code");
			data = json.isNull("data") ? "" : json.getString("data");
			msg = json.isNull("msg") ? "" : json.getString("msg");
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 订单状态查询返回参数
	 * 
	 * @param json
	 */
	public void parseOrderidJson(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			code = json.isNull("a") ? 0 : json.getInt("a");
			msg = json.isNull("b") ? "" : json.getString("b");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
