package com.weexbox.core.module

import android.app.Activity
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.utils.WXUtils
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.extension.toObject
import com.weexbox.core.model.JsOptions
import com.weexbox.core.model.Result
import com.weexbox.core.util.BitmapUtil
import com.weexbox.core.util.DeviceUtil
import com.weexbox.core.util.DisplayUtil
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import java.util.*
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView


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
        val items: Array<String?> = arrayOfNulls(actions!!.size)
        for (i in actions.indices) {
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
        builder.setMessage(info.message)
        builder.create().show()
    }

    @JSMethod(uiThread = true)
    open fun showToast(options: Map<String, Any>) {
        val info = options.toObject(JsOptions::class.java)
        var time = Toast.LENGTH_SHORT
        if (info.duration != null && info.duration!! >= 3) {
            time = Toast.LENGTH_LONG
        }
        if (!info.text.isNullOrEmpty()) {
            var toast = Toast.makeText(WeexBoxEngine.application, info.text, time)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    @JSMethod(uiThread = true)
    open fun showLoading(text: Any) {
        getActivity().loadDialogHelper.showLoadWithText(getActivity(), WXUtils.getString(text, null))
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

    @JSMethod(uiThread = true)
    open fun openTimePicker(options: Map<String, Any>, callback: JSCallback) {
        //时间选择器
        val selectTime = options.get("selectTime")
        val selectedDate = Calendar.getInstance()
        if (selectTime != null && !TextUtils.isEmpty(selectTime.toString())) {
            selectedDate.time = Date(java.lang.Long.parseLong(selectTime.toString()))
        }
        val timePicker = TimePickerBuilder(getActivity(), object : OnTimeSelectListener {
            override fun onTimeSelect(date: Date, v: View?) {
                val result = Result()
                result.data["timeEnd"] = date
                callback.invoke(result)
            }
        }).setType(booleanArrayOf(true, true, true, true, true, false))// 年月日时分秒
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.parseColor("#222222"))//确定按钮文字颜色
                .setCancelColor(Color.parseColor("#9DA4B3"))//取消按钮文字颜色
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setBgColor(Color.WHITE)//背景
                .setTitleBgColor(Color.WHITE)
                .setDate(selectedDate)
                .setLabel("", "", "", "", "", "")
                .build()
        timePicker.show()
    }

    @JSMethod(uiThread = true)
    open fun openOptionsPicker(options: JSONObject, callback: JSCallback) {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(getActivity(), OnOptionsSelectListener { options1, option2, options3, v ->
            //返回的分别是三个级别的选中位置

        })
//                .setSubmitText("确定")//确定按钮文字
//                .setCancelText("取消")//取消按钮文字
//                .setTitleText("城市选择")//标题
//                .setSubCalSize(18)//确定和取消文字大小
//                .setTitleSize(20)//标题文字大小
//                .setTitleColor(Color.BLACK)//标题文字颜色
//                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
//                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
//                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
//                .setContentTextSize(18)//滚轮文字大小
//                .setLinkage(false)//设置是否联动，默认true
//                .setLabels("省", "市", "区")//设置选择的三级单位
//                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                .setCyclic(false, false, false)//循环与否
//                .setSelectOptions(1, 1, 1)  //设置默认选中项
//                .setOutSideCancelable(false)//点击外部dismiss default true
//                .isDialog(true)//是否显示为对话框样式
//                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .build<Any>()
        pvOptions.setNPicker(options.getJSONArray("option1"), options.getJSONArray("option2"), options.getJSONArray("option3"))//不联动
        // pvOptions.setPicker(options1Items, options2Items, options3Items);//联动,对数据有要求
        pvOptions.show()

    }

}