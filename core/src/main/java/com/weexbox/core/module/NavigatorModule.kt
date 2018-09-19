package com.weexbox.core.module

import android.view.View
import com.alibaba.fastjson.JSONObject
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.model.Result
import com.weexbox.core.widget.SimpleToolbar
import java.util.*

class NavigatorModule : BaseModule() {

    //返回手势
    @JSMethod(uiThread = true)
    fun disableGestureBack() {
    }

    //中间title
    @JSMethod(uiThread = true)
    fun setCenterItem(info: Map<String, Any>, completionCallback: JSCallback) {
        if (getActionbar() != null){
            val result = Result()

            if (info.get("text") != null && info.get("color") != null){
                if(completionCallback != null){
                    getActionbar().setTitleTextListener(View.OnClickListener {
                        completionCallback.invokeAndKeepAlive(result)
                    })
                }
                getActionbar().setTitleText(info.get("text") as String?)
                getActionbar().setTitleTextColor("#" + info.get("color") as String?)

            } else if (info.get("text") != null){
                if(completionCallback != null){
                    getActionbar().setTitleTextListener(View.OnClickListener {
                        completionCallback.invokeAndKeepAlive(result)
                    })
                }
                getActionbar().setTitleText(info.get("text") as String?)

            }
        }
    }

    //左item
    @JSMethod(uiThread = true)
    fun setLeftItems(items: List<Map<String, Any>>, completionCallback: JSCallback) {
        if (getActionbar() != null){
            for (i in items.indices) {
                var info: Map<String, Any>
                val result = Result()
                result.data["index"]  = i


                if (i == 0){
                    info = items[0]
                    if (info.get("text") != null && info.get("color") != null){
                        getActionbar().setBackButton(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?, "#" + info.get("color") as String?)

                    } else if (info.get("text") != null){
                        getActionbar().setBackButton(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?)

                    } else if (info.get("image") != null){
                        getActionbar().setBackButtonDrawable(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("image") as String?)
                    }
                } else if (i == 1){
                    info = items[1]
                    if (info.get("text") != null && info.get("color") != null){
                        getActionbar().setBackButton2(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?, "#" + info.get("color") as String?)

                    } else if (info.get("text") != null){
                        getActionbar().setBackButton2(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?)

                    } else if (info.get("image") != null){
                        getActionbar().setBackButton2Drawable(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("image") as String?)
                    }
                }
            }
        }
    }

    //右item
    @JSMethod(uiThread = true)
    fun setRightItems(items: List<Map<String, Any>>, completionCallback: JSCallback) {
        if (getActionbar() != null){
            for (i in items.indices) {
                var info: Map<String, Any>
                val result = Result()
                result.data["index"]  = i

                if (i == 0){
                    info = items[0]
                    if (info.get("text") != null && info.get("color") != null){
                        getActionbar().setRightButton(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?, "#" + info.get("color") as String?)

                    } else if (info.get("text") != null){
                        getActionbar().setRightButton(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?)

                    } else if (info.get("image") != null){
                        getActionbar().setRightButtonDrawable(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("image") as String?)
                    }

                } else if (i == 1){
                    info = items[1]
                    if (info.get("text") != null && info.get("color") != null){
                        getActionbar().setRightButton2(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?, "#" + info.get("color") as String?)

                    } else if (info.get("text") != null){
                        getActionbar().setRightButton2(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("text") as String?)

                    } else if (info.get("image") != null){
                        getActionbar().setRightButton2Drawable(View.OnClickListener {
                            completionCallback.invokeAndKeepAlive(result)
                        }, info.get("image") as String?)
                    }

                }
            }
        }
    }

    //导航栏颜色
    @JSMethod(uiThread = true)
    fun setNavColor(color: String) {
        if (getActionbar() != null){
            getActionbar().setAcitionbarAndStatusbarBackground("#" + color)
        }
    }

    fun getActionbar(): SimpleToolbar {
        return getActivity().getActionbar()
    }

}