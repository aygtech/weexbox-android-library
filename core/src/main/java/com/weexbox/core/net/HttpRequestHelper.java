package com.weexbox.core.net;

import android.content.Context;

import com.weexbox.core.interfaces.IFrameHttpRequest;
import com.weexbox.core.net.callback.HttpCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by freeson on 16/9/9.
 */
public final class HttpRequestHelper implements IFrameHttpRequest, HttpCallback.IFinishListener {

    private HashMap<String, Boolean> mIsRequesting = new HashMap<>();
    private HttpCallback.IFinishListener mFinishListener;
    private Set<Object> mTagSet = null;

    public HttpRequestHelper() {
        this(null);
    }

    public HttpRequestHelper(HttpCallback.IFinishListener listener) {
        this.mFinishListener = listener;
    }

    private void init() {
        if (mTagSet == null) {
            mTagSet = new HashSet<>(5);
        }
    }

    private void addTag(Object tag) {
        init();
        mTagSet.add(tag);
    }

    public void sendGetRequest(String url, final Map<String,String> header, HttpParams params, HttpCallback callback) {
        this.sendGetRequest(url, header, params, this, false, callback);
    }

    @Override
    public void sendGetRequest(String url, final Map<String,String> header, HttpParams params, final boolean isEncrypt, HttpCallback callback) {
        this.sendGetRequest(url, header, params, this, isEncrypt, callback);
    }

    @Override
    public void sendGetRequest(String url, final Map<String,String> header, HttpParams params, Object tag, final boolean isEncrypt, HttpCallback callback) {
        this.sendRequest(url,header, params, tag, callback, true, isEncrypt, false, false, null, false);
    }

    @Override
    public void sendGetRequestWithLoadingDialog(String url,final Map<String,String> header, HttpParams params, final String loadingText,
                                                final boolean canCancel, HttpCallback callback) {
        this.sendRequest(url, header, params, this, callback, true, false, true, canCancel, loadingText, false);
    }

    public void sendPostRequest(String url, final Map<String,String> header, HttpParams params, HttpCallback callback) {
        this.sendPostRequest(url, header,  params, this, false, callback);
    }

    @Override
    public void sendPostRequest(String url, final Map<String,String> header, HttpParams params, final boolean isEncrypt, HttpCallback callback) {
        this.sendPostRequest(url, header,  params, this, isEncrypt, callback);
    }

    public void sendPostJsonRequest(String url, final Map<String,String> header, HttpParams params,HttpCallback callback) {
        this.sendPostJsonRequest(url, header,  params, this, false, callback);
    }

    @Override
    public void sendPostJsonRequest(String url, final Map<String,String> header, HttpParams params, final boolean isEncrypt, HttpCallback callback) {
        this.sendPostJsonRequest(url, header,  params, this, isEncrypt, callback);
    }

    @Override
    public void sendPostRequest(String url,final Map<String,String> header, HttpParams params, Object tag, boolean isEncrypt, HttpCallback callback) {
        this.sendRequest(url, header,  params, tag, callback, false, isEncrypt, false, false, null, false);
    }

    @Override
    public void sendPostJsonRequest(String url,final Map<String,String> header, HttpParams params, Object tag, boolean isEncrypt, HttpCallback callback) {
        this.sendRequest(url, header,  params, tag, callback, false, isEncrypt, false, false, null, true);
    }

    @Override
    public void sendPostRequestWithLoadingDialog(String url,final Map<String,String> header, HttpParams params, final String loadingText,
                                                 final boolean canCancel, HttpCallback callback) {
        this.sendRequest(url, header,  params, this, callback, false, false, true, canCancel, loadingText, false);
    }

    @Override
    public void sendPostJsonRequestWithLoadingDialog(String url,final Map<String,String> header, HttpParams params, final String loadingText,
                                                     final boolean canCancel, HttpCallback callback) {
        this.sendRequest(url, header,  params, this, callback, false, false, true, canCancel, loadingText, true);
    }

    private void sendRequest(final String url, final Map<String,String> header, final HttpParams params, final Object tag, final HttpCallback callback,
                             final boolean useGet, final boolean isEncrypt, final boolean showLoadingDialog, final boolean
                                     canCancel, final String loadingText, final boolean useJsonRequest) {
        if (isRequesting(url)) {
            if (this.mFinishListener != null) {
                this.mFinishListener.onHttpRequestFinish(url, HttpCallback.IFinishListener.REQUESTING);
            }
            return;
        }
        this.mIsRequesting.put(url, true);
        callback.setOnFinishListener(this, url);
        if (useGet) {
            HttpUtil.sendGetRequest(url,header, params, tag, callback);
        } else {
            if (useJsonRequest) {
                if(isEncrypt){
                    HttpUtil.sendPostStringRequest(url,header, params, tag, callback);
                }else {
                    HttpUtil.sendPostJsonRequest(url,header, params, tag, callback);
                }
            } else {
                HttpUtil.sendPostRequest(url,header, params, tag, callback);
            }
        }
        addTag(tag);
    }

    public void uploadFiles(final String url,final HttpParams params, final HttpCallback callback) {
        if (isRequesting(url)) {
            if (this.mFinishListener != null) {
                this.mFinishListener.onHttpRequestFinish(url, HttpCallback.IFinishListener.REQUESTING);
            }
            return;
        }
        this.mIsRequesting.put(url, true);
        callback.setOnFinishListener(this, url);
        HttpUtil.sendPostFileRequest(url,params,callback);
        addTag(this);
    }


    @Override
    public void onHttpRequestFinish(String url, @HttpCallback.IFinishListener.Status int status) {
        this.mIsRequesting.put(url, false);
        if (this.mFinishListener != null) {
            this.mFinishListener.onHttpRequestFinish(url, status);
        }
    }

    @Override
    public boolean isRequesting(final String url) {
        return this.mIsRequesting.get(url) != null && this.mIsRequesting.get(url);
    }

    @Override
    public void releaseResources(Context context) {
        if (this.mTagSet != null && this.mTagSet.isEmpty()) {
            for (Object tag : this.mTagSet) {
                HttpUtil.cancel(tag);
            }
            mTagSet.clear();
        }
        this.mIsRequesting.clear();
        this.mFinishListener = null;
    }
}
