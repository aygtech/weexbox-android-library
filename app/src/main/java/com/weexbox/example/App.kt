package com.weexbox.example

import android.app.Activity
import android.app.Application
import android.os.Bundle

import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.router.Router

class App : Application(), Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()

        application = this

        // 初始化 WeexBox
        WeexBoxEngine.setup(this, null)

        // 开启调试
        WeexBoxEngine.isDebug = true

        registerActivityLifecycleCallbacks(this)

        Router.register(Router.NAME_WEEX, MainActivity::class.java)
    }

    override fun onTerminate() {
        unregisterActivityLifecycleCallbacks(this)

        super.onTerminate()
    }


    //获取当前activity生命周期
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {}

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    companion object {

        private var application: App? = null


        fun getCurrentActivity(): Activity? {
            return application!!.currentActivity
        }
    }
}
