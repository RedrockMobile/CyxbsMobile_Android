package com.mredrock.cyxbs.course.page.link.model

import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.course.page.link.network.LinkApiServices
import com.mredrock.cyxbs.course.page.link.room.LinkDataBase
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.ApiException
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.service.impl
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
  
  /**
   * 观察当前登录人的我的关联数据
   * 1、支持换账号登录后返回新登录人的数据
   * 2、第一次观察时会请求新的数据
   * 3、使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * 4、上游不会抛出错误到下游
   */
  fun observeLinkStudent(): Observable<LinkStuEntity> {
    return IAccountService::class.impl
      .getUserService()
      .observeStuNumUnsafe()
      .switchMap { // 使用 switchMap 可以停止之前学号的订阅
        val stuNum = it.getOrNull()
        if (stuNum == null) Observable.just(LinkStuEntity.NULL)
        else {
          LinkDataBase.INSTANCE.getLinkStuDao()
            .observeLinkStu(stuNum)
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged() // 必加，因为 Room 每次修改都会回调，所以需要加个这个去重
            .doOnSubscribe {
              // 在开始订阅时请求一次云端数据
              getLinkStudent().unsafeSubscribeBy()
            }
        }
      }
  }
  
  /**
   * 只是单纯的得到数据，如果要观察请使用 [observeLinkStudent]
   */
  fun getLinkStudent(
    selfNum: String = IAccountService::class.impl.getUserService().getStuNum()
  ): Single<LinkStuEntity> {
    if (selfNum.isEmpty()) return Single.never()
    return LinkApiServices::class.api
      .getLinkStudent()
      .map {
        if (it.info == "success") {
          it.data
        } else throw ApiException(it.status, it.info)
      }.map {
        // 因为本地有个是否显示的逻辑，所以需要先拿本地数据库做比较
        val linkStu = LinkDataBase.INSTANCE.getLinkStuDao()
          .getLinkStu(selfNum)
        if (linkStu != null) {
          if (linkStu.linkNum == it.stuNum) {
            return@map linkStu
          }
        }
        val newLinkStu = LinkStuEntity(it, true)
        // 这里说明远端数据数据与本地不同，需要修改数据库
        LinkDataBase.INSTANCE.getLinkStuDao()
          .insertLinkStu(newLinkStu)
        newLinkStu
      }.onErrorReturn {
        // 这里说明网络连接失败，只能使用本地数据
        LinkDataBase.INSTANCE.getLinkStuDao()
          .getLinkStu(selfNum) ?: LinkStuEntity.NULL
      }.subscribeOn(Schedulers.io())
  }
  
  fun deleteLinkStudent(
    entity: LinkStuEntity
  ): Single<Boolean> {
    if (entity.isNull()) return Single.never()
    return LinkApiServices::class.api
      .deleteLinkStudent()
      .map {
        it.info == "success"
      }.doOnSuccess {
        // 这个只能更新，不能使用删除，
        // 因为 Observable 不能发送 null，所以删除后在观察时是不会回调的
        LinkDataBase.INSTANCE.getLinkStuDao()
          .updateLinkStu(entity.toNull())
          .unsafeSubscribeBy()
      }.onErrorReturn {
        false
      }.subscribeOn(Schedulers.io())
  }
  
  fun changeLinkStudent(
    selfNum: String = IAccountService::class.impl.getUserService().getStuNum()
  ): Single<LinkStuEntity> {
    if (selfNum.isEmpty()) return Single.never()
    return LinkApiServices::class.api
      .changeLinkStudent(selfNum)
      .map {
        if (it.info == "success") {
          LinkStuEntity(it.data, true)
        } else throw ApiException(it.status, it.info)
      }.doOnSuccess {
        LinkDataBase.INSTANCE.getLinkStuDao()
          .insertLinkStu(it)
      }.subscribeOn(Schedulers.io())
  }
}