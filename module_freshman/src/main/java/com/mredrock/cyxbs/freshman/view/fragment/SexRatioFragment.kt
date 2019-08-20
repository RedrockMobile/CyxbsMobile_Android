package com.mredrock.cyxbs.freshman.view.fragment


import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseFragment
import com.mredrock.cyxbs.freshman.bean.SexRatioText
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSexRatioModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentSexRatioPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSexRatioView
import com.mredrock.cyxbs.freshman.presenter.FragmentSexRatioPresenter
import com.mredrock.cyxbs.freshman.util.event.SexRatioEvent
import com.mredrock.cyxbs.freshman.view.widget.PieChart
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max
import kotlin.math.min

/**
 * Create by yuanbing
 * on 2019/8/6
 */
class SexRatioFragment(val college: String) : BaseFragment<IFragmentSexRatioView,
        IFragmentSexRatioPresenter, IFragmentSexRatioModel>(), IFragmentSexRatioView {
    private lateinit var mPieChart: PieChart

    override fun onCreateView(view: View, savedInstanceState: Bundle?) {
        mPieChart = view.findViewById(R.id.pie_chart_data_disclosure_sex_ratio)
        mPieChart.gone()
        presenter?.getSexRatio(college)
    }

    override fun getLayoutRes() = R.layout.freshman_fragment_data_disclosure_sex_ratio

    override fun getViewToAttach() = this

    override fun createPresenter() = FragmentSexRatioPresenter()

    override fun showSexRatio(text: SexRatioText) {
        val boy = text.boy.split("%")[0].toFloat() / 100
        val girl = text.girl.split("%")[0].toFloat() / 100
        mPieChart.mFirstGraphWeight = boy
        mPieChart.mSecondGraphWeight = girl
        mPieChart.visible()
        mPieChart.mAnimation?.start()
    }

    override fun getSexRatioFaild() {
        mPieChart.gone()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun playAnimation(sexRatioEvent: SexRatioEvent) {
        if (sexRatioEvent.token) {
            mPieChart.mAnimation?.start()
        } else {
            mPieChart.mAnimation?.cancel()
        }
    }


}