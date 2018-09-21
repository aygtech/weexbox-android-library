package com.weexbox.core.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.taobao.weex.WXEnvironment
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.WXSDKInstance
import com.weexbox.core.R
import com.weexbox.core.event.Event
import com.weexbox.core.event.EventCallback
import com.weexbox.core.router.Router
import com.weexbox.core.util.ActivityManager
import com.weexbox.core.util.EventBusUtil
import com.weexbox.core.util.SelectImageUtil
import com.weexbox.core.widget.SimpleToolbar
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBBaseActivity
 */

open class WBBaseActivity : AppCompatActivity() {

    var router: Router? = null
    lateinit var toolbar: SimpleToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.getInstance().addActivity(this)
        router = intent.getSerializableExtra(Router.EXTRA_NAME) as Router?
        if (router == null){
            router = Router()
        }
        if (isRegisterEventBus() && inCreateRegisterEventBus()) {
            EventBusUtil.register(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isRegisterEventBus() && !inCreateRegisterEventBus()) {
            EventBusUtil.register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isRegisterEventBus() && !inCreateRegisterEventBus()) {
            EventBusUtil.unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().removeActivity(this)
        if (isRegisterEventBus() && inCreateRegisterEventBus()) {
            EventBusUtil.unregister(this)
        }
    }

    override fun setContentView(layoutResID: Int) {
        val view = layoutInflater.inflate(layoutResID, null)
        setContentView(view)
    }

    override fun setContentView(view: View) {
        if (view is ViewGroup) {
            if (router != null){
                toolbar = layoutInflater.inflate(R.layout.activity_weex_title_layout, view, false) as SimpleToolbar
                view.addView(toolbar, 0)
                toolbar.setBackButton(View.OnClickListener {
                    finish()
                })

                if (!(router!!.navBarHidden)){
                    toolbar.setAcitionbarAndStatusbarVisibility(View.VISIBLE)
                } else{
                    toolbar.setAcitionbarAndStatusbarVisibility(View.GONE)
                }
            }
        }
        super.setContentView(view)
    }

    fun getActionbar(): SimpleToolbar{
        return toolbar;
    }

    fun isRegisterEventBus(): Boolean {
        return false
    }

    fun inCreateRegisterEventBus(): Boolean {
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SelectImageUtil.onActivityResult(requestCode, resultCode, data, this)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    handleDecodeInternally(result.contents)
                }
            }
        }
    }

    /**
     * 处理devtool返回的DebugProxyUrl,WX启动devtool模式
     * @param code
     */
    private fun handleDecodeInternally(url: String) {
        val code = url.replace("\\", "/")
        if (!TextUtils.isEmpty(code)) {
            var uri = Uri.parse(code)
            if (uri.queryParameterNames.contains("bundle")) {
                WXEnvironment.sDynamicMode = uri.getBooleanQueryParameter("debug", false)
                WXEnvironment.sDynamicUrl = uri.getQueryParameter("bundle")
                val tip = if (WXEnvironment.sDynamicMode) "Has switched to Dynamic Mode" else "Has switched to Normal Mode"
                Toast.makeText(applicationContext, tip, Toast.LENGTH_SHORT).show()
                finish()
                return
            } else if (uri.queryParameterNames.contains("_wx_devtool")) {   //调试debug（当前页面是weex页面）
                WXEnvironment.sRemoteDebugProxyUrl = uri.getQueryParameter("_wx_devtool")
                WXEnvironment.sDebugServerConnectable = true
                WXSDKEngine.reload()
                Toast.makeText(applicationContext, "devtool", Toast.LENGTH_SHORT).show()
                return
            } else if (code.contains("_wx_debug")) {
                uri = Uri.parse(code)
                val debug_url = uri.getQueryParameter("_wx_debug")
                // todo 新的weexCore没有这个方法
                switchDebugModel(true, debug_url)
                finish()
            } else {    //调试debug（当前页面是原生页面）
                refreshWeexUrl(code)
            }
        }
    }

    /**
     * 处理weexdebug 模式下，扫描单个weex.vue时的刷新界面
     * @param code vue的路径
     */
    fun refreshWeexUrl(code: String) {
        Toast.makeText(applicationContext, code, Toast.LENGTH_SHORT).show()
        val intent = Intent("com.weexbox.core.controller.openurl")
        intent.setPackage(applicationContext!!.packageName)
        var router = Router()
        router.url = code
        intent.putExtra(Router.EXTRA_NAME, router);
        startActivity(intent)
    }

    fun switchDebugModel(debug: Boolean, debugUrl: String) {
        if (!WXEnvironment.isApkDebugable()) {
            return
        }
        if (debug) {
            WXEnvironment.sDebugMode = true
            WXEnvironment.sDebugWsUrl = debugUrl
            try {
                val cls = Class.forName("com.taobao.weex.WXDebugTool")
                val m = cls.getMethod("connect", String::class.java)
                m.invoke(cls, debugUrl)
            } catch (e: Exception) {
                Log.d("weex", "WXDebugTool not found!")
            }

        } else {
            WXEnvironment.sDebugMode = false
            WXEnvironment.sDebugWsUrl = null
            try {
                val cls = Class.forName("com.taobao.weex.WXDebugTool")
                val m = cls.getMethod("close")
                m.invoke(cls)
            } catch (e: Exception) {
                Log.d("weex", "WXDebugTool not found!")
            }
        }
    }



    interface HaveFragmentListener {
        fun refreshFragmentWeex()
    }
    lateinit var listener: HaveFragmentListener
    fun setHaveFragmentListener(haveFragmentListener: HaveFragmentListener){
        listener = haveFragmentListener
    }

    fun refreshWeex(){
        if (listener != null){
            listener.refreshFragmentWeex()
        }
    }

}