package com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.adapter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.utils.widget.slideshow.utils.SlideShowAttrs

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/7/16
 */
abstract class BaseViewAdapter<V: View>: RecyclerView.Adapter<BaseViewAdapter<V>.BaseViewHolder>() {

    protected lateinit var attrs: SlideShowAttrs

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated("禁止重写! ")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        parent.setBackgroundColor(attrs.backgroundColor)
        val holder = BaseViewHolder(
            ViewLayout(
                parent.context,
                getNewView(parent.context)
            )
        )
        create(holder)
        return holder
    }

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated(
        "禁止重写! ",
        ReplaceWith("onBindViewHolder(view, holder, position, payloads)"))
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        // nothing
        // 使用了带有 payloads 参数的 onBindViewHolder 方法
    }

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated(
        "禁止重写! ",
        ReplaceWith("onBindViewHolder(view, holder, position, payloads)"))
    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        onBindViewHolder(holder.view, holder, position, payloads)
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    internal fun initializeAttrs(attrs: SlideShowAttrs) {
        this.attrs = attrs
    }

    /**
     * 该方法用于在 onCreateViewHolder 调用时生成新的 view 对象
     */
    abstract fun getNewView(context: Context): V

    /**
     * 是指 ViewHolder 刚被创建时, 此时用于进行一些只需进行一次的操作, 如: 设置点击监听、设置用于 item 整个生命周期的对象
     */
    abstract fun create(holder: BaseViewHolder)
    abstract fun onBindViewHolder(view: V, holder: BaseViewHolder, position: Int, payloads: MutableList<Any>)

    inner class BaseViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val view = itemView.getChildAt(0) as V
    }

    inner class ViewLayout(context: Context, view: V) : FrameLayout(context) {
        init {
            val lp = LayoutParams(attrs.viewWidth, attrs.viewHeight)
            lp.gravity = Gravity.CENTER
            lp.leftMargin = attrs.viewMarginHorizontal
            lp.topMargin = attrs.viewMarginVertical
            lp.rightMargin = attrs.viewMarginHorizontal
            lp.bottomMargin = attrs.viewMarginVertical
            val lpFl = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutParams = lpFl
            attachViewToParent(view, -1, lp)
            setBackgroundColor(0x00000000)
        }
    }

    companion object {

        /**
         * 表示刷新 item
         */
        internal const val ITEM_REFRESH = 0
    }
}