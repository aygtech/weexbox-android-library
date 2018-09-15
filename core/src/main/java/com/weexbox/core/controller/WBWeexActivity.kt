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
import com.weexbox.core.https.HotRefreshManager
import com.weexbox.core.https.HotRefreshManager.*
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.WXAnalyzerDelegate
import java.io.IOException

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBWeexActivity
 */

open class WBWeexActivity : WBBaseActivity(), IWXRenderListener, WXSDKInstance.NestedInstanceInterceptor, Handler.Callback, WBBaseActivity.HaveFragmentListener {

    open lateinit var url: String
    private var mInstance: WXSDKInstance? = null
    private var mWxAnalyzerDelegate: WXAnalyzerDelegate? = null
    private var mWXHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFormat(PixelFormat.TRANSLUCENT)

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

            mWxAnalyzerDelegate = WXAnalyzerDelegate(this)
            mWxAnalyzerDelegate?.onCreate()

            setHaveFragmentListener(this)
        }
    }

    private fun render() {
        mInstance?.destroy()
        mInstance = null
        val renderContainer = RenderContainer(this)
        mInstance = WXSDKInstance(this)
        mInstance?.setRenderContainer(renderContainer)
        mInstance?.registerRenderListener(this)
        mInstance?.setNestedInstanceInterceptor(this)
        mInstance?.isTrackComponent = true
//        mContainer.post {
        val outRect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(outRect)
        try {
            if (url.startsWith("http")) {
                // 下载
            } else {
                val file = UpdateManager.getFullUrl(url)
                val template = FileUtils.readFileToString(file)
                mInstance?.render(url, template, null, null, WXRenderStrategy.APPEND_ASYNC)
            }
        } catch (e: IOException) {
            Logger.e(e, "")
        }
//        }
    }

    override fun refreshFragmentWeex() {
        render()
    }

    /**
     * hot refresh
     */
//    private fun startHotRefresh() {
//        try {
//            val host = URL(mUri.toString()).host
//            val wsUrl = "ws://$host:8082"
//            mWXHandler?.obtainMessage(HOT_REFRESH_CONNECT, 0, 0, wsUrl)?.sendToTarget()
//        } catch (e: MalformedURLException) {
//            e.printStackTrace()
//        }
//
//    }

//    private fun registerBroadcastReceiver() {
//        mReceiver = RefreshBroadcastReceiver()
//        val filter = IntentFilter()
//        filter.addAction(IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH)
//        filter.addAction(IWXDebugProxy.ACTION_INSTANCE_RELOAD)
//
//        registerReceiver(mReceiver, filter)
//    }

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
        mWXHandler?.obtainMessage(HOT_REFRESH_DISCONNECT)?.sendToTarget()
//        unregisterBroadcastReceiver()
        mWxAnalyzerDelegate?.onDestroy()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return mWxAnalyzerDelegate != null && mWxAnalyzerDelegate!!.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event)
    }

    override fun onBackPressed() {
        if (!mInstance!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onViewCreated(instance: WXSDKInstance, view: View) {
        var wxView = view
        val wrappedView = mWxAnalyzerDelegate?.onWeexViewCreated(instance, view)
        if (wrappedView != null) {
            wxView = wrappedView
        }
//        if (wxView.parent == null) {
//            mContainer.addView(wxView)
//        }
//        mContainer.requestLayout()
        Logger.d("renderSuccess")
    }

    override fun onRenderSuccess(instance: WXSDKInstance?, width: Int, height: Int) {
        mWxAnalyzerDelegate?.onWeexRenderSuccess(instance)
        Logger.d("Render Finish...")
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
                Toast.makeText(applicationContext, "errCode:$errCode Render ERROR:$msg", Toast.LENGTH_SHORT).show()
            }
        }
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

    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.what) {
            HOT_REFRESH_CONNECT -> HotRefreshManager.getInstance().connect(msg.obj.toString())
            HOT_REFRESH_DISCONNECT -> HotRefreshManager.getInstance().disConnect()
            HOT_REFRESH_REFRESH -> refreshWeex()
            HOT_REFRESH_CONNECT_ERROR -> Logger.e("hot refresh connect error!")
        }

        return false
    }

    private fun degradeAlert(errMsg: String) {
        AlertDialog.Builder(this)
                .setTitle("Downgrade success")
                .setMessage(errMsg)
                .setPositiveButton("OK", null)
                .show()

    }

//    inner class RefreshBroadcastReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            if (IWXDebugProxy.ACTION_INSTANCE_RELOAD == intent.action || IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH == intent.action) {
//                // String myUrl = intent.getStringExtra("url");
//                // Log.e("WXPageActivity", "RefreshBroadcastReceiver reload onReceive ACTION_DEBUG_INSTANCE_REFRESH mBundleUrl:" + myUrl + " mUri:" + mUri);
//
//                Log.v(TAG, "connect to debug server success")
//                if (mUri != null) {
//                    if (TextUtils.equals(mUri.getScheme(), "http") || TextUtils.equals(mUri.getScheme(), "https")) {
//                        val weexTpl = mUri.getQueryParameter(Constants.WEEX_TPL_KEY)
//                        val url = if (TextUtils.isEmpty(weexTpl)) mUri.toString() else weexTpl
//                        // Log.e("WXPageActivity", "loadWXfromService reload url:" + url);
//                        loadWXfromService(url)
//                    } else {
//                        // Log.e("WXPageActivity", "loadWXfromLocal reload from local url:" + mUri.toString());
//                        loadWXfromLocal(true)
//                    }
//                }
//            }
//        }
//    }
}

