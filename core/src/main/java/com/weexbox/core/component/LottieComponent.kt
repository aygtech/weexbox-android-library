package com.weexbox.core.component

import android.animation.Animator
import android.content.Context
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.ui.action.BasicComponentData
import com.taobao.weex.ui.component.WXVContainer
import com.taobao.weex.utils.WXUtils
import com.weexbox.core.model.Result

/**
 * Author: Mario
 * Time: 2019/1/16 6:13 PM
 * Description: This is LottieComponent
 */

class LottieComponent(instance: WXSDKInstance?, parent: WXVContainer<*>?, basicComponentData: BasicComponentData<*>?) : BaseComponent<LottieAnimationView>(instance, parent, basicComponentData) {

    var callback: JSCallback? = null

    override fun initComponentHostView(context: Context): LottieAnimationView {
        return LottieAnimationView(context)
    }

    override fun onHostViewInitialized(host: LottieAnimationView?) {
        super.onHostViewInitialized(host)

        hostView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                complete(true)
            }

            override fun onAnimationCancel(animation: Animator?) {
                complete(false)
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })
        loadSource(attrs)
        applyProperties(attrs)
    }

    override fun updateAttrs(attrs: MutableMap<String, Any>?) {
        super.updateAttrs(attrs)

        if (loadSource(attrs!!)) {
            applyProperties(this.attrs)
        } else {
            applyProperties(attrs)
        }
    }

    fun loadSource(attributes: MutableMap<String, Any>): Boolean {
        val sourceJson = attributes["sourceJson"]
        val sourceUrl = attributes["sourceUrl"]
        if (sourceJson != null) {
            hostView.setAnimationFromJson(WXUtils.getString(sourceJson, null), null)
            return true
        } else if (sourceUrl != null) {
            hostView.setAnimationFromUrl(WXUtils.getString(sourceUrl, null))
            return true
        }
        return false
    }

    fun applyProperties(attributes: MutableMap<String, Any>) {
        val speed = attributes["speed"]
        if (speed != null) {
            hostView.speed = WXUtils.getFloat(speed)
        }
        val loop = attributes["loop"]
        if (WXUtils.getBoolean(loop, false)) {
            hostView.repeatCount = LottieDrawable.INFINITE
        }
        var scaleType: ImageView.ScaleType? = null
        when (WXUtils.getString(attributes["resizeMode"], null)) {
            "cover" -> scaleType = ImageView.ScaleType.CENTER_CROP
            "contain" -> scaleType = ImageView.ScaleType.CENTER_INSIDE
            "center" -> scaleType = ImageView.ScaleType.CENTER
        }
        if (scaleType != null) {
            hostView.scaleType = scaleType
        }
    }

    fun complete(complete: Boolean) {
        val result = Result()
        result.data["complete"] = complete
        callback?.invoke(result)
    }

    @JSMethod(uiThread = true)
    fun isAnimationPlaying(): Boolean {
        return hostView.isAnimating
    }

    @JSMethod(uiThread = true)
    fun playFromProgress(fromProgress: Any, toProgress: Any, callback: JSCallback?) {
        this.callback = callback
        hostView.setMinAndMaxProgress(WXUtils.getFloat(fromProgress), WXUtils.getFloat(toProgress))
        hostView.playAnimation()
    }

    @JSMethod(uiThread = true)
    fun playFromFrame(fromFrame: Any, toFrame: Any, callback: JSCallback?) {
        this.callback = callback
        hostView.setMinFrame(WXUtils.getInt(fromFrame))
        hostView.setMaxFrame(WXUtils.getInt(toFrame))
        hostView.playAnimation()
    }

    @JSMethod(uiThread = true)
    fun play(callback: JSCallback?) {
        this.callback = callback
        hostView.resumeAnimation()
    }

    @JSMethod(uiThread = true)
    fun pause() {
        hostView.pauseAnimation()
    }

    @JSMethod(uiThread = true)
    fun stop() {
        hostView.cancelAnimation()
        hostView.progress = 0F
    }
}