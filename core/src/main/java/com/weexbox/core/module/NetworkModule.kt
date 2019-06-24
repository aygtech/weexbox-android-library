package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.extension.toJsonMap
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.network.Network
import okhttp3.*
import java.io.IOException


/**
 * Author: Mario
 * Time: 2018/9/11 下午7:41
 * Description: This is NetworkModule
 */

open class NetworkModule : BaseModule() {

    @JSMethod(uiThread = false)
    open fun request(options: Map<String, Any>, callback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        var method = Network.HTTPMethod.GET
        if (info.method?.toUpperCase() == "POST") {
            method = Network.HTTPMethod.POST
        }
        val result = Result()

//        Network.request(info.url!!, method, info.params, info.headers, object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                result.status = response.code()
//                val data = response.body()?.string()
//                if (data != null) {
//                    if (info.responseType?.toUpperCase() == "TEXT") {
//                        result.data["data"] = data
//                    } else {
//                        result.data = data.toJsonMap()
//                    }
//                }
//                result.error = response.errorBody()?.string()
//                callback(result)
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                result.status = Result.error
//                result.error = t.message
//                callback(result)
//            }
//        })

//        info.url!!, method, info.params, info.headers


        val mOkHttpClient = OkHttpClient()
        var formEncodingBuilder = FormBody.Builder()
        if (info.params != null && info?.params?.size!! > 0) {
            for (param in info?.params!!) {
                formEncodingBuilder.add(param.key, param.value.toString())
            }
        }

        var requestBuilder = Request.Builder()
        if (info.headers != null && info?.headers?.size!! > 0) {
            for (param in info?.headers!!) {
                requestBuilder.addHeader(param.key, param.value)
            }
        }

        var request: Request? = null
        if (info.method?.toUpperCase() == "POST") {
            request = requestBuilder.url(info.url!!)
                    .post(formEncodingBuilder.build())
                    .build()
        } else{
            request = requestBuilder.url(info.url!!)
                    .get()
                    .build()
        }

        val call = mOkHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result.status = Result.error
                result.error = e.message
                callback(result)
            }

            override fun onResponse(call: Call, response: Response) {
                result.status = response.code()
                val data = response.body()?.string()
                if (data != null) {
                    if (info.responseType?.toUpperCase() == "TEXT") {
                        result.data["data"] = data
                    } else {
                        result.data = data.toJsonMap()
                    }
                }
                result.error = response.message()
                callback(result)
            }
        })
    }
}
