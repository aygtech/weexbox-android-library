package com.weexbox.core.controller

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.taobao.weex.WXEnvironment
import com.taobao.weex.WXSDKEngine
import com.weexbox.core.R
import com.weexbox.core.event.Event
import com.weexbox.core.event.EventCallback
import com.weexbox.core.extension.getParameters
import com.weexbox.core.router.Router
import com.weexbox.core.util.ActivityManager
import com.weexbox.core.util.LoadDialogHelper
import com.weexbox.core.util.SelectImageUtil
import com.weexbox.core.widget.SimpleToolbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * Author: Mario
 * Time: 2018/8/14 下午6:39
 * Description: This is WBBaseActivity
 */

open class WBBaseActivity : AppCompatActivity() {

    // 路由
    var router = Router()
    // 通用事件
    var events: MutableMap<String, EventCallback> = TreeMap()
    //导航栏
    lateinit var toolbar: SimpleToolbar
    //hud
    var loadDialogHelper: LoadDialogHelper = LoadDialogHelper(this)

    // 通用事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        val callback = events[event.name]
        callback?.invoke(event.info)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
        ActivityManager.getInstance().addActivity(this)
        val intentRouter = intent.getSerializableExtra(Router.EXTRA_NAME) as Router?
        if (intentRouter != null) {
            router = intentRouter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        loadDialogHelper.clear()
        ActivityManager.getInstance().removeActivity(this)
    }

    override fun onBackPressed() {
        val fragment = getFragment()
        if (fragment != null && fragment.isListenBack) {
            fragment.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    fun getFragment(): WBBaseFragment? {
        val fragments = supportFragmentManager.fragments
        return getRecursionFragment(fragments)
    }

    private fun getRecursionFragment(fragments: List<Fragment>): WBBaseFragment? {
        for (recursionFragment in fragments) {
            if (recursionFragment is WBBaseFragment && recursionFragment.isVisibleToUser) {
                return recursionFragment
            } else if (recursionFragment.childFragmentManager.fragments.size > 0) {
                getRecursionFragment(recursionFragment.childFragmentManager.fragments)
            }
        }
        return null
    }

    override fun setContentView(layoutResID: Int) {
        val view = layoutInflater.inflate(layoutResID, null)
        setContentView(view)
    }

    override fun setContentView(view: View) {
        val container = layoutInflater.inflate(R.layout.activity_base, null) as LinearLayout
        toolbar = layoutInflater.inflate(R.layout.activity_weex_title_layout, container, false) as SimpleToolbar
        container.addView(toolbar, 0)
        container.addView(view, 1)

        toolbar.setBackButton { finish() }
        if (!(router.navBarHidden)) {
            toolbar.setAcitionbarAndStatusbarVisibility(View.VISIBLE)
        } else {
            toolbar.setAcitionbarAndStatusbarVisibility(View.GONE)
        }

        super.setContentView(view)
    }

    fun getActionbar(): SimpleToolbar {
        return toolbar
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
                    openWeex(result.contents)
                }
            }
        }
    }

    /**
     * 处理devtool返回的DebugProxyUrl,WX启动devtool模式
     * @param code
     */
    private fun openWeex(url: String) {
        // 处理windows上的dev路径带有"\\"
        val parameters = url.replace("\\", "/").getParameters()
        val devtoolUrl = parameters["_wx_devtool"]
        val tplUrl = parameters["_wx_tpl"]
        if (devtoolUrl != null) {
            // 连服务
            WXEnvironment.sRemoteDebugProxyUrl = devtoolUrl
            WXEnvironment.sDebugServerConnectable = true
            WXSDKEngine.reload()
        } else if (tplUrl != null) {
            // 连页面
            val router = Router()
            router.name = Router.NAME_WEEX
            router.url = tplUrl
            router.open(this)
        }
    }

}