package com.weexbox.core.module

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.model.Result
import com.weexbox.core.util.LoadDialogUtil
import java.util.*


class ModalModule : BaseModule() {

    @JSMethod(uiThread = true)
    fun alert(options: Map<String, Any>, completionCallback: JSCallback) {
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setTitle(options["title"] as String)
                .setMessage(options["message"] as String)
                .setPositiveButton(options["okTitle"] as String, object : DialogInterface.OnClickListener {// 积极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        completionCallback.invoke(Result())
                    }
                })
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    fun confirm(options: Map<String, Any>, completionCallback: JSCallback) {
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setTitle(options["title"] as String)
                .setMessage(options["message"] as String)
                .setPositiveButton(options["okTitle"] as String, object : DialogInterface.OnClickListener {// 积极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        completionCallback.invoke(Result())
                    }
                })
                .setNegativeButton(options["cancelTitle"] as String, object : DialogInterface.OnClickListener {// 消极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        result.code = Result.error
                        completionCallback.invoke(Result())
                    }
                })
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    fun prompt(options: Map<String, Any>, completionCallback: JSCallback) {
        val edit = EditText(getActivity())
        edit.setText(options["placeholder"] as String)

        val builder = AlertDialog.Builder(getActivity())
                .setView(edit)
                .setTitle(options["title"] as String)
                .setMessage(options["message"] as String)
                .setPositiveButton(options["okTitle"] as String, object : DialogInterface.OnClickListener {// 积极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        var map = TreeMap<String, Any>()
                        map.put("text", edit.getText())
                        result.data = map
                        completionCallback.invoke(result)
                    }
                })
                .setNegativeButton(options["cancelTitle"] as String, object : DialogInterface.OnClickListener {// 消极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        result.code = Result.error
                        completionCallback.invoke(Result())
                    }
                })
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    fun actionSheet(options: Map<String, Any>, completionCallback: JSCallback) {

        val list = options["actions"] as List<Map<String, String>>
        var items = arrayOfNulls<String>(list.size)
        for (i in list.indices){
            items[i] = list[i].get("title")!!
        }

        // 创建对话框构建器
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setTitle(options["title"] as String)
                .setItems(items, object : DialogInterface.OnClickListener {

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        var map = TreeMap<String, Any>()
                        map.put("index", which)
                        result.data = map
                        completionCallback.invoke(result)
                    }
                })
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    fun showToast(options: Map<String, Any>) {
        Toast.makeText(getActivity().applicationContext, options["text"] as String, options["duration"] as Int * 1000).show()
    }

    @JSMethod(uiThread = true)
    fun showLoading(text: String) {
        LoadDialogUtil.showLoadWithText(getActivity(), text)
    }

    @JSMethod(uiThread = true)
    fun showProgress(options: Map<String, Any>) {
        LoadDialogUtil.showProgressWithText(getActivity(), options["text"] as String)
        LoadDialogUtil.setProgressHUD(options["progress"] as Int)
    }

    @JSMethod(uiThread = true)
    fun dismiss() {
        LoadDialogUtil.close()
    }
}