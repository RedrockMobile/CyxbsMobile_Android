package com.mredrock.cyxbs.discover.electricity.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.sp
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.electricity.bean.ElecInf
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_discover_feed.view.*

class ElectricityFeedAdapter(private val elecInf: ElecInf?) : BaseFeedFragment.Adapter() {

    override fun onCreateView(context: Context, parent: ViewGroup): View {
        view = LayoutInflater.from(context).inflate(R.layout.electricity_discover_feed, parent, false)
        refresh(elecInf)
        return view!!
    }

    fun refresh(elecInf: ElecInf?) {
        val context = view?.context
        context ?: return
        if (elecInf == null || elecInf.isEmpty()) {
            view?.csl_electricity_data_feed?.gone()
            view?.tv_electricity_no_data_feed?.visible()
        } else {
            view?.csl_electricity_data_feed?.visible()
            view?.tv_electricity_no_data_feed?.gone()
            if (elecInf.getAverage().length > 1) {
                //写出这样的代码，我很抱歉,后端不改～～
                view?.tv_electricity_feed_fee?.text = SpannableStringBuilder(elecInf.getEleCost().toDouble().run {
                    if (this < 0) {
                        "0"
                    } else {
                        this.toString()
                    }
                }.plus("元")).apply {
                    setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    setSpan(AbsoluteSizeSpan(context.sp(13)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            //elecSpend可能为空
            val spend = elecInf.elecSpend ?: "0"
            view?.tv_electricity_feed_kilowatt?.text = SpannableStringBuilder(spend.plus("度")).apply {
                setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                setSpan(AbsoluteSizeSpan(context.sp(13)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }

}