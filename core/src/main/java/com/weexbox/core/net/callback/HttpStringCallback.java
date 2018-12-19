package com.weexbox.core.net.callback;

/**
 * Created by freeson on 16/7/28.
 */
public abstract class HttpStringCallback extends HttpCallback<String> {

    @Override
    protected String parseEntity(String data, int requestId) {
        return data;
    }
}
