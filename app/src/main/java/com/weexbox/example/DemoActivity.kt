package com.weexbox.example

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast

import com.alibaba.fastjson.JSON
import com.orhanobut.logger.Logger
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.controller.WBBaseActivity
import com.weexbox.core.router.Router
import com.weexbox.core.update.UpdateManager
import com.weexbox.core.util.ToastUtil

import java.io.File
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask

import android.content.pm.PackageManager.PERMISSION_GRANTED

class DemoActivity : WBBaseActivity() {


    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router!!.navBarHidden = true
        setContentView(R.layout.activity_demo)

        WeexBoxEngine.initFloatingBtn(this@DemoActivity, BtnSerivice::class.java)

        findViewById<View>(R.id.oooo).setOnClickListener {
            //                Router.Companion.register("aaaa", MainActivity.class);
            //
            //                Router router = new  Router();
            //                router.setName("aaaa");
            //                Map<String, Integer> map = new HashMap<>();
            //                map.put("xixi", 1);
            //                router.setParams(map);
            //                router.open(DemoActivity.this);

            //                Router router = new Router();
            //                router.setName("web");
            //                router.setUrl("https://www.baidu.com");
            //                router.open(DemoActivity.this);
            //
            //                Intent intent = new Intent(DemoActivity.this,BrowserActivity.class);
            //                intent.putExtra("param_url","https://www.baidu.com");
            //                startActivity(intent);
            //                Router router = new Router();
            //                router.setUrl("index.js");
            //                router.setName(Router.Companion.getNAME_WEEX());
            //                router.open(DemoActivity.this);

            val router = Router()
            router.navBarHidden = false
            router.url = "login/page1.js"
            router.name = "weex"
            val params = HashMap<String, Int>()
            params["id"] = 20
            router.params = params
            router.open(this@DemoActivity)

            //                String data = "{\"message\":\"哈哈\",\"data\":\"\",\"code\":400204}";
            //                ToastUtil.showShortToast(getApplicationContext(), ((Map<String, String>)JSON.parse(data)).get("message"));

            //                Intent intent = new Intent();
            //                intent.setAction(Intent.ACTION_VIEW);
            //                intent.setData(Uri.parse("iquest://videodetails?id=39&source=web"));
            //                startActivity(intent);
        }

        checkNewJsVersionAction()
    }

    private fun checkNewJsVersionAction() {
        val hotdeployUrl = "https://iquest-test.aiyuangong.com/hotdeploy"
        UpdateManager.serverUrl = hotdeployUrl
        UpdateManager.forceUpdate = true
        UpdateManager.update { state, progress, error, url ->
            when (state) {
                // 解压缩过程，只有在第一次安装APP或者升级APP时内置版本比缓存版本高时才会出现。
                // 建议在此做一个loading view
                UpdateManager.UpdateState.Unzip ->
                    //                        if (dialog == null) {
                    //                            dialog = showLoadingDialog();
                    //                        }
                    Logger.i("MainActivity", "progress:$progress")
                // 静默更新成功
                // 建议在测试环境时让测试人员进入APP
                UpdateManager.UpdateState.UpdateSuccess -> {
                    if (dialog != null) {
                        dialog!!.dismiss()
                        dialog = null
                    }
                    if (!isFinishing && BuildConfig.DEBUG) {
                        startToActivity()
                    }
                    Logger.i("MainActivity", "UpdateSuccess")
                }
                UpdateManager.UpdateState.GetServerError -> updateError("GetServerError")
                UpdateManager.UpdateState.DownloadConfigError -> updateError("DownloadConfigError")
                UpdateManager.UpdateState.DownloadMd5Error -> updateError("DownloadMd5Error")
                UpdateManager.UpdateState.DownloadFileError -> updateError("DownloadFileError")
            }
        }
    }

    private fun updateError(step: String) {
        Logger.i("MainActivity", step)
        if (dialog != null) {
            if (dialog!!.isShowing && !isFinishing) {
                dialog!!.dismiss()
            }
            dialog = null
        }
        if (BuildConfig.DEBUG && !isFinishing) {
            Toast.makeText(applicationContext, "热更新在" + step + "步骤出错，请重启再试", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 100) {
            var allow = true
            for (result in grantResults) {
                if (result != PERMISSION_GRANTED) {
                    allow = false
                    break
                }
            }
        }
    }

    private fun startToActivity() {
        //        initFloatingBtn();
        //        Intent intent = new Intent();
        //        Uri data = getIntent().getData();
        //        if (data != null) {
        //            intent.setData(data);
        //            intent.setClass(MainActivity.this, WeexPageActivity.class);
        //            intent.putExtra("from", "splash");
        //            startActivity(intent);
        //        } else {
        //            boolean isLogin = UserModule.checkWhetherIsLogin();
        //            if (isLogin) {
        //                intent.setClass(MainActivity.this, MainActivity.class);
        //                startActivity(intent);
        //            } else {
        //                InvokeModule.startToLoginActivity(this);
        //            }
        //        }
        //
        //        finish();
    }
}
