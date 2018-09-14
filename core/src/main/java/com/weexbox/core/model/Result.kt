package com.weexbox.core.model

import com.weexbox.core.extension.toJsonMap
import java.util.*

/**
 * Author: Mario
 * Time: 2018/9/11 下午8:01
 * Description: This is Result
 */

typealias JsResult = Map<String, Any>?
typealias Callback = (Result) -> Unit

class Result {

    companion object {
        const val success = 0
        const val error = -1
    }

    var code: Int = Result.success
    var data: MutableMap<String, Any> = TreeMap()
    var error: String? = null
    var progress: Int? = null

    fun toJsResult(): JsResult {
        return toJsonMap()
    }
}