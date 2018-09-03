package com.weexbox.core.controller

import android.os.Bundle
import android.support.v4.app.Fragment
import com.weexbox.core.router.Router

open class WBBaseFragment: Fragment() {

    var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = arguments?.getSerializable(Router.extraName) as Router?
    }

}