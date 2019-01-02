package com.weexbox.core.controller

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.orhanobut.logger.Logger

import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import com.weexbox.core.R
import com.weexbox.core.interfaces.WebViewSetInterface
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

    private var sonicSession: SonicSession? = null


    companion object {
        var webViewSetInterface: WebViewSetInterface? = null //自定义webview设置接口
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (router.url == null) {
            Logger.e("url不能为空")
            return
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(SonicRuntimeImpl(application), SonicConfig.Builder().build())
        }

        var sonicSessionClient: SonicSessionClientImpl? = null

        // step 2: Create SonicSession
        sonicSession = SonicEngine.getInstance().createSession(router.url!!, SonicSessionConfig.Builder().build())
        if (null != sonicSession) {
            sonicSessionClient = SonicSessionClientImpl()
            sonicSession!!.bindClient(sonicSessionClient)
        }

        // step 3: BindWebView for sessionClient and bindClient for SonicSession
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        setContentView(R.layout.activity_web_view)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                if (router.title == null && !view.title.isNullOrEmpty()) {
                    getActionbar().setTitleText(view.title)
                }

                if (sonicSession != null) {
                    sonicSession!!.sessionClient.pageFinish(url)
                }
            }

            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                return sonicSessionClient?.requestResource(request.url.toString()) as WebResourceResponse?
            }
        }

        val webSettings = webView.settings

        // step 4: bind javascript
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
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

        if (webViewSetInterface != null){
            webViewSetInterface!!.setWebViwe(webView)
        }
        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView)
            sonicSessionClient.clientReady()
        } else { // default mode
            webView.loadUrl(router.url)
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
        sonicSession?.destroy()
        sonicSession = null

        super.onDestroy()
    }
}
