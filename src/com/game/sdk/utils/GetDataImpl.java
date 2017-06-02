package com.game.sdk.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdk.FYGameSDK;
import com.game.sdk.TTWAppService;
import com.game.sdk.domain.ChannelMessage;
import com.game.sdk.domain.PayWay;
import com.game.sdk.domain.ResultCode;

import android.content.Context;

/**
 * author janecer 2014年4月10日下午5:29:57
 */
public class GetDataImpl {

	private static final String TAG = "GetDataImpl";
	private static GetDataImpl getdataImpl;
	private static Context ctx;

	private GetDataImpl(Context ctxs) {
		this.ctx = ctxs;
		
	}

	public static GetDataImpl getInstance(Context ctxs) {
		if (null == getdataImpl) {
			getdataImpl = new GetDataImpl(ctxs);
		}
		if (ctx==null) {
			ctx=ctxs;
		}
		return getdataImpl;
	}

	public void test() {
		InputStream request = doRequest("http://192.168.0.159/web/test7.php",
				"ssss//!~@ssssssss12312淡定");
		String bb = parseIs2Str(request);
		String aa = Encrypt.decode(bb);
		// bb = Encrypt.decode(bb);
		Logger.msg("service test:" + bb);
		
	}
	
	 /**
	 * 根据图片地址 去服务端下载图片
	 * @param path
	 * @return
	 */
	 public InputStream getImgFromNet(String url){
		   return doRequesttwo(url, null);
	 }

