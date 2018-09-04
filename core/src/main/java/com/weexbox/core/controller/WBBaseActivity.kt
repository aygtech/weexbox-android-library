package com.weexbox.core.controller

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.weexbox.core.router.Router
import com.weexbox.core.util.EventBusUtil

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBBaseActivity
 */

open class WBBaseActivity : FragmentActivity() {

    var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = intent.getSerializableExtra(Router.extraName) as Router?
        if (isRegisterEventBus() && inCreateRegisterEventBus()) {
            EventBusUtil.register(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isRegisterEventBus() && !inCreateRegisterEventBus()) {
            EventBusUtil.register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isRegisterEventBus() && !inCreateRegisterEventBus()) {
            EventBusUtil.unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegisterEventBus() && inCreateRegisterEventBus()) {
            EventBusUtil.unregister(this)
        }
    }

    fun isRegisterEventBus(): Boolean {
        return false
    }

    fun inCreateRegisterEventBus(): Boolean {
        return false
    }
}