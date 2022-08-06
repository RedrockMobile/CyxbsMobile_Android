package com.mredrock.cyxbs.store.page.center.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.page.center.ui.item.SmallShopProductItem
import com.mredrock.cyxbs.store.page.center.ui.item.SmallShopTitleItem
import com.mredrock.cyxbs.store.page.center.viewmodel.StoreCenterViewModel
import com.mredrock.cyxbs.store.utils.StoreType
import com.mredrock.cyxbs.store.utils.SimpleRvAdapter
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/14
 */
class StampShopFragment : BaseFragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mImageView: ImageView
    private lateinit var mTextView: TextView

    // 因为我只需要 Activity 的 ViewModel, 所以没有继承于 BaseViewModelFragment
    private val viewModel by activityViewModels<StoreCenterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.store_item_small_shop, container, false)
    }

    // 建立 ViewModel 的数据观察
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initObserve()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.store_rv_fragment_small_shop)
        mImageView = view.findViewById(R.id.store_iv_fragment_small_shop)
        mTextView = view.findViewById(R.id.store_tv_fragment_small_shop)
    }

    private fun initObserve() {
        viewModel.refreshIsSuccessful.observe {
            if (it) {
                // 取消断网图片的显示
                mImageView.invisible()
                mTextView.invisible()
                mRecyclerView.visible()
            } else {
                // 显示断网图片
                mImageView.visible()
                mTextView.visible()
                mRecyclerView.gone()
                mImageView.setImageResource(R.drawable.store_ic_no_internet)
                mTextView.text = getText(R.string.store_no_internet)
            }
        }
    
        viewModel.stampCenterData.observe {
            if (it.shop == null) {
                resetData(emptyList())
            } else {
                resetData(it.shop) // 重新设置数据
            }
            if (mRecyclerView.adapter == null) { // 第一次得到数据时设置 adapter
                setAdapter(it.userAmount)
            } else {
                refreshAdapter(it.userAmount) // 再次得到数据时刷新
            }
        }
    }

    private lateinit var mSmallShopTitleItem: SmallShopTitleItem
    private lateinit var mSmallShopProductItem: SmallShopProductItem
    private fun setAdapter(stampCount: Int) {
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = if (titleMap.containsKey(position)) 2 else 1
        }
        mRecyclerView.layoutAnimation = // 入场动画
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.store_product_fade_in
                )
            )
        mRecyclerView.layoutManager = layoutManager
        mSmallShopTitleItem = SmallShopTitleItem(titleMap)
        mSmallShopProductItem = SmallShopProductItem(shopMap, stampCount)
        mRecyclerView.adapter = SimpleRvAdapter() // 自己写的解耦 Adapter 的封装类, 可用于解耦不同的 item
            .addItem(mSmallShopTitleItem)
            .addItem(mSmallShopProductItem)
            .show()
    }

    /**
     * 差分刷新
     *
     * 用于再次得到数据后的刷新, 我在 Item 中整合了 DiffUtil 的自动刷新, 不用再使用 notifyDataSetChanged()
     */
    private fun refreshAdapter(stampCount: Int) {
        mSmallShopTitleItem.resetData(titleMap)
        mSmallShopProductItem.resetData(shopMap, stampCount)
    }

    private val dressList = ArrayList<StampCenter.Shop>()
    private val goodsList = ArrayList<StampCenter.Shop>()
    private val titleMap = HashMap<Int, Pair<String, String>>() // adapter 的 position 与标题的映射
    private val shopMap = HashMap<Int, StampCenter.Shop>() // adapter 的 position 与商品数据的映射
    private fun resetData(products: List<StampCenter.Shop>) {
        dressList.clear()
        goodsList.clear()
        titleMap.clear()
        shopMap.clear()
        // 为什么要遍历一边?
        // 因为后端不同 type 是混在一起的, 不遍历的话我就不知道 "邮货" 这个 title 是在哪个位置
        for (shop in products) {
            when (shop.type) { // 后端返回的 type = 1 时为装扮, type = 0 时为邮货
                StoreType.Product.DRESS -> dressList.add(shop)
                StoreType.Product.GOODS -> goodsList.add(shop)
            }
        }
        titleMap[0] = Pair("装扮", if (dressList.isEmpty()) "敬请期待" else "请在个人资料里查看")
        titleMap[dressList.size + 1] = Pair("邮货", if (goodsList.isEmpty()) "敬请期待" else "需要到红岩网校领取哦")
        for (i in dressList.indices) {
            shopMap[i + 1] = dressList[i]
        }
        for (i in goodsList.indices) {
            shopMap[dressList.size + 2 + i] = goodsList[i]
        }
    }
}