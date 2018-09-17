package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.event.Event

/**
 * Author: Mario
 * Time: 2018/9/12 下午6:33
 * Description: This is EventModule
 */

class EventModule : BaseModule() {

    @JSMethod(uiThread = true)
    fun register(name: String, callback: JSCallback) {
        Event.register(getFragment()!!, name) {
            callback.invokeAndKeepAlive(it)
        }
    }

    @JSMethod(uiThread = true)
    fun emit(name: String, info: Map<String, Any>?) {
        Event.emit(name, info)
    }

    @JSMethod(uiThread = true)
    fun unregister(name: String) {
        Event.unregister(getFragment()!!, name)
    }

    @JSMethod(uiThread = true)
    fun unregisterAll() {
        Event.unregisterAll(getFragment()!!)
    }
}