package com.weexbox.example

import android.app.Application

import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.router.Router
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.HotReload

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化 WeexBox
        WeexBoxEngine.setup(this, null)

        // 开启调试
        WeexBoxEngine.isDebug = true

        Router.register(Router.NAME_WEEX, WeexActivity::class.java)

        UpdateManager.serverUrl = "https://aygtech.github.io/weexbox"
        UpdateManager.update { state, progress, error, url -> }

        HotReload.start("ws://192.168.4.247:9090")
    }

}
