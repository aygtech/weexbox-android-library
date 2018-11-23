package com.weexbox.core.controller

import android.os.Bundle
import com.weexbox.core.R

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBWeexActivity
 */

open class WBWeexActivity : WBBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weex)
        val weexFragment = WBWeexFragment()
        weexFragment.url = router?.url
        supportFragmentManager.beginTransaction().replace(R.id.container, weexFragment).commit()
    }
}

