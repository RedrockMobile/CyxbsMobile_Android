package com.mredrock.cyxbs.mine.page.myproduct

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.MyProduct
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_product.view.*
import com.mredrock.cyxbs.common.utils.extensions.*


/**
 * Created by roger on 2020/2/15
 */
class MyProductFragment(private val type: Int = UNCLAIMED) : BaseRVFragment<MyProduct>() {
    companion object {
        const val CLAIMED = 1
        const val UNCLAIMED = 2
    }

    private val viewModel by lazy { ViewModelProvider(this).get(MyProductViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (type == UNCLAIMED) {
            viewModel.loadMyProductUnclaimed()
            viewModel.unclaimedList.observe(viewLifecycleOwner, Observer {
                setNewData(it)
            })
            viewModel.eventOnUnClaimed.observe(viewLifecycleOwner, Observer {
                setState(it)
            })
        } else {
            viewModel.loadMyProductClaimed()
            viewModel.claimedList.observe(viewLifecycleOwner, Observer {
                setNewData(it)
            })
            viewModel.eventOnClaimed.observe(viewLifecycleOwner, Observer {
                setState(it)
            })
        }
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_product
    }

    override fun bindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }


    override fun bindDataHolder(holder: RecyclerView.ViewHolder, position: Int, data: MyProduct) {
        holder.itemView.mine_my_product_time.text = data.time.split(" ")[0].replace("-", ".")
        holder.itemView.mine_my_product_title.text = if (data.name.isNotEmpty()) data.name else "--"
        holder.itemView.mine_my_product_integral.text = if (data.integral.toString().isNotEmpty()) data.integral.toString() else "0"
        holder.itemView.mine_my_product_iv.setImageFromUrl(data.photoSrc)
        holder.itemView.setOnSingleClickListener {
            showDialog()
        }

    }

    override fun onSwipeLayoutRefresh() {
        setState(RvFooter.State.LOADING)
        if (type == UNCLAIMED) {
            viewModel.cleanUnclaimedPage()
            viewModel.loadMyProductUnclaimed()
        } else {
            viewModel.cleanClaimedPage()
            viewModel.loadMyProductClaimed()
        }
        getSwipeLayout().isRefreshing = false
    }


    private fun showDialog() {
        val tag = "notice"
        activity?.supportFragmentManager?.let { f ->
            if (f.findFragmentByTag(tag) == null) {
                CommonDialogFragment().apply {
                    initView(
                            containerRes = R.layout.mine_layout_dialog_with_title_and_content,
                            positiveString = "我知道了",
                            onPositiveClick = { dismiss() },
                            elseFunction = {
                                it.findViewById<TextView>(R.id.dialog_title).text = resources.getString(R.string.mine_my_product);

                                if (type == UNCLAIMED) {
                                    it.findViewById<TextView>(R.id.dialog_content).text = resources.getString(R.string.mine_my_product_unclaimed)
                                } else {
                                    it.findViewById<TextView>(R.id.dialog_content).text = resources.getString(R.string.mine_my_product_claimed)
                                }
                            }
                    )
                }.show(f, tag)
            }
        }
    }
}