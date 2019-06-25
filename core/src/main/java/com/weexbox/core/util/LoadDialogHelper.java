package com.weexbox.core.util;

import android.content.Context;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;

public class LoadDialogHelper {

    public int width = 68;
    public int height = 68;
    public boolean isCancel = true;
    private KProgressHUD hud;
    private KProgressHUD progressHUD;
    private Context mConetxt;

    public LoadDialogHelper(Context mConetxt) {
        this.mConetxt = mConetxt;
    }

    public void clear() {
        if (hud != null) {
            hud.dismiss();
            hud = null;
        }
        if (progressHUD != null) {
            progressHUD.dismiss();
            progressHUD = null;
        }
    }

    public void close() {
        if (hud != null) {
            hud.dismiss();
        }
    }

    public void showLoad(Context context, boolean setTransparent) {
        close();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setSize(width, height)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setDimAmount(setTransparent ? 0 : 0.5f);
        hud.show();
    }

    public void showLoadWithText(Context context, String text) {
        close();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setCancellable(isCancel)
                .setAnimationSpeed(2);
        hud.show();
    }

    public void showLoadWithText(Context context, String text, View view) {
        close();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setCustomView(view);
        hud.show();
    }

    public void showLoadWithText(Context context, String text, boolean setTransparent) {
        close();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setDimAmount(setTransparent ? 0 : 0.5f);
        hud.show();
    }

    public void showLoadWithTextAndDetail(Context context, String text, String detail) {
        close();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setDetailsLabel(detail)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    public void showLoadWithTextAndDetail(Context context, String text, String detail, boolean setTransparent) {
        close();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setDetailsLabel(detail)
                .setCancellable(isCancel)
                .setAnimationSpeed(2)
                .setDimAmount(setTransparent ? 0 : 0.5f);
        hud.show();
    }

    public void showProgress(Context context) {
        progressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setSize(width, height)
                .setMaxProgress(100)
                .show();
    }

    public void showProgressWithText(Context context, String text, int progress) {
        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                    .setLabel(text)
                    .setMaxProgress(100)
                    .show();
        } else if (!progressHUD.isShowing()) {
            progressHUD = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                    .setLabel(text)
                    .setMaxProgress(100)
                    .show();
        } else {
            progressHUD.setProgress(progress);
        }
    }
}
