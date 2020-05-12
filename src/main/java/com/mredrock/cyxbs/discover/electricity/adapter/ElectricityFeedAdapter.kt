package com.mredrock.cyxbs.discover.electricity.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.discover.electricity.bean.ElecInf
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_discover_feed.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sp

class ElectricityFeedAdapter(private val elecInf: ElecInf) : BaseFeedFragment.Adapter() {

    override fun onCreateView(context: Context, parent: ViewGroup): View {
        view = context.layoutInflater.inflate(R.layout.electricity_discover_feed, parent, false)
        refresh(elecInf)
        return view!!
    }
    fun refresh(elecInf: ElecInf){
        val context = view?.context
        context?:return
        if (elecInf.getAverage().length > 1) {
            view?.tv_electricity_feed_fee?.text = SpannableStringBuilder(elecInf.getAverage().plus("元")).apply {
                setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                setSpan(AbsoluteSizeSpan(context.sp(13)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        //elecSpend可能为空
        val spend = elecInf.elecSpend?:"0"
        view?.tv_electricity_feed_kilowatt?.text = SpannableStringBuilder(spend.plus("度")).apply {
            setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(AbsoluteSizeSpan(context.sp(13)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }


}