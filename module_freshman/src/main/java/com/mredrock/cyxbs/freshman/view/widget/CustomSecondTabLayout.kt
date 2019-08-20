package com.mredrock.cyxbs.freshman.view.widget

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.freshman.util.decoration.CustomSecondTabLayoutItemDecoration
import com.mredrock.cyxbs.freshman.view.adapter.CustomSecondTabLayoutAdapter
import com.mredrock.cyxbs.freshman.view.adapter.ModeFixedAdapter
import org.jetbrains.anko.dip

/**
 * Create by yuanbing
 * on 2019/8/13
 */
class CustomSecondTabLayout(private val rv: RecyclerView) {
    private val mContext: Context = rv.context
    private val mLinearLayoutManager = LinearLayoutManager(mContext)
    private val mGridLayoutManager = GridLayoutManager(mContext, 1)
    private val mScrollableItemDecoration: RecyclerView.ItemDecoration = CustomSecondTabLayoutItemDecoration()
    private val mModeScrollableAdapter: CustomSecondTabLayoutAdapter = CustomSecondTabLayoutAdapter()
    private val mModeFixedAdapter: ModeFixedAdapter = ModeFixedAdapter()
    private var mCurrentItem: Int = 0
    private var mListener: ((Int) -> Unit)? = null
    private val mTabs: MutableList<String> = mutableListOf()
    var mTabMode = TabLayout.MODE_FIXED

    init {
        mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv.layoutManager = mLinearLayoutManager
        rv.adapter = mModeScrollableAdapter
        mModeScrollableAdapter.addOnTabSelectedListener {
            mCurrentItem = it
            mListener?.let { it(mCurrentItem) }
        }
        mModeFixedAdapter.addOnTabSelectedListener {
            mCurrentItem = it
            mListener?.let { it(mCurrentItem) }
        }
    }

    fun commit() {
        val tabs = mutableListOf<String>()
        tabs.addAll(mTabs)

        if (mTabMode == TabLayout.MODE_FIXED) {
            mGridLayoutManager.spanCount = tabs.size
            rv.layoutManager = mGridLayoutManager
            rv.adapter = mModeFixedAdapter
            mModeFixedAdapter.addTabs(tabs)
        } else {
            rv.addItemDecoration(mScrollableItemDecoration)
            mModeScrollableAdapter.addTabs(tabs)
        }
        mTabs.clear()
    }

    fun addTab(tab: String) {
        mTabs.add(tab)
    }

    fun addOnTabSelectedListener(listener: (Int) -> Unit) {
        mListener = listener
    }

    fun select(position: Int) {
        mCurrentItem = position
        mModeScrollableAdapter.select(position)
        mModeFixedAdapter.selecte(position)
        moveTabToCenter()
        mListener?.let { it(mCurrentItem) }
    }

    // ItemDecoration设置的偏移也是算到Item的尺寸里的
    private fun moveTabToCenter() {
        val view = mLinearLayoutManager.getChildAt(mCurrentItem)
        val currentOffset = ((view?.width ?: 0) + mContext.dip(12)) / 2
        mLinearLayoutManager.scrollToPositionWithOffset(mCurrentItem,
                rv.width / 2 - currentOffset)
    }
}