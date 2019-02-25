package com.weexbox.core.adapter

import com.orhanobut.logger.Logger
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.squareup.okhttp.ws.WebSocket
import com.squareup.okhttp.ws.WebSocketCall
import com.squareup.okhttp.ws.WebSocketListener
import com.taobao.weex.appfram.websocket.IWebSocketAdapter
import com.taobao.weex.appfram.websocket.WebSocketCloseCodes
import okio.Buffer
import okio.BufferedSource
import java.io.EOFException
import java.io.IOException
import java.util.HashMap

/**
 * Author: Mario
 * Time: 2019/2/25 4:03 PM
 * Description: This is WebSocketAdapter
 */

class WebSocketAdapter: IWebSocketAdapter {

    private var ws: WebSocket? = null
    private var eventListener: IWebSocketAdapter.EventListener? = null

    override fun connect(url: String?, protocol: String?, listener: IWebSocketAdapter.EventListener?) {
        this.eventListener = listener
        val okHttpClient = OkHttpClient()
        val builder = Request.Builder()
        if (protocol != null) {
            builder.addHeader(IWebSocketAdapter.HEADER_SEC_WEBSOCKET_PROTOCOL, protocol)
        }
        builder.url(url)
        val wsRequest = builder.build()
        val webSocketCall = WebSocketCall.create(okHttpClient, wsRequest)
        try {
            val field = WebSocketCall::class.java.getDeclaredField("request")
            field.isAccessible = true
            val realRequest = field.get(webSocketCall) as Request
            val wsHeaders = realRequest.headers()
            val headers = HashMap<String, String>()
            for (name in wsHeaders.names()) {
                headers[name] = wsHeaders.values(name).toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        webSocketCall.enqueue(object : WebSocketListener {
            @Throws(IOException::class)
            override fun onOpen(webSocket: WebSocket, request: Request, response: Response) {
                ws = webSocket
                eventListener?.onOpen()
                val wsHeaders = response.headers()
                val headers = HashMap<String, String>()
                for (name in wsHeaders.names()) {
                    headers[name] = wsHeaders.values(name).toString()
                }
            }

            @Throws(IOException::class)
            override fun onMessage(payload: BufferedSource, type: WebSocket.PayloadType) {
                if (type != WebSocket.PayloadType.BINARY) {
                    val message = payload.readUtf8()
                    eventListener?.onMessage(message)
                }
                payload.close()
            }

            override fun onPong(payload: Buffer) {

            }

            override fun onClose(code: Int, reason: String) {
                eventListener?.onClose(code, reason, true)
            }

            override fun onFailure(e: IOException) {
                e.printStackTrace()
                if (e is EOFException) {
                    eventListener?.onClose(WebSocketCloseCodes.CLOSE_NORMAL.code, WebSocketCloseCodes.CLOSE_NORMAL.name, true)
                } else {
                    eventListener?.onError(e.message)
                }
            }
        })
    }

    override fun send(data: String?) {
        if (ws != null) {
            try {
                val buffer = Buffer().writeUtf8(data ?: "")
                ws!!.sendMessage(WebSocket.PayloadType.TEXT, buffer.buffer())
                buffer.flush()
                buffer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Logger.e("WebSocket is not ready")
        }
    }

    override fun close(code: Int, reason: String?) {
        if (ws != null) {
            try {
                ws!!.close(code, reason)
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.e(e.message ?: "")
            }

        }
    }

    override fun destroy() {
        if (ws != null) {
            try {
                ws!!.close(WebSocketCloseCodes.CLOSE_GOING_AWAY.code, WebSocketCloseCodes.CLOSE_GOING_AWAY.name)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}