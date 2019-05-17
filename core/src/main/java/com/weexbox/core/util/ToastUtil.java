package com.weexbox.core.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by freeson on 16/8/4.
 */
public final class ToastUtil {

    public static void showShortToast(final Context context, final String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showShortToast(final Context context, final int textId) {
        Toast toast = Toast.makeText(context, textId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showLongToast(final Context context, final String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showLongToast(final Context context, final int textId) {
        Toast toast = Toast.makeText(context, textId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
