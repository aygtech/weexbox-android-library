package com.weexbox.core.net;


import android.content.Context;
import android.text.TextUtils;

import com.weexbox.core.R;
import com.weexbox.core.net.callback.HttpCallback;
import com.weexbox.core.net.callback.HttpFileCallback;
import com.weexbox.core.okhttp.OkHttpUtils;
import com.weexbox.core.okhttp.builder.GetBuilder;
import com.weexbox.core.okhttp.builder.OkHttpRequestBuilder;
import com.weexbox.core.okhttp.builder.PostFormBuilder;
import com.weexbox.core.okhttp.builder.PostStringBuilder;
import com.weexbox.core.okhttp.cookie.CookieJarImpl;
import com.weexbox.core.okhttp.cookie.store.PersistentCookieStore;
import com.weexbox.core.okhttp.https.HttpsUtils;
import com.weexbox.core.util.AES128Util;
import com.weexbox.core.util.AndroidUtil;
import com.weexbox.core.util.LogUtil;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by freeson on 16/7/29.
 * <p>
 * https://github.com/hongyangAndroid/okhttputils
 */
public final class HttpUtil {

    private static RequestInterceptor requestInterceptor;
    public static String key = "";

    private HttpUtil() {
    }

    public static void setRequestInterceptor(RequestInterceptor interceptor) {
        requestInterceptor = interceptor;
    }

    /**
     * 初始化OkHttpClient, 默认不带上cookies管理
     */
    public static void init(HttpUtil.RequestInterceptor interceptor,String keyStr) {
        requestInterceptor = interceptor;
        key = keyStr;
        OkHttpClient.Builder builder = build();
        initHttp(builder);
    }

    /**
     * 初始化OkHttpClient, 带上cookies管理
     */
    public static void init(HttpUtil.RequestInterceptor interceptor,Context context) {
        requestInterceptor = interceptor;
        OkHttpClient.Builder builder = BuildWithCookie(context);
        initHttp(builder);
    }

    /**
     * 初始化OkHttpClient, 默认不带上cookies管理
     */
    public static void init(final InputStream[] certificates, final InputStream bksFile, final String password) {
        OkHttpClient.Builder builder = build();
        initSSL(builder, certificates, bksFile, password);
        initHttp(builder);
    }

    public static void init(final InputStream[] certificates, final InputStream bksFile, final String password,Context context) {
        OkHttpClient.Builder builder = BuildWithCookie(context);
        initSSL(builder, certificates, bksFile, password);
        initHttp(builder);
    }

