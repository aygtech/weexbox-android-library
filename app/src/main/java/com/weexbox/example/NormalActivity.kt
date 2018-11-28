package com.weexbox.example

import android.os.Bundle
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.controller.WBWeexFragment
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2018/11/23 2:10 PM
 * Description: This is NormalActivity
 */

class NormalActivity: WBBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_normal)
        val weexFragment = WBWeexFragment()
        weexFragment.url = "module1/page1.js"
        supportFragmentManager.beginTransaction().replace(R.id.weex_fragment, weexFragment).commit()

    }
}