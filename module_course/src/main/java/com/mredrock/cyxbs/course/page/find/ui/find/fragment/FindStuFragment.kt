package com.mredrock.cyxbs.course.page.find.ui.find.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.adapter.RvHistoryAdapter
import com.mredrock.cyxbs.course.page.find.room.FindPersonEntity
import com.mredrock.cyxbs.course.page.find.ui.find.activity.ShowResultActivity
import com.mredrock.cyxbs.course.page.find.viewmodel.activity.FindLessonViewModel
import com.mredrock.cyxbs.course.page.find.viewmodel.fragment.FindStuViewModel
import com.mredrock.cyxbs.course.page.link.ui.fragment.LinkCardFragment
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmFragment
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/8 17:03
 */
class FindStuFragment : BaseVmFragment<FindStuViewModel>() {
  
  // Activity 的 ViewModel
  private val mActivityViewModel by activityViewModels<FindLessonViewModel>()
  
  // 历史记录的 Adapter
  private lateinit var mRvHistoryAdapter: ListAdapter<FindPersonEntity, RvHistoryAdapter.HistoryVH>
  
  // 搜索框
  private val mEtSearch: EditText by R.id.course_et_find_course_stu.view()
  
  // 历史记录
  private val mRvHistory: RecyclerView by R.id.course_rv_find_course_stu.view()
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.course_fragment_find_stu, container, false)
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecycler()
    initEditText()
    initLinkCard()
    initObserve()
  }
  
  private fun initRecycler() {
    mRvHistory.layoutManager =
      FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
    mRvHistory.adapter = RvHistoryAdapter()
      .setOnDeleteClick {
        viewModel.deleteHistory(num)
      }.setOnTextClick {
        mActivityViewModel.changeCourseState(this)
      }.setOnLongClick {
        // 自己偷偷加的长按历史记录快速关联的功能 :)
        doIfLogin { // 登录才能使用
          if (viewModel.linkStudent.value?.linkNum == null) {
            viewModel.changeLinkStudent(num)
          } else if (num != viewModel.linkStudent.value?.linkNum) {
            ChooseDialog.Builder(this@FindStuFragment)
              .setData(
                ChooseDialog.Data(
                  content = "你已有一位关联的同学\n确定要替换吗？",
                  width = 255.dp2px,
                  height = 167.dp2px
                )
              ).setPositiveClick {
                viewModel.changeLinkStudent(num)
                dismiss()
              }.setNegativeClick {
                dismiss()
              }.show()
          }
        }
      }.apply {
        mRvHistoryAdapter = this
      }
  }
  
  private fun initEditText() {
    mEtSearch.setOnEditorActionListener { tv, _, _ ->
      val text = tv.text
      if (text.isEmpty()) {
        Snackbar.make(requireView(), "输入为空", LENGTH_SHORT).show()
        false
      } else {
        viewModel.searchStudents(text.toString())
        true
      }
    }
  }
  
  // 初始化我的关联卡片
  private fun initLinkCard() {
    replaceFragment(R.id.course_fcv_find_stu_link_card) {
      LinkCardFragment()
    }
  }
  
  private fun initObserve() {
    // 搜索的回调
    viewModel.studentSearchData.collectLaunch {
      if (it.isNotEmpty()) {
        ShowResultActivity.startActivity(
          requireContext(),
          ShowResultActivity.StuData(it, viewModel.linkStudent.value)
        )
      } else {
        toast("查无此人")
      }
    }
    // 历史记录数据的回调
    viewModel.studentHistory.observe {
      mRvHistoryAdapter.submitList(it.reversed())
    }
  }
}