package com.game.sdk.net.listeners;

import com.game.sdk.net.entry.Response;

/**
 * Created by zhangkai on 16/8/30.
 */
public interface OnHttpResonseListener {
    void onSuccess(Response response);
    void onFailure(Response response);
}
