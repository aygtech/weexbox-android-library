package com.weexbox.core.module

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Vibrator
import com.alibaba.fastjson.JSONObject
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.utils.WXUtils
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.net.entity.HttpBaseEntity
import com.weexbox.core.util.SelectImageUtil
import java.util.*

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
                var myImgs = arrayListOf<BitmapAttribute>()
                var bitmapAttribute = BitmapAttribute()
                bitmapAttribute.url = imgs[0]
                bitmapAttribute.width = getBitmapWidth(imgs[0])
                bitmapAttribute.height = getBitmapHeight(imgs[0])
                myImgs.add(bitmapAttribute)
                map["urls"] = myImgs
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
            SelectImageUtil.startImagePickActivity(getActivity(), 100, 100, false, enableCrop, false) { imgs ->
                val result = Result()
                val map = TreeMap<String, Any>()
                if (imgs != null) {
                    var myImgs = arrayListOf<BitmapAttribute>();
                    var bitmapAttribute: BitmapAttribute
                    for (img in imgs) {
                        bitmapAttribute = BitmapAttribute()
                        bitmapAttribute.url = img
                        bitmapAttribute.width = getBitmapWidth(img)
                        bitmapAttribute.height = getBitmapHeight(img)
                        myImgs.add(bitmapAttribute)
                    }
                    map["urls"] = myImgs
                }
                result.data = map
                callback.invoke(result)
            }
        } else {
            SelectImageUtil.startImagePickActivity(getActivity(), count, 0, false) { imgs ->
                val result = Result()
                val map = TreeMap<String, Any>()
                if (imgs != null) {
                    var myImgs = arrayListOf<BitmapAttribute>();
                    var bitmapAttribute: BitmapAttribute
                    for (img in imgs) {
                        bitmapAttribute = BitmapAttribute()
                        bitmapAttribute.url = img
                        bitmapAttribute.width = getBitmapWidth(img)
                        bitmapAttribute.height = getBitmapHeight(img)
                        myImgs.add(bitmapAttribute)
                    }
                    map["urls"] = myImgs
                }
                result.data = map
                callback.invoke(result)
            }
        }
    }

    //打开浏览器
    @JSMethod(uiThread = true)
    open fun openBrowser(url: Any) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(WXUtils.getString(url, null)))
        if (intent.resolveActivity(getActivity().packageManager) != null) {
            getActivity().startActivity(Intent.createChooser(intent, "请选择浏览器"))
        }
    }

    //打电话
    @JSMethod(uiThread = true)
    open fun callPhone(phone: Any, callback: JSCallback?) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        getActivity().startActivity(intent)
        callback?.invoke(Result())
    }

    //振动
    @JSMethod(uiThread = true)
    open fun vibration(options: Map<String, Any>){
        val info = options.toObject(JsOptions::class.java)
        if (info.millisecond != null && info.millisecond!! >= 0){
            val vibrator = getActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(info.millisecond!!.toLong())
        }
    }

    class BitmapAttribute: HttpBaseEntity() {
        var width: Int = 0
        var height: Int = 0
        lateinit var url: String
    }

    fun getBitmapWidth(path: String): Int {
        //获取Options对象
        val options = BitmapFactory.Options()
        //仅做解码处理，不加载到内存
        options.inJustDecodeBounds = true
        //解析文件
        BitmapFactory.decodeFile(path, options)
        return options.outWidth
    }

    fun getBitmapHeight(path: String): Int {
        //获取Options对象
        val options = BitmapFactory.Options()
        //仅做解码处理，不加载到内存
        options.inJustDecodeBounds = true
        //解析文件
        BitmapFactory.decodeFile(path, options)
        return options.outHeight
    }
}