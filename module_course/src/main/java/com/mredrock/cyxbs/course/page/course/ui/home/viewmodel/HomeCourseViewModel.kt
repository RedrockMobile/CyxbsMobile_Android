package com.mredrock.cyxbs.course.page.course.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.toStuLessonData
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/27 17:12
 */
class HomeCourseViewModel : BaseViewModel() {
  
  val homeWeekData: LiveData<Map<Int, HomePageResult>> get() = _homeWeekData
  private val _homeWeekData = MutableLiveData<Map<Int, HomePageResult>>()
  
  val linkStu: LiveData<LinkStuEntity> get() = _linkStu
  private val _linkStu = MutableLiveData<LinkStuEntity>()
  
  /**
   * 改变关联人的可见性
   */
  fun changeLinkStuVisible(isShowLink: Boolean) {
    LinkRepository.changeLinkStuVisible(isShowLink)
    // 这里更新后，所有观察关联的地方都会重新发送新数据
  }
  
  /**
   * 重新请求数据，相当于强制刷新，建议测试使用
   */
  fun retryObserveHomeWeekData() {
    mRetryObservable.onNext(Unit)
  }
  
  /**
   * Rxjava 中类似于 LiveData 的东西，用于重新请求数据
   */
  private val mRetryObservable = BehaviorSubject.createDefault(Unit)
  
  init {
    mRetryObservable
      .switchMap {
        // 我的课的观察流
        val selfLessonObservable = StuLessonRepository.observeSelfLesson()
          .map { it.toStuLessonData(StuLessonData.Who.Self) }
        
        // 关联人的课的观察流
        val linkLessonObservable = LinkRepository.observeLinkStudent()
          .doOnNext { _linkStu.postValue(it) }
          .switchMap {
            if (it.isNull()) Observable.just(emptyList()) // 没得关联人时发送空数据
            else StuLessonRepository.observeLesson(it.linkNum)
          }.map { it.toStuLessonData(StuLessonData.Who.Link) }
        
        // 我的事务的观察流
        val affairObservable = Observable.never<List<AffairData>>() // TODO 事务待完成
        
        // 合并观察流
        Observable.combineLatest(
          selfLessonObservable,
          linkLessonObservable,
          affairObservable
        ) { self, link, affair -> HomePageResultImpl.getMap(self, link, affair) }
      }.safeSubscribeBy {
        _homeWeekData.postValue(it)
      }
  }
  
  interface HomePageResult {
    val selfLesson: List<StuLessonData>
    val linkLesson: List<StuLessonData>
    val affair: List<AffairData>
  }
  
  data class HomePageResultImpl(
    override val selfLesson: MutableList<StuLessonData>,
    override val linkLesson: MutableList<StuLessonData>,
    override val affair: MutableList<AffairData>
  ) : HomePageResult {
    companion object {
      fun getMap(
        self: List<StuLessonData>,
        link: List<StuLessonData>,
        affair: List<AffairData>
      ) : Map<Int, HomePageResultImpl> {
        return buildMap {
          self.forEach {
            getOrPut(it.week) { HomePageResultImpl(arrayListOf(), arrayListOf(), arrayListOf()) }
              .selfLesson.add(it)
          }
          link.forEach {
            getOrPut(it.week) { HomePageResultImpl(arrayListOf(), arrayListOf(), arrayListOf()) }
              .linkLesson.add(it)
          }
          affair.forEach {
            getOrPut(it.week) { HomePageResultImpl(arrayListOf(), arrayListOf(), arrayListOf()) }
              .affair.add(it)
          }
        }
      }
    }
  }
}