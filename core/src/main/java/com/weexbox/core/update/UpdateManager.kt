package com.weexbox.core.update

import android.content.Context
import android.os.AsyncTask
import com.alibaba.fastjson.TypeReference
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.model.Md5Realm
import com.weexbox.core.model.UpdateConfig
import com.weexbox.core.model.UpdateMd5
import com.litesuits.common.io.FileUtils
import com.litesuits.common.io.IOUtils
import com.weexbox.core.extension.appendingPathComponent
import com.weexbox.core.extension.toObject
import com.weexbox.core.network.Network
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import okhttp3.ResponseBody
import org.zeroturnaround.zip.ZipUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

private typealias Completion = (state: UpdateManager.UpdateState, progress: Int, error: Throwable?, url: File?) -> Unit

object UpdateManager {

    enum class UpdateState {
        Unzip, // "解压文件"
        UnzipError, // "解压文件出错"
        UnzipSuccess, // "解压文件成功"
        GetServer, // "获取服务器路径"
        GetServerError, // "获取服务器路径出错"
        DownloadConfig, // "下载配置文件"
        DownloadConfigError, // "下载配置文件出错"
        DownloadConfigSuccess, // "下载配置文件成功"
        DownloadMd5, // "下载md5文件"
        DownloadMd5Error, // "下载Md5出错"
        DownloadMd5Success, // "下载md5文件成功"
        DownloadFile, // "下载文件"
        DownloadFileError, // "下载文件出错"
        DownloadFileSuccess, // "下载文件成功"
        UpdateSuccess, // "更新成功"
    }

    private var completion: Completion? = null

    private const val resourceName = "weexbox-update"
    private const val oneName = "update-one"
    private const val twoName = "update-two"
    private const val workingNameKey = "update-working-key"
    private const val sharedPreferencesName = "update-sharedPreferences"
    private val workingName = WeexBoxEngine.application.getSharedPreferences(sharedPreferencesName, 0).getString(workingNameKey, oneName)
    private var backupName = if (workingName == oneName) twoName else oneName
    private const val zipName = "www.zip"
    private const val md5Name = "update-md5.json"
    private const val configName = "update-config.json"
    private const val versionName = "update-version.txt"

    private const val resourceUrl = resourceName
    private val resourceConfigUrl = resourceUrl.appendingPathComponent(configName)
    private val resourceMd5Url = resourceUrl.appendingPathComponent(md5Name)
    private val resourceZipUrl = resourceUrl.appendingPathComponent(zipName)

    private val cahceUrl = WeexBoxEngine.application.getDir(resourceName, Context.MODE_PRIVATE)
    private val workingUrl = File(cahceUrl, workingName)
    private val workingConfigUrl = File(workingUrl, configName)

    private var backupUrl = File(cahceUrl, backupName)
    private var backupConfigUrl = File(backupUrl, configName)

    private lateinit var serverVersionUrl: String
    private lateinit var serverConfigUrl: String
    private lateinit var serverMd5Url: String
    // 设置更新服务器
    var serverUrl: String = ""
        set(url) {
            field = url
            serverVersionUrl = serverUrl.appendingPathComponent(versionName)
            serverConfigUrl = serverUrl.appendingPathComponent(configName)
            serverMd5Url = serverUrl.appendingPathComponent(md5Name)
        }
    private var serverWwwUrl: String = ""
        set(url) {
            field = url
            serverConfigUrl = serverWwwUrl.appendingPathComponent(configName)
            serverMd5Url = serverWwwUrl.appendingPathComponent(md5Name)
        }

    private val workingRealmConfig = RealmConfiguration.Builder().name("$workingName.realm").build()
    private val workingRealm = Realm.getInstance(workingRealmConfig)
    private var backupRealmConfig = RealmConfiguration.Builder().name("$backupName.realm").build()
    private var backupRealm = Realm.getInstance(backupRealmConfig)

