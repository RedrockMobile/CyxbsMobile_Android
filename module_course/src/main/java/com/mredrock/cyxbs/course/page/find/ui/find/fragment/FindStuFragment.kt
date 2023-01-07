package com.mredrock.cyxbs.course.page.find.ui.find.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.mredrock.cyxbs.course.page.find.room.FindStuEntity
import com.mredrock.cyxbs.course.page.find.ui.find.activity.ShowResultActivity
import com.mredrock.cyxbs.course.page.find.viewmodel.activity.FindLessonViewModel
import com.mredrock.cyxbs.course.page.find.viewmodel.fragment.FindStuViewModel
import com.mredrock.cyxbs.course.page.link.ui.fragment.LinkCardFragment
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/8 17:03
 */
class FindStuFragment : BaseFragment() {
  
  private val mViewModel by viewModels<FindStuViewModel>()
  
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
        mViewModel.deleteHistory(num)
      }.setOnTextClick {
        mActivityViewModel.changeCourseState(num, this is FindStuEntity)
      }.setOnLongClick {
        // 自己偷偷加的长按历史记录快速关联的功能 :)
        doIfLogin { // 登录才能使用
          if (mViewModel.linkStudent.value?.linkNum == null) {
            mViewModel.changeLinkStudent(num)
          } else if (num != mViewModel.linkStudent.value?.linkNum) {
            ChooseDialog.Builder(
              requireContext(),
              ChooseDialog.Data(
                content = "你已有一位关联的同学\n确定要替换吗？",
                width = 255.dp2px,
                height = 167.dp2px
              )
            ).setPositiveClick {
                mViewModel.changeLinkStudent(num)
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
        toast("查询中")
        mViewModel.searchStudents(text.toString())
        // 取消键盘
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
          .hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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
    mViewModel.studentSearchData.collectLaunch {
      if (it.isNotEmpty()) {
        ShowResultActivity.startActivity(
          requireContext(),
          ShowResultActivity.StuData(it, mViewModel.linkStudent.value)
        )
      } else {
        toast("查无此人")
      }
    }
    // 历史记录数据的回调
    mViewModel.studentHistory.observe {
      mRvHistoryAdapter.submitList(it.reversed())
    }
    
    // 打开界面直接查找时
    mActivityViewModel.findText.observe {
      if (it is FindLessonViewModel.StuName) {
        mEtSearch.setText(it.text)
        mViewModel.searchStudents(it.text)
      } else if (it is FindLessonViewModel.StuNum) {
        mActivityViewModel.changeCourseState(it.text, true)
      }
    }
  }
}