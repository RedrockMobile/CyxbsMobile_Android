package com.mredrock.cyxbs.mine.page.myproduct

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
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

    private val dialogDialogFragment: CommonDialogFragment by lazy {
        CommonDialogFragment().apply {
            initView(
                    containerRes = R.layout.mine_layout_dialog_my_product,
                    positiveString = "我知道了",
                    onPositiveClick = { dismiss() },
                    elseFunction = {
                        it.findViewById<TextView>(R.id.dialog_title).text = resources.getString(R.string.mine_myproduct);

                        if (type == UNCLAIMED) {
                            it.findViewById<TextView>(R.id.dialog_content).text = resources.getString(R.string.mine_myproduct_unclaimed)
                        } else {
                            it.findViewById<TextView>(R.id.dialog_content).text = resources.getString(R.string.mine_myproduct_claimed)
                        }
                    }
            )
        }
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
                when (it) {
                    RvFooter.State.ERROR -> {
                        getFooter().showLoadError()
                    }
                    RvFooter.State.NOMORE -> {
                        getFooter().showNoMore()
                    }
                    RvFooter.State.NOTHING -> {
                        getFooter().showNothing()
                    }
                }
            })
        } else {
            viewModel.loadMyProductClaimed()
            viewModel.claimedList.observe(this, Observer {
                setNewData(it)
            })
            viewModel.eventOnClaimed.observe(this, Observer {
                when (it) {
                    RvFooter.State.ERROR -> {
                        getFooter().showLoadError()
                    }
                    RvFooter.State.NOMORE -> {
                        getFooter().showNoMore()
                    }
                    RvFooter.State.NOTHING -> {
                        getFooter().showNothing()
                    }
                }
            })
        }
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_product
    }

    override fun bindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }


    override fun bindDataHolder(holder: RecyclerView.ViewHolder, position: Int, data: MyProduct) {
        holder.itemView.mine_myproduct_time.text = data.time.split(" ")[0].replace("-", ".")
        holder.itemView.mine_myproduct_title.text = data.name
        holder.itemView.mine_myproduct_integral.text = data.integral.toString()
        holder.itemView.mine_myproduct_iv.setImageFromUrl(data.photoSrc)
        holder.itemView.setOnClickListener {
            dialogDialogFragment.show(fragmentManager, "my_product")
        }

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