package com.weexbox.core.router

import android.app.Activity
import android.content.Intent
import com.alibaba.fastjson.JSON
import com.orhanobut.logger.Logger
import com.weexbox.core.R
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWebViewActivity
import com.weexbox.core.controller.WBWeexActivity
import com.weexbox.core.util.ActivityManager
import java.io.Serializable
import java.util.*

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:44
 * Description: This is Router
 */

class Router :Serializable{

    companion object {
        var routes: Map<String, Class<*>> = TreeMap()
        val extraName = "WeexBoxRouter"
        val typePush = "push"   //右往左啟動頁面
        val typePresent = "present" //下往上啟動頁面
    }

    // 下一个weex/web页面路径
    var url: String? = null
    // 页面出现方式：push, present
    var type: String = Router.typePush
    // 是否隐藏导航栏
    var navBarHidden: Boolean = false
    // 是否隐藏状态栏
    var statusBarHidden: Boolean = false
    // 需要传到下一个页面的数据
    var params: Map<String, Any>? = null

    fun openWeex(from: WBBaseActivity) {
        open(from, WBWeexActivity::class.java)
    }

    fun openWeb(from: WBBaseActivity) {
        open(from, WBWebViewActivity::class.java)
    }

    fun openNative(from: WBBaseActivity) {
        val to = Router.routes[url]
        if (to == null) {
            Logger.e("该路由名未注册")
        } else {
            open(from, to)
        }
    }

    fun openBrowser(from: WBBaseActivity) {
    }

    fun openPhone(from: WBBaseActivity) {
    }

    fun open(from: WBBaseActivity, to: Class<*>) {
        if (type == Router.typePresent) {
            from.overridePendingTransition(R.anim.present_enter, R.anim.present_exit)
        }
        val intent = Intent(from, to)
        intent.putExtra(Router.extraName, this)
        from.startActivity(intent)
    }

    fun close(from: WBBaseActivity, levels: Int? = null) {
        var count = 0;
        if (levels != null){
            count = levels;
        }

        val activities = ActivityManager.getInstance().getAllActivities()

        for (i in 0 until count) {
            val activity = activities.get(i)
            activity.finish()
        }
    }
}