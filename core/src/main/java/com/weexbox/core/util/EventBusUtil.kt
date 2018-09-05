package com.weexbox.core.util

import com.weexbox.core.model.Event
import org.greenrobot.eventbus.EventBus


/**
 *Author:leon.wen
 *Time:2018/8/31   11:51
 *Description:This is EventBusUtil
 */

object EventBusUtil {
    fun register(subscriber: Any) {
        EventBus.getDefault().register(subscriber)
    }

    fun unregister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }

    fun sendEvent(event: Event<*>) {
        EventBus.getDefault().post(event)
    }

    fun sendStickyEvent(event: Event<*>) {
        EventBus.getDefault().postSticky(event)
    }
}
