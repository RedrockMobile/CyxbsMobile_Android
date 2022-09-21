package com.mredrock.cyxbs.course.page.find.ui.find.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.bean.FindStuBean
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * 显示查找到的学生的 Adapter
 *
 * 由于包含了关联的人要显示不同的图标，所以采用 ListAdapter
 *
 * 虽然目前只会有一个关联人，看着有点大材小用，但要定向刷新某个 item，你会发现会比较麻烦，
 * 索性就直接上 ListAdapter 了，让他自己差分刷新
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/9 15:20
 */
class ShowStuResultRvAdapter :
  ListAdapter<ShowStuResultRvAdapter.FindStuWithLinkBean, ShowStuResultRvAdapter.ShowStuResultViewHolder>(
    object : DiffUtil.ItemCallback<FindStuWithLinkBean>() {
      override fun areItemsTheSame(
        oldItem: FindStuWithLinkBean,
        newItem: FindStuWithLinkBean
      ): Boolean = oldItem.bean.stuNum == newItem.bean.stuNum
      
      override fun areContentsTheSame(
        oldItem: FindStuWithLinkBean,
        newItem: FindStuWithLinkBean
      ): Boolean = oldItem == newItem
      
      override fun getChangePayload(
        oldItem: FindStuWithLinkBean,
        newItem: FindStuWithLinkBean
      ): Any = "" // 这里只要不返回 null，就可以避免 ViewHolder 的互换，提升性能
    }
  ) {
  
  /**
   * 点击整个 item 的回调
   */
  fun setOnItemClick(onItemClick: FindStuBean.(position: Int) -> Unit): ShowStuResultRvAdapter {
    mOnItemClick = onItemClick
    return this
  }
  
  /**
   * 点击没有关联人图标的回调
   */
  fun setOnLinkNoClick(onLinkNoClick: FindStuBean.(position: Int) -> Unit): ShowStuResultRvAdapter {
    mOnLinkNoClick = onLinkNoClick
    return this
  }
  
  /**
   * 点击已关联人的回调
   */
  fun setOnLinkIngClick(onLinkIngClick: FindStuBean.(position: Int) -> Unit): ShowStuResultRvAdapter {
    mOnLinkIngClick = onLinkIngClick
    return this
  }
  
  private var mOnItemClick: (FindStuBean.(position: Int) -> Unit)? = null
  private var mOnLinkNoClick: (FindStuBean.(position: Int) -> Unit)? = null
  private var mOnLinkIngClick: (FindStuBean.(position: Int) -> Unit)? = null
  
  inner class ShowStuResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.course_tv_item_show_stu_result_name)
    val tvContent: TextView = itemView.findViewById(R.id.course_tv_item_show_stu_result_content)
    val tvNum: TextView = itemView.findViewById(R.id.course_tv_item_show_stu_result_num)
    // ivLink 不建议公开，因为可能你会在 onBindViewHolder 中设置图片，这样会有严重的性能问题
    // 因为每次都会重新生成一张新的图片，所以我在 holder 中提供方法来使用缓存的图片
    private val ivLink: ImageView = itemView.findViewById(R.id.course_iv_item_show_stu_result_link)
    init {
      itemView.setOnSingleClickListener {
        mOnItemClick?.invoke(getItem(layoutPosition).bean, layoutPosition)
      }
      ivLink.setOnSingleClickListener {
        if (getItem(layoutPosition).isLink) {
          mOnLinkIngClick?.invoke(getItem(layoutPosition).bean, layoutPosition)
        } else {
          mOnLinkNoClick?.invoke(getItem(layoutPosition).bean, layoutPosition)
        }
      }
    }
    // 缓存的没有关联的图片，为什么要缓存，如果你每次都在 onBindViewHolder 去加载图片，会严重影响性能
    private val mLinkNoDrawable by lazyUnlock {
      AppCompatResources.getDrawable(
        itemView.context,
        R.drawable.course_ic_find_course_link_head_no
      )
    }
    // 缓存的已关联的图片
    private val mLinkingDrawable by lazyUnlock {
      AppCompatResources.getDrawable(
        itemView.context,
        R.drawable.course_ic_find_course_link_head_ing
      )
    }
    // 显示关联的图片
    fun showLinkingImg() {
      if (ivLink.drawable != mLinkingDrawable) {
        ivLink.setImageDrawable(mLinkingDrawable)
      }
    }
    // 显示未关联的图片
    fun showLinkNoImg() {
      if (ivLink.drawable != mLinkNoDrawable) {
        ivLink.setImageDrawable(mLinkNoDrawable)
      }
    }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowStuResultViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.course_rv_item_show_stu_result, parent, false)
    return ShowStuResultViewHolder(view)
  }
  
  override fun onBindViewHolder(holder: ShowStuResultViewHolder, position: Int) {
    holder.tvName.text = getItem(position).bean.name
    holder.tvContent.text = getItem(position).bean.major
    holder.tvNum.text = getItem(position).bean.stuNum
    if (getItem(position).isLink) {
      holder.showLinkingImg()
    } else {
      holder.showLinkNoImg()
    }
  }
  
  /**
   * 由于查找学生这个是旧接口，关联功能是新加的，所以为了减少后端的工作，关联的数据就我们自己加上吧
   */
  data class FindStuWithLinkBean(
    val bean: FindStuBean,
    val isLink: Boolean
  )
}