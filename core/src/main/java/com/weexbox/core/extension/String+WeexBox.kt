package com.weexbox.core.extension

import android.net.Uri
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*



/**
 * Author: Mario
 * Time: 2018/9/11 下午6:18
 * Description: This is `String+WeexBox`
 */

fun String.encodeURIComponent(): String {
    return URLEncoder.encode(this, "UTF-8")
}

fun String.decodeURIComponent(): String {
    return URLDecoder.decode(this, "UTF-8")
}

fun String.getParameters(): Map<String, String> {
    val query = getQuery()
    return query.queryToParameters()
}

fun String?.queryToParameters(): Map<String, String> {
    val results = TreeMap<String, String>()
    if (this == null) {
        return results
    }
    val parametersCombined = TreeMap<String, ArrayList<String>>()
    val pairs = split("&")
    for (pair in pairs) {
        val keyValue = pair.split("=")
        val key = keyValue[0].decodeURIComponent()
        val value = keyValue[1].decodeURIComponent()
        if (parametersCombined[key] != null) {
            parametersCombined[key]!!.add(value)
        } else {
            val values = ArrayList<String>()
            values.add(value)
            parametersCombined.put(key, values)
        }
    }
    for (key in parametersCombined.keys) {
        val values = parametersCombined[key]
        values!!.sort()
        var value = values[0]
        for (i in 1 until values.size) {
            value = value + key + values[i]
        }
        results[key] = value
    }
    return results
}



fun String?.getQuery(): String? {
    if (this == null) {
        return null
    }
    val index = indexOf("?")
    if (index > -1) {
        return substring(index + 1)
    }
    return null
}

///* 获取parameters里面的params */
//fun String.getParamsJsonObject(): JSONObject {
//    return JSON.parseObject(getParamsJsonString())
//}
//
//fun String?.getParamsJsonString(): String? {
//    if (this == null) {
//        return null
//    }
//    val queryParameters = getParameters()
//    return queryParameters["params"]
//}

fun String.getPath(): String {
    val uri = Uri.parse(this)
    return uri.path
}

fun String.getScheme(): String {
    val uri = Uri.parse(this)
    return uri.scheme
}

fun String.getHost(): String {
    val uri = Uri.parse(this)
    return uri.host
}

fun <T> String.toObject(clazz: Class<T>): T {
    return JSON.parseObject(this, clazz)
}

fun <T> String.toObject(type: TypeReference<T>): T {
    return JSON.parseObject(this, type)
}