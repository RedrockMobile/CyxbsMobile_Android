package com.mredrock.cyxbs.common.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.fragment_base_feed.*
import org.jetbrains.anko.textResource

abstract class BaseFeedFragment<T : BaseViewModel> : BaseViewModelFragment<T>() {
    private lateinit var adapter: Adapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_feed, container, false)
    }


    fun setTitle(name: String) {
        tv_feed_title.text = name
    }

    fun setTitle(res: Int) {
        tv_feed_title.textResource = res
    }

    fun setSubtitle(name: String) {
        tv_feed_subtitle.text = name
    }

    fun setSubtitle(res: Int) {
        tv_feed_subtitle.textResource = res
    }

    fun setAdapter(adapter: Adapter) {
        this.adapter = adapter
        if (ll_feed.childCount > 1) {
            ll_feed.removeViews(1, ll_feed.childCount - 1)
        }
        context?.let {
            ll_feed.addView(adapter.getView(it, ll_feed))
            layoutInflater?.inflate(R.layout.common_splite_line, ll_feed)
        }

    }

    fun setOnClickListener(listener: () -> Unit) {
        ll_feed.setOnClickListener { listener.invoke() }
    }

    interface Adapter {
        fun getView(context: Context, parent: ViewGroup): View
    }
}

