package com.weexbox.core.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class ImageAdapter implements IWXImgLoaderAdapter {

  public ImageAdapter() {
  }

  @Override
  public void setImage(final String url, final ImageView view,
                       WXImageQuality quality, final WXImageStrategy strategy) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        if(view==null||view.getLayoutParams()==null){
          return;
        }
        if (TextUtils.isEmpty(url)) {
          view.setImageBitmap(null);
          return;
        }
        String temp = url;
        if (url.startsWith("//")) {
          temp = "http:" + url;
        }

        if (!TextUtils.isEmpty(strategy.placeHolder)) {
          BitmapUtil.displayImage(view, strategy.placeHolder);
        }
        BlurTransformation transformation = null;
        if (strategy.blurRadius != 0) {
          transformation = new BlurTransformation(strategy.blurRadius);
        }
        if (strategy.getImageListener() == null) {
          BitmapUtil.displayImage(view, temp, transformation);
        } else {
          BitmapUtil.displayImage(new SimpleTarget<BitmapDrawable>() {
            @Override
            public void onResourceReady(@NonNull BitmapDrawable resource, @Nullable Transition transition) {
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
          }, temp, transformation);
        }
      }
    };
    if(Thread.currentThread() == Looper.getMainLooper().getThread()){
      runnable.run();
    }else {
      WXSDKManager.getInstance().postOnUiThread(runnable, 0);
    }
  }
}