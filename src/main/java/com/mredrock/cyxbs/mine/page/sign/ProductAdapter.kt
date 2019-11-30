package com.mredrock.cyxbs.mine.page.sign

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Product
import org.jetbrains.anko.find

/**
 * Created by roger on 2019/11/28
 */
class ProductAdapter() : ListAdapter<Product, ProductAdapter.ProductViewHolder>(Product.DIFF_CALLBACK) {
    init {
        setHasStableIds(true)
    }

    class ProductViewHolder(
            parent: ViewGroup
    ) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.mine_list_item_product, parent, false)
    ) {
        private val iv = itemView.find<ImageView>(R.id.mine_sign_store_item_iv)
        private val title = itemView.find<TextView>(R.id.mine_sign_store_item_tv_title)
        private val count = itemView.find<TextView>(R.id.mine_sign_store_item_tv_count)
        private val integral = itemView.find<TextView>(R.id.mine_sign_store_item_tv__integral)
        private val exchange = itemView.find<Button>(R.id.mine_sign_store_item_btn_exchange)

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            title.text = product.name
            count.text = "仅剩${product.count}"
            integral.text = product.integral.toString()
            iv.setImageFromUrl(product.src)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(parent).apply {

        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}