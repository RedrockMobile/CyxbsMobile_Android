package com.mredrock.cyxbs.qa.pages.square.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.CircleSquare
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_circle_square.view.*

/**
 *@Date 2020-11-19
 *@Time 19:22
 *@author SpreadWater
 *@description
 */
class CircleSquareAdapter() :BaseRvAdapter<CircleSquare>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CircleSquare>  = CircleSquareViewHolder(parent)
    class CircleSquareViewHolder(parent:ViewGroup):BaseViewHolder<CircleSquare>(parent, R.layout.qa_recycler_item_circle_square){
        override fun refresh(data: CircleSquare?) {
            itemView.tv_circle_square_name.text=data?.circle_name
            itemView.tv_circle_square_descriprion.text=data?.circle_square_descriprion
            itemView.tv_circle_square_person_number.text=data?.cirecle_person_number.toString()+"个成员"
            itemView.btn_circle_square_concern.text="+关注"
        }
    }
}