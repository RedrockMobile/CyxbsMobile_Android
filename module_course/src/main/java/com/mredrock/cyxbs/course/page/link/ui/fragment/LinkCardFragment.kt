package com.mredrock.cyxbs.course.page.link.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.viewmodel.activity.FindLessonViewModel
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.course.page.link.viewmodel.fragment.LinkCardViewModel
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/10 11:09
 */
class LinkCardFragment : BaseFragment() {
  
  private val viewModel by viewModels<LinkCardViewModel>()
  
  private val mActivityViewModel by activityViewModels<FindLessonViewModel>()
  
  // 没有关联时的 include（这个对象是 include 的根布局）
  private val mViewLinkNoInclude by R.id.course_include_link_card_no.view<View>()
  
  // 已关联时的 include
  private val mViewLinkIngInclude by R.id.course_include_link_card_ing.view<View>()
    .addInitialize {
      setOnSingleClickListener {
        mActivityViewModel.changeCourseState(mLinkStu.linkNum, true)
      }
    }
  
  // 已关联人的姓名
  private val mTvName by R.id.course_tv_find_course_link_name_ing.view<TextView>()
  
  // 已关联人的专业
  private val mTvMajor by R.id.course_tv_find_course_link_major_ing.view<TextView>()
  
  // 已关联人的学号
  private val mTvStuNum by R.id.course_tv_find_course_link_stu_num_ing.view<TextView>()
  
  // 我的关联旁边人的数字
  private val mTvLinkNum by R.id.course_tv_link_card_num.view<TextView>()
  
  // 删除关联的按钮
  private val mBtnLinkDelete by R.id.course_ib_link_card_delete.view<ImageButton>()
    .addInitialize {
      setOnSingleClickListener {
        ChooseDialog.Builder(this@LinkCardFragment)
          .setData(
            ChooseDialog.Data(
              content = "确定要取消关联吗？",
              width = 255.dp2px,
              height = 146.dp2px,
            )
          ).setPositiveClick {
            viewModel.deleteLinkStudent()
            dismiss()
          }.setNegativeClick {
            dismiss()
          }.show()
      }
    }
  
  private lateinit var mLinkStu: LinkStuEntity // 点击回调中会调用这个
  private var mIsFirstShow = true // 是否是第一次加载
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.course_fragment_link_card, container, false)
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initObserve()
  }
  
  private fun initObserve() {
    // 关联改变的回调
    viewModel.linkStudent.observe {
      if (it == null) {
        hide()
      } else {
        show(it)
      }
    }
  }
  
  private fun show(linkStu: LinkStuEntity) {
    mLinkStu = linkStu
    if (mIsFirstShow) {
      mIsFirstShow = false
      showInternal(linkStu)
    } else {
      ValueAnimator.ofFloat(0F, 1F).apply {
        addUpdateListener {
          val now = it.animatedValue as Float
          mViewLinkNoInclude.alpha = 1- now
          mViewLinkNoInclude.translationX = it.animatedFraction * requireView().width
          mViewLinkIngInclude.alpha = now
          mViewLinkIngInclude.translationX = -(1 - it.animatedFraction) * requireView().width
        }
        doOnEnd { showInternal(linkStu) }
        duration = 360
        start()
      }
    }
  }
  
  private fun showInternal(linkStu: LinkStuEntity) {
    mViewLinkNoInclude.gone()
    mViewLinkIngInclude.visible()
    mBtnLinkDelete.visible()
    mTvName.text = linkStu.linkName
    mTvMajor.text = linkStu.linkMajor
    mTvStuNum.text = linkStu.linkNum
    mTvLinkNum.text = "（1/1）"
  }
  
  private fun hide() {
    if (mIsFirstShow) {
      // 在第一次加载时不能使用动画，也不要修改 mFirstShow 变量
      // 因为第一次的回调是 hide()，如果有数据，那马上就会回调 show()，如果使用动画就会出现因动画延时而变成 hide 的问题
      hideInternal()
    } else {
      ValueAnimator.ofFloat(0F, 1F).apply {
        addUpdateListener {
          val now = it.animatedValue as Float
          mViewLinkNoInclude.alpha = now
          mViewLinkNoInclude.translationX = (1 - it.animatedFraction) * requireView().width
          mViewLinkIngInclude.alpha = 1- now
          mViewLinkIngInclude.translationX = -it.animatedFraction * requireView().width
        }
        doOnEnd { hideInternal() }
        duration = 360
        start()
      }
    }
  }
  
  private fun hideInternal() {
    mViewLinkNoInclude.visible()
    mViewLinkIngInclude.gone()
    mTvLinkNum.text = "（0/1）"
    mBtnLinkDelete.gone()
  }
}