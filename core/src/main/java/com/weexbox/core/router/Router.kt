package com.weexbox.core.router

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:44
 * Description: This is Router
 */

class Router {

    // 下一个weex页面路径
    var url: String? = null
    // 页面出现方式：push, present
    var type: String? = null
    // 是否隐藏导航栏
    var navBarHidden: Boolean = false
    // 需要传到下一个页面的数据
    var params: Map<String, Any>? = null
}