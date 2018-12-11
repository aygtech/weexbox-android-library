package com.weexbox.core.retrofit.interfaces

import okhttp3.Request

/**
 *Author:leon.wen
 *Time:2018/12/10   16:54
 *Description:This is HeaderInterceptor
 */
interface HeaderInterceptor{
    fun intercept(builder: Request.Builder)
}