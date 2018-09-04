package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import java.util.*

/**
 *Author:leon.wen
 *Time:2018/9/4   17:32
 *Description:This is NetworkModule
 */

class NetworkModule : WXModule() {

    @JSMethod(uiThread = false)
    fun request(options: Map<String, Any>, callback: JSCallback) {

    }

    @JSMethod(uiThread = false)
    fun upload(options: Map<String, Any>, completionCallback: JSCallback, progressCallback: JSCallback) {

    }
}