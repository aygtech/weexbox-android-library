package com.weexbox.core.module

import com.alibaba.fastjson.JSONObject
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.util.SelectImageUtil
import java.util.*
import kotlin.collections.ArrayList

open class ExternalModule : BaseModule() {

    @JSMethod(uiThread = true)
    fun openCamera(options: Map<String, Any>, callback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        val enableCrop = info.enableCrop
        val isCircle = info.isCircle
        val width = info.width
        val height = info.height
        SelectImageUtil.startCamera(getActivity(), width, height, enableCrop, isCircle) { imgs ->
            val result = Result()
            val map = TreeMap<String, Any>()
            if (imgs!![0].isNotEmpty()) {
                val array = arrayListOf<String>()
                array.add(imgs[0])
                map["urls"] = array
            }
            result.data = map
            callback.invoke(result.toJsResult())
        }
    }

    @JSMethod(uiThread = true)
    fun openPhoto(options: JSONObject, callback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        val count = info.count
        val enableCrop = info.enableCrop
        if (enableCrop) {
            SelectImageUtil.startImagePickActivity(getActivity(), 100, 100, false, enableCrop, false) {imgs ->
                val result = Result()
                val map = TreeMap<String, Any>()
                if (imgs != null) {
                    map["urls"] = imgs
                }
                result.data = map
                callback.invoke(result)
            }
        } else {
            SelectImageUtil.startImagePickActivity(getActivity(), count, 0, false) { imgs ->
                val result = Result()
                val map = TreeMap<String, Any>()
                if (imgs != null) {
                    map["urls"] = imgs
                }
                result.data = map
                callback.invoke(result)
            }
        }
    }
}