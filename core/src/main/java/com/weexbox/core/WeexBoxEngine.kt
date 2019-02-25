package com.weexbox.core

import android.app.Application
import com.alibaba.android.bindingx.plugin.weex.BindingX
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.taobao.weex.InitConfig
import com.taobao.weex.WXEnvironment
import com.taobao.weex.WXSDKEngine
import com.weexbox.core.adapter.ImageAdapter
import com.weexbox.core.adapter.WebSocketAdapterFactory
import com.weexbox.core.component.LottieComponent
import com.weexbox.core.component.RichTextComponent
import com.weexbox.core.component.richtext.WXRichText
import com.weexbox.core.controller.WBWebViewActivity
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.module.*
import com.weexbox.core.router.Router
import com.weexbox.core.util.BitmapUtil
import io.realm.Realm


/**
 * Author: Mario
 * Time: 2018/8/15 下午3:37
 * Description: This is WeexBoxEngine
 */

object WeexBoxEngine {

    lateinit var application: Application
    var isDebug = false
        set(value) {
            field = value
            if (value) {
                WXEnvironment.setOpenDebugLog(true)
                WXEnvironment.setApkDebugable(true)
            } else {
                WXEnvironment.setOpenDebugLog(false)
                WXEnvironment.setApkDebugable(false)
            }
        }

    fun setup(application: Application, weexConfig: InitConfig? = null) {
        this.application = application

        //初始化图片框架
        BitmapUtil.setContext(application)

        Realm.init(application)
        Logger.addLogAdapter(AndroidLogAdapter())
        initWeex(weexConfig)
    }

    private fun initWeex(config: InitConfig?) {
        WXSDKEngine.initialize(application, config
                ?: InitConfig.Builder().setImgAdapter(ImageAdapter()).setWebSocketAdapterFactory(WebSocketAdapterFactory()).build())
        BindingX.register()
        registerModule()
        registerRouter()
        registerComponent()
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

    private fun registerComponent() {
//        WXSDKEngine.registerComponent("wb-richtext", RichTextComponent::class.java)
        WXSDKEngine.registerComponent("richtext", WXRichText::class.java)
        WXSDKEngine.registerComponent("wb-lottie", LottieComponent::class.java)
    }
}