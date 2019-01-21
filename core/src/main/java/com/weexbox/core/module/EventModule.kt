package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.utils.WXUtils
import com.weexbox.core.event.Event
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions

/**
 * Author: Mario
 * Time: 2018/9/12 下午6:33
 * Description: This is EventModule
 */

open class EventModule : BaseModule() {

    @JSMethod(uiThread = true)
    open fun register(name: Any, callback: JSCallback) {
        Event.register(getFragment()!!, WXUtils.getString(name, null)) {
            callback.invokeAndKeepAlive(it)
        }
    }

    @JSMethod(uiThread = true)
    open fun emit(options: Map<String, Any>) {
        val info = options.toObject(JsOptions::class.java)
        Event.emit(info.name!!, info.params)
    }

    @JSMethod(uiThread = true)
    open fun unregister(name: Any) {
        Event.unregister(getFragment()!!, WXUtils.getString(name, null))
    }

    @JSMethod(uiThread = true)
    open fun unregisterAll() {
        Event.unregisterAll(getFragment()!!)
    }
}