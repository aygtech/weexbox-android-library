package com.weexbox.core.component

import android.animation.Animator
import android.content.Context
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.annotation.JSMethod
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

    companion object {
        const val eventNameEnd = "end"
    }

    var isSendEnd = false

    override fun initComponentHostView(context: Context): LottieAnimationView {
        return LottieAnimationView(context)
    }

    override fun onHostViewInitialized(host: LottieAnimationView?) {
        super.onHostViewInitialized(host)

        hostView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {

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

    override fun addEvent(type: String?) {
        when (type) {
            LottieComponent.eventNameEnd -> isSendEnd = true
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

    fun sendEnd(complete: Boolean) {
        if (isSendEnd) {
            val result = Result()
            result.data["complete"] = complete
            fireEvent(LottieComponent.eventNameEnd, result.toJsResult())
        }
    }

    @JSMethod(uiThread = false)
    fun isAnimationPlaying(): Boolean {
        return hostView.isAnimating
    }

    @JSMethod(uiThread = false)
    fun playFromProgress(fromProgress: Any, toProgress: Any) {
        hostView.setMinAndMaxProgress(WXUtils.getFloat(fromProgress), WXUtils.getFloat(toProgress))
        hostView.playAnimation()
    }

    @JSMethod(uiThread = false)
    fun playFromFrame(fromFrame: Any, toFrame: Any) {
        hostView.setMinFrame(WXUtils.getInt(fromFrame))
        hostView.setMaxFrame(WXUtils.getInt(toFrame))
        hostView.playAnimation()
    }

    @JSMethod(uiThread = false)
    fun play() {
        playFromProgress(0, 1)
    }

    @JSMethod(uiThread = false)
    fun pause() {
        hostView.pauseAnimation()
    }

    @JSMethod(uiThread = false)
    fun stop() {
        hostView.cancelAnimation()
    }
}