package com.weexbox.core.util

import com.orhanobut.logger.Logger
import com.weexbox.core.event.Event
import com.weexbox.core.extension.toJsonMap
import okhttp3.*


/**
 * Author: Mario
 * Time: 2019/4/13 2:26 PM
 * Description: This is HotReload
 */

object HotReload {

    var isStart: Boolean = false

    fun start(ws: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(ws).build()
        client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                isStart = true
                Logger.d("已连接：$ws")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Logger.e("连接失败：${t.localizedMessage}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val option = text.toJsonMap()
                val method = option["method"] as String
                if (method == "WXReloadBundle") {
                    Event.emit(method, option)
                }
            }
        })
    }
}
