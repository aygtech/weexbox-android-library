package com.weexbox.core.component

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.widget.TextView
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.ui.action.BasicComponentData
import com.taobao.weex.ui.component.WXComponentProp
import com.taobao.weex.ui.component.WXVContainer
import com.weexbox.core.util.DisplayUtil
import java.math.BigDecimal

open class RichTextComponent(instance: WXSDKInstance, parent: WXVContainer<*>, basicComponentData: BasicComponentData<*>) : BaseComponent<TextView>(instance, parent, basicComponentData) {

    override fun initComponentHostView(context: Context): TextView {
        return TextView(context)
    }

    @WXComponentProp(name = "params")
    fun setMyData(map: Map<String, Any>) {
        if (map["size"] != null) {
            var size = 0f
            if (map["size"] is BigDecimal) {
                val tt = map["size"] as BigDecimal
                size = tt.toFloat()
            } else if (map["size"] is Int) {
                size = (map["size"] as Int).toFloat()
            }
            hostView.textSize = DisplayUtil.px2sp(context, size).toFloat()
        }

        if (map["color"] != null) {
            hostView.setTextColor(Color.parseColor(map["color"] as String))
        }
    }

    @JSMethod
    fun updateSources(map: Map<String, Any>) {
        if (map["text"] != null) {
            hostView.text = Html.fromHtml(map["text"] as String)
        }
    }
}
