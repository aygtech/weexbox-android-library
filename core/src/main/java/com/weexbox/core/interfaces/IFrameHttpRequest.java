package com.weexbox.core.interfaces;


import android.content.Context;

import com.weexbox.core.net.HttpParams;
import com.weexbox.core.net.callback.HttpCallback;

import java.util.Map;

public interface IFrameHttpRequest {

    void sendGetRequest(final String url, final Map<String,String> header, final HttpParams params, final Object tag, final boolean
            showLoadingView, final HttpCallback callback);

    void sendGetRequest(final String url, final Map<String,String> header, final HttpParams params, final boolean showLoadingView, final HttpCallback callback);

    void sendGetRequestWithLoadingDialog(final String url, final Map<String,String> header, final HttpParams params, final String loadingText, final boolean
            canCancel, final HttpCallback callback);

    void sendPostRequest(final String url, final Map<String,String> header, final HttpParams params, final Object tag, final boolean
            showLoadingView, final HttpCallback callback);

    void sendPostRequest(final String url, final Map<String,String> header, final HttpParams params, final boolean showLoadingView, final HttpCallback callback);

    void sendPostRequestWithLoadingDialog(final String url, final Map<String,String> header, final HttpParams params, final String
            loadingText, final boolean canCancel, final HttpCallback callback);

    void sendPostJsonRequest(final String url, final Map<String,String> header, final HttpParams params, final Object tag, final boolean
            showLoadingView, final HttpCallback callback);

    void sendPostJsonRequest(final String url, final Map<String,String> header, final HttpParams params, final boolean showLoadingView, final HttpCallback callback);

    void sendPostJsonRequestWithLoadingDialog(final String url, final Map<String,String> header, final HttpParams params, final String
            loadingText, final boolean canCancel, final HttpCallback callback);

    boolean isRequesting(final String url);

    /**
     * 释放资源
     *
     * @param context
     */
    void releaseResources(Context context);
}
