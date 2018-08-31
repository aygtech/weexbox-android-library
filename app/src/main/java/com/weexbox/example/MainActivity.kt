package com.weexbox.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.weexbox.core.controller.WBBaseActivity

class MainActivity : WBBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
