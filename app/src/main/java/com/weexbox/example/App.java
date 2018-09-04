package com.weexbox.example;

import android.app.Application;

import com.weexbox.core.WeexBoxEngine;

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        WeexBoxEngine.INSTANCE.initialize(this);
    }

}
