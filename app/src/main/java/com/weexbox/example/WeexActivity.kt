package com.weexbox.example

import android.os.Bundle
import com.weexbox.core.controller.WBWeexActivity

/**
 * Author: Mario
 * Time: 2018/11/28 11:19 AM
 * Description: This is WeexActivity
 */

class WeexActivity : WBWeexActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        router.url = "page/module.js"

        super.onCreate(savedInstanceState)
    }
}