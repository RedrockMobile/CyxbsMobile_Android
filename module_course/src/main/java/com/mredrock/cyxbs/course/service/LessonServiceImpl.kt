package com.mredrock.cyxbs.course.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.course.COURSE_LESSON
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/14 17:09
 */
@Route(path = COURSE_LESSON, name = COURSE_LESSON)
class LessonServiceImpl : ILessonService {
  
  private val mAccountService = ServiceManager(IAccountService::class)
  
  override fun getStuLesson(stuNum: String): Single<List<ILessonService.Lesson>> {
    return StuLessonRepository.getLesson(stuNum)
      .map { it.toLesson() }
  }
  
  override fun getSelfLesson(): Single<List<ILessonService.Lesson>> {
    val stuNum = mAccountService.getUserService().getStuNum()
    return if (stuNum.isBlank()) Single.error(IllegalStateException("未登录"))
    else getStuLesson(stuNum)
  }
  
  override fun getLinkLesson(): Single<List<ILessonService.Lesson>> {
    return LinkRepository.getLinkStudent()
      .doOnSuccess {
        if (it.isNull()) throw IllegalStateException("当前登录人(${it.selfNum}关联人为空)")
      }.flatMap {
        getStuLesson(it.selfNum)
      }
  }
  
  override fun getSelfLinkLesson(): Single<Pair<List<ILessonService.Lesson>, List<ILessonService.Lesson>>> {
    return Single.zip(
      getSelfLesson(),
      getLinkLesson().onErrorReturnItem(emptyList())
    ) { self, link ->
      self to link
    }
  }
  
  override fun observeSelfLesson(isForce: Boolean): Observable<List<ILessonService.Lesson>> {
    return StuLessonRepository.observeSelfLesson(isForce)
      .map { it.toLesson() }
  }
  
  override fun init(context: Context) {
  }
}

fun List<StuLessonEntity>.toLesson(): List<ILessonService.Lesson> {
  return buildList {
    this@toLesson.forEach { entity ->
      entity.week.forEach { week ->
        add(
          ILessonService.Lesson(
            entity.stuNum,
            week,
            entity.beginLesson,
            entity.classroom,
            entity.course,
            entity.courseNum,
            entity.day,
            entity.hashDay,
            entity.period,
            entity.rawWeek,
            entity.teacher,
            entity.type
          )
        )
      }
    }
  }
}