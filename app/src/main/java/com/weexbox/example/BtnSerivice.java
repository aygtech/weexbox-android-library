package com.weexbox.example;

import android.app.Activity;

import com.weexbox.core.service.FloatingBtnService;

import org.jetbrains.annotations.Nullable;

public class BtnSerivice extends FloatingBtnService{

    @Nullable
    @Override
    public Activity getCurrentActivity() {
        return App.Companion.getCurrentActivity();
    }
}
