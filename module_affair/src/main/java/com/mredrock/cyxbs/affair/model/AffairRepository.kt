package com.mredrock.cyxbs.affair.model

import com.google.gson.Gson
import com.mredrock.cyxbs.affair.bean.toAffairDateBean
import com.mredrock.cyxbs.affair.net.AffairApiService
import com.mredrock.cyxbs.affair.room.*
import com.mredrock.cyxbs.affair.room.AffairDataBase
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.affair.room.AffairEntity.Companion.LocalRemoteId
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.config.config.PhoneCalendar
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.throwApiExceptionIfFail
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.IllegalArgumentException
import java.util.concurrent.Callable
import kotlin.IllegalStateException

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 19:30
 */
object AffairRepository {
  
  private val Api = AffairApiService.INSTANCE
  
  private val DB = AffairDataBase.INSTANCE
  private val AffairDao = DB.getAffairDao()
  private val AffairCalendarDao = DB.getAffairCalendarDao()
  private val LocalAddDao = DB.getLocalAddAffairDao()
  private val LocalUpdateDao = DB.getLocalUpdateAffairDao()
  private val LocalDeleteDao = DB.getLocalDeleteAffairDao()
  
  private val mGson = Gson()
  
  private fun List<AffairEntity.AtWhatTime>.toPostDateJson(): String {
    // 不建议让 AtWhatTime 成为转 json 的类，应该转换成 AffairDateBean 转 json
    return mGson.toJson(toAffairDateBean())
  }
  
