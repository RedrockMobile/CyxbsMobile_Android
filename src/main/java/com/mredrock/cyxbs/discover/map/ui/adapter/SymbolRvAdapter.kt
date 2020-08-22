package com.mredrock.cyxbs.discover.map.ui.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.InfoItem
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_recycle_item_symbol_places.view.*

class SymbolRvAdapter(val context: Context, val viewModel: MapViewModel, private val mList: MutableList<InfoItem>,val lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<SymbolRvAdapter.ViewHolder>() {
    var curSelectorItem: AppCompatCheckedTextView? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hot: ImageView = view.map_iv_recycle_item_hot
        val symbol: AppCompatCheckedTextView = view.map_tv_recycle_item_symbol
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.map_recycle_item_symbol_places, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //curSelectorItem = holder.symbol
        if (mList[position].isHot) {
            holder.hot.visible()
        } else {
            holder.hot.invisible()
        }
        holder.symbol.text = mList[position].title
        holder.symbol.setOnClickListener { v ->
            viewModel.unCheck.value = false
            /**
             * 二次点击取消选择
             */
            if (v == curSelectorItem) {
                if (curSelectorItem?.isChecked == true) {
                    curSelectorItem?.setTextColor(ContextCompat.getColor(context, R.color.map_text_symbol))
                    val animator2 = ValueAnimator.ofFloat(1f, 0.8f, 1.2f, 1f)
                    animator2.duration = 500
                    animator2.addUpdateListener {
                        val currentValue: Float = it.animatedValue as Float
                        curSelectorItem?.scaleX = currentValue
                        curSelectorItem?.scaleY = currentValue
                        if (currentValue > 1f) {
                            curSelectorItem?.isChecked = false
                        }
                    }
                    animator2.start()
                    viewModel.isClickSymbol.value = false
                    //清除所有标签
                    viewModel.showSomePlaceIconById.value = mutableListOf()
                    return@setOnClickListener
                }
            }
            /**
             * 选择动画
             */
            val lastSelect = curSelectorItem
            curSelectorItem = v as AppCompatCheckedTextView
            lastSelect?.setTextColor(ContextCompat.getColor(context, R.color.map_text_symbol))
            curSelectorItem?.setTextColor(ContextCompat.getColor(context, R.color.map_text_symbol_select))
            val animator = ValueAnimator.ofFloat(1f, 1.2f, 0.8f, 1f)
            animator.duration = 500
            animator.addUpdateListener {
                val currentValue: Float = it.animatedValue as Float
                curSelectorItem?.scaleX = currentValue
                curSelectorItem?.scaleY = currentValue
                if (currentValue < 1f) {
                    lastSelect?.isChecked = false
                    curSelectorItem?.isChecked = true

                }
            }
            animator.start()
            //显示标签到地图上
            viewModel.showSomePlaceIconById.value = mList[position].placeIdList.toMutableList()
            viewModel.bottomSheetStatus.value = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.isClickSymbol.value = true
        }

        viewModel.unCheck.observe(lifecycleOwner, Observer {
            if (viewModel.unCheck.value == true) {
                curSelectorItem?.isChecked = false
                viewModel.unCheck.value = false
            }
        })
    }

    fun setList(list: List<InfoItem>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

}