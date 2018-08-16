package com.weexbox.core.router

import android.content.Intent
import com.alibaba.fastjson.JSON
import com.orhanobut.logger.Logger
import com.weexbox.core.R
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWebViewActivity
import com.weexbox.core.controller.WBWeexActivity
import java.util.*

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:44
 * Description: This is Router
 */

class Router {

    companion object {
        var routes: Map<String, Class<*>> = TreeMap()
        val extraName = "WeexBoxRouter"
    }

    // 下一个weex/web页面路径
    var url: String? = null
    // 下一个原生页面的名字
    var nativeName: String? = null
    // 页面出现方式：push, present
    var type: String = "push"
    // 是否隐藏导航栏
    var navBarHidden: Boolean = false
    // 需要传到下一个页面的数据
    var params: Map<String, Any>? = null

    fun openWeex(from: WBBaseActivity) {
        open(from, WBWeexActivity::class.java)
    }

    fun openWeb(from: WBBaseActivity) {
        open(from, WBWebViewActivity::class.java)
    }

    fun openBrowser() {
        // TODO
    }

    fun callPhone() {
        // TODO
    }

    fun open(from: WBBaseActivity, to: Class<*>? = Router.routes[nativeName]) {
        if (to != null) {
            if (type == "present") {
                from.overridePendingTransition(R.anim.present_enter, R.anim.present_exit)
            }
            val intent = Intent(from, to)
            intent.putExtra(Router.extraName, JSON.toJSONString(this))
            from.startActivity(intent)
        } else {
            Logger.e("该路由名未注册")
        }
    }
}