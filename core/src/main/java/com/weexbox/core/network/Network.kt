package com.weexbox.core.network

import com.weexbox.core.extension.appendingPathComponent
import com.weexbox.core.extension.toJsonString
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
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

    var server: String? = null

    enum class HTTPMethod {
        GET,
        POST
    }

    @JvmSuppressWildcards
    interface Service {
        @GET
        fun methodGet(@Url url: String, @QueryMap parameters: Map<String, Any>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

//        @FormUrlEncoded
//        @POST
//        fun methodPost(@Url url: String, @FieldMap parameters:  Map<String, Any>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @POST
        fun methodJson(@Url url: String, @Body body: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>
    }

    val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
    var mediaTypeJSON: MediaType? = MediaType.parse("application/json")

    private fun call(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Map<String, Any>, headers: Map<String, String>): Call<ResponseBody> {
        val retrofit = Retrofit.Builder().baseUrl(url.appendingPathComponent(File.separator)).client(client).build()
        val service = retrofit.create(Service::class.java)
        if (method == HTTPMethod.POST) {
//            val contentType = headers["Content-Type"]
//            if (contentType != null && contentType.contains("application/json")) {
//                val body = RequestBody.create(mediaTypeJSON, parameters.toJsonString())
//                return service.methodJson(url, body, headers)
//            } else {
//                return service.methodPost(url, parameters, headers)
//            }
            val body = RequestBody.create(mediaTypeJSON, parameters.toJsonString())
            return service.methodJson(url, body, headers)
        } else {
            return service.methodGet(url, parameters, headers)
        }
    }

    fun request(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Map<String, Any>?, headers: Map<String, String>?, callback: Callback<ResponseBody>) {
        var urlFinal: String? = null
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            urlFinal = server?.appendingPathComponent(url)
        }
        val call = call(urlFinal ?: url, method, parameters ?: TreeMap(), headers ?: TreeMap())
        call.enqueue(callback)
    }
}