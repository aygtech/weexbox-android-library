package com.weexbox.core.controller

import android.os.Bundle
import android.support.v4.app.Fragment
import com.weexbox.core.event.Event
import com.weexbox.core.event.EventCallback
import com.weexbox.core.router.Router
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/*
* Fragment基类
*/
open class WBBaseFragment: Fragment() {

    // 路由
    var router: Router? = null
    // 通用事件
    var events: MutableMap<String, EventCallback> = TreeMap()
    // 返回键操作
    var backPressed: Boolean = false

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

    open fun doFragmentResume() {}

    open fun doFragmentPause() {}

    /**
     * Fragment当前正在显示调用
     */
    open fun onFragmentResume() {}

    /**
     * Fragment当前不显示时调用
     */
    open fun onFragmentPause() {}

    open fun onBackPressed(): Boolean {
        return backPressed
    }

    open fun onBackPressedAction() {

    }
}