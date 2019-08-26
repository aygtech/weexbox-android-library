package com.weexbox.core.router

import android.app.Activity
import android.content.Intent
import com.orhanobut.logger.Logger
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.util.ActivityManager
import java.io.Serializable
import java.util.*

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:44
 * Description: This is Router
 */

class Router : Serializable {

    companion object {

        var routes: TreeMap<String, Class<out WBBaseActivity>> = TreeMap()
        const val EXTRA_NAME = "WeexBoxRouter"

        const val TYPE_PUSH = "push"   //仅对iOS有效
        const val TYPE_PRESENT = "present" //仅对iOS有效
        const val TYPE_MODALMASK = "modalMask" //透明

        const val NAME_FLUTTER = "flutter"  //name的类型
        const val NAME_WEEX = "weex"  //name的类型
        const val NAME_WEB = "web"    //name的类型

        fun register(name: String, controller: Class<out WBBaseActivity>) {
            routes[name] = controller
        }
    }

    // 页面名称
    var name: String? = null
    // 下一个weex/web的路径
    var url: String? = null
    // 页面出现方式：push, present
    var type = Router.TYPE_PUSH
    // 是否隐藏导航栏
    var navBarHidden = false
    // 导航栏标题
    var title: String? = null
    // 禁用返回手势
    var disableGestureBack = false
    // 需要传到下一个页面的数据
    var params: Map<String, Any>? = null
    // 打开页面的同时关闭页面
    var closeFrom: Int? = null
    // 关闭页面的方向，默认和堆栈方向一致
    var closeFromBottomToTop = true
    // 关闭页面的个数
    var closeCount: Int? = null


    fun open(from: WBBaseActivity) {
        val to = routes[name]
        if (to == null) {
            Logger.e("该路由名未注册")
        } else {
            var activities: MutableList<Activity>? = null
            if (closeFrom != null) {
                val allActivities = ActivityManager.getInstance().allActivities
                if (closeFromBottomToTop) {
                    var extraCloseFrom = closeFrom
                    if (extraCloseFrom != null) {
                        closeFrom = extraCloseFrom + 1
                    };
                    var closeTo = if (closeCount != null) {
                        closeCount!! + closeFrom!!
                    } else {
                        allActivities.size
                    }
                    if (closeTo > allActivities.size) {
                        closeTo = allActivities.size
                    }
                    activities = allActivities.subList(closeFrom!!, closeTo)
                } else {
                    val closeTo = allActivities.size - closeFrom!!
                    var closeMyFrom = if (closeCount != null) {
                        allActivities.size - closeFrom!! - closeCount!!
                    } else {
                        1
                    }
                    if (closeMyFrom < 1) {
                        closeMyFrom = 1
                    }
                    activities = allActivities.subList(closeMyFrom, closeTo)
                }
            }

            // 透明主题要销毁，为了跟ios统一
            if (from.router.type == TYPE_MODALMASK && type != TYPE_MODALMASK) {
                for (i in 0 until ActivityManager.getInstance().allActivities.size) {
                    val bigAct = ActivityManager.getInstance().allActivities[i]
                    if (bigAct is WBBaseActivity && bigAct.router.type == TYPE_MODALMASK) {
                        if (activities != null) {
                            var isAdd = true
                            for (j in activities.size downTo i + 1) {
                                val smallAct = activities[j]
                                if (smallAct == bigAct) {
                                    isAdd = false
                                    break
                                }
                            }
                            if (isAdd) {
                                activities.add(bigAct)
                            }
                        } else {
                            activities = ArrayList()
                            activities.add(bigAct)
                        }
                    }
                }
            }

            val intent = Intent(from, to)
            intent.putExtra(EXTRA_NAME, this)
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

    fun close(from: WBBaseActivity, count: Int? = null) {
        var closeCount = count ?: 1
        if (closeCount < 1) {
            closeCount = 1
        }
        val activities = ActivityManager.getInstance().allActivities
        val index = activities.search(from)

        if (activities.size <= closeCount) {
            closeCount = activities.size - 1
        }

        for (i in index - 1 until closeCount) {
            val activity = activities[(activities.size - i - 1)]
            activity.finish()
        }
    }
}