package com.weexbox.example;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.weexbox.core.WeexBoxEngine;
import com.weexbox.core.router.Router;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private static App application = null;
    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        WeexBoxEngine.INSTANCE.setup(this);
        registerActivityLifecycleCallbacks(this);

        Router.Companion.register(Router.Companion.getNAME_WEEX(), MainActivity.class);
    }

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(this);

        super.onTerminate();
    }


    public static Activity getCurrentActivity() {
        return application.currentActivity;
    }



    //获取当前activity生命周期
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }
    @Override
    public void onActivityStarted(Activity activity) {
    }
    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }
    @Override
    public void onActivityPaused(Activity activity) {
    }
    @Override
    public void onActivityStopped(Activity activity) {
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
