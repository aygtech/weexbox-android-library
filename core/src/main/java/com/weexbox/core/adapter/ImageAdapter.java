package com.weexbox.core.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;
import com.weexbox.core.util.BitmapUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

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
                    Glide.with(view).load(url).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (resource.getIntrinsicWidth() >= 4096 || resource.getIntrinsicHeight() >= 4096) {
                                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            }
                            view.setImageDrawable(resource);
                            if (strategy.getImageListener() != null) {
                                strategy.getImageListener().onImageFinish(url, view, true, null);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            if (strategy.getImageListener() != null) {
                                strategy.getImageListener().onImageFinish(url, view, false, null);
                            }
                        }
                    });
                } else if (url.startsWith("bundle://")) {
                    // 本地bundle加载
                    if (url.contains("/static/")){
                        Uri uri = Uri.parse(url);
                        String path = uri.getAuthority() + uri.getPath();
                        AssetManager am = view.getContext().getResources().getAssets();
                        if (path.startsWith("/")){
                            path = path.substring(1);
                        }
                        path = "weexbox-update/" + path;
                        try {
                            InputStream is = am.open(path);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            Glide.with(view).load(bitmap).into(getTarget(url, view, strategy));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Uri uri = Uri.parse(url);
                        List<String> pathSegList = uri.getPathSegments();
                        String path = pathSegList.get(pathSegList.size() - 1);
                        int id = BitmapUtil.sContext.getResources().getIdentifier(path, "drawable", BitmapUtil.sContext.getPackageName());
                        view.setImageResource(id);
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
        SimpleTarget target = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setImageDrawable(resource);
                if (strategy.getImageListener() != null) {
                    strategy.getImageListener().onImageFinish(url, view, true, null);
                }
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