package com.mredrock.cyxbs.affair.model

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.mredrock.cyxbs.affair.bean.TodoListPushWrapper
import com.mredrock.cyxbs.affair.bean.toAffairDateBean
import com.mredrock.cyxbs.affair.net.AffairApiService
import com.mredrock.cyxbs.affair.room.*
import com.mredrock.cyxbs.affair.room.AffairDataBase
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.affair.room.AffairEntity.Companion.LocalRemoteId
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.api.course.utils.getEndRow
import com.mredrock.cyxbs.api.course.utils.getEndTimeMinute
import com.mredrock.cyxbs.api.course.utils.getStartRow
import com.mredrock.cyxbs.api.course.utils.getStartTimeMinute
import com.mredrock.cyxbs.lib.utils.utils.config.PhoneCalendar
import com.mredrock.cyxbs.config.config.SchoolCalendar
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.throwApiExceptionIfFail
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.utils.judge.NetworkUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import kotlin.IllegalStateException

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 19:30
 */
@SuppressLint("CheckResult")
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
   * - 支持换账号登录后返回新登录人的数据
   * - 第一次观察时会请求新的数据
   * - 使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * - 上游不会抛出错误到下游
   */
  fun observeAffair(): Observable<List<AffairEntity>> {
    return ServiceManager(IAccountService::class)
      .getUserService()
      .observeStuNumState()
      .observeOn(Schedulers.io())
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
      }
  }
  
  /**
   * 刷新事务
   */
  fun refreshAffair(): Single<List<AffairEntity>> {
    val selfNum: String = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isBlank()) return Single.just(emptyList())
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
   * 永远不会给下游抛出异常
   */
  fun getAffair(): Single<List<AffairEntity>> {
    val selfNum: String = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isBlank()) return Single.just(emptyList())
    return refreshAffair().onErrorReturn {
      // 上游失败了就取本地数据，可能是网络失败，也可能是本地临时上传事务失败
      AffairDao.getAffairByStuNum(selfNum)
    }
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
    val stuNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (stuNum.isBlank()) return Completable.error(IllegalStateException("学号为空！"))
    val dateJson = atWhatTime.toPostDateJson()
    return Api.addAffair(time, title, content, dateJson)
      .throwApiExceptionIfFail()
      .map { it.remoteId }
      .onErrorReturn {
        // 发送给下流本地 remoteId，表示网络连接失败，需要添加进临时数据库
        LocalRemoteId
      }.map { remoteId ->
        // 存在概率很小的同步问题，可能此时正在执行另一个 uploadLocalAffair()，所以使用 runInTransaction
        DB.runInTransaction(
          Callable {
            // 插入数据库新数据，并返回给下游 onlyId
            val entity = AffairIncompleteEntity(remoteId, time, title, content, atWhatTime)
            val onlyId = AffairDao
              .insertAffair(stuNum, entity)
              .onlyId
            if (remoteId == LocalRemoteId) {
              // 为本地 remoteId 的话就插入到本地临时添加的事务中
              val localEntity = LocalAddAffairEntity(stuNum, onlyId, time, title, content, dateJson)
              LocalAddDao.insertLocalAddAffair(localEntity)
            }
            // 注意：这里返回给下游的是 onlyId，不是 remoteId
            onlyId
          }
        )
      }.doOnSuccess { onlyId ->
        insertCalendarAfterClear(onlyId, time, title, content, atWhatTime)
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
    val stuNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (stuNum.isBlank()) return Completable.error(IllegalStateException("学号为空"))
    return updateAffairInternal(stuNum, onlyId, time, title, content, atWhatTime)
      .doOnComplete {
        insertCalendarAfterClear(onlyId, time, title, content, atWhatTime)
      }.subscribeOn(Schedulers.io())
  }
  
  @SuppressLint("CheckResult")
  private fun updateAffairInternal(
    stuNum: String,
    onlyId: Int,
    time: Int,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>
  ): Completable {
    return Completable.create { emitter ->
      try {
        // 存在概率很小的同步问题，可能此时正在执行另一个 uploadLocalAffair()，所以使用 runInTransaction
        DB.runInTransaction {
          AffairDao.findAffairByOnlyId(stuNum, onlyId)
            .toSingle() // 找不到时直接抛错
            .map { it.remoteId }
            .doOnSuccess { remoteId ->
              val dateJson = atWhatTime.toPostDateJson()
              if (remoteId == LocalRemoteId) {
                // 如果是本地临时事务，就直接更新临时添加的事务
                LocalAddDao
                  .updateLocalAddAffair(
                    LocalAddAffairEntity(stuNum, onlyId, time, title, content, dateJson)
                  )
              } else {
                // 不是本地临时事务就上传
                Api.updateAffair(remoteId, time, title, content, dateJson)
                  .throwApiExceptionIfFail()
                  .doOnError {
                    // 上传失败就暂时保存在本地临时更新的事务中
                    // insert 已改为 OnConflictStrategy.REPLACE，可进行替换插入
                    LocalUpdateDao
                      .insertLocalUpdateAffair(
                        LocalUpdateAffairEntity(
                          stuNum, onlyId, remoteId, time, title, content, dateJson
                        )
                      )
                  }.onErrorComplete { true } // 终止异常向下游传递
                  .blockingGet() // 直接堵塞，因为需要使用数据库的 runInTransaction，不能使用流来处理
              }
            }.doOnSuccess { remoteId ->
              // 最后更新本地数据
              AffairDao.updateAffair(
                AffairEntity(
                  stuNum,
                  onlyId,
                  remoteId,
                  time,
                  title,
                  content,
                  atWhatTime
                )
              )
            }.blockingGet() // 直接堵塞，因为需要使用数据库的 runInTransaction，不能使用流来处理
        }
        // 在 runInTransaction 结束后再发送
        emitter.onComplete()
      } catch (e: Exception) {
        emitter.tryOnError(e)
      }
    }
  }
  
  /**
   * 删除事务，请使用 [observeAffair] 进行观察数据
   */
  fun deleteAffair(onlyId: Int): Completable {
    val stuNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (stuNum.isEmpty()) return Completable.error(IllegalStateException("学号为空"))
    return deleteAffairInternal(stuNum, onlyId)
      .doOnComplete {
        // 删除手机上的日历
        AffairCalendarDao.remove(onlyId).forEach {
          PhoneCalendar.delete(it)
        }
      }.subscribeOn(Schedulers.io())
  }
  
  @SuppressLint("CheckResult")
  private fun deleteAffairInternal(stuNum: String, onlyId: Int): Completable {
    return Completable.create { emitter ->
      // 存在概率很小的同步问题，可能此时正在执行另一个 uploadLocalAffair()，所以使用 runInTransaction
      try {
        DB.runInTransaction {
          AffairDao.findAffairByOnlyId(stuNum, onlyId)
            .toSingle() // 找不到时直接抛错
            .map { it.remoteId }
            .doOnSuccess { remoteId ->
              if (remoteId == LocalRemoteId) {
                // 如果是本地临时事务，就直接删除临时添加的事务
                LocalAddDao.deleteLocalAddAffair(stuNum, onlyId)
              } else {
                // 不是本地临时事务就上传
                Api.deleteAffair(remoteId)
                  .throwApiExceptionIfFail()
                  .doOnError {
                    // 上传失败就暂时保存在本地临时删除的事务中
                    LocalDeleteDao.insertLocalDeleteAffair(
                      LocalDeleteAffairEntity(stuNum, onlyId, remoteId)
                    )
                    // 然后尝试删除本地临时更新的事务，不管有没有
                    LocalUpdateDao.deleteLocalUpdateAffair(stuNum, onlyId)
                  }.onErrorComplete { true } // 终止异常向下游传递
                  .blockingGet() // 直接堵塞，因为需要使用数据库的 runInTransaction，不能使用流来处理
              }
            }.doOnSuccess {
              // 最后删除本地数据
              AffairDao.deleteAffair(stuNum, onlyId)
            }.blockingGet() // 直接堵塞，因为需要使用数据库的 runInTransaction，不能使用流来处理
        }
        // 在 runInTransaction 结束后再发送
        emitter.onComplete()
      } catch (e: Exception) {
        emitter.tryOnError(e)
      }
    }
  }

  fun addTodo(pushWrapper: TodoListPushWrapper) = AffairApiService
      .INSTANCE
      .pushTodo(pushWrapper)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())

  
  /**
   * 发送本地临时保存的事务
   *
   * ## 注意：
   * ### 不使用 Rxjava 多条流的原因
   * 最开始采用的 Rxjava 多条流合并，结果发现在使用 runInTransaction 后导致线程死锁，
   * 最后就直接改成单线程运行了
   *
   * ### 使用接口包裹的原因
   * localAdd、localUpdate、localDelete 三个都是接口，
   * 一是为了同时在 runInTransaction  使用
   * 二是为了可读性，并没有全部写在 Completable.create {} 里面
   */
  @SuppressLint("CheckResult")
  private fun uploadLocalAffair(stuNum: String): Completable {
    var hasLocalAffair = false
    // 本地临时添加的事务
    val localAdd = {
      LocalAddDao.getLocalAddAffair(stuNum).forEach { entity ->
        hasLocalAffair = true
        Api.addAffair(entity.time, entity.title, entity.content, entity.dateJson) // 网络请求
          .throwApiExceptionIfFail()
          .doOnSuccess {
            // 上传成功就删除
            LocalAddDao.deleteLocalAddAffair(entity)
          }.doOnSuccess {
            // 上传成功就修改 remoteId
            AffairDao.updateRemoteId(stuNum, entity.onlyId, it.remoteId)
          }.blockingGet() // 直接同步请求，原因请看该方法注释
      }
    }
    
    // 本地临时更新的事务
    val localUpdate = {
      LocalUpdateDao.getLocalUpdateAffair(stuNum).forEach { entity ->
        hasLocalAffair = true
        Api.updateAffair(
          entity.remoteId, // 注意：这里需要使用 remoteId
          entity.time,
          entity.title,
          entity.content,
          entity.dateJson
        ).throwApiExceptionIfFail()
          .doOnSuccess {
            // 上传成功就删除
            LocalUpdateDao.deleteLocalUpdateAffair(entity)
          }.blockingGet() // 直接同步请求，原因请看该方法注释
      }
    }
    
    // 本地临时删除的事务
    val localDelete = {
      LocalDeleteDao.getLocalDeleteAffair(stuNum).forEach { entity ->
        hasLocalAffair = true
        Api.deleteAffair(entity.remoteId) // 注意：这里需要使用 remoteId
          .throwApiExceptionIfFail()
          .doOnSuccess {
            // 上传成功就删除
            LocalDeleteDao.deleteLocalDeleteAffair(entity)
          }.blockingGet() // 直接同步请求，原因请看该方法注释
      }
    }
    
    return Completable.create { emitter ->
      try {
        // 必须使用 Transaction，保证数据库的同步性
        DB.runInTransaction {
          localAdd.invoke()
          localUpdate.invoke()
          localDelete.invoke()
        }
        emitter.onComplete()
      } catch (e: Exception) {
        emitter.tryOnError(e)
      }
    }.toObservable<Unit>().flatMapCompletable {
      // 进行延时处理，防止同步问题
      Completable.complete()
        .delay(if (hasLocalAffair) 200 else 0, TimeUnit.MILLISECONDS)
    }.observeOn(Schedulers.io())
  }
  
  /**
   * 先清理 [onlyId] 已经添加进的手机日历
   * 再添加进手机日历
   */
  private fun insertCalendarAfterClear(
    onlyId: Int,
    time: Int,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>
  ) {
    // 更新手机上的日历，采取先删除再添加的方式，因为一个事务对应了多个日历中的安排，不好更新
    /*
    * todo 如果以后有学弟要更新事务逻辑，我的建议是将事务改成设置一个事务长度后，进入编辑界面时只能设置出现在哪几天内，
    *  而不是仍能设置其他长度的事务。
    * 这样一个事务才能与日历中的一个事务才能对应起来
    * */
    AffairCalendarDao.remove(onlyId).forEach {
      PhoneCalendar.delete(it)
    }
    val firstMonDay = SchoolCalendar.getFirstMonDayOfTerm() ?: return
    // 只有大于 0 才有提醒，只有需要提醒的才写进手机日历
    if (time > 0) {
      val eventIdList = arrayListOf<Long>()
      atWhatTime.forEach { whatTime ->
        val startMinute = getStartTimeMinute(getStartRow(whatTime.beginLesson))
        val endMinute = getEndTimeMinute(getEndRow(whatTime.beginLesson, whatTime.period))
        // 如果是整学期,添加重复事件
        if (whatTime.week.any { it == 0 }) {
          PhoneCalendar.add(
            PhoneCalendar.FrequencyEvent(
              title = title,
              description = content,
              remind = time,
              duration = PhoneCalendar.Event.Duration(
                minute = endMinute - startMinute
              ),
              startTime = (firstMonDay.clone() as Calendar).apply {
                add(Calendar.DATE, whatTime.day)
                add(Calendar.MINUTE, startMinute)
              },
              freq = PhoneCalendar.FrequencyEvent.Freq.WEEKLY,
              count = ICourseService.maxWeek
            )
          )?.also { eventIdList.add(it) }
        } else {
          // 如果不是整学期,添加一次性事件
          PhoneCalendar.add(
            PhoneCalendar.CommonEvent(
              title = title,
              description = content,
              remind = time,
              duration = PhoneCalendar.Event.Duration(
                minute = endMinute - startMinute
              ),
              startTime = whatTime.week.map {
                (firstMonDay.clone() as Calendar).apply {
                  add(Calendar.DATE, whatTime.day + (it - 1) * 7)
                  add(Calendar.MINUTE, startMinute)
                }
              }
            )
          )?.also { eventIdList.add(it) }
        }
      }
      if (eventIdList.isNotEmpty()) {
        AffairCalendarDao.insert(AffairCalendarEntity(onlyId, eventIdList))
      }
    }
  }

  init {
    // 监听网络状态，在网络恢复时上传临时事务
    NetworkUtil.state
      .filter { it }
      .doOnNext {
        uploadLocalAffair(ServiceManager(IAccountService::class).getUserService().getStuNum())
          .subscribeOn(Schedulers.io())
          .subscribe(Functions.EMPTY_ACTION, Functions.emptyConsumer())
      }.subscribe()
  }
}