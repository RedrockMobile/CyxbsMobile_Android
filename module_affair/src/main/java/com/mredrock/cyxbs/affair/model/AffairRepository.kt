package com.mredrock.cyxbs.affair.model

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.mredrock.cyxbs.affair.bean.toAffairDateBean
import com.mredrock.cyxbs.affair.net.AffairApiService
import com.mredrock.cyxbs.affair.room.*
import com.mredrock.cyxbs.affair.room.AffairDataBase
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.throwApiExceptionIfFail
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.IllegalStateException
import kotlin.random.Random

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 19:30
 */
object AffairRepository {
  
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
          AffairDataBase.INSTANCE.getAffairDao()
            .observeAffair(it)
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
    if (selfNum.isEmpty()) return Single.just(emptyList())
    // 先上传本地临时数据，只有本地临时数据全部上传后才能下载新的数据，防止数据混乱
    return uploadLocalAffair(selfNum)
      .andThen(AffairApiService.INSTANCE.getAffair())
      .throwApiExceptionIfFail()
      .map {
        // 装换数据并插入数据库
        val affairEntity = it.toAffairEntity()
        AffairDataBase.INSTANCE.getAffairDao().resetData(selfNum, affairEntity)
        affairEntity
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
      AffairDataBase.INSTANCE.getAffairDao().getAffairByStuNum(selfNum)
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
    return AffairApiService.INSTANCE.addAffair(dateJson, time, title, content)
      .throwApiExceptionIfFail()
      .map { it.id }
      .onErrorReturn {
        // 上传失败时生成一个随机 id
        val randomId = getRandomLocalId(selfNum)
        // 插入到本地临时数据库
        LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
          .insertLocalAddAffair(
            LocalAddAffairEntity(selfNum, randomId, dateJson, time, title, content)
          )
        // 发送给下流 id 值
        randomId
      }.doOnSuccess {
        // 插入数据库新数据
        AffairDataBase.INSTANCE.getAffairDao()
          .insertAffair(AffairEntity(selfNum, it, time, title, content, atWhatTime))
      }.flatMapCompletable { Completable.complete() }
      .subscribeOn(Schedulers.io())
  }
  
