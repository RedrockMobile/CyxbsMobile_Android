package com.mredrock.cyxbs.freshman.view.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.Image
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.BusRoute
import com.mredrock.cyxbs.freshman.bean.CampusAddress
import com.mredrock.cyxbs.freshman.bean.GroupData
import com.mredrock.cyxbs.freshman.bean.Route
import kotlinx.android.synthetic.main.freshman_recycle_item_bus_common.view.*
import org.jetbrains.anko.find
import java.lang.StringBuilder

/**
 * Create by roger
 * on 2019/8/7
 */
class BusRecyclerAdapter(val list: List<Route>, val address: CampusAddress, val ctx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_HEADER = 0
    private val ITEM_TWO_CHILD = 1
    private val ITEM_COMMON = 2
    private var mExpandedPosition = -1
    private var mPreviousExpandedPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_HEADER -> HeaderViewHolder.from(parent)
            ITEM_COMMON -> CommonViewHolder.from(parent)
            ITEM_TWO_CHILD -> TwoChildViewHolder.from(parent)
            else -> throw ClassCastException("unknown type of viewholder")
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listPos = position - 1
        when (holder) {
            is CommonViewHolder -> {
                //字符串分割
                val str = list[listPos].routes[0]
                val arr = str.split("→")
                val stringBuilder = StringBuilder()
                for (i in arr.withIndex()) {
                    if (i.index == (arr.size - 1)) {
                        stringBuilder.append("<font color='#b573ff'>").append(i.value).append("</font>")
                    } else if (i.index == 0) {
                        stringBuilder.append("<font color='#5b69ff'>").append(i.value).append("</font>")

                    } else {
                        stringBuilder.append(i.value)
                    }
                    if (i.index != arr.size - 1) {
                        stringBuilder.append("→")
                    }
                }
                holder.titleView.text = list[listPos].name
                holder.discriptView.text = HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)


                val isExpanded = position == mExpandedPosition
                holder.discriptView.visibility = if (isExpanded) View.VISIBLE else View.GONE
                if (isExpanded) mPreviousExpandedPosition = position
                if (isExpanded) {
                    holder.imageViewArrow.setImageResource(R.drawable.freshman_route_arrow_up)
                } else {
                    holder.imageViewArrow.setImageResource(R.drawable.freshman_route_arrow_down)
                }
                holder.itemView.setOnClickListener {
                    mExpandedPosition = if (isExpanded) -1 else position

                    notifyItemChanged(mPreviousExpandedPosition)
                    notifyItemChanged(position)
                }
            }
            is TwoChildViewHolder -> {
                //字体变色
                for (i in 1..2) {
                    val str = list[listPos].routes[i - 1]
                    val arr = str.split("→")
                    val stringBuilder = StringBuilder()
                    for (i in arr.withIndex()) {
                        if (i.index == (arr.size - 1)) {
                            stringBuilder.append("<font color='#b573ff'>").append(i.value).append("</font>")
                        } else if (i.index == 0) {
                            stringBuilder.append("<font color='#5b69ff'>").append(i.value).append("</font>")

                        } else {
                            stringBuilder.append(i.value)
                        }
                        if (i.index != arr.size - 1) {
                            stringBuilder.append("→")
                        }
                    }
                    if (i == 1) {
                        holder.discriptView1.text = HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        holder.discriptView2.text = HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)

                    }
                }





                holder.titleView.text = list[listPos].name


                val isExpanded = position == mExpandedPosition
                holder.rv.visibility = if (isExpanded) View.VISIBLE else View.GONE
                if (isExpanded) {
                    holder.imageViewArrow.setImageResource(R.drawable.freshman_route_arrow_up)
                } else {
                    holder.imageViewArrow.setImageResource(R.drawable.freshman_route_arrow_down)
                }
                if (isExpanded) mPreviousExpandedPosition = position
                holder.itemView.setOnClickListener {
                    mExpandedPosition = if (isExpanded) -1 else position
                    notifyItemChanged(mPreviousExpandedPosition)
                    notifyItemChanged(position)
                }
            }
            is HeaderViewHolder -> {
                holder.titleView.text = StringBuilder().append(address.title).append(":")
                holder.discriptView.text = address.message
                holder.copy.setOnClickListener {
                    Toast.makeText(ctx, "已复制", Toast.LENGTH_SHORT).show()
                    val clip: ClipboardManager = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData: ClipData = ClipData.newPlainText("address", holder.discriptView.text)

                    clip.primaryClip = clipData
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ITEM_HEADER
        }
        if (list[position - 1].routes.size > 1) {
            return ITEM_TWO_CHILD
        } else {
            return ITEM_COMMON
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.find(R.id.tv_school_name)
        val discriptView: TextView = view.find(R.id.tv_school_address)
        val copy: TextView = view.find(R.id.tv_copy)

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.freshman_recycle_item_bus_header, parent, false)
                return HeaderViewHolder(view)
            }
        }

    }

    class CommonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.find(R.id.tv_bus_title_common)
        val discriptView: TextView = view.find(R.id.tv_bus_content_common)
        val imageViewArrow: ImageView = view.find(R.id.iv_bus_arrow)

        companion object {
            fun from(parent: ViewGroup): CommonViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.freshman_recycle_item_bus_common, parent, false)
                return CommonViewHolder(view)
            }
        }

    }

    class TwoChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.find(R.id.tv_bus_title_two)
        val discriptView1: TextView = view.find(R.id.tv_bus_content_1_two)
        val discriptView2: TextView = view.find(R.id.tv_bus_content_2_two)
        val imageViewArrow: ImageView = view.find(R.id.iv_bus_arrow)
        val rv: RelativeLayout = view.find(R.id.rv_two_child_content)

        companion object {
            fun from(parent: ViewGroup): TwoChildViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.freshman_recycle_item_bus_two_child, parent, false)
                return TwoChildViewHolder(view)
            }
        }

    }

}