package com.game.sdk.net.impls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.game.sdk.net.constans.DescConstans;
import com.game.sdk.net.down.ImageDownLoader;
import com.game.sdk.net.entry.UpFileInfo;
import com.game.sdk.net.exception.NullResonseListenerException;
import com.game.sdk.net.interfaces.IHttpRequest;
import com.game.sdk.net.listeners.OnHttpResonseListener;
import com.game.sdk.net.utils.OKHttpUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.PathUtil;
import com.squareup.picasso.Picasso;

import android.content.Context;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhangkai on 16/8/18.
 */
public class OKHttpRequest implements IHttpRequest {
	public static OKHttpRequest httpRequest;
	private static final MediaType MEDIA_TYPE = MediaType.parse("text/html");
	public static OkHttpClient client = null;
	public final static int CONNECT_TIMEOUT = 30;
	public final static int READ_TIMEOUT = 60;
	public final static int WRITE_TIMEOUT = 60;
	public static Picasso picasso;
	
	public static boolean initializedPicasso = false;
	
	private OKHttpRequest() {
		initOkHttpClient();
	}
	
	public static void initOkHttpClient() {
		final Builder builder = OKHttpUtil.getHttpClientBuilder();
		builder.cache(new Cache(new File(PathUtil.getImageDir()), 1024 * 1024 * 100));
		client = builder.build();
	}

	public static OKHttpRequest getImpl() {
		if (httpRequest == null) {
			synchronized (OKHttpRequest.class) {
				httpRequest = new OKHttpRequest();
			}
		}
		return httpRequest;
	}

	public static void initPicasso(Context context) {
		if (client == null) {
			initOkHttpClient();
		}
		if(!initializedPicasso){
			Picasso picasso = new Picasso.Builder(context).downloader(new ImageDownLoader(client)).build();
			Picasso.setSingletonInstance(picasso);
			initializedPicasso = true;
		}
		
	}

	public com.game.sdk.net.entry.Response get(String url) throws IOException {
		Request request = OKHttpUtil.getRequestBuilder(url).build();
		return sendRequest(request, false);
	}

	@Override
	public com.game.sdk.net.entry.Response post(String url, Map<String, String> params) throws IOException {
		FormBody.Builder builder = OKHttpUtil.setBuilder(params);
		Request request = OKHttpUtil.getRequestBuilder(url).post(builder.build()).build();
		return sendRequest(request, false);
	}

	public com.game.sdk.net.entry.Response post(String url, byte[] data, boolean encryptResponse) throws IOException {
		RequestBody requestBody = RequestBody.create(MEDIA_TYPE, data);
		Request request = OKHttpUtil.getRequestBuilder(url).post(requestBody).build();
		return sendRequest(request, encryptResponse);
	}

	@Override
	public com.game.sdk.net.entry.Response post2(String url, Map<String, String> params, boolean encryptResponse)
			throws IOException, NullResonseListenerException {
		byte[] data = OKHttpUtil.encodeParams(params, encryptResponse);
		return post(url, data, encryptResponse);
	}

	@Override
	public void aget(String url, final OnHttpResonseListener httpResonseListener) throws IOException {
		if (httpResonseListener == null)
			throw new NullResonseListenerException();

		Request request = OKHttpUtil.getRequestBuilder(url).build();
		sendRequest(request, httpResonseListener, false);
	}

	@Override
	public void apost(String url, Map<String, String> params, final OnHttpResonseListener httpResonseListener)
			throws IOException {
		byte[] data = OKHttpUtil.encodeParams(params);
		apost2(url, data, httpResonseListener, true);
	}

	/// < 带加密的post请求
	@Override
	public void apost2(String url, Map<String, String> params, OnHttpResonseListener httpResonseListener,
			boolean encryptResponse) throws IOException, NullResonseListenerException {
		byte[] data = OKHttpUtil.encodeParams(params, encryptResponse);
		apost2(url, data, httpResonseListener, encryptResponse);
	}

	private void apost2(String url, byte[] params, OnHttpResonseListener httpResonseListener, boolean encryptResponse)
			throws IOException, NullResonseListenerException {
		if (httpResonseListener == null)
			throw new NullResonseListenerException();
		RequestBody requestBody = RequestBody.create(MEDIA_TYPE, params);
		Request request = OKHttpUtil.getRequestBuilder(url).post(requestBody).build();
		sendRequest(request, httpResonseListener, encryptResponse);
	}

	@Override
	public com.game.sdk.net.entry.Response uploadFile(String url, UpFileInfo upFileInfo, Map<String, String> params)
			throws IOException {
		if (upFileInfo == null || upFileInfo.file == null)
			throw new FileNotFoundException("file is null");

		MultipartBody requestBody = OKHttpUtil.setBuilder(upFileInfo, params).build();

		Request request = OKHttpUtil.getRequestBuilder(url).post(requestBody).build();

		return sendRequest(request, false);
	}

	@Override
	public void auploadFile(String url, UpFileInfo upFileInfo, Map<String, String> params,
			OnHttpResonseListener httpResonseListener) throws IOException, NullResonseListenerException {
		if (httpResonseListener == null)
			throw new NullResonseListenerException();

		if (upFileInfo == null || upFileInfo.file == null)
			throw new FileNotFoundException("file == null");

		MultipartBody requestBody = OKHttpUtil.setBuilder(upFileInfo, params).build();

		Request request = OKHttpUtil.getRequestBuilder(url).post(requestBody).build();

		sendRequest(request, httpResonseListener, false);
	}

	@Override
	public void cancel(String url) {
		if (client == null)
			throw new NullPointerException("client == null");

		for (Call call : client.dispatcher().queuedCalls()) {
			if (url.equals(call.request().tag()))
				call.cancel();
		}
		for (Call call : client.dispatcher().runningCalls()) {
			if (url.equals(call.request().tag()))
				call.cancel();
		}
	}

	private com.game.sdk.net.entry.Response sendRequest(Request request, boolean encryptResponse) throws IOException {
		com.game.sdk.net.entry.Response nresponse = null;
		Call call = client.newCall(request);
		Response response = call.execute();
		if (response.isSuccessful()) {
			String body = "";
			if (encryptResponse) {
				body = OKHttpUtil.decodeBody(response.body().byteStream());
			} else {
				body = response.body().string();
			}
			nresponse = OKHttpUtil.setResponse(response.code(), body);

			Logger.msg("服务器返回数据->" + body);

		}
		return nresponse;
	}

	private void sendRequest(Request request, final OnHttpResonseListener httpResonseListener,
			final boolean encryptResponse) {
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				com.game.sdk.net.entry.Response response = OKHttpUtil.setResponse(-1, DescConstans.NET_ERROR);
				httpResonseListener.onFailure(response);
				Logger.msg("网络请求失败->" + e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				com.game.sdk.net.entry.Response nresponse = null;
				String body = "";
				if (encryptResponse) {
					body = OKHttpUtil.decodeBody(response.body().byteStream());
				} else {
					body = response.body().string();
				}
				Logger.msg("服务器返回数据->" + body);
				nresponse = OKHttpUtil.setResponse(response.code(), body);
				httpResonseListener.onSuccess(nresponse);
			}
		});
	}

}
