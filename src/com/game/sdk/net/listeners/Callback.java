package com.game.sdk.net.listeners;

import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.entry.Response;

/**
 * Created by zhangkai on 16/9/20.
 */
public interface Callback<T> {
    public void onSuccess(ResultInfo<T> resultInfo);
    public void onFailure(Response response);
}
