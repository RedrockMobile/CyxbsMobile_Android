package com.mredrock.cyxbs.course2.page.affair.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.model.data.AffairArgs
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.EditAffairViewModel
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

  private val mRvDuration: RecyclerView by R.id.affair_rv_edit_affair_duration.view()

  private lateinit var mArguments: AffairArgs

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mArguments = requireArguments().get(ARG_KEY) as AffairArgs
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
    initRecyclerView()
  }

  private fun initRecyclerView() {

  }
}