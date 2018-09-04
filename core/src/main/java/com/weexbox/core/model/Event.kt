package com.weexbox.core.model

/**
 * Author:leon.wen
 * Time:2018/7/25   17:14
 * Description:This is Event
 */
class Event<T> {
    private var code: Int = 0
        get() = this.code
        set(value) {
            field  = value
        }
    private var data: T? = null
        get() = this.data
        set(value) {
            field  = value
        }

    constructor(code: Int) {
        this.code = code
    }

    constructor(code: Int, data: T) {
        this.code = code
        this.data = data
    }

    constructor()


}
