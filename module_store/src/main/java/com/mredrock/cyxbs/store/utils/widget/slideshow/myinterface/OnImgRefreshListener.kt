package com.mredrock.cyxbs.store.utils.widget.slideshow.myinterface

import com.google.android.material.imageview.ShapeableImageView
import com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.adapter.BaseViewAdapter

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/29
 */
interface OnImgRefreshListener {
    fun onRefresh(imageView: ShapeableImageView, holder: BaseViewAdapter<ShapeableImageView>.BaseViewHolder, position: Int)
}