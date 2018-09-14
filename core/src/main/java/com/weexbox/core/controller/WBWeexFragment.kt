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
import android.view.View
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
import com.weexbox.core.https.HotRefreshManager
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.WXAnalyzerDelegate
import java.io.IOException
import kotlin.math.log

/**
 * Author: Mario
 * Time: 2018/8/16 下午4:38
 */

open abstract class WBWeexFragment: WBBaseFragment() , Handler.Callback, IWXRenderListener , WXSDKInstance.NestedInstanceInterceptor {

    open lateinit var url: String
    var mInstance: WXSDKInstance? = null
    private var mWxAnalyzerDelegate: WXAnalyzerDelegate? = null
    private var mWXHandler: Handler? = null

    //调试广播
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var filter: IntentFilter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val u = router?.url
        if (u == null) {
            Logger.e("url不能为空")
        } else {
            url = u
            mWXHandler = Handler(this)
            HotRefreshManager.getInstance().setHandler(mWXHandler)
            render()
            mInstance?.onActivityCreate()
//            registerBroadcastReceiver()

            mWxAnalyzerDelegate = WXAnalyzerDelegate(activity)
            mWxAnalyzerDelegate?.onCreate()


        }
    }

    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.what) {
            HotRefreshManager.HOT_REFRESH_CONNECT -> HotRefreshManager.getInstance().connect(msg.obj.toString())
            HotRefreshManager.HOT_REFRESH_DISCONNECT -> HotRefreshManager.getInstance().disConnect()
            HotRefreshManager.HOT_REFRESH_REFRESH -> refreshWeex()
            HotRefreshManager.HOT_REFRESH_CONNECT_ERROR -> Logger.e("hot refresh connect error!")
        }

        return false
    }

    open fun refreshWeex() {
        render()
    }

    private fun render() {
        mInstance
        mInstance?.destroy()
        mInstance = null
        val renderContainer = RenderContainer(activity)
        mInstance = WXSDKInstance(activity)
        mInstance?.setRenderContainer(renderContainer)
        mInstance?.registerRenderListener(this)
        mInstance?.setNestedInstanceInterceptor(this)
        mInstance?.isTrackComponent = true
        view!!.post {
            try {
                if (url.startsWith("http")) {
                    // 下载
                    mInstance?.renderByUrl(url, url, null, null, WXRenderStrategy
                            .APPEND_ASYNC)
                } else {
                    val file = UpdateManager.getFullUrl(url)
                    val template = FileUtils.readFileToString(file)
//                    mInstance?.render(url, WXFileUtils.loadAsset(url, context), null, null, WXRenderStrategy.APPEND_ASYNC)
                    mInstance?.render(url, template, null, null, WXRenderStrategy.APPEND_ASYNC)
                }
            } catch (e: IOException) {
                Logger.e(e, "")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mWxAnalyzerDelegate?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mInstance?.onActivityResume()
        mWxAnalyzerDelegate?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mInstance?.onActivityPause()
        mWxAnalyzerDelegate?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mInstance?.onActivityStop()
        mWxAnalyzerDelegate?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mInstance?.onActivityDestroy()
        mWXHandler?.obtainMessage(HotRefreshManager.HOT_REFRESH_DISCONNECT)?.sendToTarget()
//        unregisterBroadcastReceiver()
        mWxAnalyzerDelegate?.onDestroy()
    }

    override fun onRefreshSuccess(instance: WXSDKInstance?, width: Int, height: Int) {
        Logger.d("Update Finish...")
    }

    override fun onException(instance: WXSDKInstance?, errCode: String?, msg: String?) {
        mWxAnalyzerDelegate?.onException(instance, errCode, msg)
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
        mWxAnalyzerDelegate?.onWeexRenderSuccess(instance)
        Logger.d("Render Finish...width:" + width + "   ,height="+height)
    }

    override fun onCreateNestInstance(instance: WXSDKInstance?, container: NestedContainer?) {
        Logger.d("Nested Instance created.")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mInstance?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mInstance?.onActivityResult(requestCode, resultCode, data)
    }

    private fun degradeAlert(errMsg: String) {
        AlertDialog.Builder(activity)
                .setTitle("Downgrade success")
                .setMessage(errMsg)
                .setPositiveButton("OK", null)
                .show()

    }

    override fun onViewCreated(instance: WXSDKInstance?, view: View?) {
        var wxView = view
        val wrappedView = mWxAnalyzerDelegate?.onWeexViewCreated(instance, view)
        if (wrappedView != null) {
            wxView = wrappedView
        }

        onAddWeexView(wxView)
//        if (wxView.parent == null) {
//            mContainer.addView(wxView)
//        }
//        mContainer.requestLayout()

        Logger.d("renderSuccess")
    }

    abstract fun onAddWeexView(wxView: View?);

    //调试广播
    inner class DefaultBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH == intent.action) {
                refreshWeex()
            } else if (WXSDKEngine.JS_FRAMEWORK_RELOAD == intent.action) {
                refreshWeex()
            }
        }
    }

    fun registerWeexDebugBroadcast() {
        if (mBroadcastReceiver == null){
            mBroadcastReceiver = DefaultBroadcastReceiver()
        }

        if (filter == null) {
            filter = IntentFilter()
            filter!!.addAction(IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH)
            filter!!.addAction(WXSDKEngine.JS_FRAMEWORK_RELOAD)
        }

        LocalBroadcastManager.getInstance(activity!!.applicationContext)
                .registerReceiver(mBroadcastReceiver!!, filter!!)
    }

    fun unregisterWeexDebugBroadcast() {
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(activity!!.applicationContext)
                    .unregisterReceiver(mBroadcastReceiver!!)
            mBroadcastReceiver = null
        }
    }
}