package com.weexbox.example

import android.os.Bundle
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/11/23 2:10 PM
 * Description: This is BaseActivity
 */

class BaseActivity: WBWeexActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router = Router()
//        router!!.url = ""
        setContentView(R.layout.activity_base)


    }
}