    private lateinit var resourceConfig: UpdateConfig
    private lateinit var resourceMd5: List<UpdateMd5>
    private var workingConfig: UpdateConfig? = null
    private var backupConfig: UpdateConfig? = null
    private lateinit var serverConfigData: String

//    private interface DownloadService {
//        @GET("{url}")
//        fun get(@Path("url") url: String): Call<ResponseBody>
//    }
//    private lateinit var downloadService: DownloadService

    // 设置更新服务器
//    fun setServer(url: String) {
//        downloadService = Retrofit.Builder().baseUrl(url + File.separator).build().create(DownloadService::class.java)
//    }

    // 设置强制更新
    var forceUpdate = false


    // 检查更新
    fun update(completion: Completion) {
        if (forceUpdate) {
            backupName = workingName
            backupUrl = workingUrl
            backupConfigUrl = workingConfigUrl
            backupRealmConfig = workingRealmConfig
            backupRealm = workingRealm
        }
        this.completion = completion
        loadLocalConfig()
        loadLocalMd5()
        if (isWwwFolderNeedsToBeInstalled(workingConfig)) {
            // 将APP预置包解压到工作目录
            unzipWwwToWorking()
        } else {
            enterApp()
        }
    }

    // 获取完整路径
    fun getFullUrl(file: String): File {
        return workingUrl.resolve(file)
    }

    // 清空缓存
    fun clear() {
        workingRealm.executeTransaction { realm -> realm.deleteAll() }
        backupRealm.executeTransaction { realm -> realm.deleteAll() }
        FileUtils.deleteDirectory(cahceUrl)
    }

    // 将APP预置包解压到工作目录
    private fun unzipWwwToWorking() {
        unzipWww(workingUrl, resourceMd5, workingRealm)
    }

    private fun unzipWwwToBackup() {
        unzipWww(backupUrl, resourceMd5, backupRealm)
    }

    // 静默更新
    private fun update() {
        if (isWwwFolderNeedsToBeInstalled(backupConfig)) {
            // 将APP预置包解压到缓存目录
            unzipWwwToBackup()
        } else {
            getServer()
        }
    }

