package com.weexbox.core.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by freeson on 16/8/4.
 */
public final class ToastUtil {

    public static void showShortToast(final Context context, final String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(final Context context, final int textId) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(final Context context, final String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(final Context context, final int textId) {
        Toast.makeText(context, textId, Toast.LENGTH_LONG).show();
    }
}
