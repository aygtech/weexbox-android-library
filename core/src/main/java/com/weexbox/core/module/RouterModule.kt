package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/8/16 下午5:05
 * Description: This is RouterModule
 */

class RouterModule : WXModule() {

    val activity = mWXSDKInstance.context as WBWeexActivity

    //打开weex页面
    @JSMethod(uiThread = true)
    fun openWeex(info: Map<String, Any>) {
        getRouter(info).openWeex(activity)
    }

    //打开web页面
    @JSMethod(uiThread = true)
    fun openWeb(info: Map<String, Any>) {
        getRouter(info).openWeb(activity)
    }

    //打开原生页面
    @JSMethod(uiThread = true)
    fun openNative(info: Map<String, Any>) {
        getRouter(info).openNative(activity)
    }

    //打开浏览器
    @JSMethod(uiThread = true)
    fun openBrowser(info: Map<String, Any>) {
        getRouter(info).openBrowser(activity)
    }

    //打电话
    @JSMethod(uiThread = true)
    fun openPhone(info: Map<String, Any>) {
        getRouter(info).openPhone(activity)
    }

    @JSMethod(uiThread = true)
    fun getParams(): Map<String, Any>? {
        return activity.router!!.params
    }

    @JSMethod(uiThread = true)
    fun close(levels: Int?) {
        activity.router!!.close(activity, levels)
    }

    @JSMethod(uiThread = true)
    fun refresh() {
        activity.refreshWeex()
    }

    fun getRouter(info: Map<String, Any>): Router {
        return WeexBoxEngine.gson.fromJson(WeexBoxEngine.gson.toJson(info), Router::class.java)
    }

}