    private fun getServer() {
        complete(UpdateState.GetServer)
        Network.request(serverVersionUrl, Network.HTTPMethod.GET, null, null, object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                complete(UpdateState.GetServerError, 0, t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful == true) {
                    serverWwwUrl = serverUrl.appendingPathComponent(response.body()!!.string())
                    // 获取服务端config文件
                    downloadConfig()
                } else {
                    complete(UpdateState.GetServerError)
                }
            }
        })
    }

    private fun enterApp() {
        if (!forceUpdate) {
            // 返回工作目录地址
            complete(UpdateState.UpdateSuccess, 100, null, workingUrl)
        }
        // 开始静默更新
        update()
    }

    /// 加载本地的config
    private fun loadLocalConfig() {
        val inputStream = WeexBoxEngine.application.assets.open(resourceConfigUrl)
        resourceConfig = IOUtils.toString(inputStream, "UTF-8").toObject(UpdateConfig::class.java)
        workingConfig = loadConfig(workingConfigUrl)
        backupConfig = loadConfig(backupConfigUrl)
    }

    private fun loadConfig(url: File): UpdateConfig? {
        return try {
            FileUtils.readFileToString(url, "UTF-8").toObject(UpdateConfig::class.java)
        } catch (e: IOException) {
            null
        }
    }

    /// 加载本地的Md5
    private fun loadLocalMd5() {
        val inputStream = WeexBoxEngine.application.assets.open(resourceMd5Url)
        resourceMd5 = IOUtils.toString(inputStream, "UTF-8").toObject(object : TypeReference<List<UpdateMd5>>() {})
    }

    // 获取服务端config文件
    private fun downloadConfig() {
        complete(UpdateState.DownloadConfig)
        Network.request(serverConfigUrl, Network.HTTPMethod.GET, null, null, object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                complete(UpdateState.DownloadConfigError, 0, t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful == true) {
                    complete(UpdateState.DownloadConfigSuccess)
                    serverConfigData = response.body()!!.string()
                    val serverConfig = serverConfigData.toObject(UpdateConfig::class.java)
                    // 是否需要更新
                    if (shouldDownloadWww(serverConfig)) {
                        downloadMd5()
                    } else {
                        complete(UpdateState.UpdateSuccess, 100, null, backupUrl)
                    }
                } else {
                    complete(UpdateState.DownloadConfigError)
                }
            }
        })
    }

    private fun shouldDownloadWww(serverConfig: UpdateConfig): Boolean {
        val appBuild = VersionUtil.appVersionName
        if (VersionUtil.compareVersion(appBuild, serverConfig.android_min_version) >= 0 && VersionUtil.compareVersion(serverConfig.release, backupConfig!!.release) > 0) {
            return true
        }
        return false
    }

    private fun isWwwFolderNeedsToBeInstalled(config: UpdateConfig?): Boolean {
        if (config == null || VersionUtil.compareVersion(resourceConfig.release, config.release) > 0) {
            return true
        }
        return false
    }

    private fun unzipWww(to: File, md5: List<UpdateMd5>, db: Realm) {
        db.executeTransaction { realm -> realm.deleteAll() }
        UnzipTask().execute(to, md5, db)
    }

    private class UnzipTask : AsyncTask<Any, Int, List<Any>>() {

        override fun doInBackground(vararg params: Any?): List<Any> {
            val to = params[0] as File
            try {
                FileUtils.deleteDirectory(to)
                var unzipFilesCount = 0
                val inputStream = WeexBoxEngine.application.assets.open(resourceZipUrl)
                ZipUtil.iterate(inputStream) { `in`, zipEntry ->
                    val name = zipEntry.name
                    if (name != null) {
                        val file = File(to, name)
                        if (zipEntry.isDirectory) {
                            FileUtils.forceMkdir(file)
                        } else {
                            org.zeroturnaround.zip.commons.FileUtils.forceMkdir(file.parentFile)
                            org.zeroturnaround.zip.commons.FileUtils.copy(`in`, file)
                            if (to == workingUrl) {
                                var progress = (unzipFilesCount ++) * 100 / resourceMd5.size
                                if (progress > 100) {
                                    progress = 100
                                }
                                publishProgress(progress)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                val result = ArrayList<IOException>()
                result.add(e)
                return result
            }
            return params.toList() as List<Any>
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

            complete(UpdateState.Unzip, values[0]!!)
        }

        override fun onPostExecute(result: List<Any>) {
            super.onPostExecute(result)

            if (result.size == 1) {
                complete(UpdateState.UnzipError, 0, result[0] as IOException)
            }
            val to = result[0] as File
            val md5 = result[1] as List<UpdateMd5>
            val db = result[2] as Realm
            unzipSuccess(to, md5, db)
        }
    }

    // 解压www成功
    private fun unzipSuccess(to: File, md5: List<UpdateMd5>, db: Realm) {
        complete(UpdateState.UnzipSuccess, 100)
        // 保存Md5到数据库中
        saveMd5(md5, db)
        // 保存config
        val inputStream = WeexBoxEngine.application.assets.open(resourceConfigUrl)
        FileUtils.copyInputStreamToFile(inputStream, File(to, configName))
        loadLocalConfig()
        if (to == backupUrl) {
            getServer()
        } else {
            // 解压到了工作目录，可以进入App
            WeexBoxEngine.application.getSharedPreferences(sharedPreferencesName, 0).edit().putString(workingNameKey, workingName).commit()
            enterApp()
        }
    }

    private fun downloadMd5() {
        complete(UpdateState.DownloadMd5)
        Network.request(serverMd5Url, Network.HTTPMethod.GET, null, null, object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                complete(UpdateState.DownloadMd5Error, 0, t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful == true) {
                    complete(UpdateState.DownloadMd5Success)
                    val serverMd5: List<UpdateMd5> = response.body()!!.string().toObject(object : TypeReference<List<UpdateMd5>>(){})
                    DownloadFilesTask().execute(serverMd5)
                } else {
                    complete(UpdateState.DownloadMd5Error, 0)
                }
            }
        })
    }

    private class DownloadFilesTask : AsyncTask<List<UpdateMd5>, Any?, List<UpdateMd5>>() {

        override fun doInBackground(vararg params: List<UpdateMd5>?): List<UpdateMd5> {
            val serverMd5 = params[0]!!
            return getDownloadFiles(serverMd5)
        }

        override fun onPostExecute(result: List<UpdateMd5>) {
            super.onPostExecute(result)

            if (result.isNotEmpty()) {
                complete(UpdateState.DownloadFile)
                download(result)
            } else {
                saveConfig()
                complete(UpdateState.UpdateSuccess, 0, null, backupUrl)
            }
        }
    }

    private fun getDownloadFiles(serverMd5: List<UpdateMd5>): List<UpdateMd5> {
        val backupBackgroundRealm = Realm.getInstance(backupRealmConfig)
        val backupMd5 = backupBackgroundRealm.where(Md5Realm::class.java).findAll()
        val downloadFiles = ArrayList<UpdateMd5>()
        for (file in serverMd5) {
            if (shouldDownload(file, backupMd5)) {
                downloadFiles.add(file)
            }
        }
        return downloadFiles
    }

    private fun shouldDownload(serverFile: UpdateMd5, backupMd5: RealmResults<Md5Realm>): Boolean {
        for (backupFile in backupMd5) {
            if (backupFile.path == serverFile.path && backupFile.md5 == serverFile.md5) {
                return false
            }
        }
        return true
    }

    private fun download(files: List<UpdateMd5>, index: Int = 0, retry: Int = 5) {
        val progress = index * 100 / files.count()
        complete(UpdateState.DownloadFile, progress)
        if (index == files.size) {
            downloadSuccess(files)
        } else {
            val file = files[index]
            val path = file.path!!
            val destination = File(backupUrl, path)
            Network.request(serverWwwUrl.appendingPathComponent(path), Network.HTTPMethod.GET, null, null, object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    if (retry == 0) {
                        complete(UpdateState.DownloadFileError, 0, t)
                    } else {
                        download(files, index, retry - 1)
                    }
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if (response?.isSuccessful == true) {
                        FileUtils.writeByteArrayToFile(destination, response.body()!!.bytes())
                        downloadSuccess(file)
                        download(files, index + 1)
                    } else {
                        if (retry == 0) {
                            complete(UpdateState.DownloadFileError, 0)
                        } else {
                            download(files, index, retry - 1)
                        }
                    }
                }
            })
        }
    }

    private fun downloadSuccess(file: UpdateMd5) {
        backupRealm.executeTransaction { realm -> realm.insertOrUpdate(file.toRealm()) }
    }

    // 所有下载成功
    private fun downloadSuccess(files: List<UpdateMd5>) {
        complete(UpdateState.DownloadFileSuccess)
        saveMd5(files, backupRealm)
        saveConfig()
        WeexBoxEngine.application.getSharedPreferences(sharedPreferencesName, 0).edit().putString(workingNameKey, backupName).commit()
        complete(UpdateState.UpdateSuccess, 0, null, backupUrl)
    }

    private fun complete(state: UpdateState, progress: Int = 0, error: Throwable? = null, url: File? = null) {
        if (state == UpdateState.UpdateSuccess) {
            if (forceUpdate || (!forceUpdate && url == workingUrl)) {
                completion?.invoke(state, progress, error, url)
            }
        }
        else {
            completion?.invoke(state, progress, error, url)
        }
        if (state == UpdateState.UpdateSuccess || error != null) {
            completion = null
        }
    }

    private fun saveConfig() {
        FileUtils.writeStringToFile(backupConfigUrl, serverConfigData)
    }

    private fun saveMd5(files: List<UpdateMd5>, db: Realm) {
        db.executeTransaction { realm ->
            realm.deleteAll()
            for (file in files) {
                db.insert(file.toRealm())
            }
        }
    }

}