package com.weexbox.core.module

import com.alibaba.fastjson.JSON
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.appfram.navigator.WXNavigatorModule
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.router.Router

/**
 * Author: Mario
 * Time: 2019-08-05 20:19
 * Description: This is WXNavigatorModule
 */

open class WXNavigatorModule: WXNavigatorModule() {

    @JSMethod(uiThread = true)
    override fun push(param: String?, callback: JSCallback?) {
        val jsonObject = JSON.parseObject(param)
        val url = jsonObject.getString("url")
        val router = Router()
        router.url = url
        router.name = Router.NAME_WEEX
        router.open(mWXSDKInstance.context as WBBaseActivity)
        callback?.invoke(MSG_SUCCESS)
    }
}