  /**
   * 观察当前登录人的事务
   * 1、支持换账号登录后返回新登录人的数据
   * 2、第一次观察时会请求新的数据
   * 3、使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * 4、上游不会抛出错误到下游
   */
  fun observeAffair(): Observable<List<AffairEntity>> {
    return ServiceManager(IAccountService::class)
      .getUserService()
      .observeStuNumState()
      .switchMap { value ->
        // 使用 switchMap 可以停止之前学号的订阅
        value.nullUnless(Observable.just(emptyList())) {
          AffairDao.observeAffair(it)
            .distinctUntilChanged()
            .doOnSubscribe {
              // 观察时先请求一次最新数据
              refreshAffair().unsafeSubscribeBy()
            }.subscribeOn(Schedulers.io())
        }
      }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 刷新事务
   */
  fun refreshAffair(): Single<List<AffairEntity>> {
    val selfNum: String = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Single.just(emptyList())
    // 先上传本地临时数据，只有本地临时数据全部上传后才能下载新的数据，防止数据混乱
    return uploadLocalAffair(selfNum)
      .andThen(Api.getAffair())
      .throwApiExceptionIfFail()
      .map {
        // 装换数据并插入数据库
        val affairIncompleteEntity = it.toAffairIncompleteEntity()
        AffairDao.resetData(selfNum, affairIncompleteEntity)
      }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 得到事务，但不建议你直接使用，应该用 [observeAffair] 来代替
   *
   * 永远不会抛出异常
   */
  fun getAffair(): Single<List<AffairEntity>> {
    val selfNum: String = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Single.error(RuntimeException("学号为空！"))
    return refreshAffair().onErrorReturn {
      // 上游失败了就取本地数据，可能是网络失败，也可能是本地临时上传事务失败
      AffairDao.getAffairByStuNum(selfNum)
    }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 添加事务，请使用 [observeAffair] 进行观察数据
   */
  fun addAffair(
    time: Int,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>,
  ): Completable {
    val selfNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Completable.error(IllegalStateException("学号为空！"))
    val dateJson = atWhatTime.toPostDateJson()
    return Api.addAffair(dateJson, time, title, content)
      .throwApiExceptionIfFail()
      .map { it.remoteId }
      .onErrorReturn {
        // 发送给下流本地 remoteId，表示网络连接失败，需要添加进临时数据库
        LocalRemoteId
      }.map {
        /**
         * 这里有概率很小的同步问题，所以采取事务
         *
         * 该问题为：
         * 虽然此时 [uploadLocalAffair] 失败，则存在另一个 [uploadLocalAffair] 正在运行中
         */
        DB.runInTransaction(
          Callable {
            // 插入数据库新数据，并返回给下游 onlyId
            val onlyId = AffairDao
              .insertAffair(selfNum, AffairIncompleteEntity(it, time, title, content, atWhatTime))
              .onlyId
            if (it == LocalRemoteId) {
              // 为本地 remoteId 的话就插入到本地临时添加的事务
              LocalAddDao.insertLocalAddAffair(
                LocalAddAffairEntity(selfNum, onlyId, dateJson, time, title, content)
              )
            }
            onlyId
          }
        )
      }.doOnSuccess { onlyId ->
        if (time > 0) {
          //          PhoneCalendar.add()
          //          AffairDataBase.INSTANCE.getAffairCalendarDao()
          //            .insert(AffairCalendarEntity())
          // TODO 待完成
        }
      }.flatMapCompletable { Completable.complete() }
      .subscribeOn(Schedulers.io())
  }
  
  /**
   * 更新事务，请使用 [observeAffair] 进行观察数据
   */
  fun updateAffair(
    onlyId: Int,
    time: Int,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>,
  ): Completable {
    val selfNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Completable.error(IllegalStateException("学号为空"))
    if (AffairDao.findAffairByOnlyId(selfNum, onlyId) == null) {
      return Completable.error(
        IllegalArgumentException("onlyId 不存在或已经被删除, onlyId = $onlyId, stuNum = $selfNum")
      )
    }
    val dateJson = atWhatTime.toPostDateJson()
    return uploadLocalAffair(selfNum)
      .andThen(
        Api.updateAffair(
          AffairDao.getRemoteIdByOnlyId(selfNum, onlyId), // 注意：这里需要使用 remoteId
          dateJson,
          time,
          title,
          content
        )
      ).throwApiExceptionIfFail()
      .map {
        // 请求成功的话就去拿 remoteId 值，并装换给下游
        // 这里在网络请求成功时会重复查找一次，但如果分开写，代码易读性会下降，所以多查找一次就多一次吧
        AffairDao.getRemoteIdByOnlyId(selfNum, onlyId)
      }.onErrorReturn {
        /**
         * 这里有概率很小的同步问题，所以采取事务
         *
         * 该问题为：
         * 虽然此时 [uploadLocalAffair] 失败，则存在另一个 [uploadLocalAffair] 正在运行中
         */
        DB.runInTransaction(
          Callable {
            // 这里说明网络出问题了，并且可能本地临时事务没有上传成功
            val remoteId = AffairDao.getRemoteIdByOnlyId(selfNum, onlyId)
            if (remoteId == LocalRemoteId) {
              // 如果是 LocalRemoteId，就说明是本地事务，直接 更新 本地临时添加的事务，因为之前已经添加了
              LocalAddDao
                .updateLocalAddAffair(
                  LocalAddAffairEntity(selfNum, onlyId, dateJson, time, title, content)
                )
            } else {
              // 这个分支说明不是本地临时事务，就更新或者插入本都临时更新的事务
              // insert 已改为 OnConflictStrategy.REPLACE，可进行替换插入
              LocalUpdateDao
                .insertLocalUpdateAffair(
                  LocalUpdateAffairEntity(selfNum, onlyId, remoteId, dateJson, time, title, content)
                )
            }
            remoteId
          }
        )
      }.doOnSuccess {
        // 更新本地数据
        AffairDao.updateAffair(AffairEntity(selfNum, onlyId, it, time, title, content, atWhatTime))
      }.flatMapCompletable { Completable.complete() }
      .doOnComplete {
        // 更新手机上的日历
        val calendarId = AffairDataBase.INSTANCE.getAffairCalendarDao().find(onlyId)
      }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 删除事务，请使用 [observeAffair] 进行观察数据
   */
  fun deleteAffair(onlyId: Int): Completable {
    val selfNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Completable.error(IllegalStateException("学号为空"))
    if (AffairDao.findAffairByOnlyId(selfNum, onlyId) == null) {
      return Completable.error(
        IllegalArgumentException("onlyId 不存在或已经被删除, onlyId = $onlyId, stuNum = $selfNum")
      )
    }
    return uploadLocalAffair(selfNum)
      .andThen(
        Api.deleteAffair(
          AffairDao.getRemoteIdByOnlyId(selfNum, onlyId) // 注意：这里需要使用 remoteId
        )
      ).throwApiExceptionIfFail()
      .flatMapCompletable { Completable.complete() }
      .onErrorComplete {
        /**
         * 这里有概率很小的同步问题，所以采取事务
         *
         * 该问题为：
         * 虽然此时 [uploadLocalAffair] 失败，则存在另一个 [uploadLocalAffair] 正在运行中
         */
        DB.runInTransaction(
          Callable {
            // 这里说明网络出问题了，并且可能本地临时事务没有上传成功
            val remoteId = AffairDao.getRemoteIdByOnlyId(selfNum, onlyId)
            if (remoteId == LocalRemoteId) {
              // 如果是本地临时事务，就直接删除临时添加的事务
              LocalAddDao.deleteLocalAddAffair(selfNum, onlyId)
            } else {
              // 如果不是本地临时事务
              // 直接删除本地临时更新的事务，不管有没有
              LocalUpdateDao.deleteLocalUpdateAffair(selfNum, onlyId)
              // 再插入本地临时删除的事务
              LocalDeleteDao.insertLocalDeleteAffair(
                LocalDeleteAffairEntity(
                  selfNum,
                  onlyId,
                  remoteId
                )
              )
            }
            true
          }
        )
      }.doOnComplete {
        // 最后删除本地数据
        AffairDao.deleteAffair(selfNum, onlyId)
      }.doOnComplete {
        // 删除手机上的日历
        val calendarId = AffairCalendarDao.remove(onlyId)
        if (calendarId != null) {
          PhoneCalendar.delete(calendarId)
        }
      }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 发送本地临时保存的事务
   *
   * 写了这个复杂的数据流动后，我只想说：
   * Flow    ✘ 不行
   * Rxjava  ✔ 彳亍
   *
   * 如果你能搞懂这里面的数据流动，相信会增加你对 Rxjava 的理解
   */
  private fun uploadLocalAffair(stuNum: String): Completable {
    
    // 本地临时添加的事务干流
    val addCompletable = {
      // 这里必须使用这种接口的方式，为了在 runInTransaction 中使用，下同
      LocalAddDao.getLocalAddAffair(stuNum)
        .flatMapCompletable { list ->
          val singleList = list.map { entity ->
            Api.addAffair(entity.dateJson, entity.time, entity.title, entity.content) // 网络请求
              .throwApiExceptionIfFail()
              .doOnSuccess {
                // 上传成功就删除
                LocalAddDao.deleteLocalAddAffair(entity)
              }.doOnSuccess {
                // 上传成功就修改 remoteId
                AffairDao.updateRemoteId(stuNum, entity.onlyId, it.remoteId)
              }.subscribeOn(Schedulers.io())
          }
          Single.mergeDelayError(singleList)
            .flatMapCompletable { Completable.complete() }
        }.subscribeOn(Schedulers.io())
    }
    
    // 本地临时更新的事务干流
    val updateCompletable = {
      LocalUpdateDao.getLocalUpdateAffair(stuNum)
        .flatMapCompletable { list ->
          val singleList = list.map { entity ->
            Api.updateAffair(
              entity.remoteId, // 注意：这里需要使用 remoteId
              entity.dateJson,
              entity.time,
              entity.title,
              entity.content
            ).throwApiExceptionIfFail()
              .doOnSuccess {
                // 上传成功就删除
                LocalUpdateDao.deleteLocalUpdateAffair(entity)
              }.subscribeOn(Schedulers.io())
          }
          Single.mergeDelayError(singleList)
            .flatMapCompletable { Completable.complete() }
        }.subscribeOn(Schedulers.io())
    }
    
    // 本地临时删除的事务干流
    val deleteCompletable = {
      LocalDeleteDao.getLocalDeleteAffair(stuNum)
        .flatMapCompletable { list ->
          val singleList = list.map { entity ->
            Api.deleteAffair(entity.remoteId)
              .throwApiExceptionIfFail()
              .doOnSuccess {
                // 上传成功就删除
                LocalDeleteDao.deleteLocalDeleteAffair(entity)
              }.subscribeOn(Schedulers.io())
          }
          Single.mergeDelayError(singleList)
            .flatMapCompletable { Completable.complete() }
        }.subscribeOn(Schedulers.io())
    }
    
    // 合并三条干流
    return Completable.create {
      // 必须使用 Transaction，保证数据库的同步性
      try {
        DB.runInTransaction {
          Completable.mergeDelayError(
            listOf(addCompletable.invoke(), updateCompletable.invoke(), deleteCompletable.invoke())
          ).blockingAwait()
        }
        it.onComplete()
      } catch (e: Exception) {
        it.onError(e)
      }
    }
  }
}