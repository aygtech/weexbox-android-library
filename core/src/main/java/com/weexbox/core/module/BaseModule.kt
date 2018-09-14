package com.weexbox.core.module

import com.alibaba.fastjson.JSONObject
import com.taobao.weex.common.WXModule
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWeexFragment
import java.util.*

/**
 * Author: Mario
 * Time: 2018/9/12 下午7:45
 * Description: This is BaseModule
 */

open class BaseModule: WXModule() {

    fun getActivity(): WBBaseActivity {
        return mWXSDKInstance.context as WBBaseActivity
    }

    fun getFragment(): WBWeexFragment? {
        val fragments = getActivity().supportFragmentManager.fragments as List<WBWeexFragment>
        for (fragment in fragments) {
            if (mWXSDKInstance == fragment.mInstance) {
                return fragment
            }
        }
        return null
    }
}