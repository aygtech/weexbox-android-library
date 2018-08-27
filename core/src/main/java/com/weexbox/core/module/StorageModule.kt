package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule

/**
 * Author: Mario
 * Time: 2018/8/17 下午5:24
 * Description: This is StorageModule
 */

class StorageModule : WXModule() {

    @JSMethod(uiThread = true)
    fun setData(key: String, value: String) {

    }

    @JSMethod(uiThread = true)
    fun getData(key: String): String {
        return "";
    }

    @JSMethod(uiThread = true)
    fun deleteData(key: String) {

    }

    @JSMethod(uiThread = true)
    fun deleteAll() {

    }
}