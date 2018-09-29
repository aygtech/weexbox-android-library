package com.weexbox.core.adapter;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;
import com.weexbox.core.util.BitmapUtil;

import java.io.File;

public class ImageAdapter implements IWXImgLoaderAdapter {

    public ImageAdapter() {
    }

    @Override
    public void setImage(final String url, final ImageView view,
                         WXImageQuality quality, final WXImageStrategy strategy) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (view == null || view.getLayoutParams() == null) {
                    return;
                }
                if (TextUtils.isEmpty(url)) {
                    view.setImageBitmap(null);
                    return;
                }
                if (view.getLayoutParams().width <= 0 || view.getLayoutParams().height <= 0) {
                    return;
                }
                if (!TextUtils.isEmpty(strategy.placeHolder)) {
                    BitmapUtil.displayImage(view, strategy.placeHolder);
                }
                
                if (url.startsWith("http")) {
                    // 网络加载
                    if (strategy.getImageListener() == null) {
                        BitmapUtil.displayImage(view, url, null);
                    } else {
                        BitmapUtil.displayImage(getTarget(url, view, strategy), url, null);
                    }
                } else if (url.startsWith("bundle://")) {
                    // 本地bundle加载
                    String path = url.substring(9);
                    int id = BitmapUtil.sContext.getResources().getIdentifier(path, "drawable", BitmapUtil.sContext.getPackageName());
                    if (strategy.getImageListener() == null) {
                        BitmapUtil.displayImage(view, id , null);
                    } else {
                        BitmapUtil.displayImage(getTarget(url, view, strategy), id, null);
                    }
                } else {
                    // 本地加载
                    File file = new File(url);
                    if (file.exists()) {
                        if (strategy.getImageListener() == null) {
                            BitmapUtil.displayImage(view, file, null);
                        } else {
                            BitmapUtil.displayImage(getTarget(url, view, strategy), file, null);
                        }
                    }
                }
            }
        };
        WXSDKManager.getInstance().postOnUiThread(runnable, 0);
    }

    private SimpleTarget getTarget(final String url, final ImageView view, final WXImageStrategy strategy) {
        SimpleTarget target = new SimpleTarget<Drawable>(view.getWidth(),view.getHeight()) {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (strategy.getImageListener() != null) {
                    strategy.getImageListener().onImageFinish(url, view, true, null);
                }
                view.setImageDrawable(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (strategy.getImageListener() != null) {
                    strategy.getImageListener().onImageFinish(url, view, false, null);
                }
            }
        };
        return target;
    }
}