package com.weexbox.core.module

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.model.Result
import com.weexbox.core.util.SelectImageUtil
import java.util.*

class ExternalModule : BaseModule() {

    @JSMethod(uiThread = true)
    fun openCamera(completionCallback: JSCallback) {
        SelectImageUtil.startImagePickActivity(getActivity(), 1, 0, true, object : SelectImageUtil.MultipleImageCompleteListener {

            override fun onComplete(imgs: Array<out String>?) {
                val result = Result()
                var map = TreeMap<String, Any>()
                if (imgs!![0].length > 0) {
                    map.put("url", imgs[0])
                }
                result.data = map
                completionCallback.invoke(result)
            }
        })
    }

    @JSMethod(uiThread = true)
    fun openPhoto(options: Map<String, Any>, completionCallback: JSCallback) {
        var count = options["count"] as Int
        SelectImageUtil.startImagePickActivity(getActivity(), count, 0, false, object : SelectImageUtil.MultipleImageCompleteListener {

            override fun onComplete(imgs: Array<out String>?) {
                val result = Result()
                var map = TreeMap<String, Any>()
                if (imgs != null) {
                    map.put("urls", imgs)
                }
                result.data = map
                completionCallback.invoke(result)
            }
        })
    }
}