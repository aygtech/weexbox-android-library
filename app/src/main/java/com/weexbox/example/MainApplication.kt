package com.weexbox.example

import android.app.Application
import com.weexbox.core.WeexBoxEngine

/**
 * 注意要在Manifest中设置android:name=".MainApplication"
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        WeexBoxEngine.initialize(this)
    }


}
