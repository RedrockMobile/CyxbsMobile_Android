package com.mredrock.cyxbs.mine.page.mine.adapter

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.ui.activity.IdentityActivity
import com.mredrock.cyxbs.mine.page.mine.widget.SlideLayout
import java.math.MathContext

class IdentityAdapter(val list:List<String>,val context: Context) : RecyclerView.Adapter<IdentityAdapter.VH>(),View.OnClickListener {

    private var slideLayout: SlideLayout? = null


    class VH(itemView: View):RecyclerView.ViewHolder(itemView){
        val contentView = itemView.findViewById<View>(R.id.cl_content_view)
        val menuView = itemView.findViewById<View>(R.id.rl_menu_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val convertView = LayoutInflater.from(context).inflate(R.layout.mine_slide_identity_item, parent, false) as SlideLayout
        convertView.setOnStateChangeListenter(MyOnStateChangeListenter())
            val vh = VH(convertView)
            vh.menuView.setOnClickListener(this)
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

    }

    override fun getItemCount()=list.size

    inner class MyOnStateChangeListenter : SlideLayout.OnStateChangeListenter {
        override fun  onClose(layout: SlideLayout?) {
            if (slideLayout === layout) {
                slideLayout = null
            }
        }

        override fun onDown(layout: SlideLayout?) {
          Log.e("wxtag","(IdentityAdapter.kt:46)->>onDown回调了嘛 ")
            if (slideLayout != null && slideLayout !== layout) {
                slideLayout?.closeMenu()
            }
        }

       override fun onOpen(layout: SlideLayout?) {
            slideLayout = layout
        }
    }

    override fun onClick(v: View) {
        val intent = Intent(context,IdentityActivity::class.java)
        context.startActivity(intent)
    }








}