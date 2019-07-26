package com.mredrock.cyxbs.discover.emptyroom.utils

import android.content.Context
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.emptyroom.ui.adapter.StringAdapter
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.DefaultItemDecoration
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.MultiSelector

/**
 * Created by Cynthia on 2018/9/19
 */
class ViewInitializer private constructor() {
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private lateinit var mAdapter: MultiSelector.Adapter<*, *>
    private var mItemDecoration: RecyclerView.ItemDecoration? = null

    fun getLayoutManager(): RecyclerView.LayoutManager? {
        return mLayoutManager
    }

    fun getAdapter(): MultiSelector.Adapter<*, *> {
        return mAdapter
    }

    fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return mItemDecoration
    }

    class Builder(private val mContext: Context) {
        private val mInitializer: ViewInitializer = ViewInitializer()

        fun layoutManager(layoutManager: RecyclerView.LayoutManager): Builder {
            mInitializer.mLayoutManager = layoutManager
            return this
        }

        fun adapter(adapter: MultiSelector.Adapter<*, *>): Builder {
            mInitializer.mAdapter = adapter
            return this
        }

        fun itemDecoration(itemDecoration: RecyclerView.ItemDecoration): Builder {
            mInitializer.mItemDecoration = itemDecoration
            return this
        }

        fun horizontalLinearLayoutManager(): Builder {
            return linearLayoutManager(LinearLayoutManager.HORIZONTAL)
        }

        fun verticalLinearLayoutManager(): Builder {
            return linearLayoutManager(LinearLayoutManager.VERTICAL)
        }

        fun stringAdapter(selector: MultiSelector, layoutWrapper: StringAdapter.LayoutWrapper): Builder {
            mInitializer.mAdapter = StringAdapter(selector, layoutWrapper)
            return this
        }

        fun gap(@Px head: Int, @Px middle: Int, @Px tail: Int): Builder {
            mInitializer.mItemDecoration = DefaultItemDecoration(head, middle, tail)
            return this
        }

        fun build(): ViewInitializer {
            if (mInitializer.mLayoutManager == null) {
                horizontalLinearLayoutManager()
            }
            return mInitializer
        }

        private fun linearLayoutManager(orientation: Int): Builder {
            val layoutManager = LinearLayoutManager(mContext)
            layoutManager.orientation = orientation
            mInitializer.mLayoutManager = layoutManager
            return this
        }
    }
}