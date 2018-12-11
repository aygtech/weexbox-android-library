package com.weexbox.core.retrofit
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.retrofit.interfaces.HeaderInterceptor
import com.weexbox.core.retrofit.interfaces.QueryParameterInterceptor
import okhttp3.*
import retrofit2.Converter
import java.io.File
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


/**
 *Author:leon.wen
 *Time:2018/12/10   14:19
 *Description:This is HttpApi
 */
class HttpApi private constructor() {
    // Retrofit 对象
    private var mRetrofit = getRetrofit()
    private var baseUrl = ""

    companion object {
        val instance: HttpApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpApi()
        }
        var queryParameterInterceptor: QueryParameterInterceptor? = null  //统一参数拦截器
        var headerInterceptor: HeaderInterceptor? = null //统一头部信息数拦截器
        var readTimeout = 60L
        var writeTimeout = 60L
        var connectTimeout = 60L
        var converterFactory: Converter.Factory? = null //自定义转换器，可自定义加解密或者json转换器
    }

    private fun getRetrofit(): Retrofit {
        // 获取retrofit的实例
        return Retrofit.Builder()
                .baseUrl(baseUrl)  //自己配置
                .client(getOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create()) //添加返回为字符串的支持
                .addConverterFactory(getConverterFactory())
                .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        //设置 请求的缓存的大小跟位置
        val cacheFile = File(WeexBoxEngine.application.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小

        return OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    if (queryParameterInterceptor != null) {
                        val originalRequest = chain.request()
                        val request: Request
                        var newBuilder = originalRequest.url().newBuilder()
                        queryParameterInterceptor!!.intercept(newBuilder)
                        request = originalRequest.newBuilder().url(newBuilder.build()).build()
                        chain.proceed(request)
                    } else {
                        chain.proceed(chain.request())
                    }
                })  //参数添加
                .addInterceptor(Interceptor { chain ->
                    if (headerInterceptor != null) {
                        val originalRequest = chain.request()
                        var newBuilder = originalRequest.newBuilder()
                        headerInterceptor!!.intercept(newBuilder)
                        chain.proceed(newBuilder.build())
                    } else {
                        chain.proceed(chain.request())
                    }
                }) // 设置头信息
//              .addInterceptor(addCacheInterceptor())
//                .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应度看到
                .cache(cache)  //添加缓存
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build()
    }

    private fun getConverterFactory(): Converter.Factory {
        if (converterFactory != null) {
            return converterFactory!!
        } else {
            return GsonConverterFactory.create()
        }
    }

    /**
     * 转换为对象的Service
     * @param service
     * @param <T>
     * @return 传入的类型
     */
    fun <T> create(service: Class<T>): T {
        return mRetrofit.create(service);
    }

}