package com.weexbox.core.module

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import com.weexbox.core.net.HttpParams
import com.weexbox.core.net.HttpRequestHelper
import com.weexbox.core.net.callback.HttpCallback
import com.weexbox.core.net.callback.HttpStringCallback
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.util.*

/**
 *Author:leon.wen
 *Time:2018/9/4   17:32
 *Description:This is NetworkModule
 */

class NetworkModule : WXModule() {

    @JSMethod(uiThread = false)
    fun request(options: Map<String, Any>, callback: JSCallback) {
        val url = options.get("url") ?: ""
        if(TextUtils.isEmpty(url.toString().trim())){
            return
        }
        val method = options.get("method") ?: "get"
        var headers = JSON.toJSONString(options.get("headers")?: "{}")
        var params = JSON.toJSONString(options.get("params")?: "{}")
        val headersMaps = JSON.parse(headers) as Map<String, String>?
        val httpParams = HttpParams(params)
        val type = headersMaps?.get("contentType");
        if(method.equals("post")){
            if(type != null && type.startsWith("application/json")){
                HttpRequestHelper( null).sendPostJsonRequest(url.toString(),headersMaps, httpParams, toHttpCallback(callback) ?: null)
            }else{
                HttpRequestHelper( null).sendPostRequest(url.toString(),headersMaps, httpParams, toHttpCallback(callback) ?: null)
            }
        }else{
            HttpRequestHelper( null).sendGetRequest(url.toString(),headersMaps, httpParams, toHttpCallback(callback) ?: null)
        }
    }

    @JSMethod(uiThread = false)
    fun upload(options: Map<String, Any>, completionCallback: JSCallback, progressCallback: JSCallback) {
        val url = options.get("to") ?: ""
        if(TextUtils.isEmpty(url.toString().trim())){
            return
        }
        var images = JSON.parseArray(options.get("files").toString()?: "[]")
        var httpParams = HashMap<String,File>()
        for(img in images!!){
            var key = (img as JSONObject).keys
            var file = File(img.getString(key.first()))
            var fileMap = HashMap<String,File>()
            fileMap.put(key.first() ,file)
            httpParams.plus(fileMap)
        }
        HttpRequestHelper( null).uploadFiles(url.toString(), httpParams as HttpParams, ToProgressHttpCallback(completionCallback, progressCallback) ?: null)
    }

    fun toHttpCallback(jsCallback: JSCallback) :HttpCallback<String> {
        if(jsCallback == null){
            return jsCallback
        }
        val callback = object : HttpCallback<String>() {
            override fun onSuccess(entity: String?, requestId: Int) {
                var obj = JSONObject()
                obj.put("status",200)
                obj.put("errorMsg","")
                obj.put("data",entity)
                jsCallback.invoke(obj)
            }

            override fun onFail(requestId: Int, errorCode: Int, errorMessage: String) {
                var obj = JSONObject()
                obj.put("status",errorCode)
                obj.put("errorMsg",errorMessage)
                obj.put("data","")
                jsCallback.invoke(obj)
            }
        }
        return callback
    }

    fun ToProgressHttpCallback(jsCallback: JSCallback, progressCallback: JSCallback) : HttpCallback<String>? {
        if(jsCallback == null || progressCallback == null){
            return null
        }
        val callback = object : HttpCallback<String>() {

            override fun inProgress(progress: Float, total: Long, id: Int) {
                super.inProgress(progress, total, id)
                progressCallback.invokeAndKeepAlive(progress)
            }

            override fun onSuccess(entity: String?, requestId: Int) {
                var obj = JSONObject()
                obj.put("status",200)
                obj.put("errorMsg","")
                obj.put("data",entity)
                jsCallback.invoke(obj)
            }

            override fun onFail(requestId: Int, errorCode: Int, errorMessage: String) {
                var obj = JSONObject()
                obj.put("status",errorCode)
                obj.put("errorMsg",errorMessage)
                obj.put("data","")
                jsCallback.invoke(obj)
            }
        }
        return callback
    }
}