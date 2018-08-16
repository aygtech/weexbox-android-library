package com.weexbox.core.module

import com.alibaba.fastjson.JSON
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/8/16 下午5:05
 * Description: This is RouterModule
 */

class RouteModule : WXModule() {

    val activity = mWXSDKInstance.context as WBWeexActivity

    @JSMethod(uiThread = true)
    fun open(info: Map<String, Any>) {
        val router = JSON.parseObject(JSON.toJSONString(info), Router::class.java)
        router.open(activity)
    }

    @JSMethod(uiThread = true)
    fun getParams(): Map<String, Any>? {
        return activity.router?.params
    }

    @JSMethod(uiThread = true)
    fun back(levels: Int?) {

    }

    @JSMethod(uiThread = true)
    fun refresh() {
        activity.refreshWeex()
    }



}