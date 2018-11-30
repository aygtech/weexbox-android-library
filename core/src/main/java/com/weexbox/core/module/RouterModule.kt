package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.Result
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/8/16 下午5:05
 * Description: This is RouterModule
 */

open class RouterModule : BaseModule() {

    //打开页面
    @JSMethod(uiThread = true)
    open fun open(info: Map<String, Any>) {
        getRouter(info).open(getActivity())
    }

    //打开浏览器
    @JSMethod(uiThread = true)
    open fun openBrowser(info: Map<String, Any>) {
//        getRouter(info).openBrowser(getActivity())
    }

    //打电话
    @JSMethod(uiThread = true)
    open fun openPhone(info: Map<String, Any>) {
//        getRouter(info).openPhone(getActivity())
    }

    @JSMethod(uiThread = false)
    open fun getParams(): Map<String, Any>? {
        return getFragment()!!.router.params
    }

    @JSMethod(uiThread = true)
    open  fun close(levels: Int?) {
        getFragment()!!.router.close(levels)
    }

    @JSMethod(uiThread = true)
    open fun refresh() {
        getFragment()!!.refreshWeex()
    }

    fun getRouter(info: Map<String, Any>): Router {
        return info.toObject(Router::class.java)
    }

}