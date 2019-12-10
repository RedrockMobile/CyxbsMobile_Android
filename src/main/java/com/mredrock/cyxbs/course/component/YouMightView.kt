package com.mredrock.cyxbs.course.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.mredrock.cyxbs.course.R
import org.jetbrains.anko.dip
import java.text.FieldPosition

class YouMightView : FrameLayout {
    var adapter: Adapter? = null
    set(value) {
        field = value
        adapter?.context = context
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    addItemView()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}



    private fun addItemView() {
        adapter?.let {adapter ->
            var column = 0
            var i = 0
            var itemWith: Int? = null
            var itemHeight: Int? = null

            var itemView = LayoutInflater.from(context).inflate(adapter.getItemId(), this,false)
            adapter.initItem(itemView, i)

            while (i < adapter.getItemCount()) {


                if (itemWith == null || itemHeight == null) {
                    itemView.measure(View.MeasureSpec.makeMeasureSpec(0,
                            MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,
                            MeasureSpec.UNSPECIFIED))
                    itemWith = itemView.measuredWidth
                    itemHeight = itemView.measuredHeight
                }

                val count = measuredWidth / itemWith
                for (j in 0 until count) {
                    if (i != 0) {
                        itemView = LayoutInflater.from(context).inflate(adapter.getItemId(), this,false)
                        adapter.initItem(itemView, i)
                    }
                    val layoutParams = itemView.layoutParams as FrameLayout.LayoutParams
                    layoutParams.topMargin = column* itemHeight
                    layoutParams.leftMargin = j* itemWith
                    addView(itemView)
                    if (++i >= adapter.getItemCount()){
                        break
                    }
                }
                column++
                if (++i >= adapter.getItemCount()){
                    break
                }
            }
        }
    }

    abstract class Adapter{
        lateinit var context: Context
        abstract fun getItemId():Int
        abstract fun getItemCount():Int
        abstract fun initItem(item: View, position: Int)
    }
}
