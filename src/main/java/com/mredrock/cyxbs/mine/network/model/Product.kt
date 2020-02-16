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
        val integral: String,
        @SerializedName("photo_src")
        val src: String,
        @SerializedName("isVirtual")
        val isVirtual: Int
) : Serializable {
//          格式说明：
//              "name": "strwberry",    商品名称
//              "value": "5",           商品所需积分
//                "num": 9999,          商品剩余数
//               "photo_src": "",       图片
//               "isVirtual": 1         商品类型，0为实物，1为虚拟，主要是用于区分，以后可能会有皮肤类似商品


    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.count == newItem.count
            }
        }


    }

}