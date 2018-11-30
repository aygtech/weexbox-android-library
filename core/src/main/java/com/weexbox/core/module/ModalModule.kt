package com.weexbox.core.module

import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import java.math.BigDecimal

open class ModalModule : BaseModule() {

    @JSMethod(uiThread = true)
    open fun alert(options: Map<String, Any>, callback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setTitle(info.title).setMessage(info.message).setPositiveButton(info.okTitle) { _, _ ->
            callback.invoke(Result())
        }
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun confirm(options: Map<String, Any>, callback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setTitle(info.title).setMessage(info.message)
                .setPositiveButton(info.okTitle) { _, _ ->
                    callback.invoke(Result())
                }
                .setNegativeButton(info.cancelTitle) { _, _ ->
                    val result = Result()
                    result.status = Result.error
                    callback.invoke(result)
                }
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun prompt(options: Map<String, Any>, callback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        val edit = EditText(getActivity())
        edit.setText(info.placeholder)
        val builder = AlertDialog.Builder(getActivity()).setView(edit).setTitle(info.title).setMessage(info.message)
                .setPositiveButton(info.okTitle) { _, _ ->
                    val result = Result()
                    result.data["text"] = edit.text
                    callback.invoke(result)
                }
                .setNegativeButton(info.cancelTitle) { _, _ ->
                    val result = Result()
                    result.status = Result.error
                    callback.invoke(result)
                }
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun actionSheet(options: Map<String, Any>, completionCallback: JSCallback) {
        val info = options.toObject(JsOptions::class.java)
        val actions = info.actions
        val items: Array<String?> = arrayOf()
        for (i in actions!!.indices) {
            items[i] = actions[i].title
        }
        // 创建对话框构建器
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setItems(items) { _, which ->
            val result = Result()
            result.data["index"] = which
            completionCallback.invoke(result)
        }
        builder.setTitle(info.title)
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun showToast(options: Map<String, Any>) {
        val info = options.toObject(JsOptions::class.java)
        var time = Toast.LENGTH_SHORT
        if (info.duration != null && info.duration!! >= 3) {
            time = Toast.LENGTH_LONG
        }
        Toast.makeText(WeexBoxEngine.application, info.text, time).show()
    }

    @JSMethod(uiThread = true)
    open fun showLoading(text: String) {
        getActivity().loadDialogHelper.showLoadWithText(getActivity(), text)
    }

    @JSMethod(uiThread = true)
    open fun showProgress(options: Map<String, Any>) {
        val info = options.toObject(JsOptions::class.java)
        getActivity().loadDialogHelper.showProgressWithText(getActivity(), info.text, info.progress!!)
    }

    @JSMethod(uiThread = true)
    open fun dismiss() {
        getActivity().loadDialogHelper.close()
    }
}