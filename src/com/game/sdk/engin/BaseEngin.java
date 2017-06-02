package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.DescConstans;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.impls.OKHttpRequest;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.net.listeners.OnHttpResonseListener;
import com.game.sdk.utils.Logger;

import android.content.Context;


/**
 * Created by zhangkai on 16/9/20.
 */
public abstract class BaseEngin<T> {
    protected Context context;

    public BaseEngin(){}
    public BaseEngin(Context context){
        this.context = context;
    }

    public ResultInfo<T> getResultInfo(boolean encodeResponse, Class<T> type, Map<String, String> params) {
        if(params == null) {
            params = new HashMap<String, String>();
        }
        ResultInfo<T> resultInfo = null;
        try {
            Response response = OKHttpRequest.getImpl().post2(getUrl(), params, encodeResponse);
            resultInfo = JSON.parseObject(response.body, new TypeReference<ResultInfo<T>>(type){});
        }catch (Exception e){
            resultInfo = new ResultInfo<T>();
            resultInfo.code = -1000;
            Logger.msg("getResultInfo异常:" + e.getMessage());
        }
        return resultInfo;
    }

    public void agetResultInfo(final Class<T> type,Map<String, String> params, final
    Callback<T> callback){
        if(params == null) {
            params = new HashMap<String, String>();
        }
        try {
            OKHttpRequest.getImpl().apost(getUrl(), params, new OnHttpResonseListener() {
                @Override
                public void onSuccess(Response response) {
                    ResultInfo<T> resultInfo = null;
                    try {
                        resultInfo = JSON.parseObject(response.body, new TypeReference<ResultInfo<T>>(type) {
                        });
                    }catch (Exception e){
                        response.body = DescConstans.SERVICE_ERROR;
                        callback.onFailure(response);
                        e.printStackTrace();
                        Logger.msg("agetResultInfo异常->JSON解析错误（服务器返回数据格式不正确）");
                    }
                    if(callback != null){
                        callback.onSuccess(resultInfo);
                    }
                }

                @Override
                public void onFailure(Response response) {
                    if(callback != null){
                        callback.onFailure(response);
                    }
                }
            });
        }catch (Exception e){
            Logger.msg("agetResultInfo异常->" + e.getMessage());
        }
    }

    public void agetResultInfo(boolean encodeResponse,final Class<T> type,Map<String, String> params, final
    Callback<T> callback){
        if(params == null) {
            params = new HashMap<String, String>();
        }
        try {
            OKHttpRequest.getImpl().apost2(getUrl(), params, new OnHttpResonseListener() {
                @Override
                public void onSuccess(Response response) {
                    ResultInfo<T> resultInfo = null;
                    try {
                        resultInfo = JSON.parseObject(response.body, new TypeReference<ResultInfo<T>>(type) {
                        });
                    }catch (Exception e){
                        response.body = DescConstans.SERVICE_ERROR;
                        callback.onFailure(response);
                        e.printStackTrace();
                        Logger.msg("agetResultInfo异常->JSON解析错误（服务器返回数据格式不正确）");
                    }
                    if(callback != null){
                        callback.onSuccess(resultInfo);
                    }
                }

                @Override
                public void onFailure(Response response) {
                    if(callback != null){
                        callback.onFailure(response);
                    }
                }
            }, encodeResponse);
        }catch (Exception e){
            Logger.msg("agetResultInfo异常->" + e.getMessage());
        }
    }

    public abstract String getUrl();
}
