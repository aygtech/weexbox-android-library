package com.weexbox.core.module

import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import com.weexbox.core.util.HudUtil

/**
 * Author: Mario
 * Time: 2018/9/5 上午11:48
 * Description: This is ModalModule
 */

class ModalModule: WXModule() {

    val hud = HudUtil(mWXSDKInstance.context)

    // 显示菊花
    fun showLoading(message: String?) {
        hud.showLoading(message)
    }

    // 显示进度
    fun showProgress(progress: Int, message: String?) {
        hud.showProgress(progress, message)
    }

    // 关闭菊花
    fun dismiss() {
        hud.dismiss()
    }

    // 吐司
    fun showToast(message: String) {
        hud.showToast(message)
    }

    // 提示框
    fun alert(options: Map<String, Any>, callback: JSCallback) {

    }

    // 确认框
    fun confirm(options: Map<String, Any>, callback: JSCallback) {

    }

    // 输入框
    fun prompt(options: Map<String, Any>, callback: JSCallback) {

    }

    // 操作表
    fun actionSheet(options: Map<String, Any>, callback: JSCallback) {

    }
}