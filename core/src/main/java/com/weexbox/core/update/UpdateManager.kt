package com.weexbox.core.update

import android.content.Context
import android.os.AsyncTask
import com.weexbox.core.WeexBoxEngine
import com.weexbox.core.model.Md5Realm
import com.weexbox.core.model.UpdateConfig
import com.weexbox.core.model.UpdateMd5
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.litesuits.common.io.FileUtils
import com.litesuits.common.io.IOUtils
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import okhttp3.ResponseBody
import org.zeroturnaround.zip.ZipUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File
import java.io.IOException
import retrofit2.Retrofit



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
        CanEnterApp, // "可以进入App"
    }

    private var completion: Completion? = null

    private const val resourceName = "www"
    private const val oneName = "update-one"
    private const val twoName = "update-two"
    private const val workingNameKey = "update-working-key"
    private const val sharedPreferencesName = "update-sharedPreferences"
    private val workingName = WeexBoxEngine.application.applicationContext.getSharedPreferences(sharedPreferencesName, 0).getString(workingNameKey, oneName)
    private val cacheName = if (workingName == oneName) twoName else oneName
    private const val zipName = "www.zip"
    private const val md5Name = "update-md5.json"
    private const val configName = "update-config.json"

    private const val resourceUrl = resourceName
    private val resourceConfigUrl = resourceUrl + File.separator + configName
    private val resourceMd5Url = resourceUrl + File.separator + md5Name
    private val resourceZipUrl = resourceUrl + File.separator + zipName

    private val workingUrl = WeexBoxEngine.application.applicationContext.getDir(workingName, Context.MODE_PRIVATE)
    private val workingConfigUrl = File(workingUrl, configName)

    private val cacheUrl = WeexBoxEngine.application.applicationContext.getDir(cacheName, Context.MODE_PRIVATE)
    private val cacheConfigUrl = File(cacheUrl, configName)

//    private lateinit var serverConfigUrl: String
//    private lateinit var serverMd5Url: String
    // 设置更新服务器
    private lateinit var serverUrl: String
