package com.mredrock.cyxbs.affair.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.ui.adapter.AffairDurationAdapter
import com.mredrock.cyxbs.affair.ui.adapter.TitleCandidateAdapter
import com.mredrock.cyxbs.affair.ui.adapter.data.toAtWhatTime
import com.mredrock.cyxbs.affair.ui.fragment.utils.AffairPageManager
import com.mredrock.cyxbs.affair.ui.fragment.utils.NoClassPageManager
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.NoClassAffairViewModel
import com.mredrock.cyxbs.api.affair.NoClassBean
import com.mredrock.cyxbs.lib.base.ui.BaseFragment


//没课约的安排日程
class NoClassAffairFragment : BaseFragment(R.layout.affair_fragment_noclass_affair) {

    companion object {
        fun newInstance(noClassBean: NoClassBean): NoClassAffairFragment {
            Log.d("lx", "newInstance:${noClassBean} ")
            return NoClassAffairFragment().apply {
                arguments = bundleOf(this::mNoClassBean.name to noClassBean)
            }
        }
    }

    private val mNoClassBean by arguments<NoClassBean>()
    private val mViewModel by viewModels<NoClassAffairViewModel>()

    private val mRootView: ConstraintLayout by R.id.affair_root_no_class_affair.view()
    private val mEditText by R.id.affair_et_no_class_affair.view<EditText>()
    private val mActivityViewModel by activityViewModels<AffairViewModel>()

    private val mRvTitleCandidate by R.id.affair_rv_no_class_affair_title_candidate.view<RecyclerView>()
    private val mRvTitleCandidateAdapter = TitleCandidateAdapter()

    private var mRemindMinute = 0 // 提醒时间的分钟数，用于临时保存后进行网络请求

    private val mPageManager = NoClassPageManager(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("lx", "noclassfragment:${mNoClassBean}: ")
        initRv()
        initListener()
        initObserve()

    }

    private fun initRv() {
        mRvTitleCandidate.adapter = mRvTitleCandidateAdapter
            .setClickListener {
                //todo 点击完毕之后就跳转
            }
        mRvTitleCandidate.layoutManager =
            FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
    }

    private fun initListener() {
        //点到键盘上的下个执行和点击下一个按钮相同的操作
        mEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                return if (p1 == EditorInfo.IME_ACTION_NEXT) {
                    mActivityViewModel.clickNextBtn()
                    return true
                } else false
            }
        })
    }

    private fun initObserve() {
        mViewModel.titleCandidates.observe {
            mRvTitleCandidateAdapter.submitList(it)
        }

        mActivityViewModel.clickAffect.collectLaunch {
            if (mPageManager.isEndPage()) {
                //todo 发送通知
                requireActivity().finish()
            } else {
                mPageManager.loadNextPage()
            }
        }
    }
}