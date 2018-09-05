package com.weexbox.core.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.weexbox.core.util.ImageUtil;

import java.nio.ByteBuffer;
import java.security.MessageDigest;


public class BlurTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID = "com.weexbox.core.adapter.BlurTransformation." + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(Key.CHARSET);

    private int mRadius;

    public BlurTransformation(int radius) {
        mRadius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (mRadius <= 0) {
            return toTransform;
        }
        Bitmap bitmap;
        try {
            bitmap = ImageUtil.blur(toTransform, mRadius);
        } catch (Exception e) {
            bitmap = toTransform;
        }
        return bitmap;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlurTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
