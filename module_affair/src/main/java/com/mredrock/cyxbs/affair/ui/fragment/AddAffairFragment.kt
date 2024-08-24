package com.mredrock.cyxbs.affair.ui.fragment

import android.Manifest
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.bean.RemindMode
import com.mredrock.cyxbs.affair.bean.RemindMode.Companion.generateDefaultRemindMode
import com.mredrock.cyxbs.affair.bean.Todo
import com.mredrock.cyxbs.affair.bean.TodoListPushWrapper
import com.mredrock.cyxbs.affair.ui.adapter.AffairDurationAdapter
import com.mredrock.cyxbs.affair.ui.adapter.TitleCandidateAdapter
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekData
import com.mredrock.cyxbs.affair.ui.adapter.data.toAtWhatTime
import com.mredrock.cyxbs.affair.ui.fragment.utils.AffairPageManager
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.AddAffairViewModel
import com.mredrock.cyxbs.affair.ui.dialog.RemindSelectDialog
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/3 15:12
 */
class AddAffairFragment : BaseFragment(R.layout.affair_fragment_add_affair) {
  
  companion object {
    fun newInstance(
      week: Int,
      day: Int,
      beginLesson: Int,
      period: Int
    ): AddAffairFragment {
      return AddAffairFragment().apply {
        arguments = bundleOf(
          this::mAffairWeek.name to week,
          this::mAffairDay.name to day,
          this::mAffairBeginLesson.name to beginLesson,
          this::mAffairPeriod.name to period
        )
      }
    }
  }
  
  private val mAffairWeek by arguments<Int>()
  private val mAffairDay by arguments<Int>()
  private val mAffairBeginLesson by arguments<Int>()
  private val mAffairPeriod by arguments<Int>()
  
  private val mViewModel by viewModels<AddAffairViewModel>()
  
  private val mActivityViewModel by activityViewModels<AffairViewModel>()
  
  private val mEditText by R.id.affair_et_add_affair.view<EditText>()
  private val mTvRemind by R.id.affair_tv_add_affair_remind.view<TextView>()
  private val mTvAddTodo by R.id.affair_tv_add_affair_addTodo.view<TextView>()
  
  private val mRvTitleCandidate by R.id.affair_rv_add_affair_title_candidate.view<RecyclerView>()
  private val mRvTitleCandidateAdapter = TitleCandidateAdapter()
  
  private val mRvDuration by R.id.affair_rv_add_affair_duration.view<RecyclerView>()
  private val mRvDurationAdapter = AffairDurationAdapter()
  
  private var mRemindMinute = 0 // 提醒时间的分钟数，用于临时保存后进行网络请求
  
  private val mPageManager = AffairPageManager(this)
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRv()
    initListener()
    initObserve()
  }
  
  private fun initRv() {
    mRvTitleCandidate.adapter = mRvTitleCandidateAdapter
      .setClickListener {
        val index = mEditText.selectionStart // 得到光标位置
        val text = mEditText.text
        text.insert(index, it)
      }
    mRvTitleCandidate.layoutManager =
      FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
    
    mRvDuration.adapter = mRvDurationAdapter
    mRvDuration.layoutManager =
      FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
    
    mRvDurationAdapter.submitList(
      listOf(
        AffairWeekData(mAffairWeek),
        AffairTimeData(mAffairDay, mAffairBeginLesson, mAffairPeriod)
      )
    )
  }
  
  private fun initListener() {
    mEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        return if (p1 == EditorInfo.IME_ACTION_NEXT) {
          mActivityViewModel.clickNextBtn()
          return true
        } else false
      }
    })
    mTvRemind.setOnSingleClickListener {
      val dialog = RemindSelectDialog(requireActivity()) { text, minute ->
        // 获取添加进手机日历的权限
        doPermissionAction(
          Manifest.permission.READ_CALENDAR,
          Manifest.permission.WRITE_CALENDAR
        ) {
          reason = "设置提醒需要访问您的日历哦~"
          doAfterGranted {
            mTvRemind.text = text
            mRemindMinute = minute
          }
          doAfterRefused {
            "申请权限被拒绝".toast()
          }
        }
      }
      dialog.show()
    }
    mTvAddTodo.setOnSingleClickListener {
      if (mTvAddTodo.text == "加入待办") {
        mTvAddTodo.text = "取消待办"
      } else {
        mTvAddTodo.text = "加入待办"
      }
    }
  }
  
  private fun initObserve() {
    mViewModel.titleCandidates.observe {
      mRvTitleCandidateAdapter.submitList(it)
    }
    
    mActivityViewModel.clickAffect.collectLaunch {
      if (mPageManager.isEndPage()) {
        mViewModel.addAffair(
          mRemindMinute,
          mPageManager.getTitle(),
          mPageManager.getContent(),
          mRvDurationAdapter.currentList.toAtWhatTime(),
        )
        if(mTvAddTodo.text == "取消待办"){
          mViewModel.addTodo(translationToTodo())
        }
        requireActivity().finish()
      } else {
        mPageManager.loadNextPage()
      }
    }
  }
  private fun translationToTodo(): TodoListPushWrapper {
    val todo = Todo(
      0,
      mPageManager.getTitle(),
      mPageManager.getContent(),
      0,
      generateDefaultRemindMode(),
      System.currentTimeMillis(),
      "other",
      0
    )
    val todoList = listOf(todo)
    return TodoListPushWrapper(
      todoList,
      0
    )
  }
}