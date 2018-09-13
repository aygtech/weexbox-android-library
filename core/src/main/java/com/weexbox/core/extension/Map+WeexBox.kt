package com.weexbox.core.extension

import com.alibaba.fastjson.JSON
import java.util.*

/**
 * Author: Mario
 * Time: 2018/9/11 下午6:16
 * Description: This is `Map+WeexBox`
 */

fun Map<String, Any>.toQuery(): String {
    val components: MutableList<Pair<String, String>> = java.util.ArrayList()
    for (key in this.keys.sorted()) {
        val value = this[key]!!
        components.addAll(kotlin.Pair(key, value).queryComponents())
    }

    return components.map { "${it.first}=${it.second}" }.joinToString("&")
}

fun Pair<String, Any>.queryComponents(): List<Pair<String, String>> {
    val key = first
    val value = second

    val components: MutableList<Pair<String, String>> = ArrayList()

    val dictionary = value as? Map<String, Any>
    val array = value as? List<Any>
    val bool = value as? Boolean
    if (dictionary != null) {
        for ((nestedKey, value) in dictionary) {
            components.addAll(Pair("$key[$nestedKey]", value).queryComponents())
        }
    } else if (array != null) {
        for (value in array) {
            components.addAll(Pair("$key[]", value).queryComponents())
        }
    } else if (bool != null) {
        components.add(Pair(key.encodeURIComponent(), (if (bool) "1" else "0").encodeURIComponent()))
    } else {
        components.add(Pair(key.encodeURIComponent(), value.toString().encodeURIComponent()))
    }

    return components
}

fun Any.toJsonString(): String {
    return JSON.toJSONString(this)
}

fun Any.toJsonMap(): MutableMap<String, Any> {
    if (this is String) {
        return JSON.parseObject(this).toMutableMap()
    } else {
        return JSON.parseObject(toJsonString()).toMutableMap()
    }
}

fun <T> Map<String, Any>.toObject(clazz: Class<T>): T {
    return JSON.parseObject(JSON.toJSONString(this), clazz)
}