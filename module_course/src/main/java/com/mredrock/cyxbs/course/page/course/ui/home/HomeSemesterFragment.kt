package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.distinctUntilChanged
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.config.config.SchoolCalendar
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.ui.home.utils.EnterAnimUtils
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.page.CourseSemesterFragment
import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.lib.utils.utils.judge.NetworkUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeSemesterFragment : CourseSemesterFragment() {
  
  private val mParentViewModel by createViewModelLazy(
    HomeCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initEntrance()
    initObserve()
    initCreateAffair()
    initNoInternetLogic()
  }
  
  private fun initEntrance() {
    // 如果是被异常重启，则不执行动画
    if (!mIsFragmentRebuilt) {
      /**
       * 观察第几周，因为如果是初次进入应用，会因为得不到周数而不主动翻页，所以只能观察该数据
       * 但这是因为主页课表比较特殊而采取观察，其他界面可以直接使用 *VpFragment 的 mNowWeek 变量
       */
      SchoolCalendar.observeWeekOfTerm()
        .firstElement()
        .observeOn(AndroidSchedulers.mainThread())
        .safeSubscribeBy {
          if (it == 0) {
            // 判断周数，只对当前周进行动画
            EnterAnimUtils.startEnterAnim(course, mParentViewModel, viewLifecycleOwner)
          }
        }
    }
  }
  
  /**
   * 是否显示关联人，但需要点击一次关联人图标，才会激活该变量
   * 初始值为 null 是表示没有点击过关联人图标
   */
  private var mIsShowLinkEventAfterClick: Boolean? = null
  
  private val mSelfLessonContainerProxy = SelfLessonContainerProxy(this)
  private val mLinkLessonContainerProxy = LinkLessonContainerProxy(this)
  private val mAffairContainerProxy = AffairContainerProxy(this)
  
  private fun initObserve() {
    mParentViewModel.showLinkEvent
      .collectLaunch {
        mIsShowLinkEventAfterClick = it
      }
    
    mParentViewModel.homeWeekData
      .distinctUntilChanged()
      .observe { map ->
        val self = map.mapValues { it.value.self }.mapToMinWeek()
        val link = map.mapValues { it.value.link }.mapToMinWeek()
        val affair = map.values.map { it.affair }.flatten()
        mAffairContainerProxy.diffRefresh(affair)
        mSelfLessonContainerProxy.diffRefresh(self)
        mLinkLessonContainerProxy.diffRefresh(link) {
          if (mIsShowLinkEventAfterClick == true && mParentViewModel.currentItem == 0 && it.isNotEmpty()) {
            // 这时说明触发了关联人的显示，需要开启入场动画
            mLinkLessonContainerProxy.startAnimation()
          }
        }
      }
  }
  
  /**
   * 长按创建事务
   */
  private fun initCreateAffair() {
    course.addPointerDispatcher(
      CreateAffairDispatcher(this).apply {
        setOnClickListener {
          IAffairService::class.impl
            .startActivityForAddAffair(0, lp.weekNum - 1, getBeginLesson(lp.startRow), lp.length)
          cancelShow()
        }
      }
    )
  }
  
  override fun isExhibitionItem(item: IItem): Boolean {
    return super.isExhibitionItem(item) || item is ITouchAffairItem
  }
  
  /**
   * 点击中午和傍晚的折叠
   */
  override fun initFoldLogic() {
    super.initFoldLogic()
    if (!mIsFragmentRebuilt) {
      // 只有在第一次显示 Fragment 时才进行监听，后面的折叠状态会由 CourseFoldHelper 保存
      course.addItemExistListener(
        object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem, view: View) {
            val lp = item.lp
            if (compareNoonPeriod(lp.startRow) * compareNoonPeriod(lp.endRow) <= 0) {
              unfoldNoon()
            }
            if (compareDuskPeriod(lp.startRow) * compareDuskPeriod(lp.endRow) <= 0) {
              unfoldDusk()
            }
          }
        }
      )
    }
  }
  
  /**
   * 初始化没有网络连接时的逻辑
   */
  private fun initNoInternetLogic() {
    if (!ILessonService.isUseLocalSaveLesson) {
      val noLessonImage = ivNoLesson.drawable
      val noLessonText = tvNoLesson.text
      if (!NetworkUtil.isAvailable()) {
        ivNoLesson.setImageResource(com.mredrock.cyxbs.config.R.drawable.config_ic_404)
        tvNoLesson.text = "联网才能查看课表哦~"
        mParentViewModel.homeWeekData.observeUntil {
          NetworkUtil.isAvailable().also {
            if (it) {
              // 网络可用了就还原设置
              ivNoLesson.setImageDrawable(noLessonImage)
              tvNoLesson.text = noLessonText
            }
          }
        }
      }
    }
  }
  
  /**
   * 筛选掉重复的课程，只留下最小周数的课程
   */
  private fun <T : LessonData> Map<Int, List<T>>.mapToMinWeek(): List<T> {
    val map = hashMapOf<LessonData.Course, T>()
    forEach { entry ->
      entry.value.forEach {
        val value = map[it.course]
        if (value == null || value.week > it.week) {
          map[it.course] = it
        }
      }
    }
    return map.map { it.value }
  }
}