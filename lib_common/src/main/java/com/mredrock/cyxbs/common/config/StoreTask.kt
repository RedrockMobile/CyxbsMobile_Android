package com.mredrock.cyxbs.common.config

import android.content.SharedPreferences
import androidx.core.content.edit
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import io.reactivex.Observable
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

    enum class Base(val s: String) {
        // 以下跳转到 module_qa 的 DailySignActivity
        DAILY_SIGN("今日签到"),

        // 以下跳转到 module_qa 的 DynamicFragment
        SEE_DYNAMIC("逛逛邮问"), // 浏览动态
        PUBLISH_DYNAMIC("斐然成章"), // 发布动态
        SHARE_DYNAMIC("围观吃瓜"), // 分享动态
        POST_COMMENT("能说会道"), // 发布评论
        GIVE_A_LIKE("拍案叫绝") // 点赞
    }

    enum class More(val s: String) {
        // 以下跳转到 module_mine 的 EditInfoActivity
        EDIT_INFO("完善个人信息"),

        // 以下跳转到 module_volunteer 的 VolunteerLoginActivity
        LOGIN_VOLUNTEER("绑定志愿者账号")
    }

    private val lastSaveDate by lazy {
        val share = context.sharedPreferences(this::class.java.simpleName)
        share.getString("last_save_date", "")
    }

    fun postBaseTask(task: Base) {
        val share = context.sharedPreferences(this::class.java.simpleName)
        val nowDate = SimpleDateFormat("yyyy.M.d", Locale.CHINA).format(Date())
        if (lastSaveDate != nowDate) {
            share.edit {
                putString("last_save_date", nowDate)
                clearLocalAllBaseTaskProgress(this)
            }
        }
        val currentProgress = share.getInt(task.s, 0)

    }

    fun postMoreTask(task: More) {

    }

    private fun clearLocalAllBaseTaskProgress(editor: SharedPreferences.Editor) {
        editor.putInt(Base.DAILY_SIGN.s, 0)
        editor.putInt(Base.SEE_DYNAMIC.s, 0)
        editor.putInt(Base.PUBLISH_DYNAMIC.s, 0)
        editor.putInt(Base.SHARE_DYNAMIC.s, 0)
        editor.putInt(Base.POST_COMMENT.s, 0)
        editor.putInt(Base.GIVE_A_LIKE.s, 0)
    }

    private interface ApiService {
        // 用于改变积分商城界面的任务
        @POST("/magipoke-intergral/Integral/progress")
        @FormUrlEncoded
        fun postTaskIsSuccessful(
            @Field("title") title: String,
            @Field("current_progress") currentProgress: Int
        ): Observable<RedrockApiStatus>
    }
}