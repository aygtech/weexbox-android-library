package com.weexbox.core.adapter

import com.taobao.weex.appfram.websocket.IWebSocketAdapter
import com.taobao.weex.appfram.websocket.IWebSocketAdapterFactory

/**
 * Author: Mario
 * Time: 2019/2/25 4:01 PM
 * Description: This is WebSocketAdapterFactory
 */

class WebSocketAdapterFactory: IWebSocketAdapterFactory {

    override fun createWebSocketAdapter(): IWebSocketAdapter {
        return WebSocketAdapter()
    }
}