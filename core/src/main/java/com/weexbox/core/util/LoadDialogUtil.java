package com.weexbox.core.util;

import android.content.Context;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Author:leon.wen
 * Time:2018/9/10   14:45
 * Description:This is LoadDialogUtil
 */
public class LoadDialogUtil {

    private static KProgressHUD hud;
    private static KProgressHUD progressHUD;
    public static int width = 68;
    public static int height = 68;
    public static boolean isCancel = true;

    public static void close() {
        if (hud != null) {
            hud.dismiss();
        }
    }

    public static void showLoad(Context context) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setSize(width,height)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    public static void showLoadWithText(Context context, String text) {
        KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setSize(width,height)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    public static void showLoadWithTextAndDetail(Context context, String text, String detail) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setSize(width,height)
                .setDetailsLabel(detail)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    public static void showProgress(Context context) {
        progressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setSize(width,height)
                .setMaxProgress(100)
                .show();
    }

    public static void showProgressWithText(Context context, String text) {
        progressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setSize(width,height)
                .setLabel(text)
                .setMaxProgress(100)
                .show();
    }

    public static void setProgressHUD(int progress){
        if(progressHUD == null){
            return;
        }
        progressHUD.setProgress(progress);
    }
}
