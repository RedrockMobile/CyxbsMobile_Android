package com.mredrock.cyxbs.mine.page.myproduct

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.MyProduct
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_product.view.*

/**
 * Created by roger on 2020/2/15
 */
class MyProductFragment(private val type: Int = UNCLAIMED) : BaseRVFragment<MyProduct>() {
    companion object {
        const val CLAIMED = 1
        const val UNCLAIMED = 2
    }

    private val viewModel by lazy { ViewModelProviders.of(this).get(MyProductViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (type == UNCLAIMED) {
            viewModel.loadMyProductUnclaimed()
            viewModel.unclaimedList.observe(this, Observer {
                setNewData(it)
            })
            viewModel.eventOnUnClaimed.observe(this, Observer {
                if (it == RvFooter.State.ERROR) {
                    getFooter().showLoadError()
                } else if (it == RvFooter.State.NOMORE) {
                    getFooter().showNoMore()
                }
            })
        } else {
            viewModel.loadMyProductClaimed()
            viewModel.claimedList.observe(this, Observer {
                setNewData(it)
            })
            viewModel.eventOnClaimed.observe(this, Observer {
                if (it == RvFooter.State.ERROR) {
                    getFooter().showLoadError()
                } else if (it == RvFooter.State.NOMORE) {
                    getFooter().showNoMore()
                }
            })
        }
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_product
    }

    override fun bindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getFooter().state == RvFooter.State.LOADING) {
            if (type == UNCLAIMED) {
                viewModel.loadMyProductUnclaimed()
            } else {
                viewModel.loadMyProductClaimed()
            }
        }
    }


    override fun bindDataHolder(holder: RecyclerView.ViewHolder, position: Int, data: MyProduct) {
        holder.itemView.mine_myproduct_time.text = data.time.split(" ")[0].replace("-", ".")
        holder.itemView.mine_myproduct_title.text = data.name

    }

    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        if (type == UNCLAIMED) {
            viewModel.cleanUnclaimedPage()
            viewModel.loadMyProductUnclaimed()
        } else {
            viewModel.cleanClaimedPage()
            viewModel.loadMyProductClaimed()
        }
        getSwipeLayout().isRefreshing = false
    }
}