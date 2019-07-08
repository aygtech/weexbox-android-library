package com.weexbox.core.controller

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.litesuits.common.io.FileUtils
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.common.WXRenderStrategy
import com.weexbox.core.R
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.event.Event
import com.weexbox.core.extension.appendingPathComponent
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.HotReload
import com.weexbox.core.util.ToastUtil
import java.io.IOException
import java.util.*

/**
 * Author: Mario
 * Time: 2018/8/16 下午4:38
 */

open class WBWeexFragment : WBBaseFragment(), IWXRenderListener {

    var instance: WXSDKInstance? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var isFirstSendDidAppear = true
    private var url: String? = null
    private var refreshTime = Date().time

    override fun getLayoutId(): Int {
        return R.layout.fragment_weex
    }

    fun refreshWeex() {
        val currentTime = Date().time
        if (WeexBoxEngine.isDebug && currentTime - refreshTime < 1000) {
            return
        }
        refreshTime = currentTime
        createWeexInstance()
        render()
    }

    private fun render() {
        if (url != null) {
            if (url!!.startsWith("http")) {
                renderByUrl()
            } else {
                val host = HotReload.url
                if (host != null) {
                    url = host.replace("ws", "http") + "/www/" + url
                    renderByUrl()
                } else {
                    renderByFile()
                }
            }
        } else {
            ToastUtil.showLongToast(activity, "url不能为空")
        }
    }

    private fun renderByUrl() {
        val vueUrl = "$url?bundleType=Vue"
        instance?.renderByUrl(null, vueUrl, null, null, WXRenderStrategy.APPEND_ASYNC)
    }

    private fun renderByFile() {
        try {
            val file = UpdateManager.getFullUrl(url!!)
            val template = FileUtils.readFileToString(file)
            instance?.render(null, template, null, null, WXRenderStrategy.APPEND_ASYNC)
        } catch (e: IOException) {
            ToastUtil.showLongToast(activity, "文件不存在")
        }
    }

    private fun createWeexInstance() {
        destoryWeexInstance()
        instance = WXSDKInstance(activity)
        instance?.registerRenderListener(this)
    }

    private fun destoryWeexInstance() {
        instance?.registerRenderListener(null)
        instance?.destroy()
        instance = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        url = router.url
        createWeexInstance()
        instance?.onActivityCreate()

        render()
        registerWXReloadBundle()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterWXReloadBundle()
        instance?.onActivityDestroy()
        instance?.destroy()
    }

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
        super.onVisibleToUserChanged(isVisibleToUser)

        if (isVisibleToUser) {
            registerRefreshInstance()
            if (!isFirstSendDidAppear) {
                sendViewDidAppear()
            }
        } else {
            unregisterRefreshInstance()
            sendViewDidDisappear()
        }
    }

    // IWXRenderListener
    override fun onException(instance: WXSDKInstance?, errCode: String?, msg: String?) {
        if (WeexBoxEngine.isDebug) {
            val errMsg = "errorCode:$errCode\n message:$msg"
            AlertDialog.Builder(activity).setMessage(errMsg).setPositiveButton("OK", null).show()
        }
    }

    override fun onRenderSuccess(instance: WXSDKInstance?, width: Int, height: Int) {
        sendViewDidAppear()
    }

    override fun onRefreshSuccess(instance: WXSDKInstance?, width: Int, height: Int) {

    }

    override fun onViewCreated(instance: WXSDKInstance?, view: View?) {
        val container = rootView as? ViewGroup
        container?.removeAllViews()
        container?.addView(view)
    }

    // 调试广播
    inner class RefreshBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refreshWeex()
        }
    }

    private fun registerRefreshInstance() {
        if (WeexBoxEngine.isDebug) {
            broadcastReceiver = RefreshBroadcastReceiver()
            val filter = IntentFilter()
            filter.addAction(WXSDKInstance.ACTION_DEBUG_INSTANCE_REFRESH)
            filter.addAction(WXSDKInstance.ACTION_INSTANCE_RELOAD)
            activity!!.registerReceiver(broadcastReceiver, filter)
        }
    }

    private fun registerWXReloadBundle() {
        if (WeexBoxEngine.isDebug) {
            Event.register(this, "WXReloadBundle") {
                if (url != null) {
                    var name = url!!
                    if (url!!.startsWith("http")) {
                        val before = url!!.substringBeforeLast("/")
                        val after = url!!.substringAfterLast("/")
                        name = before.substringAfterLast("/").appendingPathComponent(after)
                    }
                    val params = it!!["params"] as String
                    if (params.endsWith(name)) {
                        url = params
                        refreshWeex()
                    }
                }
            }
        }
    }

    private fun unregisterRefreshInstance() {
        if (broadcastReceiver != null) {
            activity!!.unregisterReceiver(broadcastReceiver)
            broadcastReceiver = null
        }
    }

    private fun unregisterWXReloadBundle() {
        Event.unregister(this, "WXReloadBundle")
    }

    private fun sendViewDidAppear() {
        instance?.fireGlobalEventCallback("viewDidAppear", null)
    }

    private fun sendViewDidDisappear() {
        isFirstSendDidAppear = false
        instance?.fireGlobalEventCallback("viewDidDisappear", null)
    }

    /**
     * 获取页面名称
     * 代码混淆后不能对应上
     * @return
     */
    fun getFragmentSimpleName(): String? {
        return url ?: "WBWeexFragment"
    }

    override fun onBackPressed() {
        super.onBackPressed()

        Event.emit(backName, null)
    }

    override fun onStart() {
        super.onStart()

        instance?.onActivityStart()
    }

    override fun onResume() {
        super.onResume()

        instance?.onActivityResume()
    }

    override fun onPause() {
        super.onPause()

        instance?.onActivityPause()
    }

    override fun onStop() {
        super.onStop()

        instance?.onActivityStop()
    }

}