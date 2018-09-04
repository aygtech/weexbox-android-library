package com.weexbox.core.controller

import android.os.Bundle
import android.support.v4.app.Fragment
import com.weexbox.core.router.Router
import com.weexbox.core.util.EventBusUtil

open class WBBaseFragment: Fragment() {

    var router: Router? = null
    var mFrageMentTag = "";// fragement标识

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = arguments?.getSerializable(Router.extraName) as Router?
        if (isRegisterEventFrageMent() && inCreateEventFrageMent()) {
            EventBusUtil.register(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isRegisterEventFrageMent() && !inCreateEventFrageMent()) {
            EventBusUtil.register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isRegisterEventFrageMent() && !inCreateEventFrageMent()) {
            EventBusUtil.unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegisterEventFrageMent() && inCreateEventFrageMent()) {
            EventBusUtil.unregister(this)
        }
    }

    fun isRegisterEventFrageMent(): Boolean {
        return false
    }

    fun inCreateEventFrageMent(): Boolean {
        return false
    }

}