package com.weexbox.core.controller

import android.app.Fragment
import android.os.Bundle
import com.weexbox.core.router.Router

open class WBBaseFragment: Fragment() {

    var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = savedInstanceState?.get("router") as? Router
    }

}