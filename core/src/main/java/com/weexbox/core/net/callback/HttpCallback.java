package com.weexbox.core.net.callback;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.weexbox.core.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by freeson on 16/7/28.
 */
public abstract class HttpCallback<T> extends Callback<T> {

    protected static final int NET_ERROR = -22;
    protected static final int JSON_EXCEPTION = -33;
    protected static final int CANCEL = -44;

    protected static final int RIGHT_CODE = 200;
    private static String keyCode = "code";
    private static String keyData = "data";
    private static String keyMessage = "message";
    private static int rightCode = RIGHT_CODE;

    private int errorCode = RIGHT_CODE;
    private String msg = null;

    private IFinishListener mListener;
    private String mRequestUrl;

    public static void config(final int rightCode, final String keyCode, final String keyData, final String keyMessage) {
        HttpCallback.rightCode = rightCode;
        HttpCallback.keyCode = keyCode;
        HttpCallback.keyData = keyData;
        HttpCallback.keyMessage = keyMessage;
    }

    public void setOnFinishListener(final IFinishListener listener, final String url) {
        this.mListener = listener;
        this.mRequestUrl = url;
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public void onBefore(Request request, int requestId) {
        super.onBefore(request, requestId);
    }

    @Override
    public final void onAfter(int requestId) {
        super.onAfter(requestId);
        onFinish(this.errorCode == RIGHT_CODE ? IFinishListener.SUCCESS : IFinishListener.FAIL);
    }

    @Override
    public final void onResponse(T entity, int requestId) {
        this.errorCode = RIGHT_CODE;
        this.onSuccess(entity, requestId);
    }

    @Override
    public final void onError(Call call, final Exception e, int requestId) {
        if (call.isCanceled()) {
            onFail(requestId, CANCEL, e.getMessage());
        } else {
            if (this.errorCode == RIGHT_CODE) {
                this.errorCode = NET_ERROR;
            }
            onFail(requestId, this.errorCode, e.getMessage());
        }
    }

    @Override
    public boolean validateReponse(Response response, int id) {
        boolean success = response.isSuccessful();
        this.errorCode = success ? RIGHT_CODE : response.code();
        return success;
    }

    @Override
    public T parseNetworkResponse(Response response, int requestId) throws Exception {
        T t = null;
        if (useInputStream()) {
            t = decodeInputStream(response.body().byteStream(), response.body().contentLength(), requestId);
        } else {
            final String string = response.body().string();
            if (!TextUtils.isEmpty(string)) {
                JSONObject jsonObject = new JSONObject(string);
                if (jsonObject == null) {
                    errorCode = JSON_EXCEPTION;
                    throw new JSONException("服务器异常");
                }
                final int code = jsonObject.optInt(keyCode);
                this.msg = jsonObject.optString(keyMessage);
                if (code != rightCode) {
                    this.errorCode = code;
                    throw new Exception(this.msg);
                }
                final String data = jsonObject.optString(keyData);
                t = parseEntity(data, requestId);
            }
        }

        return t;
    }

    protected boolean useInputStream() {
        return false;
    }

    protected T parseEntity(String data, int requestId) throws Exception {
        return null;
    }

    protected T decodeInputStream(InputStream inputStream, long length, int requestId) throws IOException {
        return null;
    }

    public abstract void onSuccess(T entity, int requestId);

    public abstract void onFail(int requestId, int errorCode, String errorMessage);

    /***
     * @param status the http request status, the value maybe
     *               {@link IFinishListener#FAIL}(失败)、{@link IFinishListener#SUCCESS}(成功)、{@link IFinishListener#REQUESTING}(正在请求);
     */
    @CallSuper
    public void onFinish(@HttpCallback.IFinishListener.Status int status) {
        if (mListener != null) {
            mListener.onHttpRequestFinish(mRequestUrl, status);
        }
    }

    public interface IFinishListener {
        /**
         * 失败
         **/
        int FAIL = 0;
        /**
         * 成功
         **/
        int SUCCESS = 1;
        /**
         * 正在请求
         **/
        int REQUESTING = 2;

        /***
         * @param url    the url you send request
         * @param status the status of http request, the value maybe is
         *               {@link IFinishListener#FAIL}(失败)、{@link IFinishListener#SUCCESS}(成功)、{@link IFinishListener#REQUESTING}(正在请求);
         */
        void onHttpRequestFinish(final String url, @HttpCallback.IFinishListener.Status int status);

        @IntDef({FAIL, SUCCESS, REQUESTING})
        @Retention(RetentionPolicy.SOURCE)
        @interface Status {
        }
    }
}
