package com.weexbox.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.common.WXException
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.router.Router

class MainActivity : WBBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragment()
    }

    private var weexFragment: MainFragment? = null

    private fun initFragment() {

        val router = Router()
        router.url = "index.js"

        weexFragment = MainFragment()
        val bundle = Bundle()
        bundle.putSerializable(Router.extraName, router)
        weexFragment!!.setArguments(bundle)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                weexFragment).commit()
    }
}
