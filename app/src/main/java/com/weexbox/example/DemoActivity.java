package com.weexbox.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.weexbox.core.WeexBoxEngine;
import com.weexbox.core.controller.WBBaseActivity;
import com.weexbox.core.service.FloatingBtnService;

public class DemoActivity extends WBBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);

        WeexBoxEngine.INSTANCE.initFloatingBtn(DemoActivity.this, BtnSerivice.class);

        findViewById(R.id.oooo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
