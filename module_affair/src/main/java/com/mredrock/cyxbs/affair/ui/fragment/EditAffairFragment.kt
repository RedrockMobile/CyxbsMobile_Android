package com.mredrock.cyxbs.affair.ui.fragment

import android.os.Bundle
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
import com.mredrock.cyxbs.affair.ui.adapter.AffairDurationAdapter
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.EditAffairViewModel
import com.mredrock.cyxbs.affair.utils.AffairDataUtils
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmFragment

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
  lateinit var mRvDurationAdapter: AffairDurationAdapter
  private lateinit var mArguments: AffairEditArgs
  private var affairId = 0

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
      viewModel.updateAffair(
        affairId,
        1,
        mEtTitle.text.toString(),
        mEtContent.text.toString(),
        AffairDataUtils.affairAdapterDataToAtWhatTime(mRvDurationAdapter.currentList)
      )
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
    }
  }
}