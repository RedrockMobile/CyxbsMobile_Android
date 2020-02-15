package com.mredrock.cyxbs.mine.util.ui

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

/**
 * Created by roger on 2019/11/28
 */
class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(Product.DIFF_CALLBACK) {

    var onExChangeClick: ((Product) -> Unit)? = null

    class ProductViewHolder(
            parent: ViewGroup
    ) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.mine_list_item_product, parent, false)
    ) {
        private val iv = itemView.findViewById<ImageView>(R.id.mine_sign_store_item_iv)
        private val title = itemView.findViewById<TextView>(R.id.mine_sign_store_item_tv_title)
        private val count = itemView.findViewById<TextView>(R.id.mine_sign_store_item_tv_count)
        private val integral = itemView.findViewById<TextView>(R.id.mine_sign_store_item_tv__integral)
        private val exchange = itemView.findViewById<Button>(R.id.mine_sign_store_item_btn_exchange)

        @SuppressLint("SetTextI18n")
        fun bind(product: Product, onExChangeClick: ((Product) -> Unit)?) {
            //只有随机高度
            val param = iv.layoutParams
            param.height = (Math.random() * 200 + 200).toInt()
            iv.layoutParams = param

            title.text = product.name
            count.text = "仅剩${product.count}"
            integral.text = product.integral
            iv.setImageFromUrl(product.src)
            exchange.setOnClickListener {
                onExChangeClick?.invoke(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onExChangeClick)

    }

}