	/**
	 * 登录
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode login(Context context, String username, String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_LOGIN, jasonStr);
		ResultCode result = new ResultCode();
		try {
			String str = unzip(request);
			boolean isWrite = true;
			if(str == null){
				str = Util.readInfoInSDCard(context, username);
				isWrite = false;
			}
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.regJson(json);
				if(isWrite){
					Util.writeInfoInSDCard(context, str, username);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public String createOrderId(){
		JSONObject jsonStr = new JSONObject();
		try {
			jsonStr = new JSONObject();	
			jsonStr.put("timestamp", Util.getOrderId());
			jsonStr.put("version", FYGameSDK.defaultSDK().getVersion());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream request = doRequest(Constants.URL_CREATE_ORDER, jsonStr.toString());
		return unzip(request);
	}

	/**
	 * 一键注册
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode UserOneKeyRegister(String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_ONKEY2REGISTER,
				jasonStr);
		ResultCode result = new ResultCode();
		
		try {
			String str = unzip(request);		
			if(null != str){		
				JSONObject json = new JSONObject(str);
				result.regJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 注册
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode register(String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_REGISTER, jasonStr);
		ResultCode result = new ResultCode();

		try {
			// Logger.msg("test = :"+parseIs2Str(request));
			String str = unzip(request);
			
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.regJson(json);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * 现在支付 
	 **/
	public ResultCode nowpayserver(String type, float amount, String username,
			String roleid, String serverid, String gameid, String orderid,
			String imeil, String appid, String agent, String productname,
			String productdesc, String fcallback, String attach,String preSignStr) {
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", username);
			json.put("d", roleid);
			json.put("e", serverid);
			json.put("f", gameid);
			json.put("g", orderid);
			json.put("h", imeil);
			json.put("j", appid);
			json.put("k", agent);
			json.put("l", productname);
			//json.put("m", productdesc);
			json.put("n", attach);
			json.put("fcallbackurl", fcallback);
			json.put("o", preSignStr);
			json.put("timestamp", Util.getOrderId());
			json.put("version", FYGameSDK.defaultSDK().getVersion());
			//Logger.msg("json :" + json.toString());
			InputStream request = doRequest(Constants.URL_CHARGER_NOWPAY,
					json.toString());
			
			String str = unzip(request);
			
			
			
			if(null != str){
				
				json = new JSONObject(str);
				result = new ResultCode();
				result.parseNowPayJson(json);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	/**
	 * 获取平台币
	 * 
	 * @param jasonStr
	 * @return
	 */
	public void getTTB(String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_PAYTTB, jasonStr);
		try {
			
			String str = unzip(request);
			if(null != str){
				JSONObject json = new JSONObject(str);
				
				JSONObject data = json.getJSONObject("b");
				
				TTWAppService.ttb = data.isNull("money") ? "0" : data.getString("money");
				TTWAppService.gttb = data.isNull("gamemoney") ? "0" : data.getString("gamemoney");
				
				
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	/**
	 * 获取平台币
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode getTTBTwo(String jasonStr) {
		
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			
			InputStream request = doRequest(Constants.URL_USER_PAYTTB,
						jasonStr);
			String str = unzip(request);
			if(null != str){
				json = new JSONObject(str);
			}

			result = new ResultCode();
			result.parseTTBTwoJson(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * 支付宝充值
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode alipay2server(String type, float amount, String username,
			String roleid, String serverid, String gameid, String orderid,
			String imeil, String appid, String agent, String productname,
			String productdesc, String fcallback, String attach) {
		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", username);
			json.put("d", roleid);
			json.put("e", serverid);
			json.put("f", gameid);
			json.put("g", orderid);
			json.put("h", imeil);
			json.put("j", appid);
			json.put("k", agent);
			json.put("l", productname);
			json.put("m", productdesc);
			json.put("n", attach);
			json.put("fcallbackurl", fcallback);
			json.put("timestamp", Util.getOrderId());
			json.put("version", FYGameSDK.defaultSDK().getVersion());
			InputStream request = doRequest(Constants.URL_CHARGER_ZIFUBAO,
					json.toString());
			
			String str = unzip(request);
			
			if(null != str){
				
				json = new JSONObject(str);
				result = new ResultCode();
				result.parseAlipayJson(json);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	

	/**
	 * 平台币支付
	 * 
	 * @param jasonStr
	 * @return
	 */
	public ResultCode changeTTB(String type, float amount, String imeil,
			String appid, String agent, String username, String roleid,
			String serverid, String gameid, String productname,
			String productdes, String attach, String sign, String orderid) {

		ResultCode result = null;

		try {
			JSONObject json = new JSONObject();
			json.put("a", type);
			json.put("b", amount);
			json.put("c", username);
			json.put("d", roleid);
			json.put("e", serverid);
			json.put("f", gameid);
			json.put("g", orderid);
			json.put("h", imeil);
			json.put("j", appid);
			json.put("k", agent);
			json.put("l", productname);
			json.put("timestamp", Util.getOrderId());
			json.put("version", FYGameSDK.defaultSDK().getVersion());
			InputStream request = doRequest(Constants.URL_CREATE_ORDER,
					json.toString());
			String str=unzip(request);
			
			json = new JSONObject(str);

			result = new ResultCode();
			result.parseTTBJson(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * imeil查询用户信息
	 * 
	 * @param appid
	 * @param imeil
	 * @return
	 */
	public String searchLoginUserinfoByImel(String appid, String imeil) {
		try {
			JSONObject json = new JSONObject();
			json.put("a", appid);
			json.put("b", imeil);
			json.put("timestamp", Util.getOrderId());
			json.put("version", FYGameSDK.defaultSDK().getVersion());
			InputStream request = doRequest(Constants.URL_IMSI_USERINFO,
					json.toString());
			//Logger.msg("searchLoginUserinfoByImel = :"+parseIs2Str(request));
			return unzip(request);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 发送验证码
	 * 
	 * @param appid
	 * @param imeil
	 * @return
	 */
	public String sendCodeByPhone(String phone) {
		try {
			JSONObject json = new JSONObject();
			json.put("a", phone);
			json.put("b", TTWAppService.userinfo.username);
			json.put("deviceId", TTWAppService.dm.imeil);
			json.put("timestamp", Util.getOrderId());
			json.put("version", FYGameSDK.defaultSDK().getVersion());

			InputStream request = doRequest(Constants.URL_SEND_CODE,
					json.toString());
			//Logger.msg("searchLoginUserinfoByImel = :"+parseIs2Str(request));
			return unzip(request);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 验证验证码
	 * 
	 * @param appid
	 * @param imeil
	 * @return
	 */
	public String checkCodeByPhone(String phone, String code, String username) {
		try {
			JSONObject json = new JSONObject();
			json.put("a", phone);
			json.put("b", code);
			json.put("c", username);
			json.put("deviceId", TTWAppService.dm.imeil);
			json.put("timestamp", Util.getOrderId());
			json.put("version", FYGameSDK.defaultSDK().getVersion());

			InputStream request = doRequest(Constants.URL_CHECK_CODE,
					json.toString());
			return unzip(request);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void getInit(Context context){
		try {
			JSONObject jsonparam = new JSONObject();
			jsonparam.put("a", TTWAppService.gameid);
			jsonparam.put("b", TTWAppService.agentid);
			jsonparam.put("timestamp", Util.getOrderId());
			jsonparam.put("version", FYGameSDK.defaultSDK().getVersion());

			InputStream request = doRequest(Constants.URL_GET_Init, jsonparam.toString());
			String jsonStr =unzip(request);
			boolean isWrite = true;
			if(jsonStr == null) {
				jsonStr = Util.readInfoInSDCard(context, "init.json");
				isWrite = false;
			}
			if(null != jsonStr){
				JSONObject jsonObject = new JSONObject(jsonStr);
				int code = jsonObject.getInt("code");
				TTWAppService.code = code;
				if(code == 1){
					JSONObject jsonInit = jsonObject.getJSONObject("data");					
					TTWAppService.tips  = jsonInit.getString("tip");
					TTWAppService.logo = jsonInit.getString("logo");
					TTWAppService.channels = new ArrayList<PayWay>();
					TTWAppService.debug  = jsonInit.isNull("debug") ? 0 : jsonInit.getInt("debug");
					PayWay cm = null;
					JSONArray json_channels =  jsonInit.getJSONArray("payway");
					for (int i = 0; i < json_channels.length(); i++) {
						JSONObject jb = json_channels.getJSONObject(i);
						cm = new PayWay(
									jb.isNull("a") ? 0 : jb.getInt("a"),
									jb.isNull("c") ? "" : jb.getString("c"),
									jb.isNull("b") ? "" : jb.getString("b"));
						TTWAppService.channels.add(cm);
						cm = null;
					}
					if(isWrite){
						Util.writeInfoInSDCard(context,jsonStr, "init.json");
					}
					Logger.msg("调试状态->"+TTWAppService.debug);
				}
				if(code == -100){
					String publickey = jsonObject.getString("publickey");
					if(publickey != null && !publickey.isEmpty()){	
						TTWAppService.publicKey = Util.getKey(publickey);	
						Logger.msg("公钥不对重新初始化");
						getInit(context);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		
	}
	
	 /**
	    * 上传用户图像
	    * 
	    * @param infoid
	    * @return
	    */
	   public String uploadImage(String imageBase64str){
		  
			try {
				JSONObject json = new JSONObject();
				json.put("a", TTWAppService.userinfo.username);
				json.put("b", imageBase64str);
				json.put("timestamp", Util.getOrderId());
				json.put("version", FYGameSDK.defaultSDK().getVersion());
				
				InputStream is= doRequest(Constants.URL_UPLOAD_PIC,json.toString());
				return unzip(is);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		   
		   return null;
	   }
	
	
	/**
	 * 用户退出登录
	 */
	public ResultCode loginOut(String jasonStr) {
		InputStream request = doRequest(Constants.URL_USER_LOGIN_OUT, jasonStr);
		ResultCode result = new ResultCode();

		try {
			String str = unzip(request);
			if(null != str){
				JSONObject json = new JSONObject(str);
				result.loginoutJson(json);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.msg("用户退出");
		return result;
	}

	/**************************************************************************************************************************/
	/**
	 * 负责发送请求与请求后获取数据(加密压缩发送)
	 * 
	 * @param url
	 *            请求地址
	 * @param str
	 *            请求携带的参数
	 * @return inputstream服务端返回的输入流
	 */
	public InputStream doRequest(String url, String str) {
		if(Constants.DEBUG){
			url = url.replace("http://api.6071.com/", "http://sdk.289.com/Api/");
		}	
		url += "/"+TTWAppService.gameid;
		
		if(TTWAppService.isLogin && TTWAppService.userinfo != null && TTWAppService.userinfo.username != null){
			url += "/" + TTWAppService.userinfo.username;
		}
		Logger.msg(url+"->"+str);
		HttpClient client = NetworkImpl.getHttpClient(ctx);
		if (null == client) {
			return null;
		}
		HttpPost post = new HttpPost(url);
		post.setHeader("content-type", "text/html");
		if (str != null) {
			String[] strs = sectionStr(str);
			if(strs == null){
				str = Rsa.encrypt(str, TTWAppService.publicKey);
			} else {
				try{
				List<byte[]> bytes = new ArrayList<byte[]>();
				int len = 0;
				for(int i=0; i < strs.length; i++){
					Logger.msg(i+"."+strs[i]);
					byte[] output= Rsa.encrypt2(strs[i], TTWAppService.publicKey);
					len += output.length;
					bytes.add(output);
				}
				byte[] dest = new byte[len];
				for(int i=0, start=0; i<bytes.size(); i++){
					byte[] tmp = bytes.get(i);
					System.arraycopy(tmp, 0, dest, start, tmp.length);
					start += tmp.length;
				}
				str=Base64.encode(dest);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			Logger.msg("客户端发送数据->"+str);
			if(str == null){
				return null;
			}
			HttpEntity entity = new ByteArrayEntity(compress(str.getBytes()));
			post.setEntity(entity);
		}
		try {
			HttpResponse response = client.execute(post);				
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return response.getEntity().getContent();
			}
		} catch (ClientProtocolException e) {
			Logger.msg("ClientProtocol异常");
			e.printStackTrace();
		} catch (IOException e) {
			Logger.msg("IO异常");
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("null")
	private String[] sectionStr(String str){
		if(str == null || str.isEmpty()){
			return null;
		}
		String[] strs = null;
		int length = str.length();
		if(length > 128){
			int len =length / 128 + (length % 128 > 0 ? 1 : 0);
			strs = new String[len];
			for(int i=0, j=0; i < length; i+=1){
				int start = i * 128;
				int last = (i+1)*128;
				if(last > length){
					last = length;
				}
				if(j >= len){
					break;
				}
				strs[j++] = str.substring(start, last);
			}
		}
		return strs;
	}
	
	/**
	    * 负责发送请求与请求后获取数据(无加密压缩发送)
	    * @param url 请求地址
	    * @param str 请求携带的参数
	    * @return inputstream服务端返回的输入流
	    */
	   public InputStream doRequesttwo(String url,String str){
		   HttpClient client=NetworkImpl.getHttpClient(ctx);
		   if(null==client){
			   return null;
		   }
		   
		   HttpPost post=new HttpPost(url);
		   post.setHeader("content-type", "text/html");
		   //Logger.msg("request url and data:"+url+"   data:"+str);
		   if(str!=null){
		     HttpEntity entity=new ByteArrayEntity(str.getBytes());
		     post.setEntity(entity);
		   }
		   int count=0;
		   //等待3秒在请求2次
		   while(count<2){
			  try {
			     HttpResponse response= client.execute(post);
			     if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
	               return response.getEntity().getContent();			  
			     }
		      } catch (ClientProtocolException e) {
					Logger.msg("网络连接异常");
					e.printStackTrace();
				} catch (IOException e) {
					Logger.msg("网络连接异常");
					e.printStackTrace();
				}
		     count++;
		     try {
				Thread.sleep(3000);
			    } catch (InterruptedException e) {
			    	Logger.msg("网络连接异常");
			    	e.printStackTrace();
			 }
		   }
		   return null;
	   }
	   
	  
	   
	   

	/**
	 * 解析服务端返回过来的输入流
	 * 
	 * @param is
	 * @return
	 */
	public String parseIs2Str(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			String str = new String(bos.toByteArray());
			//Logger.msg("service response data:" + str);
			return str;
		} catch (ClientProtocolException e) {
			Logger.msg("网络连接异常");
			e.printStackTrace();
		} catch (IOException e) {
			Logger.msg("网络连接异常");
			e.printStackTrace();
		} finally {
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					Logger.msg("网络连接异常");
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 解析服务端返回过来的输入流(无解压)
	 * 
	 * @param is
	 * @return
	 */
	public String parseIs3Str(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			String str = new String(bos.toByteArray());
			String dest = "";
	        if (str!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(str);
	            dest = m.replaceAll("");
	        }
			//Logger.msg("service response data:" + dest);
			//Logger.msg("service response data:--------" + Encrypt.decode2(dest));
			return Encrypt.decode2(dest);
		} catch (IOException e) {
			Logger.msg("数据获取异常");
			e.printStackTrace();
		} catch (NullPointerException e) {
			Logger.msg("数据获取异常");
			e.printStackTrace();
		} catch (Exception e) {
			Logger.msg("数据获取异常");
			e.printStackTrace();
		} finally {
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					Logger.msg("数据获取异常");
					e.printStackTrace();
					//return null;
				}
			}
		}
		return null;
	}

	/**
	 * 数据压缩
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] compress(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// 压缩
			compress(bais, baos);

			byte[] output = baos.toByteArray();

			baos.flush();
			baos.close();
			bais.close();

			return output;
		} catch (Exception e) {
			Logger.msg("数据压缩异常");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 数据压缩
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public static void compress(InputStream is, OutputStream os)
			throws Exception {
		GZIPOutputStream gos = new GZIPOutputStream(os);
		int count;
		byte data[] = new byte[1024];
		while ((count = is.read(data, 0, data.length)) != -1) {
			gos.write(data, 0, count);
		}
		// gos.flush();
		gos.finish();
		gos.close();
	}

	/**
	 * 数据解压
	 */
	public static String unzip(InputStream in) {
		// Open the compressed stream
		GZIPInputStream gin;
		try {
			if (in == null) {
				Logger.msg("服务器没有返回数据->null");
				return null;
			}
			gin = new GZIPInputStream(new BufferedInputStream(in));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// Transfer bytes from the compressed stream to the output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = gin.read(buf)) > 0) {
				out.write(buf, 0, len);
			}	
			gin.close();
			out.close();
			String str_result = new String(out.toByteArray());
			String result = Encrypt.decode(str_result);
			Logger.msg("服务器返回数据->"+ result);
			return result;
		} catch (IOException e) {
			Logger.msg("服务器返回数据解压异常:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
