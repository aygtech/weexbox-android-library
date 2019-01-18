package com.weexbox.core.component

import android.view.View
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.ui.action.BasicComponentData
import com.taobao.weex.ui.component.WXComponent
import com.taobao.weex.ui.component.WXVContainer

/**
 * Author: Mario
 * Time: 2019/1/16 5:58 PM
 * Description: This is BaseComponent
 */

open class BaseComponent<T : View>(instance: WXSDKInstance?, parent: WXVContainer<*>?, basicComponentData: BasicComponentData<*>?) : WXComponent<T>(instance, parent, basicComponentData) {

}