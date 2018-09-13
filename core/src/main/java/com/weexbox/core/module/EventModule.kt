package com.weexbox.core.module

import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.event.Event

/**
 * Author: Mario
 * Time: 2018/9/12 下午6:33
 * Description: This is EventModule
 */

class EventModule : BaseModule() {

    fun register(name: String, callback: JSCallback) {
        Event.register(getFragment()!!, name) {
            callback.invokeAndKeepAlive(it)
        }
    }

    fun emit(name: String, info: Map<String, Any>?) {
        Event.emit(name, info)
    }

    fun unregister(name: String) {
        Event.unregister(getFragment()!!, name)
    }

    fun unregisterAll() {
        Event.unregisterAll(getFragment()!!)
    }
}