package com.weexbox.core.network

import com.alibaba.fastjson.JSON
import com.taobao.weex.http.Status
import com.weexbox.core.extension.toJsonString
import com.weexbox.core.model.Result
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Author: Mario
 * Time: 2018/9/11 下午4:46
 * Description: This is Network
 */

object Network {

    enum class HTTPMethod {
        GET,
        POST
    }

    interface Service {
        @GET
        fun methodGet(@Url url: String, @QueryMap parameters: Map<String, Any>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @FormUrlEncoded
        @POST
        fun methodPost(@Url url: String, @FieldMap parameters:  Map<String, Any>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @POST
        fun methodJson(@Url url: String, @Body body: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>
    }

    val client: OkHttpClient = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
    var mediaTypeJSON: MediaType?=  MediaType.parse("application/json")

    fun call(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Map<String, Any> = TreeMap(), headers: Map<String, String> = TreeMap()): Call<ResponseBody> {
        val retrofit = Retrofit.Builder().baseUrl(url + File.separator).client(client).build()
        val service = retrofit.create(Service::class.java)
        if (method == HTTPMethod.POST) {
            val contentType = headers["Content-Type"]
            if (contentType != null && contentType.contains("application/json")) {
                val body = RequestBody.create(mediaTypeJSON, parameters.toJsonString())
                return service.methodJson(url, body, headers)
            } else {
                return service.methodPost(url, parameters, headers)
            }
        } else {
            return service.methodGet(url, parameters, headers)
        }
    }

    fun request(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Map<String, Any>? = null, headers: Map<String, String>? = null, callback: Callback<ResponseBody>) {
        val call = call(url, method, parameters ?: TreeMap(), headers ?: TreeMap())
        call.enqueue(callback)
    }
}