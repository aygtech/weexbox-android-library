package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import com.weexbox.core.extension.toMap
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.network.Network
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Author: Mario
 * Time: 2018/9/11 下午7:41
 * Description: This is NetworkModule
 */

class NetworkModule : BaseModule() {

    @JSMethod(uiThread = false)
    fun request(options: Map<String, Any>, callback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        var method = Network.HTTPMethod.GET
        if (info.method == "post") {
            method = Network.HTTPMethod.POST
        }
        val result = Result()

        Network.request(info.url!!, method, info.params, info.headers, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                result.code = response.code()
                val data = response.body()?.string()
                if (data != null) {
                    result.data = data.toMap()
                }
                result.error = response.errorBody()?.string()
                callback(result)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                result.code = Result.error
                result.error = t.message
                callback(result)
            }
        })
    }
}
