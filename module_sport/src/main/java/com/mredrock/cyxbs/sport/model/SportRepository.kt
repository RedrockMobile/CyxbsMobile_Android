package com.mredrock.cyxbs.sport.model

import androidx.core.content.edit
import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.spi.InitialManager
import com.mredrock.cyxbs.lib.base.spi.InitialService
import com.mredrock.cyxbs.lib.utils.extensions.*
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.sport.model.network.SportDetailApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 体育打卡的仓库层
 *
 * 因为体育打卡的数据是现扒的，接口速度很慢，并且每次请求一遍就会登录一遍，如果多次登录会导致账号被封禁，
 * 所以单独拿出来做一个仓库层
 *
 * 该仓库层使用 [InitialService] 实现依赖注入，在 Application 被初始化时就进行网络请求，该接口因为其特殊性，
 * 所以采用这种预加载的方式，其他的一般接口请不要尝试！！！
 *
 * 目前的逻辑是：
 * - 因为接口太慢，所以打卡数据需要提前加载，越早越好
 * - 因为重复请求容易被冻结账号，所以打卡数据需要做一层本地缓存，有效时间是 4 个小时
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 10:50
 */
@AutoService(InitialService::class)
class SportRepository : InitialService {
  
  companion object {
    /**
     * 观察体育打卡数据
     *
     * 该 ShareFloe 因为拥有一个缓存值，所以会导致数据倒灌
     */
    val sportDataShareFlow: SharedFlow<Result<SportDetailBean>>
      get() = sportDataMutableShareFlow
    
    private val sportDataMutableShareFlow = MutableSharedFlow<Result<SportDetailBean>>(replay = 1)
    
    /**
     * 这个是本地保存的数据，如果保存的时间超过 4 个小时，则会返回 null
     */
    private var sSportData: SportDetailBean?
      get() {
        val sp = appContext.getSp("sport")
        val lastTime = sp.getLong("上次加载的时间戳", 0)
        return if (System.currentTimeMillis() - lastTime < 4 * 60 * 60 * 1000) {
          val data = sp.getString("SportDetailBean", null)
          if (data != null) {
            Gson().fromJson(data, SportDetailBean::class.java)
          } else {
            // 大于 4 个小时就返回 null，表示可以刷新
            null
          }
        } else null
      }
      set(value) {
        val sp = appContext.getSp("sport")
        sp.edit {
          if (value == null) {
            putString("SportDetailBean", null)
            putLong("上次加载的时间戳", 0L)
          } else {
            putString("SportDetailBean", Gson().toJson(value))
            putLong("上次加载的时间戳", System.currentTimeMillis())
          }
        }
      }
  }
  
  override fun onMainProcess(manager: InitialManager) {
    super.onMainProcess(manager)
    
    IAccountService::class.impl
      .getUserService()
      .observeStuNumState()
      .switchMap {
        it.nullIf { sSportData = null }.nullUnless(Observable.never()) {
          if (sSportData == null) {
            SportDetailApiService.INSTANCE
              .getSportDetailData()
              .subscribeOn(Schedulers.io())
              .mapOrThrowApiException()
              .interceptExceptionByResult {
                emitter.onSuccess(Result.failure(throwable))
              }.toObservable()
          } else Observable.never()
        }
      }.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .unsafeSubscribeBy {
        sSportData = it.getOrNull()
        processLifecycleScope.launch {
          sportDataMutableShareFlow.emit(it)
        }
      }
    
    
    // 应用刚开始初始化时，如果 sSportData 有值，则直接拿来用
    if (sSportData != null) {
      processLifecycleScope.launch {
        sportDataMutableShareFlow.emit(Result.success(sSportData!!))
      }
    }
  }
}