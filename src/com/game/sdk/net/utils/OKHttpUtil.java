package com.game.sdk.net.utils;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.game.sdk.FYGameSDK;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.entry.UpFileInfo;
import com.game.sdk.security.Encrypt;
import com.game.sdk.utils.EmulatorCheckUtil;
import com.game.sdk.utils.EncryptUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.Util;

import android.os.Build;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhangkai on 16/9/9.
 */
public final class OKHttpUtil {
    public static Request.Builder getRequestBuilder(String url) {
        if (GoagalInfo.gameid != null) {
            url += "/p/" + GoagalInfo.gameid;
        } else {
            url += "/p/67";
        }
    	
        Logger.msg("客户端请求url->" + url);
        Request.Builder builder = new Request.Builder()
                .tag(url)
                .url(url);
        return builder;
    }

    public static OkHttpClient.Builder getHttpClientBuilder() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS);
        return builder;
    }

    public static Response setResponse(int code, String body) {
        Response response = new Response();
        response.code = code;
        response.body = body;
        return response;
    }

    public static FormBody.Builder setBuilder(Map<String, String> params) {
        setDefaultParams(params, false);
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key, value);
            }
        }
        return builder;
    }

    private static void setDefaultParams(Map<String, String> params, boolean encryptResponse) {
        if (params != null) {
            
			params.put("g", GoagalInfo.gameid);
			params.put("ts", Util.getOrderId());
			params.put("a", GoagalInfo.agentid);
			params.put("d", EmulatorCheckUtil.isEmulator() ? "4" : "2");//如果是模拟器则值为:4,如果为手机，则值为2
			params.put("i", GoagalInfo.imei);
			params.put("sv", Build.VERSION.RELEASE);
			params.put("version", FYGameSDK.defaultSDK().getVersion() != null ? FYGameSDK.defaultSDK().getVersion() : "");//增加参数-->sdk当前版本号
        }
    }

    public static MultipartBody.Builder setBuilder(UpFileInfo upFileInfo, Map<String, String> params) {
        setDefaultParams(params, false);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.addFormDataPart(key, value);
            }
        }
        builder.addFormDataPart(upFileInfo.name, upFileInfo.filename, RequestBody.create(MediaType.parse
                        ("multipart/form-data"),
                upFileInfo.file));
        return builder;
    }

    /**
     * Encode params data
     *
     * @param params          The params of http port
     * @param encryptResponse The response if encryptResponse by true, false.
     */
    public static byte[] encodeParams(Map params, boolean
            encryptResponse) {
        setDefaultParams(params, encryptResponse);
        JSONObject jsonObject = new JSONObject(params);
        String jsonStr = jsonObject.toString();
        Logger.msg("客户端请求数据->" + jsonStr);
        jsonStr = EncryptUtil.rsa(jsonStr);
        return EncryptUtil.compress(jsonStr);
    }

    /**
     * compress data
     *
     * @param params The params of http port
     */
    public static byte[] encodeParams(Map params) {
        setDefaultParams(params, true);
        JSONObject jsonObject = new JSONObject(params);
        String jsonStr = jsonObject.toString();
        Logger.msg("客户端请求数据->" + jsonStr);
        return EncryptUtil.compress(jsonStr);
    }


    ///< 解密返回值
    public static String decodeBody(InputStream in) {
        return Encrypt.decode(EncryptUtil.unzip(in));
    }

}
