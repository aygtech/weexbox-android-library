package com.weexbox.core.util

import com.orhanobut.logger.Logger
import com.weexbox.core.event.Event
import com.weexbox.core.extension.toJsonMap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*


/**
 * Author: Mario
 * Time: 2019/4/13 2:26 PM
 * Description: This is HotReload
 */

object HotReload {

    private val hotReloadSocket = OkHttpClient()
    private lateinit var request: Request
    private var isReconnect = false
    private val listener = object : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Logger.d("连接关闭：$reason")
            reconnect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Logger.e("连接失败：${t.localizedMessage}")
            reconnect()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val option = text.toJsonMap()
            val method = option["method"] as String
            if (method == "WXReloadBundle") {
                Event.emit(method, option)
            }
        }
    }

    fun start(ws: String) {
        request = Request.Builder().url(ws).build()
        hotReloadSocket.newWebSocket(request, listener)
    }

    fun reconnect() {
        if (isReconnect) {
            return
        }
        isReconnect = true
        GlobalScope.launch {
            delay(2000)
            hotReloadSocket.newWebSocket(request, listener)
            isReconnect = false
        }
    }
}
