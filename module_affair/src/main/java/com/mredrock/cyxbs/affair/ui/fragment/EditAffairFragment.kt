package com.mredrock.cyxbs.affair.ui.fragment

import android.Manifest
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
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
import com.mredrock.cyxbs.affair.ui.adapter.AffairDurationAdapter
import com.mredrock.cyxbs.affair.ui.adapter.data.toAtWhatTime
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.EditAffairViewModel
import com.mredrock.cyxbs.affair.widge.RemindSelectDialog
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/3 15:14
 */
class EditAffairFragment : BaseFragment(R.layout.affair_fragment_edit_affair) {

  companion object {
    fun newInstance(affairId: Int): EditAffairFragment {
      return EditAffairFragment().apply {
        arguments = bundleOf(
          this::mAffairId.name to affairId
        )
      }
    }
  }
  
  private val mAffairId by arguments<Int>()
  
  private val mViewModel by viewModels<EditAffairViewModel>()
  private val mActivityViewModel by activityViewModels<AffairViewModel>()
  
  private val mEtTitle: EditText by R.id.affair_et_edit_affair_title.view()
  private val mEtContent: EditText by R.id.affair_et_edit_affair_content.view()
  private val mTvRemind: TextView by R.id.affair_tv_edit_affair_remind.view()
  
  private val mRvDuration: RecyclerView by R.id.affair_rv_edit_affair_duration.view()
  private val mRvDurationAdapter = AffairDurationAdapter()
  
  private var mRemindMinute = 0 // 提醒时间的分钟数，用于临时保存后进行网络请求

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mViewModel.findAffairEntity(mAffairId)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()
    initListener()
    initObserve()
  }
  
  private fun initRecyclerView() {
    mRvDuration.adapter = mRvDurationAdapter
    mRvDuration.layoutManager =
      FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
  }

  private fun initListener() {
    mEtContent.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        return if (p1 == EditorInfo.IME_ACTION_NEXT) {
          mActivityViewModel.clickNextBtn()
          return true
        } else false
      }
    })
    
    mTvRemind.setOnSingleClickListener {
      val dialog = RemindSelectDialog(requireActivity()) { text, minute ->
        //获取权限
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
  }

  private fun initObserve() {
    mActivityViewModel.clickAffect.collectLaunch {
      if (mEtTitle.text.isBlank()) {
        toast("掌友，标题不能为空哟！")
      } else {
        mViewModel.updateAffair(
          mAffairId,
          mRemindMinute,
          mEtTitle.text.toString(),
          mEtContent.text.toString(),
          mRvDurationAdapter.currentList.toAtWhatTime()
        )
        requireActivity().finish()
      }
    }
    
    mViewModel.affairEntity.observe {
      mEtTitle.setText(it.title)
      mEtContent.setText(it.content)
      mRvDurationAdapter.submitList(it.toAffairAdapterData())
      mRemindMinute = it.time
      mTvRemind.text = RemindSelectDialog.getTextByMinute(it.time)
    }
  }
}