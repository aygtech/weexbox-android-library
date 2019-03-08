package com.weexbox.example

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWeexFragment
import com.weexbox.core.router.Router
import com.weexbox.core.util.ActivityManager
import com.weexbox.core.util.ToastUtil
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Author: Mario
 * Time: 2018/11/23 2:10 PM
 * Description: This is NormalActivity
 */

class NormalActivity : WBBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_normal)

        val weexFragment = WBWeexFragment()
        weexFragment.router.url = "http://dotwe.org/raw/dist/791c8507ae8f35a9e134abe8a776588d.bundle.wx"
//        weexFragment.router.url = "module1/page1.js"
        supportFragmentManager.beginTransaction().replace(R.id.weex_fragment, weexFragment).commit()

//        getActionbar().setTitleText( {
//            val intent = Intent(this, NormalActivity::class.java)
//            startActivity(intent)
//        }, "haode")
//
//
//        getActionbar().setRightButton ({
//
//                router.close(2, this)
//
//        },"haha ")
//
//
//        val size = ActivityManager.getInstance().allActivities.size
//        if (size == 5){
//            var timer = Timer()
//            timer.schedule(timerTask { router.close(2, this@NormalActivity) }, 3000)
//            timer.schedule(object : TimerTask(){
//                override fun run() {
//                    router.close(2, this@NormalActivity)
//                }
//
//            }, 3000)
//        }
    }
}