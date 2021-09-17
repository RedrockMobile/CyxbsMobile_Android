package com.mredrock.cyxbs.mine.page.mine.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.R
import java.math.MathContext

class IdentityAdapter(val list:List<String>,val context: Context) : RecyclerView.Adapter<IdentityAdapter.VH>() {




    class VH(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val convertView = LayoutInflater.from(context).inflate(R.layout.mine_slide_identity_item, parent, false);//View.inflate(context, R.layout.mine_slide_identity_item, parent)
        return VH(convertView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

    }

    override fun getItemCount()=list.size


}