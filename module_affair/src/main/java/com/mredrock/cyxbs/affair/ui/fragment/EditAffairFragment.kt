package com.mredrock.cyxbs.affair.ui.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.model.data.AffairArgs
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs.AffairDurationArgs.Companion.REMIND_ARRAY
import com.mredrock.cyxbs.affair.ui.adapter.AffairDurationAdapter
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.EditAffairViewModel
import com.mredrock.cyxbs.affair.utils.AffairDataUtils
import com.mredrock.cyxbs.affair.utils.TimeUtils
import com.mredrock.cyxbs.affair.widge.RemindSelectDialog
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmFragment
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.utils.CalendarUtils

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/3 15:14
 */
class EditAffairFragment : BaseVmFragment<EditAffairViewModel>() {

  companion object {

    fun newInstance(args: AffairArgs): EditAffairFragment {
      return EditAffairFragment().apply {
        arguments = bundleOf(Pair(ARG_KEY, args))
      }
    }

    private const val ARG_KEY = "arg_key"
  }

  private val mEtTitle: EditText by R.id.affair_et_edit_affair_title.view()

  private val mEtContent: EditText by R.id.affair_et_edit_affair_content.view()
  private val mActivityViewModel by activityViewModels<AffairViewModel>()

  private val mRvDuration: RecyclerView by R.id.affair_rv_edit_affair_duration.view()
  private val mTvRemind: TextView by R.id.affair_tv_edit_affair_remind.view()
  private lateinit var mRvDurationAdapter: AffairDurationAdapter
  private lateinit var mArguments: AffairEditArgs
  private var affairId = 0
  private var remind = 0


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mArguments = requireArguments().get(ARG_KEY) as AffairEditArgs
    viewModel.findAffairEntity(mArguments.id)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.affair_fragment_edit_affair, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView()
    initRecyclerView()
    initObserve()
  }

  private fun initView() {
    mEtContent.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        return if (p1 == EditorInfo.IME_ACTION_NEXT) {
          mActivityViewModel.clickNextBtn()
          return true
        } else false
      }
    })
    mTvRemind.setOnSingleClickListener {
      val dialog = RemindSelectDialog(requireActivity()) { remind ->
        mTvRemind.text = REMIND_ARRAY[remind]
        this.remind = remind
      }
      dialog.show()
    }
  }

  private fun initRecyclerView() {
    mRvDurationAdapter = AffairDurationAdapter(requireActivity())
    mRvDuration.adapter = mRvDurationAdapter
    mRvDuration.layoutManager =
      FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)

    mRvDurationAdapter.submitList(
      AffairDataUtils.getNewList(
        listOf()
      )
    )
  }

  private fun initObserve() {
    mActivityViewModel.clickAffect.collectLaunch {
      val data = AffairDataUtils.affairAdapterDataToAtWhatTime(mRvDurationAdapter.currentList)
      if (remind!=0) {
        // 添加日历
        data.forEach {
          updateRemind(
            mEtTitle.text.toString(), mEtContent.text.toString(), it.beginLesson, it.period,
            it.day, TimeUtils.getRemind(remind)
          )
        }
      }
//      data.forEach {
//        deleteRemind(
//          mEtTitle.text.toString(),
//          mEtContent.text.toString(),
//          it.beginLesson,
//          it.day
//        )
//      }

      viewModel.updateAffair(
        affairId,
        1,
        mEtTitle.text.toString(),
        mEtContent.text.toString(),
        AffairDataUtils.affairAdapterDataToAtWhatTime(mRvDurationAdapter.currentList)
      )
      activity?.finish()
    }
    viewModel.affairEntity.observe {
      mEtTitle.setText(it.title)
      mEtContent.setText(it.content)
      affairId = it.id
      mRvDurationAdapter.submitList(
        AffairDataUtils.getNewList(
          AffairDataUtils.affairEntityToAffairAdapterData(
            it
          )
        )
      )
      remind = 0
      mTvRemind.text = REMIND_ARRAY[remind]
    }
  }

  private fun updateRemind(
    title: String,
    description: String,
    beginTime: Int,
    period: Int,
    week: Int,
    remindMinutes: Int
  ) {
    //获取权限
    doPermissionAction(
      Manifest.permission.READ_CALENDAR,
      Manifest.permission.WRITE_CALENDAR
    ) {
      reason = "设置提醒需要访问您的日历哦~"
      doAfterGranted {
        CalendarUtils.addCalendarEventRemind(
          requireActivity(),
          title,
          description,
          TimeUtils.getBegin(beginTime, week),
          TimeUtils.getDuration(period),
          TimeUtils.getRRule(week),
          remindMinutes,
          object : CalendarUtils.OnCalendarRemindListener {
            override fun onFailed(error_code: CalendarUtils.OnCalendarRemindListener.Status?) {
              "更新失败".toast()
              Log.e("TAG", "onFailed: 更新失败")
            }

            override fun onSuccess() {
              "更新日历成功".toast()
            }
          })
      }
      doAfterRefused {
        "呜呜呜".toast()
      }
    }
  }

  private fun deleteRemind(
    title: String,
    description: String,
    beginTime: Int,
    week: Int
  ) {
    CalendarUtils.deleteCalendarEventRemind(
      appContext,
      title,
      description,
      TimeUtils.getBegin(beginTime, week),
      object : CalendarUtils.OnCalendarRemindListener {
        override fun onFailed(error_code: CalendarUtils.OnCalendarRemindListener.Status?) {
          "删除失败".toast()
          Log.e("TAG", "onFailed: 删除失败")
        }

        override fun onSuccess() {
          "删除成功".toast()
        }
      })
  }
}