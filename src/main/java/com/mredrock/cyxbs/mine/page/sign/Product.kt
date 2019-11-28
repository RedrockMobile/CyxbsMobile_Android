package com.mredrock.cyxbs.mine.page.sign

import androidx.recyclerview.widget.DiffUtil
import java.io.Serializable

/**
 * Created by roger on 2019/11/28
 */
data class Product(
        val name: String,
        val count: Int,
        val integral: Int,
        val src: String
) : Serializable {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

}