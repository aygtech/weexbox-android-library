package com.weexbox.core.model

/**
 * Author: Mario
 * Time: 2018/9/11 下午7:45
 * Description: This is JsOptions
 */

class JsOptions {

    // network
    var url: String? = null
    var method: String? = null
    var headers: Map<String, String>? = null
    var params: Map<String, Any>? = null
    var responseType: String? = null
//    var files: Array<UploadFile>?

    var name: String? = null

    //ExternalModule 相机相册使用options
    var enableCrop = false
    var isCircle = true
    var width = 100
    var height = 100
    var count: Int = 0
}