package com.mredrock.cyxbs.mine.util.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.widget.RvFooter


/**
 * Created by zzzia on 2018/8/17.
 * 针对问一问和帮一帮写的fragment共有类
 * 能够快速建立一个带swipeFreshLayout和footer的recyclerView
 * 可参考 {@link com.mredrock.cyxbs.mine.page.ask.AskAdoptedFm}
 * 为了适配更多的页面，没有进行更深的抽象
 */
abstract class BaseRVFragment<D> : Fragment() {

    abstract fun getItemLayout(): Int

    abstract fun bindFooterHolder(
        holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int
    )

    abstract fun bindDataHolder(
        holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int,
        data: D
    )

    abstract fun onSwipeLayoutRefresh()

    lateinit var mine_fragment_base_rv: RecyclerView
    lateinit var mine_fragment_base_sf: SwipeRefreshLayout
    lateinit var mine_fragment_base_placeholder: FrameLayout

    protected fun setNewData(newData: List<D>?) {
        if (mine_fragment_base_rv.isComputingLayout) {
            mine_fragment_base_rv.post { setNewData(newData) }
        } else {
            baseRVAdapter?.setNewData(newData)
        }
    }

    private var baseRVAdapter: BaseRVAdapter<D>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mine_fragment_base_sf = view.findViewById(R.id.mine_fragment_base_sf)
        mine_fragment_base_rv = view.findViewById(R.id.mine_fragment_base_rv)
        mine_fragment_base_placeholder = view.findViewById(R.id.mine_fragment_base_placeholder)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseRVAdapter = RvAdapter()
        setAdapter(baseRVAdapter as RvAdapter)

        mine_fragment_base_rv.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(context)

        mine_fragment_base_sf.setOnRefreshListener { onSwipeLayoutRefresh() }

    }

    private fun setAdapter(adapter: RvAdapter) {
        this.baseRVAdapter = adapter
        mine_fragment_base_rv.adapter = adapter
    }

    fun showPlaceHolder(view: View) {
        mine_fragment_base_placeholder.visibility = View.VISIBLE
        mine_fragment_base_placeholder.removeAllViews()
        mine_fragment_base_placeholder.addView(view)
    }

    fun hidePlaceHolder() {
        mine_fragment_base_placeholder.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mine_fragment_base_rv, container, false)
    }

    protected fun getAdapter(): BaseRVAdapter<D> {
        return baseRVAdapter!!
    }

    protected fun getSwipeLayout(): androidx.swiperefreshlayout.widget.SwipeRefreshLayout {
        return mine_fragment_base_sf
    }

    protected fun getRecyclerView(): androidx.recyclerview.widget.RecyclerView {
        return mine_fragment_base_rv
    }

    protected fun setState(state: RvFooter.State) {
        baseRVAdapter?.setState(state)
    }

    open inner class RvAdapter : BaseRVAdapter<D>() {

        override fun getNormalLayout(): Int {
            return this@BaseRVFragment.getItemLayout()
        }

        override fun bindFooterHolder(
            holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
            position: Int
        ) {
            this@BaseRVFragment.bindFooterHolder(holder, position)
        }

        override fun bindDataHolder(
            holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
            position: Int,
            data: D
        ) {
            this@BaseRVFragment.bindDataHolder(holder, position, data)
        }
    }
}
