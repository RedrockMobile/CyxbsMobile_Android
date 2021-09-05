package com.mredrock.cyxbs.common.config

import android.util.Log
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
 * 邮票任务, 分别会跳转到不同模块中的界面, 而且完成任务后需要向后端发送进度(以任务名称为请求), 所以写在这里
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
        DAILY_SIGN("今日打卡", TaskType.BASE),

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

    // 上一次发送任务的时间
    private val lastSaveDate by lazy {
        val share = context.sharedPreferences(this::class.java.simpleName)
        share.getString("last_save_date", "")
    }

    /**
     * 用于向后端发送任务更新的请求(接手改积分商城项目的后端老哥后面也承认任务进度该他们做)
     *
     * [onlyTag] 为发送该类型任务的唯一标记, 比如我要看 5 个动态, 总不能让我看 5 个相同的就能得到积分,
     * 该形参就用于传入一个唯一的标记, 然后会自动判断是否发送过该标记的请求, 你只管在看了评论后调用该方法即可
     *
     * @param task 任务
     * @param onlyTag 唯一标记
     */
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
                            this.putString("list_${it.title}", null)
                        }
                    }
                }
            }
            TaskType.MORE -> {
            }
        }

        if (isAllowPost(task.title, onlyTag)) {
            postTask(task.title, onSuccess = {
                val s = share.getString("list_${task.title}", null)
                if (!s.isNullOrEmpty()) {
                    share.edit { putString("list_${task.title}", "$s$%@$onlyTag") }
                }else {
                    share.edit { putString("list_${task.title}", onlyTag) }
                }
            })
        }
    }

    // 检查之前是否存在相同的 onlyTage
    private fun isAllowPost(title: String, onlyTag: String? = null): Boolean {
        if (onlyTag != null) {
            val share = context.sharedPreferences(this::class.java.simpleName)
            val s = share.getString("list_${title}", null)
            if (!s.isNullOrEmpty()) {
            val list = s.split("$%@")
                return !list.contains(onlyTag) // 如果包含就返回 false, 说明已经请求过了
            }else { // s 为 null 时直接发请求
                return true
            }
        }else { // onlyTag 为 null 时直接发请求
            return true
        }
    }

    // 发送请求, 该网络请求私有
    private fun postTask(
        title: String,
        onSlopOver: (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        ApiGenerator.getApiService(ApiService::class.java)
            .changeTaskProgress(title)
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is HttpException) {
                        // 在任务进度大于最大进度时, 后端返回 http 的错误码 500 导致回调到 onError 方法 所以这里手动拿到返回的 bean 类
                        val responseBody: ResponseBody? = it.response()?.errorBody()
                        val code = it.response()?.code() // 返回的 code 如果为 500
                        if (code == 500) {
                            val gson = Gson()
                            val data = gson.fromJson(responseBody?.string(), RedrockApiStatus::class.java)
                            if (data.status == 400) { // 此时说明超过了最大进度
                                onSlopOver?.invoke()
                            }
                        }
                    }
                },
                onNext = {
                    onSuccess?.invoke()
                }
            )
    }

    private interface ApiService {
        // 用于改变积分商城界面的任务
        @POST("/magipoke-intergral/Integral/progress")
        @FormUrlEncoded
        fun changeTaskProgress(
            @Field("title") title: String
        ): Observable<RedrockApiStatus>
    }
}