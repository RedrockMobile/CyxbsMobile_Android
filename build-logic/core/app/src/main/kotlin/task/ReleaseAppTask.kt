package task

import config.Config
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import task.net.ReleaseData
import task.net.TaskService
import java.io.File

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/11/11
 * @Description: 一键发版的task,执行任务过程无异常处理,异常都将抛到控制台。
 * 使用方式（三种）：
 * 1.右侧gradle栏的Run Configurations下找到releaseApp(在module_app下Task下的other也能找到)这个任务名称，点击执行
 * 2.或者右侧gradle有个搜索task的选项，搜索releaseApp点击执行
 * 3.命令行执行./gradlew releaseApp
 */
open class ReleaseAppTask : DefaultTask() {
    //发版的token由运维下发
    private val okHttpClient = OkHttpClient.Builder().addInterceptor {
        it.proceed(
            it.request()
                .newBuilder()
                .header(
                    "token",
                    File("${project.rootDir}/build-logic/secret/release-token.txt").readText()
                )
                .build()
        )
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://app.redrock.team")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val netService = retrofit.create(TaskService::class.java)

    @TaskAction
    fun taskAction() {
        val apk = getApkFile()
        apk?.let {
            val lastVersion = getUpdateContent()
            //忘记改updateContent和versionName的情况
            if (lastVersion.versionCode < Config.versionCode &&
                (lastVersion.updateContent == Config.updateContent || lastVersion.versionName == Config.versionName)) {
                throw RuntimeException("当前版本更新信息或版本号与上个版本无变化，请更新新版本的更新信息或版本号再发版")
                //versionCode相同，name和content不同时
            } else if (lastVersion.versionCode == Config.versionCode &&
                (lastVersion.versionName != Config.versionName || lastVersion.updateContent != Config.updateContent)
            ) {
                val input = project.findProperty("input")
                if (input != null && input == "Yes") {
                    releaseApp(apk)
                } else {
                    throw RuntimeException("当前的versionCode已经上线，但versionName与updateContent和线上不一样，如果确定要上传请命令行执行:./gradlew releaseApp -P input=\"Yes\"")
                }
            } else {
                releaseApp(apk)
            }
        } ?: throw RuntimeException(
            "获取apk失败！请检查module_app\\build\\channel路径下是否有apk文件，如果没有，请执行腾讯多渠道打包channelRelease任务后再发版。"
        )
    }

    private fun releaseApp(apk: File) {
        val apkUrl = uploadApk(apk)
        println("发版成功:${postUpdateContent(apkUrl)}\n请及时更新github的tag!!!")
    }

    /**
     * 上传apk
     */
    private fun uploadApk(apk: File): String {
        val filePart = MultipartBody.Part.createFormData(
            "file", apk.name,
            apk.asRequestBody("application/octet-stream".toMediaType())
        )
        val response = netService.uploadApk(filePart).execute()
        if (!response.isSuccessful) throw RuntimeException("apk上传失败:message:${response.message()},code:${response.code()}")
        val apkUploadData = response.body()!!
        if (apkUploadData.ok) {
            println("上传apk成功:$apkUploadData")
            return apkUploadData.data
        } else {
            throw RuntimeException("上传apk失败:$apkUploadData")
        }
    }

    /**
     * 上传更新信息
     */
    private fun postUpdateContent(apkUrl: String): ReleaseData {
        val data = ReleaseData(apkUrl)
        val response = netService.postUpdateContent(data).execute()
        if (!response.isSuccessful) throw RuntimeException("发版信息上传失败:message:${response.message()},code:${response.code()}")
        return response.body()!!
    }

    /**
     * 获取线上版本信息
     */
    private fun getUpdateContent(): ReleaseData {
        val response = netService.getUpdateContent().execute()
        if (!response.isSuccessful) throw RuntimeException("获取更新信息失败:message:${response.message()},code:${response.code()}")
        return response.body()!!
    }

    /**
     * 获取腾讯多渠道打包文件
     */
    private fun getApkFile(): File? {
        val apks = File("${project.buildDir.path}\\channel").listFiles()
        for (apk: File in apks) {
            if (apk.name.matches("掌上重邮-${Config.versionName}-official-release-\\d+-\\d+\\.apk".toRegex()))
                return apk
        }
        return null
    }
}