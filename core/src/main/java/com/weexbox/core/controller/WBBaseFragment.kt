package com.weexbox.core.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.taobao.weex.WXEnvironment
import com.taobao.weex.WXSDKEngine
import com.weexbox.core.router.Router

open class WBBaseFragment: Fragment() {

    var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = arguments?.getSerializable(Router.extraName) as Router?
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == IntentIntegrator.REQUEST_CODE) {
//            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//            if (result != null) {
//                if (result.contents == null) {
//                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
//                } else {
//                    handleDecodeInternally(result.contents)
//                }
//            }
//        }
//    }

    /**
     * 处理devtool返回的DebugProxyUrl,WX启动devtool模式
     * @param code
     */
    private fun handleDecodeInternally(code: String) {
        if (!TextUtils.isEmpty(code)) {
            var uri = Uri.parse(code)
            if (uri.queryParameterNames.contains("bundle")) {
                WXEnvironment.sDynamicMode = uri.getBooleanQueryParameter("debug", false)
                WXEnvironment.sDynamicUrl = uri.getQueryParameter("bundle")
                val tip = if (WXEnvironment.sDynamicMode) "Has switched to Dynamic Mode" else "Has switched to Normal Mode"
                Toast.makeText(context, tip, Toast.LENGTH_SHORT).show()
                activity?.finish()
                return
            } else if (uri.queryParameterNames.contains("_wx_devtool")) {
                WXEnvironment.sRemoteDebugProxyUrl = uri.getQueryParameter("_wx_devtool")
                WXEnvironment.sDebugServerConnectable = true
                WXSDKEngine.reload()
                Toast.makeText(context, "devtool", Toast.LENGTH_SHORT).show()
                return
            } else if (code.contains("_wx_debug")) {
                uri = Uri.parse(code)
                val debug_url = uri.getQueryParameter("_wx_debug")
                // todo 新的weexCore没有这个方法
//                switchDebugModel(true, debug_url)
                activity?.finish()
            } else {
                refreshWeexUrl(code)
            }
        }
    }

    /**
     * 处理weexdebug 模式下，扫描单个weex.vue时的刷新界面
     * @param code vue的路径
     */
    fun refreshWeexUrl(code: String) {
        Toast.makeText(context, code, Toast.LENGTH_SHORT).show()
        val intent = Intent("com.alibaba.weex.protocol.openurl")
        intent.setPackage(context!!.packageName)
        intent.data = Uri.parse(code)
        startActivity(intent)
    }
}