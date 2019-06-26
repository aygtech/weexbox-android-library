package com.weexbox.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.controller.WBWeexFragment
import com.weexbox.core.extension.toJsonMap
import com.weexbox.core.model.Result
import com.weexbox.core.router.Router
import com.weexbox.core.util.ActivityManager
import com.weexbox.core.util.ToastUtil
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Author: Mario
 * Time: 2018/11/23 2:10 PM
 * Description: This is NormalActivity
 */

class NormalActivity : WBBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_normal)

        val weexFragment = WBWeexFragment()
        weexFragment.router.url = "http://dotwe.org/raw/dist/791c8507ae8f35a9e134abe8a776588d.bundle.wx"
//        weexFragment.router.url = "module1/page1.js"
        supportFragmentManager.beginTransaction().replace(R.id.weex_fragment, weexFragment).commit()

        getActionbar().setTitleText( {
            val mOkHttpClient = OkHttpClient()
            var formEncodingBuilder = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), "")
//                    formEncodingBuilder.add("mode", "2")
//            formEncodingBuilder.add("userName", "xixi")
//            formEncodingBuilder.add("password", "123123")

            var requestBuilder = Request.Builder()
                    .addHeader("X-Requested-With", "XMLHttpRequest");

//            if (info.headers != null && info?.headers?.size!! > 0) {
//                for (param in info?.headers!!) {
//                    requestBuilder.addHeader(param.key, param.value)
//                }
//            }

            var request: Request? = null
//            if (info.method?.toUpperCase() == "POST") {
                request = requestBuilder.url("http://10.1.1.12:8888/msg/sms/authCode?phoneNumber=18975190024")
                        .post(formEncodingBuilder)
                        .build()
//            } else{
//                request = requestBuilder.url(info.url!!)
//                        .get()
//                        .build()
//            }

            val call = mOkHttpClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("88888", "e = "+e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("88888", "res = "+response.body()?.string())
                }
            })
        }, "haode")
//
//
//        getActionbar().setRightButton ({
//
//                router.close(2, this)
//
//        },"haha ")
//
//
//        val size = ActivityManager.getInstance().allActivities.size
//        if (size == 5){
//            var timer = Timer()
//            timer.schedule(timerTask { router.close(2, this@NormalActivity) }, 3000)
//            timer.schedule(object : TimerTask(){
//                override fun run() {
//                    router.close(2, this@NormalActivity)
//                }
//
//            }, 3000)
//        }
    }
}