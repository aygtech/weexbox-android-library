package com.weexbox.core.controller

import android.app.Activity
import android.os.Bundle
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBBaseActivity
 */

open class WBBaseActivity : Activity() {

    var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = intent.extras.get("router") as? Router
    }
}