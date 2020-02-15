package com.mredrock.cyxbs.mine.util.ui

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
    //通过集合存储高度，防止错位
    private val mHeights: MutableList<Int> = mutableListOf()

    class ProductViewHolder(
            parent: ViewGroup
    ) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.mine_list_item_product, parent, false)
    ) {
        val iv = itemView.findViewById<ImageView>(R.id.mine_sign_store_item_iv)
        val title = itemView.findViewById<TextView>(R.id.mine_sign_store_item_tv_title)
        val count = itemView.findViewById<TextView>(R.id.mine_sign_store_item_tv_count)
        val integral = itemView.findViewById<TextView>(R.id.mine_sign_store_item_tv__integral)
        val exchange = itemView.findViewById<Button>(R.id.mine_sign_store_item_btn_exchange)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)


        val param = holder.iv.layoutParams
        if (mHeights.size - 1 >= position) {
            param.height = mHeights[position]
        } else {
            val height = (Math.random() * 200 + 225).toInt()
            mHeights.add(position, height)
            param.height = height
        }
        holder.iv.layoutParams = param

        holder.title.text = product.name
        holder.count.text = "仅剩${product.count}"
        holder.integral.text = product.integral
        holder.iv.setImageFromUrl(product.src)
        holder.exchange.setOnClickListener {
            onExChangeClick?.invoke(product)
        }

    }

}