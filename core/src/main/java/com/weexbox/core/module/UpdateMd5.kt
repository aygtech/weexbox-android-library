package com.weexbox.core.module

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class Md5Realm: RealmObject() {

    @PrimaryKey
    var path: String? = null
    var md5: String? = null
}

internal class UpdateMd5 {

    var path: String? = null
    var md5: String? = null

    fun toRealm(): Md5Realm {
        val fileRealm = Md5Realm()
        fileRealm.path = path
        fileRealm.md5 = md5
        return fileRealm
    }
}

