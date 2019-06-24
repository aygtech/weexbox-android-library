package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.extension.toJsonMap
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.network.Network
import okhttp3.ResponseBody
import retrofit2.Response
import android.widget.Toast
import android.R.string
import android.R.attr.tag
import android.util.Log
import com.litesuits.common.utils.HandlerUtil.runOnUiThread
import com.squareup.okhttp.Callback
import com.weexbox.core.okhttp.OkHttpUtils.post
import com.squareup.okhttp.FormEncodingBuilder
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.weexbox.core.util.TaskManager
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


        TaskManager.execAsynTask {
            val mOkHttpClient = OkHttpClient()
            var formEncodingBuilder = FormEncodingBuilder()
            if(info.params != null && info?.params?.size!! > 0){
                for (param in info?.params!!){
                    formEncodingBuilder.add(param.key, param.value.toString())
                }
            }

            var requestBuilder = Request.Builder()
            if(info.headers != null && info?.headers?.size!! > 0){
                for (param in info?.headers!!){
                    requestBuilder.addHeader(param.key, param.value)
                }
            }
            var request = requestBuilder.url(info.url!!)
                    .post(formEncodingBuilder.build())
                    .build()


            val call = mOkHttpClient.newCall(request)
            call.enqueue(object : Callback {

                override fun onResponse(response: com.squareup.okhttp.Response?) {
                    val str = response?.body()!!.toString()
                    Log.i("yzw", str)
                    runOnUiThread(Runnable { Toast.makeText(getActivity(), "请求成功", Toast.LENGTH_SHORT).show() })
                }

                override fun onFailure(request: Request, e: IOException) {
                    Log.d("输出:", "onFailure: " + e.message);
                }
            })

        }
    }
}
