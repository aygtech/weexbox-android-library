package com.weexbox.core.controller

import android.app.AlertDialog
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.litesuits.common.io.FileUtils
import com.orhanobut.logger.Logger
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.RenderContainer
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.common.WXRenderStrategy
import com.taobao.weex.ui.component.NestedContainer
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.WXAnalyzerDelegate
import java.io.IOException

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBWeexActivity
 */

open class WBWeexActivity : WBBaseActivity() {
}

