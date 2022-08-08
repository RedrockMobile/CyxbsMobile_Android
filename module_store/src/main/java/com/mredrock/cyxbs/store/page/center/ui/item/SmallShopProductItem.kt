package com.mredrock.cyxbs.store.page.center.ui.item

import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemSmallShopProductBinding
import com.mredrock.cyxbs.store.page.exchange.ui.activity.ProductExchangeActivity
import com.mredrock.cyxbs.store.utils.SimpleRvAdapter

/**
 * 自己写了个用于解耦不同的 item 的 Adapter 的封装类, 详情请看 [SimpleRvAdapter]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/9
 */
class SmallShopProductItem(
    shopMap: HashMap<Int, StampCenter.Shop>,
    private var stampCount: Int
): SimpleRvAdapter.DBItem<StoreRecyclerItemSmallShopProductBinding, StampCenter.Shop>(
    shopMap, R.layout.store_recycler_item_small_shop_product
) {

    /**
     * 该方法调用了 [diffRefreshAllItemMap] 用于自动刷新
     *
     * 因为我在 Item 中整合了 DiffUtil 自动刷新, 只有你全部的 Item 都调用了 [diffRefreshAllItemMap],
     * 就会自动启动 DiffUtil
     */
    fun resetData(shopMap: HashMap<Int, StampCenter.Shop>, stampCount: Int) {
        this.stampCount = stampCount
        diffRefreshAllItemMap(shopMap,
            isSameName = { oldData, newData ->
                oldData.id == newData.id // 这个是判断新旧数据中 张三 是否是 张三 (可以点进去看注释)
            },
            isSameData = { oldData, newData ->
                oldData == newData // 这个是判断其他数据是否相等
            })
    }

    override fun onCreate(
        binding: StoreRecyclerItemSmallShopProductBinding,
        holder: SimpleRvAdapter.BindingVH,
        map: Map<Int, StampCenter.Shop>
    ) {
        //设置跳转到兑换界面
        binding.storeCvStampSmallShop.setOnSingleClickListener {
            val shop = map[holder.layoutPosition]
            if (shop != null) {
                ProductExchangeActivity.activityStart(it.context, shop.id, stampCount, shop.isPurchased)
            }
        }
    }

    override fun onRefactor(
        binding: StoreRecyclerItemSmallShopProductBinding,
        holder: SimpleRvAdapter.BindingVH,
        position: Int,
        value: StampCenter.Shop
    ) {
        binding.storeIvSmallShopProduct.setImageFromUrl(value.url)
        binding.shop = value // 设置 xml 中 binding 了的属性
    }
}