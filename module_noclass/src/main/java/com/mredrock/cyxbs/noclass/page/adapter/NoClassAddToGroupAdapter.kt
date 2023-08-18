package com.mredrock.cyxbs.noclass.page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassGroup

class NoClassAddToGroupAdapter(
    val groupListByPage: HashMap<Int, ArrayList<NoClassGroup>>,
    val context: Context,
    val setDoneStatus: (isOk: Boolean, chooseGroup: List<NoClassGroup>) -> Unit
) : RecyclerView.Adapter<NoClassAddToGroupAdapter.MyHolder>() {

    /**
     * 选择分组缓冲区
     */
    private val chooseGroup by lazy { ArrayList<NoClassGroup>() }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val containerView = itemView.findViewById<GridLayout>(R.id.noclass_gl_container)
        // 在containerView中添加八个子视图，方便设置点击事件
        init {
            for (i in 0 until 8) {
                val child = groupNameView(containerView.context)
                child.invisible()
                child.setOnClickListener {
                    if (child.visibility == View.VISIBLE){
                        //判断是否选中，选中需要更改背景,同时加入缓冲区
                        val noClassGroup = groupListByPage[bindingAdapterPosition]?.get(i)!!
                        val linearLayout =
                            it.findViewById<LinearLayout>(R.id.noclass_ll_gathering_item_container)
                        if (chooseGroup.contains(noClassGroup)) {
                            //这是是取消选中
                            chooseGroup.remove(noClassGroup)
                            linearLayout.setBackgroundResource(R.drawable.noclass_shape_button_save_default_bg)
                        } else {
                            //这里是选中
                            chooseGroup.add(noClassGroup)
                            linearLayout.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
                        }
                        //判断分组缓冲区是否为空，如果为空，那么就回调传进来的点击事件
                        if (chooseGroup.isEmpty()) {
                            setDoneStatus(false, chooseGroup)
                        } else {
                            setDoneStatus(true, chooseGroup)
                        }
                    }
                }
                // 别忘了把这个子view添加进去
                containerView.addView(child)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.noclass_layout_gathering, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return groupListByPage.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val parentView = holder.containerView
        // 一页内的八条数据
        val onePageData = groupListByPage[position]!!
        for (index in 0 until onePageData.size){
            val child = parentView.getChildAt(index)
            child.visible()
            child.findViewById<TextView>(R.id.noclass_tv_gathering_name).apply {
                var ordinateText = onePageData[index].name
                val len = if (ordinateText.length <= 3) ordinateText.length else 3
                ordinateText = ordinateText.substring(0,len) + ".."
                text = ordinateText
            }
        }
    }

    /**
     * 一页中的一个view
     */
    private fun groupNameView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.noclass_item_gathering, null).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                setMargins(12, 14, 12, 14)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
        }
    }

}