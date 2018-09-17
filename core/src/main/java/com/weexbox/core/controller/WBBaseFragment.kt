package com.weexbox.core.controller

import android.os.Bundle
import android.support.v4.app.Fragment
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.WXSDKManager
import com.weexbox.core.event.Event
import com.weexbox.core.event.EventCallback
import com.weexbox.core.router.Router
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

open class WBBaseFragment: Fragment() {

    var router: Router? = null
    var events: MutableMap<String, EventCallback> = TreeMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = arguments?.getSerializable(Router.EXTRA_NAME) as Router?
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    // 通用事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        val callback = events[event.name]
        callback?.invoke(event.info)
    }

    fun doFragmentResume() {}

    fun doFragmentPause() {}
}