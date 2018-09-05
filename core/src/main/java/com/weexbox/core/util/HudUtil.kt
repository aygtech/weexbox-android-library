package com.weexbox.core.util

import android.content.Context
import com.kaopiz.kprogresshud.KProgressHUD

/**
 * Author: Mario
 * Time: 2018/9/5 上午11:54
 * Description: This is HudUtil
 */

class HudUtil(context: Context) {

    val hud = KProgressHUD.create(context)
    val style: KProgressHUD.Style? = null

    // 显示菊花
    fun showLoading(message: String?) {
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(message)
                .show()
    }

    // 显示进度
    fun showProgress(progress: Int, message: String?) {
        if (style != KProgressHUD.Style.ANNULAR_DETERMINATE) {
            hud.setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                    .setLabel(message)
                    .setMaxProgress(100)
                    .show()
        }
        hud.setProgress(progress)
    }

    // 关闭菊花
    fun dismiss() {
        hud.dismiss()
    }

    // 吐司
    fun showToast(message: String) {
        hud.setCustomView(null)
                .setLabel(message)
                .show()
    }
}