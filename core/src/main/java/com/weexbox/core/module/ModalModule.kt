package com.weexbox.core.module

import android.support.v7.app.AlertDialog
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import android.content.DialogInterface
import com.alibaba.fastjson.JSONObject
import com.weexbox.core.controller.WBBaseActivity

class ModalModule : WXModule() {

    @JSMethod(uiThread = true)
    fun alert(title: String, message: String, okTitle: String, completionCallback: JSCallback) {
        // 创建构建器
//        val builder = AlertDialog.Builder(getActivity())
//        // 设置参数
//        builder.setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(okTitle, object : DialogInterface.OnClickListener {// 积极
//
//                    override fun onClick(dialog: DialogInterface, which: Int) {
//                        completionCallback.invoke(toResult(0, -1))
//                    }
//                })
//        builder.create().show()
    }















    fun getActivity(): WBBaseActivity {
        return mWXSDKInstance.context as WBBaseActivity
    }

    private fun toResult(status: Int, index: Int): JSONObject {
        var obj = JSONObject()
        obj.put("status",status)

        if (index >= 0){
            var data = JSONObject()
            data.put("index", index)
            obj.put("data", data)
        }

        return obj
    }
}