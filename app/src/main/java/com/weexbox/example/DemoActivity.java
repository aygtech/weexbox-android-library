package com.weexbox.example;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.weexbox.core.WeexBoxEngine;
import com.weexbox.core.controller.WBBaseActivity;
import com.weexbox.core.router.Router;
import com.weexbox.core.update.UpdateManager;
import com.weexbox.core.util.LoadDialogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;

public class DemoActivity extends WBBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRouter().setNavBarHidden(true);
        setContentView(R.layout.activity_demo);

        WeexBoxEngine.INSTANCE.initFloatingBtn(DemoActivity.this, BtnSerivice.class);

        findViewById(R.id.oooo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, MainActivity.class);
                startActivity(intent);

//                Router router = new Router();
//                router.setUrl("index.js");
//                router.setName(Router.Companion.getNAME_WEEX());
//                router.open(DemoActivity.this);

//                Router router = new Router();
//                router.setNavBarHidden(true);
//                router.setUrl("login/page1.js");
//                router.setName("weex");
//                Map<String, Integer> params = new HashMap<String, Integer>();
//                params.put("id", 20);
//                router.setParams(params);
//                router.open(DemoActivity.this);


            }
        });

        checkNewJsVersionAction();
    }


    private Dialog dialog = null;

    private void checkNewJsVersionAction() {
        String url = "http://iquest-test92.aiyuangong.com/hotdeploy/cornerstone-update-url.txt";
        UpdateManager.INSTANCE.setServer(url);
        UpdateManager.INSTANCE.update(new Function4<UpdateManager.UpdateState, Integer, Throwable, File, Unit>() {
            @Override
            public Unit invoke(UpdateManager.UpdateState updateState, Integer integer, Throwable throwable, File file) {
                switch (updateState) {
                    // 解压缩过程，只有在第一次安装APP或者升级APP时内置版本比缓存版本高时才会出现。
                    // 建议在此做一个loading view
                    case Unzip:
//                        if (dialog == null) {
//                            dialog = showLoadingDialog();
//                        }
                        Logger.i("MainActivity", "integer:" + integer);
                        break;
                    // 已经做好进入APP的准备，建议在准生产和生产环境时让用户进入APP
                    // 可以将url存到全局静态变量供之后使用
                    case CanEnterApp:
                        Logger.i("MainActivity", "CanEnterApp:");
                        Logger.i("MainActivity", "CanEnterApp:file = " + file);
                        if (!BuildConfig.DEBUG) {
                            if (dialog != null) {
                                dialog.dismiss();
                                dialog = null;
                            }
                            startToActivity();
                        } else {
//                            if (dialog == null) {
//                                dialog = showLoadingDialog();
//                            }
                        }
                        break;
                    // 静默更新成功
                    // 建议在测试环境时让测试人员进入APP
                    case UpdateSuccess:
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        if (!isFinishing() && BuildConfig.DEBUG) {
                            startToActivity();
                        }
                        Logger.i("MainActivity", "UpdateSuccess");
                        break;
                    case GetServerError:
                        updateError("GetServerError");
                        break;
                    case DownloadConfigError:
                        updateError("DownloadConfigError");
                        break;
                    case DownloadMd5Error:
                        updateError("DownloadMd5Error");
                        break;
                    case DownloadFileError:
                        updateError("DownloadFileError");
                        break;
                }
                return null;
            }
        });
    }

    private void updateError(final String step) {
        Logger.i("MainActivity", step);
        if (dialog != null) {
            if (dialog.isShowing() && !isFinishing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        if (BuildConfig.DEBUG && !isFinishing()) {
            Toast.makeText(getApplicationContext(), "热更新在" + step + "步骤出错，请重启再试", Toast.LENGTH_LONG).show();
        }
    }

    private void startToActivity() {
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
