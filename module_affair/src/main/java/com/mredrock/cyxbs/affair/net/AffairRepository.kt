package com.mredrock.cyxbs.affair.net

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.mredrock.cyxbs.affair.model.bean.toAffairDateBean
import com.mredrock.cyxbs.affair.room.*
import com.mredrock.cyxbs.affair.service.AffairDataBase
import com.mredrock.cyxbs.affair.service.AffairEntity
import com.mredrock.cyxbs.affair.service.StuNumWithAffairId
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.ApiException
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
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
      .switchMap { // 使用 switchMap 可以停止之前学号的订阅
        val stuNum = it.value
        if (stuNum == null) Observable.just(emptyList())
        else {
          AffairDataBase.INSTANCE.getAffairDao()
            .observeAffair(stuNum)
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnSubscribe {
              // 观察时先请求一次最新数据
              getAffair().unsafeSubscribeBy()
            }
        }
      }
  }
  
  /**
   * 刷新事务
   */
  fun refreshAffair(): Single<List<AffairEntity>> {
    val selfNum: String = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Single.error(RuntimeException("学号为空！"))
    // 先检查是否有本地临时数据，只有本地临时数据全部上传后才能下载新的数据，防止数据混乱
    return uploadLocalAffair(selfNum)
      .toSingle {
        // 去拿远端数据
        val bean = AffairApiService.INSTANCE.getAffair().blockingGet()
        // 是 200 就装换数据并插入数据库
        if (bean.status == 200 && bean.info == "success") {
          val affairEntity = bean.toAffairEntity()
          AffairDataBase.INSTANCE.getAffairDao().resetData(selfNum, affairEntity)
          affairEntity
        } else throw ApiException(bean.status, bean.info)
      }.subscribeOn(Schedulers.io())
  }

  /**
   * 得到事务，但不建议你直接使用，应该用 [observeAffair] 来代替
   */
  fun getAffair(): Single<List<AffairEntity>> {
    val selfNum: String = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return Single.error(RuntimeException("学号为空！"))
    return refreshAffair().onErrorReturn {
        // 上游失败了就取本地数据，可能是网络失败，也可能是本地临时上传事务失败
        AffairDataBase.INSTANCE.getAffairDao().getAllAffair(selfNum)
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
  ) {
    val selfNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (selfNum.isEmpty()) return
    val dateJson = atWhatTime.toPostDateJson()
    AffairApiService.INSTANCE.addAffair(dateJson, time, title, content)
      .map {
        // 成功就把 id 传给下流，不成功就抛错
        if (it.status == 200 && it.info == "success") it.id
        else throw ApiException(it.status, it.info)
      }.onErrorReturn {
        // 上传失败时生成一个随机 id
        val randomId = getRandomLocalId(selfNum)
        // 插入到本地临时数据库
        LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
          .insertLocalAddAffair(
            LocalAddAffairEntity(selfNum, randomId, dateJson, time, title, content)
          )
        // 发送给下流 id 值
        randomId
      }.map {
        // 装换数据
        val newAffair = AffairEntity(selfNum, it, time, title, content, atWhatTime)
        // 插入数据库
        AffairDataBase.INSTANCE.getAffairDao().insertAffair(newAffair)
      }.subscribeOn(Schedulers.io())
      .unsafeSubscribeBy {
        "添加成功".toast()
      }
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
    if (selfNum.isEmpty()) return Completable.error(RuntimeException("学号为空"))
    val dateJson = atWhatTime.toPostDateJson()
    return uploadLocalAffair(selfNum)
      .doOnComplete {
        // 更新远端数据
        val bean =
          AffairApiService.INSTANCE.updateAffair(id, dateJson, time, title, content).blockingGet()
        if (bean.info == "success") {
          // 更新本地数据
          AffairDataBase.INSTANCE.getAffairDao()
            .updateAffair(AffairEntity(selfNum, id, time, title, content, atWhatTime))
        } else throw ApiException(bean.status, bean.info)
      }.onErrorResumeWith {
        // 这里说明网络出问题了或者本地临时事务没有上传成功
        // 要先判断是否是本地临时的事务，如果是本地临时的事务就更新 add 表
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
    if (selfNum.isEmpty()) return Completable.error(RuntimeException("学号为空"))
    return uploadLocalAffair(selfNum)
      .doOnComplete {
        val result = AffairApiService.INSTANCE.deleteAffair(id).blockingGet()
        if (result.info == "success") {
          AffairDataBase.INSTANCE.getAffairDao().deleteAffair(StuNumWithAffairId(selfNum, id))
        } else throw ApiException(result.status, result.info)
      }.doOnError {
        // 这里说明网络出问题了或者本地临时事务没有上传成功
        // 先检查是否是本地临时事务
        val result = LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
          .deleteLocalAddAffair(StuNumWithLocalAffairId(selfNum, id))
        if (result == 0) {
          // result = 0 则说明不是本地临时事务
          // 直接删除本地 Update 表对应数据，不管有没有
          LocalAffairDataBase.INSTANCE.getLocalUpdateAffairDao()
            .deleteLocalUpdateAffair(StuNumWithLocalAffairId(selfNum, id))
          // 再插入本地临时数据库
          LocalAffairDataBase.INSTANCE.getLocalDeleteAffairDao()
            .insertLocalDeleteAffair(LocalDeleteAffairEntity(selfNum, id))
        }
        // 更新本地数据
        AffairDataBase.INSTANCE.getAffairDao().deleteAffair(StuNumWithAffairId(selfNum, id))
      }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
    val dateBase = LocalAffairDataBase.INSTANCE
    // 添加事务的干流
    val addCompletable = dateBase.getLocalAddAffairDao().getLocalAddAffair(stuNum)
      .flatMapPublisher { list ->
        // 每条事务的小流
        val maybeList = list.map { entity ->
          dateBase.getLocalAddAffairDao().deleteLocalAddAffair(entity)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
              // 为了保证同步性，所以先删除本地后再上传远端，如果远端上传失败就重新插入本地
              // 这里数据库返回的数据为 0 就表示数据库没有这个数据，说明已经被其他线程抢先了
              if (it == 0) throw RuntimeException("$entity 已经被另一个线程删除！")
            }.flatMap {
              // 上游删除成功了，这里就可以上传到远端
              AffairApiService.INSTANCE
                .addAffair(entity.dateJson, entity.time, entity.title, entity.content)
                .doOnSuccess { bean ->
                  if (bean.info == "success") {
                    AffairDataBase.INSTANCE.getAffairDao().apply {
                      getAffairById(stuNum, entity.id)?.let { old ->
                        // 删除旧的数据
                        deleteAffair(old)
                        // 更改为新的 id
                        val new = old.copy(id = bean.id)
                        // 插入新的数据
                        insertAffair(new)
                      }
                    }
                  } else throw ApiException(bean.status, bean.info)
                }.doOnError {
                  // 如果上传到远端失败了，就把之前删除的数据给插回去
                  dateBase.getLocalAddAffairDao().insertLocalAddAffair(entity)
                } // 错误会继续抛给下游
            }
        }
        // 合并成一个 Flowable，这里只能使用 mergeDelayError 不能使用 merge，因为 merge 会使网络请求不能得到正常的处理
        Single.mergeDelayError(maybeList)
      }.flatMapCompletable {
        // 转换成 Completable，
        // 1、因为下游只需要知道这条干流是否成功即可；
        // 2、Completable 便于下游再次合并
        if (it.info == "success") Completable.complete()
        else Completable.error(ApiException(it.status, it.info))
      }
    // 更新事务的干流
    val updateCompletable = dateBase.getLocalUpdateAffairDao().getLocalUpdateAffair(stuNum)
      .flatMapPublisher { list ->
        val singleList = list.map { entity ->
          dateBase.getLocalUpdateAffairDao()
            .deleteLocalUpdateAffair(entity)
            .doOnSuccess {
              // 为了保证同步性，所以先删除本地后再上传远端，如果远端上传失败就重新插入本地
              // 这里数据库返回的数据为 0 就表示数据库没有这个数据，说明已经被其他线程抢先了
              if (it == 0) throw RuntimeException("$entity 已经被另一个线程删除！")
            }.flatMap {
              // 每条事务的小流
              AffairApiService.INSTANCE
                .updateAffair(entity.id, entity.dateJson, entity.time, entity.title, entity.content)
                .doOnSuccess {
                  if (it.info == "success") {
                    dateBase.getLocalUpdateAffairDao().deleteLocalUpdateAffair(entity)
                  } else throw ApiException(it.status, it.info)
                }.doOnError {
                  // 如果上传到远端失败了，就把之前删除的数据给插回去
                  dateBase.getLocalUpdateAffairDao().insertLocalUpdateAffair(entity)
                }
            }.subscribeOn(Schedulers.io())
        }
        // 合并成干流
        Single.mergeDelayError(singleList)
      }.flatMapCompletable {
        if (it.info == "success") Completable.complete()
        else Completable.error(ApiException(it.status, it.info))
      }
    // 删除事务的上流
    val deleteCompletable = dateBase.getLocalDeleteAffairDao().getLocalDeleteAffair(stuNum)
      .flatMapPublisher { list ->
        // 每条事务的小流
        val singleList = list.map { entity ->
          dateBase.getLocalDeleteAffairDao()
            .deleteLocalDeleteAffair(entity)
            .doOnSuccess {
              // 为了保证同步性，所以先删除本地后再上传远端，如果远端上传失败就重新插入本地
              // 这里数据库返回的数据为 0 就表示数据库没有这个数据，说明已经被其他线程抢先了
              if (it == 0) throw RuntimeException("$entity 已经被另一个线程删除！")
            }.flatMap {
              AffairApiService.INSTANCE.deleteAffair(entity.id)
                .doOnSuccess {
                  if (it.info == "success") {
                    dateBase.getLocalDeleteAffairDao().deleteLocalDeleteAffair(entity)
                  } else throw ApiException(it.status, it.info)
                }.doOnError {
                  // 如果上传到远端失败了，就把之前删除的数据给插回去
                  dateBase.getLocalDeleteAffairDao().insertLocalDeleteAffair(entity)
                }
            }.subscribeOn(Schedulers.io())
        }
        // 合并成干流
        Single.mergeDelayError(singleList)
      }.flatMapCompletable {
        if (it.info == "success") Completable.complete()
        else Completable.error(ApiException(it.status, it.info))
      }
    // 将三条干流汇聚成最后的一条主流
    return Completable.mergeDelayError(listOf(addCompletable, updateCompletable, deleteCompletable))
  }

  /**
   * 得到一个随机的事务 id
   */
  @WorkerThread
  private fun getRandomLocalId(stuNum: String): Int {
    // 后端 id 是正数，所以本地使用负数来保存，但为了以防万一，请尽量不要使用本特殊条件
    var localId = Random.nextInt(-9999, 0)
    var localAffair = LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
      .getLocalAddAffair(stuNum, localId)
    // 保证与本地临时保存数据库不重复
    while (localAffair != null) {
      localId = Random.nextInt(-9999, 0)
      localAffair = LocalAffairDataBase.INSTANCE.getLocalAddAffairDao()
        .getLocalAddAffair(stuNum, localId)
    }
    return localId
  }
}