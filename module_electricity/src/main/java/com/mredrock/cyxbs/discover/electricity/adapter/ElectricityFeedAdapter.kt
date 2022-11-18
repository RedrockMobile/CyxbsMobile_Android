package com.mredrock.cyxbs.discover.electricity.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.sp
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.electricity.bean.ElecInf
import com.mredrock.cyxbs.electricity.R

class ElectricityFeedAdapter(private val elecInf: ElecInf?) : BaseFeedFragment.Adapter() {
    private var csl_electricity_data_feed:ConstraintLayout? = null
    private var tv_electricity_no_data_feed:TextView? = null
    private var tv_electricity_feed_fee:TextView? = null
    private var tv_electricity_feed_kilowatt:TextView? = null

    override fun onCreateView(context: Context, parent: ViewGroup): View {
        view = LayoutInflater.from(context).inflate(R.layout.electricity_discover_feed, parent, false)
        csl_electricity_data_feed = view?.findViewById(R.id.csl_electricity_data_feed)
        tv_electricity_no_data_feed = view?.findViewById(R.id.tv_electricity_no_data_feed)
        tv_electricity_feed_fee = view?.findViewById(R.id.tv_electricity_feed_fee)
        tv_electricity_feed_kilowatt = view?.findViewById(R.id.tv_electricity_feed_kilowatt)
        refresh(elecInf)
        return view!!
    }

    fun refresh(elecInf: ElecInf?) {
        val context = view?.context
        context ?: return
        if (elecInf == null || elecInf.isEmpty()) {
            csl_electricity_data_feed?.gone()
            tv_electricity_no_data_feed?.visible()
        } else {
            csl_electricity_data_feed?.visible()
            tv_electricity_no_data_feed?.gone()
            if (elecInf.getAverage().length > 1) {
                //写出这样的代码，我很抱歉,后端不改～～
                tv_electricity_feed_fee?.text = SpannableStringBuilder(elecInf.getEleCost().toDouble().run {
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
            tv_electricity_feed_kilowatt?.text = SpannableStringBuilder(spend.plus("度")).apply {
                setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                setSpan(AbsoluteSizeSpan(context.sp(13)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }

}