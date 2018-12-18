package com.weexbox.core.module

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import com.alibaba.fastjson.JSONObject
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.util.SelectImageUtil
import com.weexbox.core.util.ToastUtil
import java.util.*
import kotlin.collections.ArrayList

open class ExternalModule : BaseModule() {

    @JSMethod(uiThread = true)
    open fun openCamera(options: Map<String, Any>, callback: JSCallback) {
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
    open fun openPhoto(options: JSONObject, callback: JSCallback) {
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

    //打开浏览器
    @JSMethod(uiThread = true)
    open fun openBrowser(url: String) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivity(Intent.createChooser(intent, "请选择浏览器"));
        }
    }

    //打电话
    @JSMethod(uiThread = true)
    open fun callPhone(phone: String, callback: JSCallback) {
        if (phone != null){
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ phone))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
            callback.invoke(Result())
        }
    }
}