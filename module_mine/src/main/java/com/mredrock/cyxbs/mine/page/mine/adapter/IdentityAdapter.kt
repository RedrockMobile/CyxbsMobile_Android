package com.mredrock.cyxbs.mine.page.mine.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.page.mine.ui.activity.IdentityActivity
import com.mredrock.cyxbs.mine.page.mine.widget.SlideLayout
import com.mredrock.cyxbs.mine.util.widget.loadBitmap
import android.text.Spanned

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.STORE_ENTRY
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.page.mine.widget.MineDialog
import java.util.regex.Pattern


class IdentityAdapter(val list:List<AuthenticationStatus.Data>, val context: Context,val redid:String?,val isSelf:Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),View.OnClickListener {

    private var slideLayout: SlideLayout? = null



    class VH(itemView: View):RecyclerView.ViewHolder(itemView){
        val contentView = itemView.findViewById<View>(R.id.cl_content_view)
        val menuViewdelete = itemView.findViewById<View>(R.id.rl_menu_view)
        val menuViewSetting  = itemView.findViewById<View>(R.id.rl_menu_setting)
        val statuNameView  = itemView.findViewById<TextView>(R.id.tv_item_identity_name)
        val statuView = itemView.findViewById<TextView>(R.id.tv_item_identity)
        val timeView = itemView.findViewById<TextView>(R.id.tv_item_identity_time)
    }

    class noDataVH(itemView: View):RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vh:RecyclerView.ViewHolder?=null
        if (list.isNotEmpty()){
            //用户自己查看自己的情况
            val convertView = LayoutInflater.from(context).inflate(R.layout.mine_slide_identity_item, parent, false) as SlideLayout
            convertView.setOnStateChangeListenter(MyOnStateChangeListenter())
           vh = VH(convertView)
            vh.menuViewdelete.setOnClickListener{
                if(isSelf){
                    MineDialog.show(context,"确定要删除这个身份吗",{},{
                        val activity = context as HomepageActivity
                        activity.viewModel.deleteStatus(list[(vh as VH).layoutPosition].id)
                    })

                }else{
                    context.toast("不可以对别人的身份动手动脚哦！")
                }
            }
            vh.menuViewSetting.setOnClickListener(this)
            initTypeface(vh)
        }else if (!isSelf){
            //用户访问别人的情况
            val convertView = LayoutInflater.from(context).inflate(R.layout.mine_default_identity_item, parent, false)
           convertView.findViewById<TextView>(R.id.mine_textview4).text="没有身份就是他的身份..."
            vh = noDataVH(convertView)
      }else{
          //错误情况
            val convertView = LayoutInflater.from(context).inflate(R.layout.mine_default_identity_item, parent, false)
            val mine_textview4:TextView = convertView.findViewById(R.id.mine_textview4)
            initspannableString(mine_textview4)
            vh = noDataVH(convertView)
        }
        return vh!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IdentityAdapter.VH){
            holder.timeView.text  = list[position].date
            holder.statuNameView.text = list[position].form
            holder.statuView.text = list[position].position
            loadBitmap(holder.itemView.context, list[position].background){
                holder.contentView.background =  BitmapDrawable(context.resources,it)
            }
        }
    }

    override fun getItemCount():Int{

        return if (list.isEmpty()){
            1
        }else{
            list.size
        }
    }

    inner class MyOnStateChangeListenter : SlideLayout.OnStateChangeListenter {
        override fun  onClose(layout: SlideLayout?) {
            if (slideLayout === layout) {
                slideLayout = null
            }
        }

        override fun onDown(layout: SlideLayout?) {
            if (slideLayout != null && slideLayout !== layout) {
                slideLayout?.closeMenu()
            }
        }

       override fun onOpen(layout: SlideLayout?) {
            slideLayout = layout
        }
    }

    override fun onClick(v: View) {
        if(isSelf){
            val intent = Intent(context,IdentityActivity::class.java)
            intent.putExtra("redid",redid)
            context.startActivity(intent)
        }else{
            context.toast("不可以对别人的身份动手动脚哦！")
        }

    }



            fun initTypeface(vh:VH){
                vh.statuNameView.setTypeface(Typeface.createFromAsset(context.assets,"YouSheBiaoTiHei-2.ttf"))
                vh.statuView.setTypeface(Typeface.createFromAsset(context.assets,"YouSheBiaoTiHei-2.ttf"))
                vh.timeView.setTypeface(Typeface.createFromAsset(context.assets,"YouSheBiaoTiHei-2.ttf"))
            }


    fun initspannableString(v:TextView){
        val spannableString = SpannableString(context.resources.getString(R.string.mine_lack_text))
        val p = Pattern.compile("邮票中心")
        v.movementMethod = LinkMovementMethod.getInstance();
        val m= p.matcher(spannableString)
        while (m.find()) {
            val start = m.start()
            val end = m.end()
            spannableString.setSpan(object :ClickableSpan(){
                override fun onClick(widget: View) {
                        ARouter.getInstance().build(STORE_ENTRY).navigation()
                }
            },start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        v.text = spannableString

    }
}