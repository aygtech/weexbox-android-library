package com.weexbox.example

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.common.WXException
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.router.Router
import java.util.HashMap

class MainActivity : WBBaseActivity(), WBBaseActivity.HaveFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var xixi: Int = router?.params?.get("xixi") as Int
//        findViewById<TextView>(R.id.xixi).setText(xixi.toString())

        initFragment()
//        getActionbar().setBackButton(object: View.OnClickListener {
//            override fun onClick(v: View?) {
////                refreshWeex()
//                val intent = Intent(this@MainActivity, MainActivity::class.java)
//                startActivity(intent)
//            }
//        })
//        getActionbar().setBackButton2(object: View.OnClickListener{
//            override fun onClick(v: View?) {
//                var router = Router()
//                router.name = "aaaa"
//                val map = HashMap<String, Int>()
//                map.put("xixi", xixi + 1)
//                router.params = map
//                router.open(this@MainActivity)
//            }
//
//        }, "聊天")
//        getActionbar().setTitleText(object: View.OnClickListener {
//            override fun onClick(v: View?) {
//                var router = Router()
//                router.closeFrom = 1
//                router.closeCount = 2
//                router.closeFromBottomToTop = false
//                router.name = "aaaa"
//                val map = HashMap<String, Int>()
//                map.put("xixi", xixi + 1)
//                router.params = map
//                router.open(this@MainActivity)
//            }
//
//        }, "的身份为是东方财富网而我是大伟大房产额挖坟第三方")
//        setHaveFragmentListener(this)
    }

    private var weexFragment: MainFragment? = null

    private fun initFragment() {

        weexFragment = MainFragment()
        val bundle = Bundle()
        bundle.putSerializable(Router.EXTRA_NAME, intent.getSerializableExtra(Router.EXTRA_NAME))
        weexFragment!!.setArguments(bundle)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                weexFragment).commit()
    }

    override fun refreshFragmentWeex() {
        weexFragment?.refreshWeex()
    }
}
