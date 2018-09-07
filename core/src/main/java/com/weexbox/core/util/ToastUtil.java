package com.weexbox.core.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by freeson on 16/8/4.
 */
public final class ToastUtil {

    private static Toast sToast;

    private ToastUtil() {

    }

    private static void showToast(final Context context, final String text, final int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, text, duration);
        } else {
            sToast.setText(text);
        }
        sToast.show();
    }

    private static void showToast(Context context, int textId, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, textId, duration);
        } else {
            sToast.setText(textId);
        }
        sToast.show();
    }

    public static void showShortToast(final Context context, final String text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(final Context context, final int textId) {
        showToast(context, textId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(final Context context, final String text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    public static void showLongToast(final Context context, final int textId) {
        showToast(context, textId, Toast.LENGTH_LONG);
    }

    public static void cancelToast() {
        if (sToast != null) {
            sToast.cancel();
        }
    }

    public static void releaseResources() {
        sToast = null;
    }
}
