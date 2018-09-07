package com.weexbox.core.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.weexbox.core.R
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.provider.Settings.canDrawOverlays
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi


abstract class FloatingBtnService : Service(){

    private var view: LinearLayout? = null

    // 获取到手机的窗体管理器
    private var wm: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * 把一个view对象显示到手机窗体上
     */
    fun showAddress() {
        val inflater = LayoutInflater.from(application)
        view = inflater.inflate(R.layout.toast_location, null) as LinearLayout
        // 给窗体上的view对象注册点击事件
        view!!.setOnClickListener {
            val integrator = IntentIntegrator(getCurrentActivity())
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan a barcode")
            //integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(true)
            integrator.setOrientationLocked(false)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }

        // 给窗体上的view对象注册触摸事件
        view!!.setOnTouchListener(object : View.OnTouchListener {
            internal var startX: Int = 0
            internal var startY: Int = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.i(TAG, "呼叫界面,更改位置+摸到")
                        startX = event.rawX.toInt()
                        startY = event.rawY.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        Log.i(TAG, "呼叫界面,更改位置+移动")
                        val newX = event.rawX.toInt()
                        val newY = event.rawY.toInt()
                        val dx = newX - startX
                        val dy = newY - startY
                        // 更改view对象在窗体上显示的位置.
                        params!!.x += dx
                        params!!.y += dy

                        if (params!!.x < 0) {
                            params!!.x = 0
                        }
                        if (params!!.y < 0) {
                            params!!.y = 0
                        }
                        if (params!!.x > wm!!.defaultDisplay.width) {
                            params!!.x = wm!!.defaultDisplay.width
                        }
                        if (params!!.y > wm!!.defaultDisplay.height) {
                            params!!.y = wm!!.defaultDisplay.height
                        }
                        wm!!.updateViewLayout(view, params)
                        // 重新初始化手指的位置
                        startX = event.rawX.toInt()
                        startY = event.rawY.toInt()
                    }
                    MotionEvent.ACTION_UP -> {
                        val lastx = params!!.x
                        val lasty = params!!.y
                        val sp = getSharedPreferences("config", Context.MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putInt("lastx", lastx)
                        editor.putInt("lasty", lasty)
                        editor.commit()
                    }
                }
                return false
            }
        })
        //         tv = (ImageView) view.findViewById
        //                (R.id.tv_toast_address);
        //        tv.setText(address);
        val sp = getSharedPreferences("config",
                Context.MODE_PRIVATE)
        params = WindowManager.LayoutParams()
        params!!.gravity = Gravity.TOP or Gravity.LEFT
        val lastx = sp.getInt("lastx", 0)
        val lasty = sp.getInt("lasty", 0)
        params!!.x = lastx
        params!!.y = lasty
        params!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        params!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        // 定义控件 可以触摸 删除一个flag
        params!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        params!!.format = PixelFormat.TRANSLUCENT
        // 定义窗体的类型 TYPE_PRIORITY_PHONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params!!.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE
        }
        wm!!.addView(view, params)
    }

    abstract fun getCurrentActivity(): Activity?

    override fun onCreate() {

        Log.i("ccccccc", "3333333333333333")
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

//        //8.0以后startForegroundService问题
//        val channel = NotificationChannel("1", "startForegroundService问题", NotificationManager.IMPORTANCE_HIGH)
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.createNotificationChannel(channel)
//        val notification = Notification.Builder(applicationContext, "1").build()
//        startForeground(1, notification)

        //8.0以后权限问题
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(intent)
                return
            } else {
                //Android6.0以上
                showAddress()
            }
        } else {
            //Android6.0以下，不用动态声明权限
            showAddress()
        }

        STATAG = "start"
        super.onCreate()
    }

    override fun onDestroy() {
        if (view != null) {
            wm!!.removeView(view)
            view = null
        }
        STATAG = "stop"
        super.onDestroy()
    }

    companion object {
        protected val TAG = "ShowFloatingBtnService"
        var STATAG = "stop"
    }
}