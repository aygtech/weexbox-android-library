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

        var routes: TreeMap<String, Class<out WBBaseActivity>> = TreeMap()
        const val EXTRA_NAME = "WeexBoxRouter"

        const val TYPE_PUSH = "push"   //右往左啟動頁面
        const val TYPE_PRESENT = "present" //下往上啟動頁面

        const val NAME_WEEX = "weex"  //name的类型
        const val NAME_WEB = "web"    //name的类型

        fun register(name: String, controller: Class<out WBBaseActivity>){
            routes[name] = controller
        }
    }

    // 页面名称
    var name: String = ""
    // 下一个weex/web的路径
    var url: String? = null
    // 页面出现方式：push, present
    var type: String = Router.TYPE_PUSH
    // 是否隐藏导航栏
    var navBarHidden: Boolean = false
    // 需要传到下一个页面的数据
    var params: Map<String, Any>? = null
    // 打开页面的同时关闭页面
    var closeFrom: Int? = null
    // 关闭页面的方向，默认和堆栈方向一致
    var closeFromBottomToTop = true
    // 关闭页面的个数
    var closeCount: Int? = null


    fun open(from: WBBaseActivity) {
        val to = Router.routes[name]
        if (to == null) {
            Logger.e("该路由名未注册")
        } else {
            var activities: List<Activity>? = null
            if (closeFrom != null) {
                if (closeFromBottomToTop){
                    val allActivities = ActivityManager.getInstance().allActivities
                    val closeTo = if (closeCount != null) {closeCount!! + closeFrom!!} else {allActivities.size }
                    activities = allActivities.subList(closeFrom!!, closeTo)
                } else{
                    val allActivities = ActivityManager.getInstance().allActivities
                    val closeTo = allActivities.size - closeFrom!!
                    val closeMyFrom = if (closeCount != null) {allActivities.size - closeFrom!! - closeCount!!} else {1}
                    activities = allActivities.subList(closeMyFrom, closeTo)
                }
            }

            if (type == Router.TYPE_PRESENT) {
                from.overridePendingTransition(R.anim.present_enter, R.anim.present_exit)
            }
            val intent = Intent(from, to)
            intent.putExtra(Router.EXTRA_NAME, this)
            from.startActivity(intent)
            if (activities != null) {
                removeActivitys(activities)
            }

        }
    }

    fun removeActivitys(activities: List<Activity>) {


            for (activity in activities) {
                activity.finish()
            }

    }

    fun close(from: WBBaseActivity, levels: Int? = null) {
        var count = 0
        if (levels != null) {
            count = levels
        }

        val activities = ActivityManager.getInstance().allActivities

        if (activities.size < count){
            count = activities.size
        }

        for (i in 0 until count) {
            val activity = activities[(activities.size - i -1)]
            activity.finish()
        }
    }
}