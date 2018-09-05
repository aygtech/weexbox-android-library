package com.weexbox.core.net.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by freeson on 16/7/29.
 */
public abstract class HttpBitmapCallback extends HttpCallback<Bitmap> {

    @Override
    protected boolean useInputStream() {
        return true;
    }

    @Override
    protected Bitmap decodeInputStream(InputStream inputStream, final long length, int requestId) {
        return BitmapFactory.decodeStream(inputStream);
    }
}
