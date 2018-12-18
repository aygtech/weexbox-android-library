package com.weexbox.core.module

import android.text.TextUtils
import android.view.View
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.event.Event
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.util.AndroidUtil
import com.weexbox.core.widget.SimpleToolbar

open class NavigatorModule : BaseModule() {

    //返回手势
    @JSMethod(uiThread = true)
    open fun disableGestureBack() {
    }

    //中间title
    @JSMethod(uiThread = true)
    open fun setCenterItem(options: Map<String, Any>, callback: JSCallback?) {
        val info = options.toObject(JsOptions::class.java)
        getActionbar().setTitleTextListener {
            callback?.invokeAndKeepAlive(Result())
        }

        getActionbar().setTitleText(info.text)
        if (info.color?.startsWith("#") == true){
            getActionbar().setTitleTextColor(info.color)
        } else if (info.color != null){
            getActionbar().setTitleTextColor("#" + info.color)
        }
    }

    //左item
    @JSMethod(uiThread = true)
    open fun setLeftItems(items: List<Map<String, Any>>, callback: JSCallback?) {
        for (i in items.indices) {
            var info: Map<String, Any>
            val result = Result()
            result.data["index"] = i

            if (i == 0) {
                info = items[0]
                if (info["text"] != null && !TextUtils.isEmpty(info["color"] as CharSequence?)) {
                    var color: String = info["color"] as String
                    if (color.startsWith("#") == false){
                        color = "#" + color
                    }
                    getActionbar().setBackButton({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?, color)

                } else if (info["text"] != null) {
                    getActionbar().setBackButton({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?)

                } else if (!TextUtils.isEmpty(info["image"] as CharSequence?)) {
                    getActionbar().setBackButtonDrawable({
                        callback?.invokeAndKeepAlive(result)
                    }, info["image"] as String?)
                } else {
                    getActionbar().setBackButton {
                        callback?.invokeAndKeepAlive(result)
                    }
                }
            } else if (i == 1) {
                info = items[1]
                if (info["text"] != null && !TextUtils.isEmpty(info["color"] as CharSequence?)) {
                    var color: String = info["color"] as String
                    if (color.startsWith("#") == false){
                        color = "#" + color
                    }
                    getActionbar().setBackButton2({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?, color)

                } else if (info["text"] != null) {
                    getActionbar().setBackButton2({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?)

                } else if (!TextUtils.isEmpty(info["image"] as CharSequence?)) {
                    getActionbar().setBackButton2Drawable({
                        callback?.invokeAndKeepAlive(result)
                    }, info["image"] as String?)
                }
            }
        }
    }

    //右item
    @JSMethod(uiThread = true)
    open fun setRightItems(items: List<Map<String, Any>>, callback: JSCallback?) {
        for (i in items.indices) {
            var info: Map<String, Any>
            val result = Result()
            result.data["index"] = i

            if (i == 0) {
                info = items[0]
                if (info["text"] != null && !TextUtils.isEmpty(info["color"] as CharSequence?)) {
                    var color: String = info["color"] as String
                    if (color.startsWith("#") == false){
                        color = "#" + color
                    }
                    getActionbar().setRightButton({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?, color)

                } else if (info["text"] != null) {
                    getActionbar().setRightButton({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?)

                } else if (!TextUtils.isEmpty(info["image"] as CharSequence?)) {
                    getActionbar().setRightButtonDrawable( {
                        callback?.invokeAndKeepAlive(result)
                    }, info.get("image") as String?)
                }

            } else if (i == 1) {
                info = items[1]
                if (info["text"] != null && !TextUtils.isEmpty(info["color"] as CharSequence?)) {
                    var color: String = info["color"] as String
                    if (color.startsWith("#") == false){
                        color = "#" + color
                    }
                    getActionbar().setRightButton2({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?, color)

                } else if (info["text"] != null) {
                    getActionbar().setRightButton2({
                        callback?.invokeAndKeepAlive(result)
                    }, info["text"] as String?)

                } else if (!TextUtils.isEmpty(info["image"] as CharSequence?)) {
                    getActionbar().setRightButton2Drawable({
                        callback?.invokeAndKeepAlive(result)
                    }, info["image"] as String?)
                }
            }
        }
    }

    //导航栏颜色
    @JSMethod(uiThread = true)
    open fun setNavColor(color: String) {
            getActionbar().setAcitionbarAndStatusbarBackground("#" + color)
    }

    //物理返回键
    @JSMethod(uiThread = true)
    open fun onBackPressed(callback: JSCallback) {
        val fragment = getFragment()!!
        fragment.isListenBack = true
        Event.register(fragment, fragment.backName) {
            callback.invokeAndKeepAlive(it)
        }
    }

    @JSMethod(uiThread = false)
    open fun getHeight(): Int {
        return AndroidUtil.getStateBarHeight(getActivity());
    }

    private fun getActionbar(): SimpleToolbar {
        return getActivity().getActionbar()
    }

}