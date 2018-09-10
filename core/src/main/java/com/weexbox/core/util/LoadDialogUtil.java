package com.weexbox.core.util;

import android.content.Context;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Author:leon.wen
 * Time:2018/9/10   14:45
 * Description:This is LoadDialogUtil
 */
public class LoadDialogUtil {

    private KProgressHUD hud;
    private KProgressHUD progressHUD;

    public void close() {
        if (hud != null) {
            hud.dismiss();
        }
    }

    public void showLoad(Context context) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    public void showLoadWithText(Context context, String text) {
        KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    public void showLoadWithTextAndDetail(Context context, String text, String detail) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setDetailsLabel(detail)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    public void showProgress(Context context) {
        progressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setMaxProgress(100)
                .show();
    }

    public void showProgressWithText(Context context, String text) {
        progressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel(text)
                .setMaxProgress(100)
                .show();
    }

    public void setProgressHUD(int progress){
        if(progressHUD == null){
            return;
        }
        progressHUD.setProgress(progress);
    }
}
