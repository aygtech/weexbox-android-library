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

class ExternalModule : BaseModule() {

    @JSMethod(uiThread = true)
    fun openCamera(options: JSONObject, completionCallback: JSCallback) {
        var enableCrop =  false
        var isCircle =  true
        var width =  1
        var height =  1
        if(options.get("enableCrop") != null){
            options.getBoolean("enableCrop")
        }
        if(options.get("isCircle") != null){
            options.getBoolean("isCircle")
        }
        if(options.get("width") != null){
            options.getInteger("width")
        }
        if(options.get("height") != null){
            options.getInteger("height")
        }
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
        val count = options.getInteger("count")
        var enableCrop = false
        if(options.get("enableCrop") != null){
            enableCrop = options.getBoolean("enableCrop")
        }
        val listener = SelectImageUtil.MultipleImageCompleteListener {
            fun onComplete(imgs: Array<out String>?) {
                val result = Result()
                val map = TreeMap<String, Any>()
                if (imgs != null) {
                    map.put("urls", imgs)
                }
                result.data = map
                completionCallback.invoke(result)
            }
        }
        if (enableCrop) {
            SelectImageUtil.startImagePickActivity(getActivity(), 100, 100, false, enableCrop, false, listener)
        } else {
            SelectImageUtil.startImagePickActivity(getActivity(), count, 0, false, listener)
        }
    }
}