package com.weexbox.core.net;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.util.Date;
import java.util.List;

import okhttp3.Cookie;

/**
 * Created by freeson on 16/8/4.
 */
public final class CookiesUtil {

    private CookiesUtil() {
    }

    /**
     * 获取cookies
     *
     * @return
     */
    public static List<Cookie> getCookies() {
        return HttpUtil.getCookies();
    }

    /**
     * 设置cookies到WebView
     *
     * @param context
     * @param url
     */
    public static void syncCookiesToWebView(Context context, String url) {
        setCookiesToWebView(context, url, getCookies());
    }

    /**
     * 设置cookies到WebView
     *
     * @param context
     * @param url
     * @param cookies
     */
    public static void setCookiesToWebView(Context context, String url, List<Cookie> cookies) {
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            for (Cookie cookie : cookies) {
                String cookieStr = cookie.name() + "=" + cookie.value() + "; expires=" + cookie.expiresAt()
                        + "; path=" + cookie.path() + "; domain=" + cookie.domain();
                cookieManager.setCookie(url, cookieStr);
            }
            CookieSyncManager.getInstance().sync();
        }
    }

    /**
     * 设置cookies到WebView
     *
     * @param context
     * @param url
     */
    public static void setCookiesToWebView(Context context, String url) {
        List<Cookie> cookies = getCookies();
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            for (Cookie cookie : cookies) {
                String cookieStr = cookie.name() + "=" + cookie.value() + "; expires=" + cookie.expiresAt()
                        + "; path=" + cookie.path() + "; domain=" + cookie.domain();
                cookieManager.setCookie(url, cookieStr);
            }
            CookieSyncManager.getInstance().sync();
        }
    }

    /**
     * 设置cookies到WebView
     *
     * @param context
     * @param url
     * @param cookie
     */
    public static void setCookieToWebview(Context context, String url, String cookie) {
        CookieSyncManager.createInstance(context);
        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);
        mCookieManager.setCookie(url, cookie);
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 设置同步cookies到httpclient
     *
     * @param cookies
     * @param domain
     * @param expireDate
     */
    public static void setCookiesToHttp(String cookies, String domain, Date expireDate) {
        HttpUtil.setCookiesToHttp(cookies, domain, expireDate);
    }

    /**
     * 清除WebView的cookies
     *
     * @param context
     */
    public static void clearWebViewCookies(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    /**
     * 清除http的cookies
     */
    public static void clearHttpCookies() {
        HttpUtil.clearCookies();
    }

    /**
     * 清除http和webView的cookie
     *
     * @param context
     */
    public static void clearCookies(Context context) {
        clearHttpCookies();
        clearWebViewCookies(context);
    }

}
