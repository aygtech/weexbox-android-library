package com.weexbox.core.module

import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.router.Router
import com.weexbox.core.util.GsonUtil

/**
 * Author: Mario
 * Time: 2018/8/16 下午5:05
 * Description: This is RouterModule
 */

class RouterModule : WXModule() {

    //打开页面
    @JSMethod(uiThread = true)
    fun open(info: Map<String, Any>) {
        getRouter(info).open(getActivity())
    }

    //打开浏览器
    @JSMethod(uiThread = true)
    fun openBrowser(info: Map<String, Any>) {
        getRouter(info).openBrowser(getActivity())
    }

    //打电话
    @JSMethod(uiThread = true)
    fun openPhone(info: Map<String, Any>) {
        getRouter(info).openPhone(getActivity())
    }

    @JSMethod(uiThread = true)
    fun getParams(): Map<String, Any>? {
        return getActivity().router!!.params
    }

    @JSMethod(uiThread = true)
    fun close(levels: Int?) {
        getActivity().router!!.close(getActivity(), levels)
    }

    @JSMethod(uiThread = true)
    fun refresh() {
        getActivity().refreshWeex()
    }

    fun getRouter(info: Map<String, Any>): Router {
        return GsonUtil.getWeexGson(info, Router::class.java) as Router
    }

    fun getActivity(): WBBaseActivity {
        return mWXSDKInstance.context as WBBaseActivity
    }

}