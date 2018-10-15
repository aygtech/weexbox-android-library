package com.weexbox.core.module

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
    fun openCamera(options: Map<String, Any>, completionCallback: JSCallback) {
        val enableCrop = options["enableCrop"] as Boolean ?: false
        val isCircle = options["isCircle"] as Boolean ?: true
        val width = options["width"] as Int ?: 1
        val height = options["height"] as Int ?: 1
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
    fun openPhoto(options: Map<String, Any>, completionCallback: JSCallback) {
        val count = options["count"] as Int
        val enableCrop = options["enableCrop"] as Boolean ?: false
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