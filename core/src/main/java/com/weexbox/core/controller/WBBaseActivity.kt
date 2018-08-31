package com.weexbox.core.controller

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBBaseActivity
 */

open class WBBaseActivity : FragmentActivity() {

    var router: Router? = null
    val ROUTER_NAME: String = "router"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = intent.extras.get(ROUTER_NAME) as? Router
    }
}