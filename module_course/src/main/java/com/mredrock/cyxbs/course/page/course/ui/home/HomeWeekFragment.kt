package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.config.config.SchoolCalendar
import com.mredrock.cyxbs.course.page.course.ui.home.utils.EnterAnimUtils
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.ui.home.widget.MovableTouchAffairItem
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.page.CourseWeekFragment
import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ICreateAffairConfig
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchCallback
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.lib.utils.utils.judge.NetworkUtil
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeWeekFragment : CourseWeekFragment() {
  
  companion object {
    fun newInstance(week: Int): HomeWeekFragment {
      return HomeWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  override val mWeek by arguments<Int>()
  
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
          if (it == mWeek) {
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
      .map { it[mWeek] ?: HomeCourseViewModel.HomePageResult }
      .distinctUntilChanged()
      .observe {
        mAffairContainerProxy.diffRefresh(it.affair)
        mSelfLessonContainerProxy.diffRefresh(it.self)
        mLinkLessonContainerProxy.diffRefresh(it.link) { data ->
          if (mIsShowLinkEventAfterClick == true && mParentViewModel.currentItem == mWeek && data.isNotEmpty()) {
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
      CreateAffairDispatcher(
        this,
        object : ICreateAffairConfig by ICreateAffairConfig.Default {
          override fun createTouchAffairItem(
            course: ICourseViewGroup,
            event: IPointerEvent
          ): ITouchAffairItem = MovableTouchAffairItem(course)
        }
      ).apply {
        setOnClickListener {
          IAffairService::class.impl
            .startActivityForAddAffair(mWeek, lp.weekNum - 1, getBeginLesson(lp.startRow), lp.length)
          cancelShow()
        }
        addTouchCallback(
          object : ITouchCallback {
            override fun onTouchEnd(
              pointerId: Int,
              initialRow: Int,
              initialColumn: Int,
              touchRow: Int,
              topRow: Int,
              bottomRow: Int,
            ) {
              if (bottomRow == topRow) {
                tryToastSingleRow()
              }
            }
  
            /**
             * 在只是单独点击时，只会生成长度为 1 的 [ITouchAffairItem]，
             * 所以需要弹个 toast 来提示用户可以长按空白区域上下移动来生成更长的事务
             * (不然可能他一直不知道怎么生成更长的事务)
             */
            private fun tryToastSingleRow() {
              val sp = course.getContext().getSp("课表长按生成事务")
              val times = sp.getInt("点击单行事务的次数", 0)
              when (times) {
                1, 5, 12 -> {
                  toast("可以长按空白处上下移动添加哦~")
                }
              }
              sp.edit { putInt("点击单行事务的次数", times + 1) }
            }
          }
        )
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
          override fun onItemAddedAfter(item: IItem, view: View?) {
            if (view == null) return
            val lp = item.lp
            if (compareNoonPeriodByRow(lp.startRow) * compareNoonPeriodByRow(lp.endRow) <= 0) {
              unfoldNoon()
              course.postRemoveItemExistListener(this) // 只需要展开一次
            }
          }
        }
      )
      course.addItemExistListener(
        object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem, view: View?) {
            if (view == null) return
            val lp = item.lp
            if (compareDuskPeriodByRow(lp.startRow) * compareDuskPeriodByRow(lp.endRow) <= 0) {
              unfoldDusk()
              course.postRemoveItemExistListener(this) // 只需要展开一次
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
        mParentViewModel.homeWeekData.map {
          it[mWeek] ?: HomeCourseViewModel.HomePageResult
        }.observeUntil {
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
}