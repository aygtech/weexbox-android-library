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
    fun openCamera(options: Map<String, Any>, completionCallback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        var enableCrop = info.enableCrop
        var isCircle = info.isCircle
        var width = info.width
        var height = info.height
        SelectImageUtil.startCamera(getActivity(), width, height, enableCrop, isCircle, object : SelectImageUtil.MultipleImageCompleteListener {
            override fun onComplete(imgs: Array<out String>?) {
                val result = Result()
                val map = TreeMap<String, Any>()
                if (imgs!![0].length > 0) {
                    val array = arrayListOf<String>();
                    array.add(imgs[0])
                    map.put("urls", array)
                }
                result.data = map
                completionCallback.invoke(result.toJsResult())
            }
        })
    }

    @JSMethod(uiThread = true)
    fun openPhoto(options: JSONObject, completionCallback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        val count = info.count
        var enableCrop = info.enableCrop
        var isCircle = info.isCircle
        var width = info.width
        var height = info.height
        if (enableCrop) {
            SelectImageUtil.startImagePickActivity(getActivity(), 100, 100, false, enableCrop, false, object : SelectImageUtil.MultipleImageCompleteListener {
                override fun onComplete(imgs: Array<out String>?) {
                    val result = Result()
                    val map = TreeMap<String, Any>()
                    if (imgs != null) {
                        map.put("urls", imgs)
                    }
                    result.data = map
                    completionCallback.invoke(result)
                }
            })
        } else {
            SelectImageUtil.startImagePickActivity(getActivity(), count, 0, false, object : SelectImageUtil.MultipleImageCompleteListener {
                override fun onComplete(imgs: Array<out String>?) {
                    val result = Result()
                    val map = TreeMap<String, Any>()
                    if (imgs != null) {
                        map.put("urls", imgs)
                    }
                    result.data = map
                    completionCallback.invoke(result)
                }
            })
        }
    }
}