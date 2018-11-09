package com.weexbox.core.event

import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBBaseFragment
import org.greenrobot.eventbus.EventBus

/**
 * Author: Mario
 * Time: 2018/9/12 下午6:31
 * Description: This is Event
 */

typealias EventCallback = (Map<String, Any>?) -> Unit

class Event {

    lateinit var name: String
    var info: Map<String, Any>? = null

    companion object {
        // 注册事件
        fun register(target: WBBaseFragment, name: String, callback: EventCallback) {
            target.events[name] = callback
        }

        // 注销事件
        fun unregister(target: WBBaseFragment, name: String) {
            target.events.remove(name)
        }

        // 注销所有事件
        fun unregisterAll(target: WBBaseFragment) {
            target.events.clear()
        }

        // 发送事件
        fun emit(name: String, info: Map<String, Any>?) {
            val event = Event()
            event.name = name
            event.info = info
            EventBus.getDefault().post(event)
        }

        // 注册事件
        fun register(target: WBBaseActivity, name: String, callback: EventCallback) {
            target.events[name] = callback
        }

        // 注销事件
        fun unregister(target: WBBaseActivity, name: String) {
            target.events.remove(name)
        }

        // 注销所有事件
        fun unregisterAll(target: WBBaseActivity) {
            target.events.clear()
        }
    }
}