  /**
   * 更新事务，请使用 [observeAffair] 进行观察数据
   */
  fun updateAffair(
    id: Int,
    time: Int,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>,
  ): Completable {
    val selfNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Completable.error(IllegalStateException("学号为空"))
    val dateJson = atWhatTime.toPostDateJson()
    return uploadLocalAffair(selfNum)
      .andThen(AffairApiService.INSTANCE.updateAffair(id, dateJson, time, title, content))
      .throwApiExceptionIfFail()
      .flatMapCompletable { Completable.complete() }
      .onErrorComplete {
        // 这里说明网络出问题了，并且可能本地临时事务没有上传成功
        // 要先判断是否是本地临时的事务，如果是本地临时的事务就先更新 add 表
        val result = LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
          .updateLocalAddAffair(LocalAddAffairEntity(selfNum, id, dateJson, time, title, content))
        if (result == 0) {
          // result = 0 则说明不是本地临时事务，就修改 update 表，确保 update 表不会保存本地临时添加的
          LocalAffairDataBase.INSTANCE.getLocalUpdateAffairDao()
            .insertLocalUpdateAffair(
              LocalUpdateAffairEntity(
                selfNum,
                id,
                dateJson,
                time,
                title,
                content
              )
            )
          // insert 已改为 OnConflictStrategy.REPLACE，可进行替换插入
        }
        true
      }
      .doOnComplete {
        // 更新本地数据
        AffairDataBase.INSTANCE.getAffairDao()
          .updateAffair(AffairEntity(selfNum, id, time, title, content, atWhatTime))
      }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 删除事务，请使用 [observeAffair] 进行观察数据
   */
  fun deleteAffair(id: Int): Completable {
    val selfNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Completable.error(IllegalStateException("学号为空"))
    return uploadLocalAffair(selfNum)
      .andThen(AffairApiService.INSTANCE.deleteAffair(id))
      .throwApiExceptionIfFail()
      .flatMapCompletable { Completable.complete() }
      .onErrorComplete {
        // 这里说明网络出问题了，并且可能本地临时事务没有上传成功
        // 先检查是否是本地临时添加的事务，是的话就直接删除
        val result = LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
          .deleteLocalAddAffair(selfNum, id)
        if (result == 0) {
          // result = 0 则说明不是本地临时添加的事务
          // 直接删除本地临时更新的事务，不管有没有
          LocalAffairDataBase.INSTANCE.getLocalUpdateAffairDao()
            .deleteLocalUpdateAffair(selfNum, id)
          // 再插入本地临时删除事务的数据库
          LocalAffairDataBase.INSTANCE.getLocalDeleteAffairDao()
            .insertLocalDeleteAffair(LocalDeleteAffairEntity(selfNum, id))
        }
        true
      }.doOnComplete {
        // 删除本地数据
        AffairDataBase.INSTANCE.getAffairDao().deleteAffair(selfNum, id)
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
   *
   * ## 注意
   * 因为数据同步性的问题，所以采取先删除本地临时事务，然后上传远端，如果上传远端失败，就重新插回去。
   * 其实我也考虑过使用 Room 的 Transaction，但因为 Rxjava 流的问题，所以不好设置
   */
  private fun uploadLocalAffair(stuNum: String): Completable {
    val localDB = LocalAffairDataBase.INSTANCE
    // 本地临时添加的事务干流
    val addCompletable = Single.create<List<LocalAddAffairEntity>> {
      // 先删除数据，后面上传远端失败后再重新插回去
      it.onSuccess(localDB.getLocalAddAffairDao().removeLocalAddAffair(stuNum))
    }.flatMapCompletable {
      val singleList = it.map { entity ->
        AffairApiService.INSTANCE
          .addAffair(entity.dateJson, entity.time, entity.title, entity.content) // 网络请求
          .throwApiExceptionIfFail()
          .doOnSuccess { bean ->
            // 因为 id 不一样，所以需要修改事务数据库
            AffairDataBase.INSTANCE
              .getAffairDao()
              .updateId(stuNum, entity.id, bean.id) // 更新 id
          }.doOnError {
            // 删除失败的话就重新插入回去
            localDB.getLocalAddAffairDao()
              .insertLocalAddAffair(entity)
          }.subscribeOn(Schedulers.io())
      }
      Single.mergeDelayError(singleList)
        .flatMapCompletable { Completable.complete() }
    }.subscribeOn(Schedulers.io())
    
    // 本地临时更新的事务干流
    val updateCompletable = Single.create<List<LocalUpdateAffairEntity>> {
      it.onSuccess(localDB.getLocalUpdateAffairDao().removeLocalUpdateAffair(stuNum))
    }.flatMapCompletable {
      val singleList = it.map { entity ->
        AffairApiService.INSTANCE
          .updateAffair(
            entity.id,
            entity.dateJson,
            entity.time,
            entity.title,
            entity.content
          ) // 网络请求
          .throwApiExceptionIfFail()
          .doOnError {
            // 删除失败的话就重新插入回去
            localDB.getLocalUpdateAffairDao()
              .insertLocalUpdateAffair(entity)
          }.subscribeOn(Schedulers.io())
      }
      Single.mergeDelayError(singleList)
        .flatMapCompletable { Completable.complete() }
    }.subscribeOn(Schedulers.io())
    
    // 本地临时删除的事务干流
    val deleteCompletable = Single.create<List<LocalDeleteAffairEntity>> {
      it.onSuccess(localDB.getLocalDeleteAffairDao().removeLocalDeleteAffair(stuNum))
    }.flatMapCompletable {
      val singleList = it.map { entity ->
        AffairApiService.INSTANCE
          .deleteAffair(entity.id) // 网络请求
          .throwApiExceptionIfFail()
          .doOnError {
            // 删除失败的话就重新插入回去
            localDB.getLocalDeleteAffairDao()
              .insertLocalDeleteAffair(entity)
          }.subscribeOn(Schedulers.io())
      }
      Single.mergeDelayError(singleList)
        .flatMapCompletable { Completable.complete() }
    }.subscribeOn(Schedulers.io())
    
    // 将三条干流汇聚成最后的一条主流
    return Completable.mergeDelayError(
      listOf(addCompletable, updateCompletable, deleteCompletable)
    )
  }
  
  /**
   * 得到一个随机的事务 id
   */
  @WorkerThread
  private fun getRandomLocalId(stuNum: String): Int {
    // 后端 id 是正数，所以本地使用负数来保存
    var localId = Random.nextInt(-9999999, -1)
    var localAffair = LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
      .getLocalAddAffair(stuNum, localId)
    // 保证与本地临时保存数据库不重复
    while (localAffair != null) {
      localId = Random.nextInt(-9999999, 0)
      localAffair = LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
        .getLocalAddAffair(stuNum, localId)
    }
    return localId
  }
}