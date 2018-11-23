package com.weexbox.core.controller

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weexbox.core.event.Event
import com.weexbox.core.event.EventCallback
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/*
* Fragment基类
*/
abstract class WBBaseFragment: Fragment() {

    // 通用事件
    var events: MutableMap<String, EventCallback> = TreeMap()
    // 返回键操作
    var backPressed: Boolean = false
    // 可见性相关
    private var isVisibleToUser = false
    private var isOnCreateView = false
    private var isSetUserVisibleHint = false
    private var isHiddenChanged = false
    private var isFirstResume = false

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isOnCreateView = true
        rootView = inflater.inflate(getResId(), container, false)
        return rootView
    }

    abstract fun getResId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    open fun onBackPressed(): Boolean {
        return backPressed
    }

    open fun onBackPressedAction() {

    }

    override fun onResume() {
        super.onResume()

        if (!isHiddenChanged && !isSetUserVisibleHint) {
            if (isFirstResume) {
                setVisibleToUser(true)
            }
        }
        if (isSetUserVisibleHint || (!isFirstResume && !isHiddenChanged)) {
            isVisibleToUser = true
        }
        isFirstResume = true
    }
    override fun onPause() {
        super.onPause()

        isHiddenChanged = false
        isSetUserVisibleHint = false
        setVisibleToUser(false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        isSetUserVisibleHint = true
        setVisibleToUser(isVisibleToUser)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        isHiddenChanged = true
        setVisibleToUser(!hidden)
    }

    private fun setVisibleToUser(isVisibleToUser: Boolean) {
        if (!isOnCreateView) {
            return
        }
        if (isVisibleToUser == this.isVisibleToUser) {
            return
        }
        this.isVisibleToUser = isVisibleToUser
        onVisibleToUserChanged(this.isVisibleToUser)
    }

    protected open fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
    }
}