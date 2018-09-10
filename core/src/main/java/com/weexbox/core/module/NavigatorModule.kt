package com.weexbox.core.module

import android.view.View
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.widget.SimpleToolbar

class NavigatorModule : WXModule() {

    //返回手势
    @JSMethod(uiThread = true)
    fun disableGestureBack() {
    }

    //右item
    @JSMethod(uiThread = true)
    fun setRightItems(info: Map<String, Any>, completionCallback: JSCallback) {

        if (info.get("text") != null && info.get("color") != null){
            getActionbar().setRightButton(View.OnClickListener {
                completionCallback.invoke(null)
            }, info.get("text") as String?, "#" + info.get("color") as String?)

        } else if (info.get("text") != null){
            getActionbar().setRightButton(View.OnClickListener {
                completionCallback.invoke(null)
            }, info.get("text") as String?)

        } else if (info.get("image") != null){
            getActionbar().setRightButtonDrawable(View.OnClickListener {
                completionCallback.invoke(null)
            }, info.get("image") as String?)
        }
    }

    //左item
    @JSMethod(uiThread = true)
    fun setLeftItems(info: Map<String, Any>, completionCallback: JSCallback) {

        if (info.get("text") != null && info.get("color") != null){
            getActionbar().setBackButton(View.OnClickListener {
                completionCallback.invoke(null)
            }, info.get("text") as String?, "#" + info.get("color") as String?)

        } else if (info.get("text") != null){
            getActionbar().setBackButton(View.OnClickListener {
                completionCallback.invoke(null)
            }, info.get("text") as String?)

        } else if (info.get("image") != null){
            getActionbar().setBackButtonDrawable(View.OnClickListener {
                completionCallback.invoke(null)
            }, info.get("image") as String?)
        }
    }

    //中间title
    @JSMethod(uiThread = true)
    fun setCenterItem(info: Map<String, Any>, completionCallback: JSCallback) {

        if (info.get("text") != null && info.get("color") != null){
            getActionbar().setTitleTextListener(View.OnClickListener {
                completionCallback.invoke(null)
            })
            getActionbar().setTitleText(info.get("text") as String?)
            getActionbar().setTitleTextColor("#" + info.get("color") as String?)

        } else if (info.get("text") != null){
            getActionbar().setTitleTextListener(View.OnClickListener {
                completionCallback.invoke(null)
            })
            getActionbar().setTitleText(info.get("text") as String?)

        }
    }

    //导航栏颜色
    @JSMethod(uiThread = true)
    fun setNavColor(color: String) {
        getActionbar().setAcitionbarAndStatusbarBackground("#" + color)
    }

    fun getActivity(): WBBaseActivity {
        return mWXSDKInstance.context as WBBaseActivity
    }

    fun getActionbar(): SimpleToolbar {
        return getActivity().getActionbar()
    }
}