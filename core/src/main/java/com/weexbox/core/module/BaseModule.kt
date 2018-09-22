package com.weexbox.core.module

import android.support.v4.app.Fragment
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
        val fragments = getActivity().supportFragmentManager.fragments as List<Fragment>
        for (fragment in fragments) {
            if (fragment is WBWeexFragment){
                if (mWXSDKInstance == fragment.mInstance) {
                    return fragment
                }
            } else if (fragment.childFragmentManager.fragments.size > 0){
                val fragmentss = fragment.childFragmentManager.fragments as List<Fragment>
                for (fragmentt in fragmentss){
                    if (fragmentt is WBWeexFragment){
                        if (mWXSDKInstance == fragmentt.mInstance) {
                            return fragmentt
                        }
                    }
                }
            }
        }
        return null
    }
}