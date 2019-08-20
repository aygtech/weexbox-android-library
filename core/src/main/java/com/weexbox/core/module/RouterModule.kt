package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.utils.WXUtils
import com.weexbox.core.extension.toObject
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/8/16 下午5:05
 * Description: This is RouterModule
 */

open class RouterModule : BaseModule() {

    //打开页面
    @JSMethod(uiThread = true)
    open fun open(options: Map<String, Any>) {
        getRouter(options).open(getActivity())
    }

    @JSMethod(uiThread = false)
    open fun getParams(): Map<String, Any>? {
        return getFragment()!!.router.params
    }

    @JSMethod(uiThread = true)
    open fun close(levels: Any?) {
        getFragment()!!.router.close(getActivity(), WXUtils.getInt(levels))
    }

    @JSMethod(uiThread = true)
    open fun refresh() {
        getFragment()!!.refreshWeex()
    }

    fun getRouter(options: Map<String, Any>): Router {
        return options.toObject(Router::class.java)
    }
}