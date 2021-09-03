package com.mredrock.cyxbs.common.config

import androidx.core.content.edit
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.util.*


/**
 * 邮票任务的名称, 分别会跳转到不同模块中的界面, 而且完成任务后需要向后端发送进度(以任务名称为请求), 所以写在这里
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/9/3
 * @time 0:33
 */
object StoreTask {

    enum class TaskType(val type: String) {
        BASE("base"),
        MORE("more")
    }

    enum class Task(val title: String, val type: TaskType) {
        // 以下跳转到 module_qa 的 DailySignActivity
        DAILY_SIGN("今日签到", TaskType.BASE),

        // 以下跳转到 module_qa 的 DynamicFragment
        SEE_DYNAMIC("逛逛邮问", TaskType.BASE), // 浏览动态
        PUBLISH_DYNAMIC("斐然成章", TaskType.BASE), // 发布动态
        SHARE_DYNAMIC("围观吃瓜", TaskType.BASE), // 分享动态
        POST_COMMENT("能说会道", TaskType.BASE), // 发布评论
        GIVE_A_LIKE("拍案叫绝", TaskType.BASE), // 点赞

        // 以下跳转到 module_mine 的 EditInfoActivity
        EDIT_INFO("完善个人信息", TaskType.MORE),

        // 以下跳转到 module_volunteer 的 VolunteerLoginActivity
        LOGIN_VOLUNTEER("绑定志愿者账号", TaskType.MORE)
    }

    private val lastSaveDate by lazy {
        val share = context.sharedPreferences(this::class.java.simpleName)
        share.getString("last_save_date", "")
    }

    fun postTask(
        task: Task,
        onlyTag: String?
    ) {
        val share = context.sharedPreferences(this::class.java.simpleName)
        when (task.type) {
            TaskType.BASE -> { // Base 任务是每天刷新的, 不相等时就先清空所有本地保存的 sharedPreferences
                val nowDate = SimpleDateFormat("yyyy.M.d", Locale.CHINA).format(Date())
                if (lastSaveDate != nowDate) {
                    share.edit {
                        putString("last_save_date", nowDate)
                        Task.values().forEach {
                            this.putInt(it.title, 0)
                            this.putStringSet("${it.title}_set", null)
                        }
                    }
                }
            }
            TaskType.MORE -> {
            }
        }

        if (isAllowPost(task.title, onlyTag)) {
            postTask(task.title)
        }
    }

    private fun isAllowPost(title: String, onlyTag: String? = null): Boolean {
        if (onlyTag != null) {
            val share = context.sharedPreferences(this::class.java.simpleName)
            val set = share.getStringSet("${title}_set", setOf())
            if (set != null) {
                if (set.contains(onlyTag)) { // 如果包含就返回 false, 说明已经请求过了
                    return false
                }else {
                    share.edit { putStringSet("${title}_set", setOf(onlyTag)) }
                    return true
                }
            }else { // set 为 null 时直接发请求
                return true
            }
        }else { // onlyTag 为 null 时直接发请求
            return true
        }
    }

    private fun postTask(
        title: String,
        onSlopOver: ((maxProgress: Int) -> Unit)? = null,
        onSuccess: ((currentProgress: Int) -> Unit)? = null
    ) {
        val share = context.sharedPreferences(this::class.java.simpleName)
        val currentProgress = share.getInt(title, 0)
        ApiGenerator.getApiService(ApiService::class.java)
            .changeTaskProgress(title, currentProgress)
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is HttpException) {
                        // 在任务进度大于最大进度时, 后端返回 http 的错误码 500 导致回调到 onError 方法 所以这里手动拿到返回的 bean 类
                        val responseBody: ResponseBody? = it.response()?.errorBody()
                        val code = it.response()?.code()// 返回的 code 如果为 500
                        if (code == 500) {
                            val gson = Gson()
                            val data = gson.fromJson(responseBody?.string(), RedrockApiStatus::class.java)
                            if (data.status == 400) {
                                onSlopOver?.invoke(currentProgress)
                            }
                        }
                    }
                },
                onNext = {
                    share.edit { putInt(title, currentProgress + 1) }
                    onSuccess?.invoke(currentProgress + 1)
                }
            )
    }

    private interface ApiService {
        // 用于改变积分商城界面的任务
        @POST("/magipoke-intergral/Integral/progress")
        @FormUrlEncoded
        fun changeTaskProgress(
            @Field("title") title: String,
            @Field("current_progress") currentProgress: Int
        ): Observable<RedrockApiStatus>
    }
}