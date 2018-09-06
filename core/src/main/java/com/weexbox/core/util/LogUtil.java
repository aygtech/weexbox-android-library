package com.weexbox.core.util;

import android.content.Context;
import com.orhanobut.logger.Logger;

public final class LogUtil {

    private LogUtil() {
        /* Protect from instantiations */
    }

    public static void init(Context context) {
    }

    public static boolean isDebuggable() {
        return true;
    }

    public static void e(String message, Object... params) {
        if (!isDebuggable()) {
            return;
        }
        Logger.e(message, params);
    }

    public static void i(String message, Object... params) {
        if (!isDebuggable()) {
            return;
        }
        Logger.i(message, params);

    }

    public static void d(String message, Object... params) {
        if (!isDebuggable()) {
            return;
        }
        Logger.d(message, params);

    }

    public static void v(String message, Object... params) {
        if (!isDebuggable()) {
            return;
        }
        Logger.v(message, params);

    }

    public static void w(String message, Object... params) {
        if (!isDebuggable()) {
            return;
        }
        Logger.w(message, params);

    }

    public static void wtf(String message, Object... params) {
        if (!isDebuggable()) {
            return;
        }
        Logger.wtf(message, params);

    }

    public static void json(String json) {
        if (!isDebuggable()) {
            return;
        }
        Logger.json(json);

    }

    public static void xml(String xml) {
        if (!isDebuggable()) {
            return;
        }
        Logger.xml(xml);
    }

}
