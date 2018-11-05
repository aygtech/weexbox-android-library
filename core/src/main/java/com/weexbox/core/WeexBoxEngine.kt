package com.weexbox.core

import android.app.Application
import android.content.Context
import android.content.Intent
import com.alibaba.android.bindingx.plugin.weex.BindingX
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.taobao.weex.InitConfig
import com.taobao.weex.WXEnvironment
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.bridge.WXBridgeManager
import com.weexbox.core.adapter.ImageAdapter
import com.weexbox.core.controller.WBWebViewActivity
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.module.*
import com.weexbox.core.router.Router
import com.weexbox.core.service.FloatingBtnService
import com.weexbox.core.util.BitmapUtil
import io.realm.Realm


/**
 * Author: Mario
 * Time: 2018/8/15 下午3:37
 * Description: This is WeexBoxEngine
 */

object WeexBoxEngine {

    lateinit var application: Application

    fun setup(application: Application) {
        this.application = application

        //初始化图片框架
        BitmapUtil.setContext(application)

        Realm.init(application)
        Logger.addLogAdapter(AndroidLogAdapter())
        initWeex()
    }

    /**
     * 启动全局悬浮按钮service
     */
    fun initFloatingBtn(context: Context, service: Class<out FloatingBtnService>) {
        if (FloatingBtnService.STATAG.equals("stop")) {
            val serviceIntent = Intent(context, service)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
            context.startService(serviceIntent)
//            }
        }
    }


    private fun initWeex() {
//        WXBridgeManager.updateGlobalConfig("wson_on")
//        WXEnvironment.setOpenDebugLog(true)
//        WXEnvironment.setApkDebugable(true)
        val config = InitConfig.Builder().setImgAdapter(ImageAdapter()).build()
        WXSDKEngine.initialize(application, config)
        BindingX.register()
        registerModule()
        registerRouter()
    }

    private fun registerModule() {
        WXSDKEngine.registerModule("wb-router", RouterModule::class.java)
        WXSDKEngine.registerModule("wb-storage", StorageModule::class.java)
        WXSDKEngine.registerModule("wb-navigator", NavigatorModule::class.java)
        WXSDKEngine.registerModule("wb-network", NetworkModule::class.java)
        WXSDKEngine.registerModule("wb-modal", ModalModule::class.java)
        WXSDKEngine.registerModule("wb-external", ExternalModule::class.java)
        WXSDKEngine.registerModule("wb-event", EventModule::class.java)
    }

    private fun registerRouter() {
        Router.register(Router.NAME_WEEX, WBWeexActivity::class.java)
        Router.register(Router.NAME_WEB, WBWebViewActivity::class.java)
    }
}