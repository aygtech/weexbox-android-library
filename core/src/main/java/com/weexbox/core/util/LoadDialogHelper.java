package com.weexbox.core.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.weexbox.core.WeexBoxEngine;

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
        setCustomView(context);
        hud.show();
    }

    public void showLoadWithText(Context context, String text) {
        close();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setCancellable(isCancel)
                .setAnimationSpeed(2);
        setCustomView(context);
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

    private void setCustomView(Context context) {
        if (!TextUtils.isEmpty(WeexBoxEngine.INSTANCE.getLoadingIconRes())) {
            LottieAnimationView lottieAnimationView = new LottieAnimationView(context);
            lottieAnimationView.setAnimation(WeexBoxEngine.INSTANCE.getLoadingIconRes());
            lottieAnimationView.loop(true);
            lottieAnimationView.playAnimation();
            int width = DisplayUtil.dip2px(context, 36f);
            int height = DisplayUtil.dip2px(context, 26f);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);
            lottieAnimationView.setLayoutParams(layoutParams);
            layoutParams.setMargins(0,10,0,0);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.addView(lottieAnimationView);
            hud.setCustomView(linearLayout);
        }
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
