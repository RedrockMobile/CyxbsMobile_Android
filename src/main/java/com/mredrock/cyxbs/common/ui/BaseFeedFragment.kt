package com.mredrock.cyxbs.common.ui

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.fragment_base_feed.*
import org.jetbrains.anko.textResource

//module_discover 中的信息部分
abstract class BaseFeedFragment<T : BaseViewModel> : BaseViewModelFragment<T>() {
    private lateinit var adapter: Adapter
    protected abstract var hasTopSplitLine: Boolean
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasTopSplitLine) {
            feed_split_line.visibility = View.VISIBLE
        }
    }

    fun setTitle(name: String) {
        tv_feed_title.text = name
    }

    fun setTitle(res: Int) {
        tv_feed_title.textResource = res
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    fun setSubtitle(name: String) {
        tv_feed_subtitle.text = name
    }

    fun setSubtitle(res: Int) {
        tv_feed_subtitle.textResource = res
    }

    fun setAdapter(adapter: Adapter) {
        this.adapter = adapter
        TransitionManager.beginDelayedTransition(ll_feed)
        if (ll_feed.childCount > 2) {
            ll_feed.removeViews(2, ll_feed.childCount - 2)
        }
        context?.let {
            ll_feed.addView(adapter.getView(it, ll_feed))
        }

    }

    fun getAdapter() = adapter

    fun setOnClickListener(listener: () -> Unit) {
        ll_feed.setOnClickListener { listener.invoke() }
    }

    abstract fun onRefresh()

    abstract class Adapter {
        protected var view: View? = null
        fun getView(context: Context, parent: ViewGroup): View {
            view = view ?: onCreateView(context, parent)
            return view!!
        }

        abstract fun onCreateView(context: Context, parent: ViewGroup): View

    }
}

