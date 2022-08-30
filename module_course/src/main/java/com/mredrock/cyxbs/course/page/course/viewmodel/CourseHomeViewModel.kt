package com.mredrock.cyxbs.course.page.course.viewmodel

import androidx.lifecycle.*
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.toStuLessonData
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository
import com.mredrock.cyxbs.course.page.course.page.viewmodel.CoursePageViewModel
import com.mredrock.cyxbs.course.page.course.utils.CourseDiff
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 18:02
 */
class CourseHomeViewModel : CoursePageViewModel() {
  
  fun changeLinkStuVisible(isShowLink: Boolean) {
    LinkRepository.changeLinkStuVisible(isShowLink)
    // 这里更新后，所有观察关联的地方都会重新发送新数据
  }
  
  /**
   * 重新得到课表数据
   *
   * 注意：
   * - 该方法请用于测试使用
   * - 课表数据是通过观察来回调的，一般不需要自己主动刷新
   * - ViewModel 的 init 中已经加载了课表数据，请不要在 Activity 初始化时加载
   */
  fun resetLessonData() {
    mLessonDataDisposable?.dispose()
    observableLessons()
  }
  
  private val _lessonList = MutableLiveData<LessonResult>()
  val lessonList: LiveData<LessonResult>
    get() = _lessonList
  
  init {
    observableLessons()
  }
  
  private var mLessonDataDisposable: Disposable? = null
  
  private fun observableLessons() {
    val selfObservable = StuLessonRepository.observeSelfLesson(true)
      .map {
        val selfNewList = it.newList.toStuLessonData(StuLessonData.Who.Self)
        val selfOldList = (it.oldList ?: emptyList()).toStuLessonData(StuLessonData.Who.Self)
        SelfResult(it.stuNum, selfNewList, CourseDiff.start(it.stuNum, selfNewList, selfOldList))
      }
    val linkObservable = LinkRepository.observeLinkStudent()
      .switchMap { entity ->
        /*
        * 这里使用 switchMap 有一些原因
        * 1、如果本地数据库保存的是老数据，那么仓库层是会优先返回本地老数据的，然后你就立马去请求老数据对应人的课了
        * 2、而在订阅时会进行一次网络请求得到新的数据，如果数据有变，新数据又会回调到这里
        * 3、此时取消关联的操作会比请求老数据对应人课返回结果快，就会导致取消了关联，但又被老数据覆盖了
        * 4、所以需要使用 switchMap 使收到新数据时关闭老数据请求的流
        * 5、因为是得到课程数据，所以中途断开流并不会造成什么影响
        * */
        if (entity.isNotNull()) {
          StuLessonRepository.observeLesson(entity.linkNum)
            .map {
              // 不显示就返回 emptyList
              val linkNewList =
                if (entity.isShowLink) it.newList.toStuLessonData(StuLessonData.Who.Link) else emptyList()
              // Observable 因为不能直接发送 null 值，所以使用 Result 包裹
              Result.success(LinkResult(it.stuNum, linkNewList, entity))
            }
        } else Observable.just(Result.success(null))
      }
  
    // 用 combineLatest 操作符结合数据
    mLessonDataDisposable = Observable.combineLatest(
      selfObservable,
      linkObservable,
    ) { self, link ->
      LessonResult(
        // 如果 self 的对象还是之前那个对象，要使 diffResult = null 才行
        if (lessonList.value?.self !== self) self else self.copy(diffResult = null),
        link.getOrNull()
      )
    }.safeSubscribeBy {
      _lessonList.postValue(it)
    }
  }
  
  /**
   * @param selfNum 自己的学号
   * @param list 自己课程的数据
   * @param diffResult 与数据库中旧数据的比对结果
   */
  data class SelfResult(
    val selfNum: String,
    val list: List<StuLessonData>,
    val diffResult: CourseDiff.Result?
  )
  
  /**
   * @param linkNum 关联人的学号
   * @param list 关联人的课程，注意：如果 [linkStuEntity] 中 isShowLink 为 false，则该数据为 emptyList
   * @param linkStuEntity 关联人的实体
   */
  data class LinkResult(
    val linkNum: String,
    val list: List<StuLessonData>,
    val linkStuEntity: LinkStuEntity,
  )
  
  
  /**
   * @param self 自己的课程
   * @param link 关联人的课程，为 null 时说明没得关联人
   * @param affair 自己的事务
   * @param nowWeek 当前周数
   */
  data class LessonResult(
    val self: SelfResult,
    val link: LinkResult?,
    val nowWeek: Int? = SchoolCalendarUtil.getWeekOfTerm()
  )
}