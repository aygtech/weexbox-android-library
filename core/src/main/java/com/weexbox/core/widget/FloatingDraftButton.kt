package com.weexbox.core.widget

import android.content.Context
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.weexbox.core.util.AndroidUtil

/**
 *Author:leon.wen
 *Time:2018/12/12   14:13
 *Description:This is FloatingDraftButton
 */

class FloatingDraftButton : FloatingActionButton, View.OnTouchListener {

    private var lastX = 0
    private var lastY = 0
    private var originX = 0
    private var originY = 0
    private val screenWidth: Int
    private val screenHeight: Int
    private var floatingActionButtons = ArrayList<FloatingActionButton>()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        screenWidth = AndroidUtil.getScreenWidth(context)
        screenHeight = AndroidUtil.getScreenHeight(context)
        setOnTouchListener(this)
    }

    //注册归属的FloatingActionButton
    fun registerButton(floatingActionButton: FloatingActionButton) {
        floatingActionButtons.add(floatingActionButton)
    }

    fun getButtons(): ArrayList<FloatingActionButton> {
        return floatingActionButtons
    }

    fun getButtonSize(): Int {
        return floatingActionButtons.size
    }

    //是否可拖拽  一旦展开则不允许拖拽
    fun isDraftable(): Boolean {
        for (btn in floatingActionButtons) {
            if (btn.visibility == View.VISIBLE) {
                return false
            }
        }
        return true
    }

    //当被拖拽后其所属的FloatingActionButton 也要改变位置
    private fun slideButton(l: Int, t: Int, r: Int, b: Int) {
        for (floatingActionButton in floatingActionButtons) {
            floatingActionButton.layout(l, t, r, b)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (!isDraftable()) {
            return false
        }
        val ea = event.action
        when (ea) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX.toInt() // 获取触摸事件触摸位置的原始X坐标
                lastY = event.rawY.toInt()
                originX = lastX
                originY = lastY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX.toInt() - lastX
                val dy = event.rawY.toInt() - lastY
                var l = v.left + dx
                var b = v.bottom + dy
                var r = v.right + dx
                var t = v.top + dy
                // 下面判断移动是否超出屏幕
                if (l < 0) {
                    l = 0
                    r = l + v.width
                }
                if (t < 0) {
                    t = 0
                    b = t + v.height
                }
                if (r > screenWidth) {
                    r = screenWidth
                    l = r - v.width
                }
                if (b > screenHeight) {
                    b = screenHeight
                    t = b - v.height
                }
                v.layout(l, t, r, b)
                slideButton(l, t, r, b)
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                v.postInvalidate()
            }
            MotionEvent.ACTION_UP -> {
                val distance = event.rawX.toInt() - originX + event.rawY.toInt() - originY
                if (Math.abs(distance) < 20) {
                    //当变化太小的时候什么都不做 OnClick执行
                } else {
                    return true
                }
            }
        }
        return false

    }
}