package com.weexbox.core

import android.app.Application
import com.alibaba.android.bindingx.plugin.weex.BindingX
import com.google.gson.Gson
import com.taobao.weex.InitConfig
import com.taobao.weex.WXSDKEngine
import io.realm.Realm
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.weexbox.core.module.RouteModule
import com.weexbox.core.module.StorageModule


/**
 * Author: Mario
 * Time: 2018/8/15 下午3:37
 * Description: This is WeexBoxEngine
 */

object WeexBoxEngine {

    lateinit var application: Application
    val gson = Gson()

    fun initialize(application: Application) {
        this.application = application
        Realm.init(application)
        Logger.addLogAdapter(AndroidLogAdapter())
        initWeex()
    }

    private fun initWeex() {
//        val config = InitConfig.Builder().setImgAdapter(ImageAdapter()).build()
        WXSDKEngine.initialize(application, null)
        BindingX.register()
//        WXSDKEngine.registerModule("route", RouteModule::class.java)
//        WXSDKEngine.registerModule("storage", StorageModule::class.java)
    }
}