package com.weexbox.core.util

import com.google.gson.Gson
import com.weexbox.core.router.Router

object GsonUtil {

    val gson = Gson()

    fun getWeexGson(info: Any, clazz: Class<*>): Any {
        return gson.fromJson(gson.toJson(info), clazz)
    }

    fun fromGson(info: String, clazz: Class<*>): Any {
        return gson.fromJson(info, clazz)
    }

    fun toGson(info: Any): String {
        return gson.toJson(info)
    }
}