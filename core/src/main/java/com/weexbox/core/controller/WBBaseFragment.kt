package com.weexbox.core.controller

import android.os.Bundle
import android.support.v4.app.Fragment
import com.weexbox.core.router.Router

open class WBBaseFragment: Fragment() {

    var router: Router? = null
    val ROUTER_NAME: String = "router"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = savedInstanceState?.get(ROUTER_NAME) as? Router
    }

}