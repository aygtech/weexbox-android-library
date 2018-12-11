package com.weexbox.core.network

import com.weexbox.core.extension.appendingPathComponent
import com.weexbox.core.extension.toJsonString
import com.weexbox.core.retrofit.HttpApi
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import retrofit2.http.Url
import retrofit2.http.GET
import retrofit2.http.Streaming


/**
 * Author: Mario
 * Time: 2018/9/11 下午4:46
 * Description: This is Network
 */

object Network {

    var server: String? = null

    enum class HTTPMethod {
        GET,
        POST,
        DOWNLOAD,
        UPLOAD
    }

    @JvmSuppressWildcards
    interface Service {
        @GET
        fun methodGet(@Url url: String, @QueryMap parameters: Map<String, Any>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @FormUrlEncoded
        @POST
        fun methodPost(@Url url: String, @FieldMap parameters: Map<String, Any>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @POST
        fun methodJson(@Url url: String, @Body body: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @Streaming //大文件时要加不然会OOM
        @GET
        fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

        @Multipart
        @POST
        fun uploadFile(@Url fileUrl: String, @QueryMap params: Map<String, Any>, @Part file: MultipartBody.Part): Call<ResponseBody>
    }

    val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
    var mediaTypeJSON: MediaType? = MediaType.parse("application/json")

    private fun call(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Map<String, Any>, headers: Map<String, String>): Call<ResponseBody> {
//        val retrofit = Retrofit.Builder().baseUrl(url.appendingPathComponent(File.separator)).client(client).build()
        val service = HttpApi.instance.create(Service::class.java)
        when (method) {
            HTTPMethod.POST -> {
                val contentType = headers["Content-Type"]
                if (contentType != null && contentType.contains("application/json")) {
                    val body = RequestBody.create(mediaTypeJSON, parameters.toJsonString())
                    return service.methodJson(url, body, headers)
                } else {
                    return service.methodPost(url, parameters, headers)
                }
            }
            HTTPMethod.GET -> {
                return service.methodGet(url, parameters, headers)
            }
            else -> {
                return service.downloadFile(url)
            }
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

    fun upload(url: String, parameters: Map<String, Any>?, file: MultipartBody.Part, callback: Callback<ResponseBody>) {
        var urlFinal: String? = null
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            urlFinal = server?.appendingPathComponent(url)
        }
        val service = HttpApi.instance.create(Service::class.java)
        val call = service.uploadFile(urlFinal ?: url, parameters ?: TreeMap(), file)
        call.enqueue(callback)
    }
}