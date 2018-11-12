package com.weexbox.core.controller

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import com.litesuits.common.io.FileUtils
import com.orhanobut.logger.Logger
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.RenderContainer
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.common.IWXDebugProxy
import com.taobao.weex.common.WXRenderStrategy
import com.taobao.weex.ui.component.NestedContainer
import com.taobao.weex.utils.WXFileUtils
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.event.Event
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.WXAnalyzerDelegate
import java.io.IOException
import kotlin.math.log

/**
 * Author: Mario
 * Time: 2018/8/16 下午4:38
 */

abstract class WBWeexFragment: WBBaseFragment(), IWXRenderListener {

    lateinit var url: String
    var instance: WXSDKInstance? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var hasSendViewDidAppear = false

    fun refreshWeex() {
        render()
    }

    private fun render() {
        instance?.destroy()
        val renderContainer = RenderContainer(activity)
        instance = WXSDKInstance(activity)
        instance?.setRenderContainer(renderContainer)
        instance?.registerRenderListener(this)
        instance?.isTrackComponent = false
        try {
            if (url.startsWith("http")) {
                // 下载
                instance?.renderByUrl(url, url, null, null, WXRenderStrategy.APPEND_ASYNC)
            } else {
                val file = UpdateManager.getFullUrl(url)
                val template = FileUtils.readFileToString(file)
                instance?.render(url, template, null, null, WXRenderStrategy.APPEND_ASYNC)
            }
        } catch (e: IOException) {
            Logger.e(e, "文件不存在")
        }
    }

    override fun onStart() {
        super.onStart()

        val u = router?.url
        if (u == null) {
            Logger.e("url不能为空")
        } else {
            url = u
            render()
        }
    }

    override fun onFragmentResume() {
        super.onFragmentResume()

        registerWeexDebugBroadcast()
        sendViewDidAppear()
    }

    override fun onFragmentPause() {
        super.onFragmentPause()

        unregisterWeexDebugBroadcast()
        sendViewDidDisappear()
    }

    override fun onException(instance: WXSDKInstance?, errCode: String?, msg: String?) {
        if (!TextUtils.isEmpty(errCode) && errCode!!.contains("|")) {
            val errCodeList = errCode.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val code = errCodeList[1]
            val codeType = errCode.substring(0, errCode.indexOf("|"))

            if (TextUtils.equals("1", codeType)) {
                val errMsg = "codeType:$codeType\n errCode:$code\n ErrorInfo:$msg"
                degradeAlert(errMsg)
                return
            } else {
                Toast.makeText(activity?.applicationContext, "errCode:$errCode Render ERROR:$msg", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRenderSuccess(instance: WXSDKInstance?, width: Int, height: Int) {
        sendViewDidAppear()
    }

    override fun onRefreshSuccess(instance: WXSDKInstance?, width: Int, height: Int) {

    }

    private fun degradeAlert(errMsg: String) {
        AlertDialog.Builder(activity)
                .setTitle("Downgrade success")
                .setMessage(errMsg)
                .setPositiveButton("OK", null)
                .show()

    }

    override fun onViewCreated(instance: WXSDKInstance?, view: View?) {
        onAddWeexView(view)
    }

    abstract fun onAddWeexView(wxView: View?)

    //调试广播
    inner class RefreshBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refreshWeex()
        }
    }

    private fun registerWeexDebugBroadcast() {
        if (WeexBoxEngine.isDebug) {
            broadcastReceiver = RefreshBroadcastReceiver()
            val filter = IntentFilter()
            filter.addAction(IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH)
            filter.addAction(IWXDebugProxy.ACTION_INSTANCE_RELOAD)
            activity!!.registerReceiver(broadcastReceiver, filter)
        }
    }

    private fun unregisterWeexDebugBroadcast() {
        if (WeexBoxEngine.isDebug) {
            if (broadcastReceiver != null) {
                activity!!.unregisterReceiver(broadcastReceiver)
                broadcastReceiver = null
            }
        }
    }

    private fun sendViewDidAppear() {
        if (!hasSendViewDidAppear) {
            instance?.fireGlobalEventCallback("viewDidAppear", null)
            hasSendViewDidAppear = true
        }
    }

    private fun sendViewDidDisappear() {
        instance?.fireGlobalEventCallback("viewDidDisappear", null)
        hasSendViewDidAppear = false
    }

    /**
     * 获取页面名称
     * 代码混淆后不能对应上
     * @return
     */
    open fun getFragmentSimpleName(): String? {
        return if (router != null && router!!.url != null) {
            router!!.url
        } else "WBWeexFragment"
    }

    override fun onBackPressedAction() {
        super.onBackPressedAction()
        Event.emit(this.getFragmentSimpleName()!! + id, null)
    }

}