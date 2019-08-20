package com.mredrock.cyxbs.freshman.util.decoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import org.jetbrains.anko.dip

class SearchResultItemDecoration(
        private var mDividerWidth: Int,
        @ColorRes private val mDividerColor: Int
) : RecyclerView.ItemDecoration() {
    private lateinit var mDividerPaint: Paint

    init {
        initPaint()
    }

    private fun initPaint() {
        mDividerPaint = Paint()
        mDividerPaint.style = Paint.Style.FILL
        mDividerWidth = BaseApp.context.dip(mDividerWidth)
        mDividerPaint.color = BaseApp.context.resources.getColor(mDividerColor)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.childCount
        for (i in 0 until count) {
            val view = parent.getChildAt(i)
            c.drawRect(view.left.toFloat(), view.bottom.toFloat(), view.right.toFloat(),
                    view.bottom.toFloat() + mDividerWidth, mDividerPaint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.adapter?.itemCount ?: 0
        val position = parent.getChildAdapterPosition(view)
        if (position < count - 1) {
            outRect.set(0, 0, 0, mDividerWidth)
        }
    }
}