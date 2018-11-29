package com.weexbox.core.module

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.weexbox.core.model.Result
import java.math.BigDecimal
import java.util.*


open class ModalModule : BaseModule() {

    @JSMethod(uiThread = true)
    open fun alert(options: Map<String, Any>, completionCallback: JSCallback) {
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setTitle(options["title"] as String?)
                .setMessage(options["message"] as String?)
                .setPositiveButton(options["okTitle"] as String?, object : DialogInterface.OnClickListener {// 积极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        completionCallback.invoke(Result())
                    }
                })
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun confirm(options: Map<String, Any>, completionCallback: JSCallback) {
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setTitle(options["title"] as String?)
                .setMessage(options["message"] as String?)
                .setPositiveButton(options["okTitle"] as String?, object : DialogInterface.OnClickListener {// 积极
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        completionCallback.invoke(Result())
                    }
                })
                .setNegativeButton(options["cancelTitle"] as String?, object : DialogInterface.OnClickListener {// 消极
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        result.status = Result.error
                        completionCallback.invoke(result)
                    }
                })
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun prompt(options: Map<String, Any>, completionCallback: JSCallback) {
        val edit = EditText(getActivity())
        edit.setText(options["placeholder"] as String?)

        val builder = AlertDialog.Builder(getActivity())
                .setView(edit)
                .setTitle(options["title"] as String?)
                .setMessage(options["message"] as String?)
                .setPositiveButton(options["okTitle"] as String?, object : DialogInterface.OnClickListener {// 积极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        var map = TreeMap<String, Any>()
                        map.put("text", edit.getText())
                        result.data = map
                        completionCallback.invoke(result)
                    }
                })
                .setNegativeButton(options["cancelTitle"] as String?, object : DialogInterface.OnClickListener {// 消极

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        result.status = Result.error
                        completionCallback.invoke(Result())
                    }
                })
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun actionSheet(options: Map<String, Any>, completionCallback: JSCallback) {

        val list = options["actions"] as List<Map<String, String>>
        var items = arrayOfNulls<String>(list.size)
        for (i in list.indices) {
            items[i] = list[i].get("title")!!
        }

        // 创建对话框构建器
        val builder = AlertDialog.Builder(getActivity())
        // 设置参数
        builder.setItems(items, object : DialogInterface.OnClickListener {

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val result = Result()
                        var map = TreeMap<String, Any>()
                        map.put("index", which)
                        result.data = map
                        completionCallback.invoke(result)
                    }
                })
        if (options["title"] != null){
            builder.setTitle(options["title"] as String?)
        }

        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun showToast(options: Map<String, Any>) {
        if (options["duration"] is Int){
            Toast.makeText(getActivity().applicationContext, options["text"] as String?, options["duration"] as Int * 1000).show()
        } else if (options["duration"] is BigDecimal){
            Toast.makeText(getActivity().applicationContext, options["text"] as String?, ((options["duration"] as BigDecimal).toDouble() * 1000).toInt()).show()
        }
    }

    @JSMethod(uiThread = true)
    open fun showLoading(text: String) {
        getActivity().loadDialogHelper.showLoadWithText(getActivity(), text)
    }

//    @JSMethod(uiThread = true)
//    fun showLoading(text: String, setTransparent: Boolean) {
//        var isTransparent = false;
//        if (setTransparent != null) {
//            isTransparent = setTransparent;
//        }
//        LoadDialogUtil.showLoadWithText(getActivity(), text, isTransparent)
//    }

    @JSMethod(uiThread = true)
    open fun showProgress(options: Map<String, Any>) {
        getActivity().loadDialogHelper.showProgressWithText(getActivity(), options["text"] as String?, options["progress"] as Int)
    }

    @JSMethod(uiThread = true)
    open fun dismiss() {
        getActivity().loadDialogHelper.close()
    }
}