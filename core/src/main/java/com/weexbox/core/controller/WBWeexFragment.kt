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
    var mInstance: WXSDKInstance? = null

    //调试广播
    private var mBroadcastReceiver: BroadcastReceiver? = null

    fun refreshWeex() {
        render()
    }

    private fun render() {
        mInstance?.destroy()
        val renderContainer = RenderContainer(activity)
        mInstance = WXSDKInstance(activity)
        mInstance?.setRenderContainer(renderContainer)
        mInstance?.registerRenderListener(this)
        mInstance?.isTrackComponent = false
        try {
            if (url.startsWith("http")) {
                // 下载
                mInstance?.renderByUrl(url, url, null, null, WXRenderStrategy.APPEND_ASYNC)
            } else {
                val file = UpdateManager.getFullUrl(url)
                val template = FileUtils.readFileToString(file)
                mInstance?.render(url, template, null, null, WXRenderStrategy.APPEND_ASYNC)
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

    override fun onResume() {
        super.onResume()

        registerWeexDebugBroadcast()
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        mInstance?.fireGlobalEventCallback("viewDidAppear", null)
    }

    override fun onFragmentPause() {
        super.onFragmentPause()
        mInstance?.fireGlobalEventCallback("viewDidDisappear", null)
    }

    override fun onPause() {
        super.onPause()

        unregisterWeexDebugBroadcast()
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
        mInstance?.fireGlobalEventCallback("viewDidAppear", null)
    }

    override fun onRefreshSuccess(p0: WXSDKInstance?, p1: Int, p2: Int) {

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
            mBroadcastReceiver = RefreshBroadcastReceiver()
            val filter = IntentFilter()
            filter.addAction(IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH)
            filter.addAction(IWXDebugProxy.ACTION_INSTANCE_RELOAD)
            activity!!.registerReceiver(mBroadcastReceiver, filter)
        }
    }

    private fun unregisterWeexDebugBroadcast() {
        if (mBroadcastReceiver != null) {
            activity!!.unregisterReceiver(mBroadcastReceiver)
            mBroadcastReceiver = null
        }
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