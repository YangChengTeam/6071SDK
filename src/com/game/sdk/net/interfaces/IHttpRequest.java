package com.game.sdk.net.interfaces;

import java.io.IOException;
import java.util.Map;

import com.game.sdk.net.entry.Response;
import com.game.sdk.net.entry.UpFileInfo;
import com.game.sdk.net.exception.NullResonseListenerException;
import com.game.sdk.net.listeners.OnHttpResonseListener;

/**
 * Created by zhangkai on 16/8/18.
 */
public interface IHttpRequest {

	Response get(String url) throws IOException;

	void aget(String url, final OnHttpResonseListener httpResonseListener)
			throws IOException, NullResonseListenerException;

	Response post(String url, Map<String, String> params) throws IOException, NullResonseListenerException;

	Response post2(String url, Map<String, String> params, boolean encryptResponse)
			throws IOException, NullResonseListenerException;

	void apost(String url, Map<String, String> params, final OnHttpResonseListener httpResonseListener)
			throws IOException, NullResonseListenerException;

	void apost2(String url, Map<String, String> params, final OnHttpResonseListener httpResonseListener,
			boolean encryptResponse) throws IOException, NullResonseListenerException;

	Response uploadFile(String url, UpFileInfo upFileInfo, Map<String, String> params) throws IOException;

	void auploadFile(String url, UpFileInfo upFileInfo, Map<String, String> params,
			OnHttpResonseListener httpResonseListener) throws IOException, NullResonseListenerException;

	void cancel(String url);

}
