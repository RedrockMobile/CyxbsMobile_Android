package com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import java.util.*

/**
 * 官方写的那个 CompositePageTransformer 不能操控添加进去的数组，所以就把代码复制过来了
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
class BaseMultipleTransformer : ViewPager2.PageTransformer {
    val mTransformers: MutableList<ViewPager2.PageTransformer> = ArrayList()

    /**
     * Adds a page transformer to the list.
     *
     *
     * Transformers will be executed in the order that they were added.
     */
    fun addTransformer(transformer: ViewPager2.PageTransformer) {
        mTransformers.add(transformer)
    }

    /** Removes a page transformer from the list.  */
    fun removeTransformer(transformer: ViewPager2.PageTransformer) {
        mTransformers.remove(transformer)
    }

    override fun transformPage(page: View, position: Float) {
        for (transformer in mTransformers) {
            transformer.transformPage(page, position)
        }
    }
}