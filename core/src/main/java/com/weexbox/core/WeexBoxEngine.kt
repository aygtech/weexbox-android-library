package com.weexbox.core

import android.app.Application
import com.taobao.weex.InitConfig
import com.taobao.weex.WXSDKEngine
import com.weexbox.core.adapter.ImageAdapter
import io.realm.Realm
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger



/**
 * Author: Mario
 * Time: 2018/8/15 下午3:37
 * Description: This is WeexBoxEngine
 */

object WeexBoxEngine {

    lateinit var application: Application

    fun initialize(application: Application) {
        this.application = application
        Realm.init(application)
        Logger.addLogAdapter(AndroidLogAdapter())
        initWeex()
    }

    private fun initWeex() {
        val config = InitConfig.Builder().setImgAdapter(ImageAdapter()).build()
        WXSDKEngine.initialize(application, config)

    }
}