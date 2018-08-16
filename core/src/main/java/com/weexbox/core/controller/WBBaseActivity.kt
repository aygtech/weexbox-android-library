package com.weexbox.core.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBBaseActivity
 */

open class WBBaseActivity : AppCompatActivity() {

    var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = intent.extras.get("router") as? Router
    }
}