    private static OkHttpClient.Builder build() {
//        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(App.getFrameApplicationContext()));
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        if (requestInterceptor != null) {
                            Request request = chain.request();
                            Request.Builder newBuilder = request.newBuilder();
                            requestInterceptor.intercept(newBuilder);
                            return chain.proceed(newBuilder.build());
                        } else {
                            return chain.proceed(chain.request());
                        }
                    }
                });
        return builder;
    }

    private static OkHttpClient.Builder BuildWithCookie(Context context) {
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context));
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        if (requestInterceptor != null) {
                            Request request = chain.request();
                            Request.Builder newBuilder = request.newBuilder();
                            requestInterceptor.intercept(newBuilder);
                            return chain.proceed(newBuilder.build());
                        } else {
                            return chain.proceed(chain.request());
                        }
                    }
                })//其他配置
                .cookieJar(cookieJar);
        return builder;
    }


    private static void initHttp(OkHttpClient.Builder builder) {
        OkHttpUtils.initClient(builder.build());
    }

    private static void initSSL(OkHttpClient.Builder builder, final InputStream[] certificates, final InputStream bksFile, final
    String password) {
        //具体证书
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(certificates, bksFile, password);
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(证书的inputstream, null, null);
//      HttpsUtils.SSLParams sslParams HttpsUtils.getSslSocketFactory(证书的inputstream, 本地证书的inputstream, 本地证书的密码);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

    }

    public static void sendGetRequest(final String url, final Map<String,String> header, final HttpParams params, final Object tag, final HttpCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        final GetBuilder getBuilder = OkHttpUtils.get().url(url);
        if (params != null) {
            getBuilder.params(params.getUrlParams());
        } else {
            getBuilder.params(new HttpParams().getUrlParams());
        }
        if (header != null) {
            getBuilder.headers(params.getUrlParams());
        }
        requestAction(getBuilder, tag, callback);
    }

    public static void sendPostRequest(final String url, final Map<String,String> header,  final HttpParams params, final Object tag, final HttpCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        final PostFormBuilder formBuilder = OkHttpUtils.post().url(url);
        if (params != null) {
            formBuilder.params(params.getUrlParams());
        } else {
            formBuilder.params(new HttpParams().getUrlParams());
        }
        if (header != null) {
            formBuilder.headers(params.getUrlParams());
        }
        requestAction(formBuilder, tag, callback);
    }

    public static void sendPostJsonRequest(final String url, final Map<String,String> header,  final HttpParams params, final Object tag, final HttpCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        PostStringBuilder stringBuilder = OkHttpUtils.postString().url(url);
        if (params != null) {
            stringBuilder.content(params.convertToJson());
        } else {
            stringBuilder.content(new HttpParams().convertToJson());
        }
        if (header != null) {
            stringBuilder.headers(params.getUrlParams());
        }
        stringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        requestAction(stringBuilder, tag, callback);
    }

    public static void sendPostStringRequest(final String url, final Map<String,String> header,  final HttpParams params, final Object tag, final HttpCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        PostStringBuilder stringBuilder = OkHttpUtils.postString().url(url);
        if (params != null) {
            stringBuilder.content(AES128Util.encrypt(key,params.convertToJson()));
        } else {
            stringBuilder.content(AES128Util.encrypt(key,new HttpParams().convertToJson()));
        }
        if (header != null) {
            stringBuilder.headers(params.getUrlParams());
        }
        requestAction(stringBuilder, tag, callback);
    }

    public static void requestAction(OkHttpRequestBuilder builder, final Object tag, final HttpCallback callback) {
        final HttpCallback tempCallback = callback;
        if (callback != null) {
            if (tag != null) {
                builder.tag(tag).build().execute(tempCallback);
            } else {
                builder.build().execute(tempCallback);
            }
        } else {
            try {
                if (tag != null) {
                    builder.tag(tag).build().execute();
                } else {
                    builder.build().execute();
                }
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
    }

    /**
     * 表单形式上传文件
     *
     * @param url
     * @param params
     * @param callback
     */
    public static void sendPostFileRequest(final String url, final HttpParams params, final HttpCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (params == null) {
            return;
        }
        final LinkedHashMap<String, File> fileParams = params.getFileParams();
        if (fileParams == null) {
            return;
        }
        final PostFormBuilder formBuilder = OkHttpUtils.post().url(url);
        for (String key : fileParams.keySet()) {
            formBuilder.addFile("mFile", key, fileParams.get(key));
        }
        formBuilder.params(params.getUrlParams());
        formBuilder.build().execute(callback);
    }

    public static void sendDownloadFileRequest(final String url, final HttpFileCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.get().url(url).build().execute(callback);
    }

    public static void cancel(Object object) {
        OkHttpUtils.getInstance().cancelTag(object);
    }

    /*****************************
     * cookies管理
     ***************************************/

    public static void clearCookies() {
        CookieJarImpl cookieJar = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        cookieJar.getCookieStore().removeAll();
    }

    public static List<Cookie> getCookies() {
        CookieJarImpl cookieJar = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        return cookieJar.getCookieStore().getCookies();
    }

    /**
     * 设置同步cookies到httpclient
     *
     * @param cookies
     * @param domain
     * @param expireDate
     */
    public static void setCookiesToHttp(String cookies, String domain, Date expireDate) {

        if (TextUtils.isEmpty(cookies)) {
            return;
        }
        clearCookies();
        String[] array = cookies.split(";");
        List<Cookie> list = new ArrayList<>();
        for (String nameValueStr : array) {
            String[] nameValue = nameValueStr.split("=");
            Cookie.Builder builder = new Cookie.Builder()
                    .name(nameValue[0])
                    .value(nameValue[1])
                    .domain(domain)
                    .expiresAt(expireDate.getTime());
            Cookie cookie = builder.build();
            list.add(cookie);
        }
        HttpUrl url = new HttpUrl.Builder().scheme("http").host(domain).build();
        CookieJarImpl cookieJar = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        PersistentCookieStore cookieStore = (PersistentCookieStore) cookieJar.getCookieStore();
        cookieStore.add(url, list);
    }

    public interface RequestInterceptor {
        void intercept(Request.Builder builder);
    }
}
