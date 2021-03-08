package com.mredrock.cyxbs.qa.pages.mine.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

/**
 * @date 2021-03-05
 * @author Sca RayleighZ
 */
class SimpleRvAdapter<T: Serializable>(
        private val viewHolderClass: Class<out RecyclerView.ViewHolder>,
        private val itemResId: Int,
        private val tList: ArrayList<T>,
        val onBind: (viewHolder: RecyclerView.ViewHolder,position: Int)->Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(itemResId, parent,false)
        val constructor = viewHolderClass.getConstructor(View::class.java)
        return constructor.newInstance(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind(holder, position)
    }

    override fun getItemCount(): Int {
        return tList.size
    }
}