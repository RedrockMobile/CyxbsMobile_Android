package com.mredrock.cyxbs.freshman.view.fragment

import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseFragment
import com.mredrock.cyxbs.freshman.bean.SubjectDataMessage
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSubjectDataModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentSubjectDataPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSubjectDataView
import com.mredrock.cyxbs.freshman.presenter.FragmentSubjectDataPresenter
import com.mredrock.cyxbs.freshman.util.event.SubjectDataEvent
import com.mredrock.cyxbs.freshman.view.widget.Histogram
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Create by yuanbing
 * on 2019/8/5
 */
class SubjectDataFragment(val college: String) : BaseFragment<IFragmentSubjectDataView,
        IFragmentSubjectDataPresenter, IFragmentSubjectDataModel>(), IFragmentSubjectDataView {
    private lateinit var mHistogram: Histogram

    override fun onCreateView(view: View, savedInstanceState: Bundle?) {
        mHistogram = view.findViewById(R.id.histogram_data_disclosure_subject_data)
        mHistogram.gone()
        presenter?.getSubjectData(college)
    }

    override fun getLayoutRes() = R.layout.freshman_fragment_data_disclosure_subject_data

    override fun getViewToAttach() = this

    override fun createPresenter() = FragmentSubjectDataPresenter()

    override fun showSubjectData(subjectData: List<SubjectDataMessage>) {
        mHistogram.mFirstGraphWeight = subjectData[0].data.toFloat()
        mHistogram.mFirstGraphDescription = subjectData[0].subject

        mHistogram.mSecondGraphWeight = subjectData[1].data.toFloat()
        mHistogram.mSecondGraphDescription = subjectData[1].subject

        mHistogram.mThirdGraphWeight = subjectData[2].data.toFloat()
        mHistogram.mThirdGraphDescription = subjectData[2].subject

        mHistogram.visible()
        mHistogram.mAnimation?.start()
    }

    override fun getSubJectDataFailed() {
        mHistogram.gone()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun playAnimation(subjectDataEvent: SubjectDataEvent) {
        mHistogram.mAnimation?.start()
    }
}