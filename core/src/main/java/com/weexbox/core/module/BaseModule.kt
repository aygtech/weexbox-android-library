package com.weexbox.core.module

import androidx.fragment.app.Fragment
import com.taobao.weex.common.WXModule
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWeexFragment

/**
 * Author: Mario
 * Time: 2018/9/12 下午7:45
 * Description: This is BaseModule
 */

open class BaseModule : WXModule() {

    fun getActivity(): WBBaseActivity {
        return mWXSDKInstance.context as WBBaseActivity
    }

    fun getFragment(): WBWeexFragment? {
        val fragments = getActivity().supportFragmentManager.fragments as List<androidx.fragment.app.Fragment>
        return getRecursionFragment(fragments)
    }

    private fun getRecursionFragment(fragments: List<androidx.fragment.app.Fragment>): WBWeexFragment? {
        for (recursionFragment in fragments) {
            if (recursionFragment is WBWeexFragment && mWXSDKInstance == recursionFragment.instance) {
                return recursionFragment
            } else if (recursionFragment.childFragmentManager.fragments.size > 0) {
                getRecursionFragment(recursionFragment.childFragmentManager.fragments)
            }
        }
        return null
    }
}