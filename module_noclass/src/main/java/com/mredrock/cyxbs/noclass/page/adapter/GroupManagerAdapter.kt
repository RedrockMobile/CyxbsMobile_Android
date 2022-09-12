package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.callback.ISlideMenuAction
import com.mredrock.cyxbs.noclass.callback.OnSlideChangedListener
import com.mredrock.cyxbs.noclass.page.ui.activity.GroupManagerActivity
import com.mredrock.cyxbs.noclass.util.alphaAnim
import com.mredrock.cyxbs.noclass.util.translateXLeftAnim
import com.mredrock.cyxbs.noclass.util.translateXRightAnim
import com.mredrock.cyxbs.noclass.widget.SlideMenuLayout

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.adapter
 * @ClassName:      GroupManagerAdapter
 * @Author:         Yan
 * @CreateDate:     2022年08月20日 23:39:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    分组管理界面的Adapter页面
 */


class GroupManagerAdapter(
    private val mOnDeleteItemsSelected : (MutableSet<Int>) -> Unit,
) : ListAdapter<NoclassGroup,GroupManagerAdapter.VH>(groupManageDiffUtil){

    companion object{
        private val groupManageDiffUtil : DiffUtil.ItemCallback<NoclassGroup> = object : DiffUtil.ItemCallback<NoclassGroup>(){
            override fun areItemsTheSame(oldItem: NoclassGroup, newItem: NoclassGroup): Boolean {
                return oldItem.id == newItem.name
            }

            override fun areContentsTheSame(oldItem: NoclassGroup, newItem: NoclassGroup): Boolean {
                return oldItem.isTop == newItem.isTop && oldItem.id == newItem.id && oldItem.name ==oldItem.name
            }

        }
    }

    /**
     * 记录所有VH
     */
    private val mViews : HashMap<Int,VH> = hashMapOf()

    /**
     * 分组管理的状态
     */
    private var mGroupState : GroupManagerActivity.GroupState = GroupManagerActivity.GroupState.NORMAL

    /**
     * 记录被点击的item
     */
    private var mSelectedList = mutableSetOf<Int>()

    /**
     * 记录最后一条被选中的item
     */
    private var mSelPosition = -1

    /**
     * 临时记录上一条item
     */
    private var mLastSelPosition = -1

    /**
     * 临时记录现在滑动的view
     */
    private var mCurScrollPosition = -1

    /**
     * 回调开启新activity
     */
    private var mOnGroupDetailStart : ((Int) -> Unit)? = null

    /**
     * 点击置顶/取消置顶的回调
     */
    private var mOnTopClick : ((Int) -> Unit)? = null

    /**
     * 点击删除的回调
     */
    private var mOnDeleteClick : ((Int) -> Unit)? = null

    inner class VH (itemView : View) : RecyclerView.ViewHolder(itemView){
        val topText : TextView = itemView.findViewById<TextView?>(R.id.tv_noclass_group_top_name).apply {
            setOnClickListener {
                mOnTopClick?.invoke(absoluteAdapterPosition)
                slideContainer.closeRightSlide()
            }
        }
        val deleteText : TextView = itemView.findViewById<TextView?>(R.id.tv_noclass_group_delete_item).apply {
            setOnClickListener {
                mOnDeleteClick?.invoke(absoluteAdapterPosition)
                slideContainer.closeRightSlide()
            }
        }
        val deleteIcon : ImageView = itemView.findViewById(R.id.iv_noclass_group_delete_icon)
        val groupName : TextView = itemView.findViewById(R.id.tv_noclass_group_name)
        val slideContainer : SlideMenuLayout = itemView.findViewById<SlideMenuLayout>(R.id.slide_noclass_container).apply{

            setOnTapTouchListener {
                if (mGroupState === GroupManagerActivity.GroupState.NORMAL){
                    mOnGroupDetailStart?.invoke(absoluteAdapterPosition)
                }
            }

            setOnDownListener {
                mLastSelPosition = mSelPosition
                mSelPosition = layoutPosition
                //改变点击状态
                notifyItemChanged(mLastSelPosition)
            }

            setOnClickListener {
                if (mGroupState === GroupManagerActivity.GroupState.DELETE){
                    if(mSelectedList.contains(absoluteAdapterPosition)){
                        mSelectedList.remove(absoluteAdapterPosition)
                        deleteIcon.setImageResource(R.drawable.noclass_ic_delete_normal)
                    }else{
                        mSelectedList.add(absoluteAdapterPosition)
                        deleteIcon.setImageResource(R.drawable.noclass_ic_delete_confirm)
                    }
                    mOnDeleteItemsSelected.invoke(mSelectedList)
                }
                notifyItemChanged(mLastSelPosition)
            }

            setOnSlideChangedListener(
                object : OnSlideChangedListener {
                    override fun onSlideStateChanged(
                        slideMenu: SlideMenuLayout,
                        isLeftSlideOpen: Boolean,
                        isRightSlideOpen: Boolean
                    ) {

                    }

                    override fun onSlideRightChanged(percent: Float) {
                        if (mCurScrollPosition != layoutPosition){
                            notifyItemChanged(mLastSelPosition)
                            mCurScrollPosition = layoutPosition
                            //改变滑动状态
                            notifyItemChanged(mLastSelPosition)
                        }
                    }

                    override fun onSlideLeftChanged(percent: Float) {
                    }
                }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_group_item,parent,false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        mViews[position] = holder
        //test:
        holder.groupName.text = currentList[position].name
        holder.topText.text = if (currentList[position].isTop) "取消置顶" else "置顶"
        holder.slideContainer.isSelected = currentList[position].isTop

        if (mSelectedList.contains(position)){
            holder.deleteIcon.alpha = if(mGroupState === GroupManagerActivity.GroupState.DELETE) 1f else 0f
            holder.deleteIcon.setImageResource(R.drawable.noclass_ic_delete_confirm)
        }else{
            holder.deleteIcon.alpha = if(mGroupState === GroupManagerActivity.GroupState.DELETE) 1f else 0f
            holder.deleteIcon.setImageResource(R.drawable.noclass_ic_delete_normal)
        }

        if (holder.layoutPosition != mCurScrollPosition){
            if(holder.slideContainer.isRightSlideOpen()){
                holder.slideContainer.closeRightSlide()
            }
        }
    }

    fun setOnGroupDetailStart(listener : (Int) -> Unit){
        mOnGroupDetailStart = listener
    }

    fun setOnTopClick(listener: (Int) -> Unit){
        mOnTopClick = listener
    }

    fun setOnDeleteClick(listener: (Int) -> Unit){
        mOnDeleteClick = listener
    }

    fun onStateChange(state : GroupManagerActivity.GroupState){
        mGroupState = state
        when(state){
            GroupManagerActivity.GroupState.NORMAL -> {
                for(itemView in mViews){
                    val deleteIcon : ImageView = itemView.value.deleteIcon
                    deleteIcon.alphaAnim(1f,0f).apply {
                        doOnEnd {
                            deleteIcon.setImageResource(R.drawable.noclass_ic_delete_normal)
                        }
                    }.start()
                    val groupName : TextView = itemView.value.groupName
                    groupName.translateXLeftAnim(12f.dp2pxF).start()
                    val slideContainer : SlideMenuLayout = itemView.value.slideContainer
                    slideContainer.setSlideMode(ISlideMenuAction.SLIDE_MODE_RIGHT)
                }
                mSelectedList.clear()
            }
            GroupManagerActivity.GroupState.DELETE -> {
                for(itemView in mViews){
                    val deleteIcon : ImageView= itemView.value.deleteIcon
                    deleteIcon.alphaAnim(0f,1f).start()
                    val groupName : TextView = itemView.value.groupName
                    groupName.translateXRightAnim(12f.dp2pxF).start()
                    val slideContainer : SlideMenuLayout = itemView.value.slideContainer
                    slideContainer.setSlideMode(ISlideMenuAction.SLIDE_MODE_NONE)
                }
            }
        }
    }
}