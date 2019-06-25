package com.weexbox.core.util

import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.event.Event
import com.weexbox.core.extension.toJsonMap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*


/**
 * Author: Mario
 * Time: 2019/4/13 2:26 PM
 * Description: This is HotReload
 */

object HotReload {

    private var isConnect = false
    var url: String? = null
    private val listener = object : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            connect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            connect()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val option = text.toJsonMap()
            val method = option["method"] as String
            if (method == "WXReloadBundle") {
                Event.emit(method, option)
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            MainScope().launch {
                ToastUtil.showLongToast(WeexBoxEngine.application, "热重载开启")
            }
        }
    }

    fun open(url: String) {
        HotReload.url = url
        connect()
    }

    fun connect() {
        if (isConnect) {
            return
        }
        isConnect = true
        GlobalScope.launch {
            delay(2000)
            isConnect = false
            val okHttpClient = OkHttpClient.Builder().build()
            val request = Request.Builder().url(url!!).build()
            okHttpClient.newWebSocket(request, listener)
        }
    }
}
