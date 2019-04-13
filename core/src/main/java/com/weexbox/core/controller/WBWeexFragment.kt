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
import com.orhanobut.logger.Logger
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.RenderContainer
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.common.WXRenderStrategy
import com.weexbox.core.R
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.event.Event
import com.weexbox.core.extension.appendingPathComponent
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.HotReloadManager
import java.io.IOException

/**
 * Author: Mario
 * Time: 2018/8/16 下午4:38
 */

open class WBWeexFragment : WBBaseFragment(), IWXRenderListener {

    var instance: WXSDKInstance? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var isFirstSendDidAppear = true
    private var url: String? = null
    private var hotReloadManager: HotReloadManager? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_weex
    }

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
        if (url != null) {
            if (url!!.startsWith("http")) {
                // 下载
                instance?.renderByUrl(url, url, null, null, WXRenderStrategy.APPEND_ASYNC)
            } else {
                try {
                    val file = UpdateManager.getFullUrl(url!!)
                    val template = FileUtils.readFileToString(file)
                    instance?.render(url, template, null, null, WXRenderStrategy.APPEND_ASYNC)
                } catch (e: IOException) {
                    Logger.e(e, "文件不存在")
                }
            }
        } else {
            Logger.e("url不能为空")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        url = router.url

        if (WeexBoxEngine.isDebug) {
            hotReloadManager = HotReloadManager(object : HotReloadManager.ActionListener {
                override fun reload() {
                    activity?.runOnUiThread {
                        refreshWeex()
                    }
                }

                override fun render(bundleUrl: String) {
                    activity?.runOnUiThread {
                        if (url != null && isVisibleToUser) {
                            var name = url!!
                            if (url!!.startsWith("http")) {
                                val before = url!!.substringBeforeLast("/")
                                val after = url!!.substringAfterLast("/")
                                name = before.substringAfterLast("/").appendingPathComponent(after)
                            }
                            if (bundleUrl.endsWith(name)) {
                                url = bundleUrl
                                refreshWeex()
                            }
                        }
                    }
                }
            })
        }

        render()
    }

    override fun onDestroy() {
        super.onDestroy()

        hotReloadManager?.destroy()
        instance?.destroy()
    }

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
        super.onVisibleToUserChanged(isVisibleToUser)

        if (isVisibleToUser) {
            if (WeexBoxEngine.isDebug) {
                registerWeexDebugBroadcast()
            }
            if (!isFirstSendDidAppear) {
                sendViewDidAppear()
            }
        } else {
            unregisterWeexDebugBroadcast()
            sendViewDidDisappear()
        }
    }

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
        if (view!!.parent == null) {
            (rootView as ViewGroup).addView(view)
        }
        rootView.requestLayout()
    }

    //调试广播
    inner class RefreshBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refreshWeex()
        }
    }

    private fun registerWeexDebugBroadcast() {
        broadcastReceiver = RefreshBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(WXSDKInstance.ACTION_DEBUG_INSTANCE_REFRESH)
        filter.addAction(WXSDKInstance.ACTION_INSTANCE_RELOAD)
        activity!!.registerReceiver(broadcastReceiver, filter)
    }

    private fun unregisterWeexDebugBroadcast() {
        if (broadcastReceiver != null) {
            activity!!.unregisterReceiver(broadcastReceiver)
            broadcastReceiver = null
        }
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

}