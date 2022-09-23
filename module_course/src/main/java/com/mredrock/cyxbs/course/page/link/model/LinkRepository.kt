package com.mredrock.cyxbs.course.page.link.model

import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.course.page.link.network.LinkApiServices
import com.mredrock.cyxbs.course.page.link.room.LinkDataBase
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import com.mredrock.cyxbs.lib.utils.network.throwApiExceptionIfFail
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 19:02
 */
object LinkRepository {
  
  private val mLinkStuDB by lazyUnlock { LinkDataBase.INSTANCE.getLinkStuDao() }
  
  /**
   * 观察当前登录人的我的关联数据
   * - 支持换账号登录后返回新登录人的数据
   * - 第一次观察时会请求新的数据
   * - 使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * - 上游不会抛出错误到下游
   *
   * ## 注意
   * 只要开始订阅，就一定会发送数据下来，但是否有关联人请通过 [LinkStuEntity.isNull] 来判断
   */
  fun observeLinkStudent(): Observable<LinkStuEntity> {
    return IAccountService::class.impl
      .getUserService()
      .observeStuNumState()
      .switchMap { value ->
        // 使用 switchMap 可以停止之前学号的订阅
        value.nullUnless(Observable.just(LinkStuEntity.NULL)) {
          mLinkStuDB.observeLinkStu(it) // 然后观察数据库
            .distinctUntilChanged() // 必加，因为 Room 每次修改都会回调，所以需要加个这个去重
            .doOnSubscribe {
              // 在开始订阅时请求一次云端数据
              getLinkStudent().unsafeSubscribeBy()
            }.subscribeOn(Schedulers.io())
        }
      }
  }
  
  /**
   * 只是单纯的得到数据，如果要观察请使用 [observeLinkStudent]
   *
   * ## 注意
   * - 只要学号不为空串，就不会返回异常
   * - 网络连接失败时会返回本地数据，本地数据为 null 时会返回一个空的 [LinkStuEntity]，但会包含自己的学号
   */
  fun getLinkStudent(): Single<LinkStuEntity> {
    val selfNum = IAccountService::class.impl.getUserService().getStuNum()
    if (selfNum.isBlank()) return Single.error(IllegalStateException("学号为空！"))
    return LinkApiServices::class.api
      .getLinkStudent()
      .mapOrThrowApiException()
      .map {
        // 因为本地有其他字段，所以需要先拿本地数据库做比较
        val linkStu = mLinkStuDB.getLinkStu(selfNum)
        if (linkStu != null && linkStu.isNotNull()) {
          if (linkStu.linkNum == it.linkNum) {
            return@map linkStu
          }
        }
        // 这里说明与远端的关联人不一样，需要修改数据库
        // 但注意后端对应没有关联人时会返回空串，所以需要使用 it.isNotEmpty()
        val newLinkStu = LinkStuEntity(it, it.isNotEmpty(), it.gender == "男")
        mLinkStuDB.insertLinkStu(newLinkStu)
        newLinkStu
      }.onErrorReturn {
        // 这里说明网络连接失败，只能使用本地数据
        mLinkStuDB.getLinkStu(selfNum) ?: LinkStuEntity.NULL.copy(selfNum = selfNum)
      }.subscribeOn(Schedulers.io())
  }
  
  fun deleteLinkStudent(): Completable {
    val selfNum = IAccountService::class.impl.getUserService().getStuNum()
    if (selfNum.isEmpty()) return Completable.error(IllegalStateException("学号为空！"))
    return LinkApiServices::class.api
      .deleteLinkStudent()
      .throwApiExceptionIfFail()
      .flatMapCompletable {
        // 这个只能更新，不能使用删除，
        // 因为 Observable 不能发送 null，所以删除后的观察中是不会回调的
        mLinkStuDB.updateLinkStu(LinkStuEntity.NULL.copy(selfNum = selfNum))
        Completable.complete()
      }.subscribeOn(Schedulers.io())
  }
  
  fun changeLinkStudent(linkNum: String): Single<LinkStuEntity> {
    return LinkApiServices::class.api
      .changeLinkStudent(linkNum)
      .mapOrThrowApiException()
      .map {
        LinkStuEntity(it, true, it.gender == "男")
      }.doOnSuccess {
        mLinkStuDB.insertLinkStu(it)
      }.subscribeOn(Schedulers.io())
  }
  
  fun changeLinkStuVisible(visible: Boolean): Completable {
    val selfNum = IAccountService::class.impl.getUserService().getStuNum()
    if (selfNum.isEmpty()) return Completable.error(IllegalStateException("学号为空！"))
    return Completable.create {
      val linkStuEntity = mLinkStuDB.getLinkStu(selfNum)
      if (linkStuEntity != null) {
        if (linkStuEntity.isShowLink != visible) {
          mLinkStuDB.updateLinkStu(linkStuEntity.copy(isShowLink = visible))
        }
        it.onComplete()
      } else {
        it.onError(RuntimeException("数据库不存在该学号（$selfNum）的关联人"))
      }
    }.subscribeOn(Schedulers.io())
  }
}