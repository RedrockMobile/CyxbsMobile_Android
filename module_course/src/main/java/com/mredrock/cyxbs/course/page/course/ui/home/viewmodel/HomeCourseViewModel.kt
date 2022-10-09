package com.mredrock.cyxbs.course.page.course.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.api.widget.IWidgetService
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.toAffairData
import com.mredrock.cyxbs.course.page.course.data.toStuLessonData
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.course.service.CourseServiceImpl
import com.mredrock.cyxbs.course.service.toLesson
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/27 17:12
 */
class HomeCourseViewModel : BaseViewModel() {
  
  private val _homeWeekData = MutableLiveData<Map<Int, HomePageResult>>()
  val homeWeekData: LiveData<Map<Int, HomePageResult>> get() = _homeWeekData
  
  private val _linkStu = MutableLiveData<LinkStuEntity>()
  val linkStu: LiveData<LinkStuEntity> get() = _linkStu
  
  private val _refresh = MutableSharedFlow<Boolean>()
  val refreshEvent: SharedFlow<Boolean> get() = _refresh
  
  private val _showLinkEvent = MutableSharedFlow<Boolean>()
  val showLinkEvent: SharedFlow<Boolean> get() = _showLinkEvent
  
  val courseService = ICourseService::class.impl as CourseServiceImpl
  
  // Vp2 的 currentItem
  var currentItem: Int = 0
  
  /**
   * 改变关联人的可见性
   */
  fun changeLinkStuVisible(isShowLink: Boolean) {
    LinkRepository.changeLinkStuVisible(isShowLink)
      .safeSubscribeBy {
        viewModelScope.launch {
          _showLinkEvent.emit(isShowLink)
        }
      }
    // 这里更新后，所有观察关联人的地方都会重新发送新数据
  }
  
  /**
   * 重新请求数据，相当于强制刷新
   */
  fun refreshData() {
    LinkRepository.getLinkStudent()
      .flatMapCompletable {
        // 直接调用网络刷新，请求成功后会修改数据库，然后上面的观察流会重新发送新的值
        val self = StuLessonRepository.refreshLesson(it.selfNum)
        val affair = IAffairService::class.impl.refreshAffair()
        if (it.isNotNull()) {
          val link = StuLessonRepository.refreshLesson(it.linkNum)
          Single.mergeDelayError(self, link, affair) // 使用 mergeDelayError() 延迟异常
            .flatMapCompletable { Completable.complete() }
        } else {
          Single.mergeDelayError(self, affair)
            .flatMapCompletable { Completable.complete() }
        }
      }.doOnError {
        viewModelScope.launch {
          _refresh.emit(false)
        }
      }.safeSubscribeBy {
        viewModelScope.launch {
          _refresh.emit(true)
        }
      }
  }
  
  init {
    initObserve()
  }
  
  /**
   * 注意：整个课表采用了观察者模式。数据库对应的数据改变，会自动修改视图内容
   */
  private fun initObserve() {
    // 自己课的观察流
    val selfLessonObservable = StuLessonRepository.observeSelfLesson()
  
    // 关联人课的观察流
    val linkLessonObservable = LinkRepository.observeLinkStudent()
      .doOnNext { _linkStu.postValue(it) }
      .switchMap {
        // 没得关联人和不显示关联课程时发送空数据
        if (it.isNull() || !it.isShowLink) Observable.just(emptyList())
        else StuLessonRepository.observeLesson(it.linkNum)
      }
  
    // 事务的观察流
    val affairObservable = IAffairService::class.impl
      .observeSelfAffair()
  
    // 合并观察流
    Observable.combineLatest(
      selfLessonObservable,
      linkLessonObservable,
      affairObservable
    ) { self, link, affair ->
      // 通知小组件更新
      IWidgetService::class.impl
        .notifyWidgetRefresh(
          self.toLesson(),
          link.toLesson(),
          affair
        )
      // 装换为 data 数据类
      HomePageResultImpl.flatMap(
        self.toStuLessonData(),
        link.toStuLessonData(),
        affair.toAffairData()
      )
    }.subscribeOn(Schedulers.io())
      .safeSubscribeBy {
        _homeWeekData.postValue(it)
      }
  }
  
  
  
  
  interface HomePageResult {
    val self: List<StuLessonData>
    val link: List<StuLessonData>
    val affair: List<AffairData>
    
    companion object Empty : HomePageResult {
      override val self: List<StuLessonData>
        get() = emptyList()
      override val link: List<StuLessonData>
        get() = emptyList()
      override val affair: List<AffairData>
        get() = emptyList()
    }
  }
  
  data class HomePageResultImpl(
    override val self: MutableList<StuLessonData> = arrayListOf(),
    override val link: MutableList<StuLessonData> = arrayListOf(),
    override val affair: MutableList<AffairData> = arrayListOf()
  ) : HomePageResult {
    companion object {
      fun flatMap(
        self: List<StuLessonData>,
        link: List<StuLessonData>,
        affair: List<AffairData>
      ) : Map<Int, HomePageResultImpl> {
        return buildMap {
          self.forEach {
            getOrPut(it.week) { HomePageResultImpl() }
              .self.add(it)
          }
          link.forEach {
            getOrPut(it.week) { HomePageResultImpl() }
              .link.add(it)
          }
          affair.forEach { data ->
            if (data.week == 0) {
              // 因为 affair 模块那边对于整学期的事务使用 week = 0 来记录，
              // 所以这里需要单独做适配，把 week = 0 扩展到每一周去
              repeat(ICourseService.maxWeek) {
                getOrPut(it + 1) { HomePageResultImpl() }
                  .affair.add(data.copy(week = it + 1))
              }
            } else {
              getOrPut(data.week) { HomePageResultImpl() }
                .affair.add(data)
            }
          }
        }
      }
    }
  }
}