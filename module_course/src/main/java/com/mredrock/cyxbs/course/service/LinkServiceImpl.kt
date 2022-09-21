package com.mredrock.cyxbs.course.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.course.COURSE_LINK
import com.mredrock.cyxbs.api.course.ILinkService
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/3 12:47
 */
@Route(path = COURSE_LINK)
class LinkServiceImpl : ILinkService {
  
  override fun getLinkStu(): Single<ILinkService.LinkStu> {
    return LinkRepository.getLinkStudent()
      .map { it.toLinkStu() }
  }
  
  override fun observeSelfLinkStu(): Observable<ILinkService.LinkStu> {
    return LinkRepository.observeLinkStudent()
      .map { it.toLinkStu() }
  }
  
  override fun init(context: Context) {
  }
  
  private fun LinkStuEntity.toLinkStu(): ILinkService.LinkStu {
    return ILinkService.LinkStu(selfNum, linkNum, linkMajor, linkName, isShowLink)
  }
}