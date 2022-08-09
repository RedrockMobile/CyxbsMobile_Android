package com.mredrock.cyxbs.store.service

import android.content.Context
import android.content.SharedPreferences
import androidx.collection.arraySetOf
import androidx.core.content.edit
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.store.IStoreService
import com.mredrock.cyxbs.api.store.STORE_SERVICE
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.IApi
import com.mredrock.cyxbs.lib.utils.network.api
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.util.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/6 15:51
 */
@Route(path = STORE_SERVICE)
class StoreServiceImpl : IStoreService {
  
  override fun postTask(task: IStoreService.Task, onlyTag: String?) {
    when (task.type) {
      IStoreService.TaskType.BASE -> postTask(baseSp, task.title)
      IStoreService.TaskType.MORE -> postTask(moreSp, task.title)
    }
  }
  
  private fun postTask(sp: SharedPreferences, title: String) {
    // 先检查进度条是否已满
    val inEnd = sp.getBoolean(title, false)
    if (!inEnd) {
      if (checkOnlyTag(title)) {
        postTask(
          title,
          onSlopOver = {
            sp.edit { putBoolean(title, true) }
          }
        )
      }
    }
  }
  
  private fun checkOnlyTag(title: String, onlyTag: String? = null): Boolean {
    if (onlyTag != null) {
      val set = onlyTagSp.getStringSet(title, null) ?: arraySetOf()
      if (set.contains(onlyTag)) {
        return false
      } else {
        set.add(onlyTag)
        onlyTagSp.edit {
          putStringSet(title, set)
        }
        return true
      }
    } else {
      return true
    }
  }
  
  override fun init(context: Context) {
    // Base 任务是每天刷新的, 不相等时就先清空所有本地保存的 sharedPreferences
    val nowDate = SimpleDateFormat("yyyy.M.d", Locale.CHINA).format(Date())
    if (lastSaveDate != nowDate) {
      lastSaveDate = nowDate
      baseSp.edit { clear() }
    }
  }
  
  // 上一次发送任务的时间, 用于清空每日任务
  private var lastSaveDate: String
    get() = dateSp.getString("last_save_date", null) ?: ""
    set(value) {
      dateSp.edit { putString("last_save_date", value) }
    }
  private val dateSp = appContext.getSp(this::class.java.simpleName + "_date")
  private val baseSp = appContext.getSp(this::class.java.simpleName + "_base")
  private val moreSp = appContext.getSp(this::class.java.simpleName + "_more")
  private val onlyTagSp = appContext.getSp(this::class.java.simpleName + "_onlyTag")
  
  // 发送请求, 该网络请求私有
  private fun postTask(
    title: String,
    onSlopOver: (() -> Unit)? = null,
    onSuccess: (() -> Unit)? = null
  ) {
    ApiService::class.api
      .changeTaskProgress(title)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnError {
        if (it is HttpException) {
          // 在任务进度大于最大进度时, 后端返回 http 的错误码 500 导致回调到 onError 方法 所以这里手动拿到返回的 bean 类
          /*
          * todo 如果以后要改这里接口，我想说以下几点：
          *  1、把这个 http 的错误码 500 改成 200（这本来就是他们的不规范，我们端上也不好处理）；
          *  2、任务请求在我们端上点都不好做，希望能把逻辑写在后端
          * */
          val code = it.response()?.code() // 返回的 code 如果为 500
          if (code == 500) {
            onSlopOver?.invoke()
          }
        }
      }.unsafeSubscribeBy {
        onSuccess?.invoke()
      }
  }
  
  private interface ApiService : IApi {
    // 用于改变积分商城界面的任务
    @POST("/magipoke-intergral/Integral/progress")
    @FormUrlEncoded
    fun changeTaskProgress(
      @Field("title") title: String
    ): Single<ApiStatus>
  }
}