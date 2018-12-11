package com.weexbox.core.retrofit.interfaces

import okhttp3.HttpUrl


/**
 *Author:leon.wen
 *Time:2018/12/10   16:45
 *Description:This is QueryParameterInterceptor
 */
interface QueryParameterInterceptor{
    fun intercept(builder: HttpUrl.Builder)
}