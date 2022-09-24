package com.mredrock.cyxbs.affair.ui.fragment

import android.Manifest
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs.AffairDurationArgs.Companion.REMIND_ARRAY
import com.mredrock.cyxbs.affair.ui.adapter.AffairDurationAdapter
import com.mredrock.cyxbs.affair.ui.adapter.TitleCandidateAdapter
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekData
import com.mredrock.cyxbs.affair.ui.fragment.AddAffairFragment.AffairPageManager.Companion.getCurrentPage
import com.mredrock.cyxbs.affair.ui.fragment.AddAffairFragment.AffairPageManager.Companion.loadLastPage
import com.mredrock.cyxbs.affair.ui.fragment.AddAffairFragment.AffairPageManager.Companion.loadNextPage
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.AddAffairViewModel
import com.mredrock.cyxbs.affair.utils.AffairDataUtils
import com.mredrock.cyxbs.affair.utils.TimeUtils
import com.mredrock.cyxbs.affair.widge.RemindSelectDialog
import com.mredrock.cyxbs.affair.widge.TextViewTransition
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmFragment
import com.mredrock.cyxbs.lib.utils.extensions.*
import com.mredrock.cyxbs.lib.utils.utils.CalendarUtils

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/3 15:12
 */
class AddAffairFragment : BaseVmFragment<AddAffairViewModel>() {

  companion object {
    fun newInstance(args: AffairEditArgs.AffairDurationArgs): AddAffairFragment {
      return AddAffairFragment().apply {
        arguments = bundleOf(Pair(ARG_KEY, args))
      }
    }


    private const val ARG_KEY = "arg_key"
  }

  private lateinit var mArguments: AffairEditArgs.AffairDurationArgs

  private val mActivityViewModel by activityViewModels<AffairViewModel>()

  private val mRootView: ConstraintLayout by R.id.course_root_add_affair.view()

  private val mTvText1: TextView by R.id.affair_tv_add_affair_text_1.view()
  private val mTvText2: TextView by R.id.affair_tv_add_affair_text_2.view()
  private val mTvText3: TextView by R.id.affair_tv_add_affair_text_3.view()

  private val mEtTitle: EditText by R.id.affair_tv_add_affair_title.view()
  private val mEditText: EditText by R.id.affair_et_add_affair.view()
  private val mTvRemind: TextView by R.id.affair_tv_add_affair_remind.view()
  private val mRvTitleCandidate: RecyclerView by R.id.affair_rv_add_affair_title_candidate.view()
  private val mRvTitleCandidateAdapter = TitleCandidateAdapter()
  lateinit var mRvDurationAdapter: AffairDurationAdapter
  private val mRvDuration: RecyclerView by R.id.affair_rv_add_affair_duration.view()
  private var a = 0 //当a=2时,才可以添加事务
  private var remind = 0

