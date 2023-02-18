package com.mredrock.cyxbs.common.ui

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel


//module_discover 中的信息部分
@Deprecated("因 common 模块进行了迁移，但该基类并没有迁移的必要性，所以废弃。如果需要使用，可以自己写一个额外的 Fragment")
abstract class BaseFeedFragment<T : BaseViewModel> : BaseViewModelFragment<T>() {
    private lateinit var adapter: Adapter
    protected abstract var hasTopSplitLine: Boolean
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.common_fragment_base_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasTopSplitLine) {
            view.findViewById<View>(R.id.feed_split_line).visibility = View.VISIBLE
        }
    }
    
    private val tv_feed_title by R.id.tv_feed_title.view<TextView>()

    fun setTitle(name: String) {
        tv_feed_title.text = name
    }

    fun setTitle(res: Int) {
        tv_feed_title.setText(res)
    }
    
    private val tv_feed_second_title by R.id.tv_feed_second_title.view<TextView>()

    fun setSecondTitle(name: String) {
        tv_feed_second_title.visible()
        tv_feed_second_title.text = name
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }
    
    private val tv_feed_subtitle by R.id.tv_feed_subtitle.view<TextView>()

    fun setSubtitle(name: String) {
        tv_feed_subtitle.text = name
    }

    fun setSubtitle(res: Int) {
        tv_feed_subtitle.setText(res)
    }
    
    private val iv_feed_left_icon by R.id.iv_feed_left_icon.view<ImageView>()

    fun setLeftIcon(res: Int) {
        iv_feed_left_icon.background = ContextCompat.getDrawable(requireContext(), res)
    }

    fun onLeftIconClick(onClick: (view: View) -> Unit) {
        iv_feed_left_icon.setOnClickListener {
            onClick.invoke(it)
        }
    }
    
    private val ll_feed by R.id.ll_feed.view<LinearLayoutCompat>()

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
        ll_feed.setOnSingleClickListener { listener.invoke() }
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

