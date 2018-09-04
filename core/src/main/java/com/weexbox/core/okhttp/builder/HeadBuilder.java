package com.weexbox.core.okhttp.builder;

import com.weexbox.core.okhttp.OkHttpUtils;
import com.weexbox.core.okhttp.request.OtherRequest;
import com.weexbox.core.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