  // 拦截返回键
  private val mOnBackPressedCallback = object : OnBackPressedCallback(false) {
    override fun handleOnBackPressed() {

      if (loadLastPage() == 0) {
        isEnabled = false
      }
      // 计数清零
      if (getCurrentPage() != 2) {
        a = 0
      }
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    requireActivity().onBackPressedDispatcher.addCallback(
      this,
      mOnBackPressedCallback
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mArguments = requireArguments().get(ARG_KEY) as AffairEditArgs.AffairDurationArgs
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(
      R.layout.affair_fragment_add_affair,
      container,
      false
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initView()
    initRv()
    initObserve()
  }

  private fun initView() {
    mEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
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


  private fun initRv() {
    mRvTitleCandidate.adapter = mRvTitleCandidateAdapter.setClickListener {
      mEditText.setText(it)
    }
    mRvTitleCandidate.layoutManager =
      FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)

    mRvDurationAdapter = AffairDurationAdapter(requireActivity())
    mRvDuration.adapter = mRvDurationAdapter
    mRvDuration.layoutManager =
      FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)

    mRvDurationAdapter.submitList(
      AffairDataUtils.getNewList(
        listOf(
          AffairWeekData(mArguments.week, listOf()),
          AffairTimeData(mArguments.weekNum, mArguments.beginLesson, mArguments.period)
        )
      )
    )
  }

  private fun initObserve() {
    viewModel.titleCandidates.observe {
      mRvTitleCandidateAdapter.submitList(it)
    }



    mActivityViewModel.clickAffect.collectLaunch {
      loadNextPage()
      if (getCurrentPage() == 2) a++
      // 最后点击添加事务
      if (a > 1) {
        val data = AffairDataUtils.affairAdapterDataToAtWhatTime(mRvDurationAdapter.currentList)
        // 不提醒
        if (remind != 0) {
          data.forEach {
            addRemind(
              mEtTitle.text.toString(), mEditText.text.toString(), it.beginLesson, it.period,
              it.day, TimeUtils.getRemind(remind)
            )
          }
        }
        viewModel.addAffair(
          1,
          mEtTitle.text.toString(),
          mEditText.text.toString(),
          data,
        )
        activity?.finish()
      }
    }
  }


  protected var mTitle: String = ""
  protected var mContent: String = ""

  /**
   * 封装事务点击按钮到下一页的逻辑
   */
  private class AffairPageManager private constructor() {
    companion object {
      // 静态变量，不会随 Fragment 生命周期而变化
      private var sAffairPage: AffairPage = AffairPage.ADD_TITLE

      /**
       * @return 返回剩余页数
       */
      fun AddAffairFragment.loadNextPage(): Int {
        if (sAffairPage.ordinal + 1 == AffairPage.values().size) return 0
        sAffairPage = sAffairPage.next(this)
        return AffairPage.values().size - sAffairPage.ordinal - 1
      }

      /**
       * @return 返回剩余页数
       */
      fun AddAffairFragment.loadLastPage(): Int {
        if (sAffairPage.ordinal == 0) return 0
        sAffairPage = sAffairPage.last(this)
        return sAffairPage.ordinal
      }

      fun getCurrentPage(): Int {
        return sAffairPage.ordinal
      }
    }

    private enum class AffairPage {
      ADD_TITLE {
        override fun AddAffairFragment.nextInterval(): Boolean {
          mTitle = mEditText.text.toString()
          if (mTitle.isNotBlank()) {
            TransitionManager.beginDelayedTransition(mRootView, mTransitionSet)
            mTvText1.visible()
            mTvText3.text = "具体内容"
            mEtTitle.setText(mTitle)
            mEtTitle.visible()
            mEditText.text = null
            mRvTitleCandidate.gone()
            mOnBackPressedCallback.isEnabled = true
            return true
          } else {
            toast("掌友，标题不能为空哟！")
          }
          return false
        }

        override fun AddAffairFragment.lastInterval(): Boolean = false
      },
      ADD_CONTENT {
        override fun AddAffairFragment.nextInterval(): Boolean {
          mEtTitle.textSize = 34F
          mEtTitle.setText(mTitle)
          mContent = mEditText.text.toString()
          TransitionManager.beginDelayedTransition(mRootView, mTransitionSet)
          mTvText1.invisible()
          mTvText2.invisible()
          mTvText3.invisible()
          mRvDuration.visible()
          mTvRemind.visible()
          val set = ConstraintSet().apply { clone(mRootView) }
          set.connect(mEtTitle.id, ConstraintSet.START, mRootView.id, ConstraintSet.START)
          set.connect(mEtTitle.id, ConstraintSet.TOP, mTvText1.id, ConstraintSet.BOTTOM)
          set.clear(mEtTitle.id, ConstraintSet.BOTTOM)


//          mEtTitle.typeface = Typeface.DEFAULT_BOLD
          set.connect(mEditText.id, ConstraintSet.TOP, mEtTitle.id, ConstraintSet.BOTTOM)
          set.applyTo(mRootView)
//          childFragmentManager.commit {
//            addSharedElement(mEditText, getString(R.string.transitionName_add_affair_to_edit_affair_title))
//            add(EditAffairFragment.newInstance(mArguments), null)
//          }
          return true
        }

        override fun AddAffairFragment.lastInterval(): Boolean {
          TransitionManager.beginDelayedTransition(mRootView, mTransitionSet)
          mTvText1.invisible()
          mTvText3.text = "一个标题"
          mEtTitle.invisible()
          mEditText.setText(mTitle)
          mRvTitleCandidate.visible()
          return true
        }
      },
      ADD_TIME {
        override fun AddAffairFragment.nextInterval(): Boolean {
          return false
        }

        override fun AddAffairFragment.lastInterval(): Boolean {
          TransitionManager.beginDelayedTransition(mRootView, mTransitionSet)
          mTvText1.visible()
          mTvText2.visible()
          mTvText3.visible()
          mRvDuration.gone()
          mTvRemind.gone()
          val set = ConstraintSet().apply { clone(mRootView) }
          set.connect(mEtTitle.id, ConstraintSet.START, mTvText1.id, ConstraintSet.END)
          set.connect(mEtTitle.id, ConstraintSet.TOP, mTvText1.id, ConstraintSet.TOP)
          set.connect(mEtTitle.id, ConstraintSet.BOTTOM, mTvText1.id, ConstraintSet.BOTTOM)
          mEtTitle.textSize = 15F
          mEtTitle.typeface = Typeface.DEFAULT
          set.connect(mEditText.id, ConstraintSet.TOP, mTvText3.id, ConstraintSet.BOTTOM)
          set.applyTo(mRootView)
          mEditText.setText(mContent)
          return true
        }
      };

      // 进行下一页
      fun next(fragment: AddAffairFragment): AffairPage {
        if (fragment.run { nextInterval() }) {
          // 当返回 true 时表示下一页成功
          return values()[ordinal + 1]
        }
        return this
      }

      abstract fun AddAffairFragment.nextInterval(): Boolean

      fun last(fragment: AddAffairFragment): AffairPage {
        if (fragment.run { lastInterval() }) {
          return values()[ordinal - 1]
        }
        return this
      }

      abstract fun AddAffairFragment.lastInterval(): Boolean

      protected val mTransitionSet = TransitionSet().apply {
        addTransition(Fade())
        addTransition(Slide().apply { slideEdge = Gravity.END })
        addTransition(ChangeBounds())
        addTransition(TextViewTransition())
        duration = 300
      }
    }
  }

  private fun addRemind(
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
              "添加失败".toast()
              Log.e("TAG", "onFailed: 添加失败")
            }

            override fun onSuccess() {
              "添加成功".toast()
            }
          })
      }
      doAfterRefused {
        "呜呜呜".toast()
      }
    }
  }

}