//        set(url) {
//            field = url
//            serverConfigUrl = serverUrl + File.separator + configName
//            serverMd5Url = serverUrl + File.separator + md5Name
//        }
    private lateinit var serverWwwFile: String
    private lateinit var serverWwwName: String


    private val workingRealm = Realm.getInstance(RealmConfiguration.Builder().name("$workingName.realm").build())
    private val cacheRealmConfig = RealmConfiguration.Builder().name("$cacheName.realm").build()
    private val cacheRealm = Realm.getInstance(cacheRealmConfig)

    private lateinit var resourceConfig: UpdateConfig
    private lateinit var resourceMd5: List<UpdateMd5>
    private var workingConfig: UpdateConfig? = null
    private var cacheConfig: UpdateConfig? = null
    private lateinit var serverConfigData: String

    private val gson = Gson()
    private interface DownloadService {
        @GET("{url}")
        fun get(@Path("url") url: String): Call<ResponseBody>
    }
    private lateinit var downloadService: DownloadService

    // 设置更新服务器
    fun setServer(url: String) {
        val host = url.substringBeforeLast(File.separator)
        serverWwwFile = url.substringAfterLast(File.separator)
        downloadService = Retrofit.Builder().baseUrl(host + File.separator).build().create(DownloadService::class.java)
    }

    // 检查更新
    fun update(completion: Completion) {
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

    // 结束回调
    fun noMoreCallback() {
        completion = null
    }

    // 将APP预置包解压到工作目录
    private fun unzipWwwToWorking() {
        unzipWww(workingUrl, resourceMd5, workingRealm)
    }

    private fun unzipWwwToCache() {
        unzipWww(cacheUrl, resourceMd5, cacheRealm)
    }

    // 静默更新
    private fun update() {
        if (isWwwFolderNeedsToBeInstalled(cacheConfig)) {
            // 将APP预置包解压到缓存目录
            unzipWwwToCache()
        } else {
            getServer()
        }
    }

    private fun getServer() {
        complete(UpdateState.GetServer)

        downloadService.get(serverWwwFile).enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                complete(UpdateState.GetServerError, 0, t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful == true) {
                    serverWwwName = response.body()!!.string()
                    // 获取服务端config文件
                    downloadConfig()
                } else {
                    complete(UpdateState.GetServerError)
                }
            }
        })
    }

    private fun enterApp() {
        // 返回工作目录地址
        complete(UpdateState.CanEnterApp, 100, null, workingUrl)
        // 开始静默更新
        update()
    }

    /// 加载本地的config
    private fun loadLocalConfig() {
        val inputStream = WeexBoxEngine.application.applicationContext.assets.open(resourceConfigUrl)
        resourceConfig = gson.fromJson(IOUtils.toString(inputStream, "UTF-8"), UpdateConfig::class.java)
        workingConfig = loadConfig(workingConfigUrl)
        cacheConfig = loadConfig(cacheConfigUrl)
    }

    private fun loadConfig(url: File): UpdateConfig? {
        return try {
            gson.fromJson(FileUtils.readFileToString(url, "UTF-8"), UpdateConfig::class.java)
        } catch (e: IOException) {
            null
        }
    }

    /// 加载本地的Md5
    private fun loadLocalMd5() {
        val inputStream = WeexBoxEngine.application.applicationContext.assets.open(resourceMd5Url)
        resourceMd5 = gson.fromJson(IOUtils.toString(inputStream, "UTF-8"), object: TypeToken<List<UpdateMd5>>(){}.type)
    }

    // 获取服务端config文件
    private fun downloadConfig() {
        complete(UpdateState.DownloadConfig)
        downloadService.get(serverWwwName + File.separator + configName).enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                complete(UpdateState.DownloadConfigError, 0, t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful == true) {
                    complete(UpdateState.DownloadConfigSuccess)
                    serverConfigData = response.body()!!.string()
                    val serverConfig = gson.fromJson(serverConfigData, UpdateConfig::class.java)
                    // 是否需要更新
                    if (shouldDownloadWww(serverConfig)) {
                        downloadMd5()
                    } else {
                        complete(UpdateState.UpdateSuccess, 100, null, workingUrl)
                    }
                } else {
                    complete(UpdateState.DownloadConfigError)
                }
            }
        })
    }

    private fun shouldDownloadWww(serverConfig: UpdateConfig): Boolean {
        val appBuild = VersionUtil.appVersionName
        if (VersionUtil.compareVersion(appBuild, serverConfig.android_min_version) >= 0 && VersionUtil.compareVersion(serverConfig.release, cacheConfig!!.release) > 0) {
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
                val inputStream = WeexBoxEngine.application.applicationContext.assets.open(resourceZipUrl)
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
        val inputStream = WeexBoxEngine.application.applicationContext.assets.open(resourceConfigUrl)
        FileUtils.copyInputStreamToFile(inputStream, File(to, configName))
        loadLocalConfig()
        if (to == cacheUrl) {
            getServer()
        } else {
            // 解压到了工作目录，可以进入App
            WeexBoxEngine.application.applicationContext.getSharedPreferences(sharedPreferencesName, 0).edit().putString(workingNameKey, workingName).commit()
            enterApp()
        }
    }

    private fun downloadMd5() {
        complete(UpdateState.DownloadMd5)
        downloadService.get(serverWwwName + File.separator + md5Name).enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                complete(UpdateState.DownloadMd5Error, 0, t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful == true) {
                    complete(UpdateState.DownloadMd5Success)
                    val serverMd5: List<UpdateMd5> = gson.fromJson(response.body()!!.string(), object: TypeToken<List<UpdateMd5>>(){}.type)
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
                complete(UpdateState.UpdateSuccess, 0, null, cacheUrl)
            }
        }
    }

    private fun getDownloadFiles(serverMd5: List<UpdateMd5>): List<UpdateMd5> {
        val cacheBackgroundRealm = Realm.getInstance(cacheRealmConfig)
        val cacheMd5 = cacheBackgroundRealm.where(Md5Realm::class.java).findAll()
        val downloadFiles = ArrayList<UpdateMd5>()
        for (file in serverMd5) {
            if (shouldDownload(file, cacheMd5)) {
                downloadFiles.add(file)
            }
        }
        return downloadFiles
    }

    private fun shouldDownload(serverFile: UpdateMd5, cacheMd5: RealmResults<Md5Realm>): Boolean {
        for (cacheFile in cacheMd5) {
            if (cacheFile.path == serverFile.path && cacheFile.md5 == serverFile.md5) {
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
            val destination = File(cacheUrl, path)
            downloadService.get(serverWwwName + File.separator + path).enqueue(object: Callback<ResponseBody> {
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
        cacheRealm.executeTransaction { realm -> realm.insertOrUpdate(file.toRealm()) }
    }

    // 所有下载成功
    private fun downloadSuccess(files: List<UpdateMd5>) {
        complete(UpdateState.DownloadFileSuccess)
        saveMd5(files, cacheRealm)
        saveConfig()
        WeexBoxEngine.application.applicationContext.getSharedPreferences(sharedPreferencesName, 0).edit().putString(workingNameKey, cacheName).commit()
        complete(UpdateState.UpdateSuccess, 0, null, cacheUrl)
    }

    private fun complete(state: UpdateState, progress: Int = 0, error: Throwable? = null, url: File? = null) {
        completion?.invoke(state, progress, error, url)
    }

    private fun saveConfig() {
        FileUtils.writeStringToFile(cacheConfigUrl, serverConfigData)
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