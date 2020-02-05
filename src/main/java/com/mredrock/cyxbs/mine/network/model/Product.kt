package com.mredrock.cyxbs.mine.network.model

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2019/11/28
 */
data class Product(
        @SerializedName("name")
        val name: String,
        @SerializedName("num")
        val count: Int,
        @SerializedName("value")
        val integral: Int,
        @SerializedName("photo_src")
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