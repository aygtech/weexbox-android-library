package com.weexbox.core.controller

import android.annotation.TargetApi
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import com.weexbox.core.R
import com.weexbox.core.webview.SonicJavaScriptInterface
import com.weexbox.core.webview.SonicRuntimeImpl
import com.weexbox.core.webview.SonicSessionClientImpl
import kotlinx.android.synthetic.main.activity_web_view.*

/**
 * Author:leon.wen
 * Time:2018/10/20   19:00
 * Description:This is WBWebViewActivity
 */
open class WBWebViewActivity : WBBaseActivity() {
    var url: String? = ""
    private var sonicSession: SonicSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = router.url
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(SonicRuntimeImpl(application), SonicConfig.Builder().build())
        }

        var sonicSessionClient: SonicSessionClientImpl? = SonicSessionClientImpl()
        val sessionConfigBuilder = SonicSessionConfig.Builder()
        sessionConfigBuilder.setSupportLocalServer(true)
        // create sonic session and run sonic flow
        sonicSession = SonicEngine.getInstance().createSession(url!!, sessionConfigBuilder.build())
        if (null != sonicSession) {
            sonicSession!!.bindClient(sonicSessionClient)
        } else {

            Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_LONG).show()
        }

        setContentView(R.layout.activity_web_view)
        getActionbar().setBackButton(View.OnClickListener {
            finish()
        }, "关闭")
        val webView = webView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val title = view.title
                if (!TextUtils.isEmpty(title)) {
                    getActionbar().setTitleText(title)
                }
                if (sonicSession != null) {
                    sonicSession!!.sessionClient.pageFinish(url)
                }
            }

            @TargetApi(21)
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                return shouldInterceptRequest(view, request.url.toString())
            }

            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                return sonicSessionClient?.requestResource(url) as WebResourceResponse?
            }
        }

        val webSettings = webView.settings

        webSettings.javaScriptEnabled = true
        webView.removeJavascriptInterface("searchBoxJavaBridge_")
        webView.addJavascriptInterface(SonicJavaScriptInterface(sonicSessionClient, Intent()), "sonic")

        // init webview settings
        webSettings.allowContentAccess = true
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.savePassword = false
        webSettings.saveFormData = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true


        // webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView)
            sonicSessionClient.clientReady()
        } else { // default mode
            webView.loadUrl(url)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (null != sonicSession) {
            sonicSession!!.destroy()
            sonicSession = null
        }
        super.onDestroy()
    }
}
