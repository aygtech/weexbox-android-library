package com.weexbox.core

import android.app.Application
import android.content.Context
import android.content.Intent
import com.alibaba.android.bindingx.plugin.weex.BindingX
import com.taobao.weex.InitConfig
import com.taobao.weex.WXSDKEngine
import com.weexbox.core.adapter.ImageAdapter
import io.realm.Realm
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.weexbox.core.module.RouteModule
import com.weexbox.core.module.StorageModule
import com.weexbox.core.service.FloatingBtnService
import android.support.v4.content.ContextCompat.startForegroundService
import android.os.Build
import android.util.Log
import com.weexbox.core.controller.WBWebViewActivity
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.router.Router


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
        BindingX.register()
        WXSDKEngine.registerModule("route", RouteModule::class.java)
        WXSDKEngine.registerModule("storage", StorageModule::class.java)
        registerRouter()
    }

    /**
     * 启动全局悬浮按钮service
     */
    fun initFloatingBtn(context: Context, service: Class<out FloatingBtnService>) {
        Log.i("ccccccc", "11111")
        if (FloatingBtnService.STATAG.equals("stop")) {
            val serviceIntent = Intent(context, service)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
            context.startService(serviceIntent)
//            }
            Log.i("ccccccc", "22222222222")
        }
    }

    fun registerRouter() {
        Router.register(Router.NAME_WEEX, WBWeexActivity::class.java)
        Router.register(Router.NAME_WEB, WBWebViewActivity::class.java)
    }

}