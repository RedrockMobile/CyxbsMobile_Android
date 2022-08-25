package com.mredrock.cyxbs.noclass.page.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.callback.ISlideMenuAction
import com.mredrock.cyxbs.noclass.callback.OnSlideChangedListener
import com.mredrock.cyxbs.noclass.page.ui.GroupDetailActivity
import com.mredrock.cyxbs.noclass.page.ui.GroupManagerActivity
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
    private val mList: List<NoclassGroup>,
    private val mOnDeleteItemsSelected : (MutableSet<Int>) -> Unit,
) : RecyclerView.Adapter<GroupManagerAdapter.VH>(){

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

    inner class VH (itemView : View) : RecyclerView.ViewHolder(itemView){

        val deleteIcon : ImageView= itemView.findViewById(R.id.iv_noclass_group_delete_icon)
        val groupName : TextView = itemView.findViewById(R.id.tv_noclass_group_name)
        val slideContainer : SlideMenuLayout = itemView.findViewById<SlideMenuLayout?>(R.id.slide_noclass_container).apply{

            setOnTapTouchListener {
                if (mGroupState === GroupManagerActivity.GroupState.NORMAL){
                    context.startActivity(Intent(context,GroupDetailActivity::class.java))
                }
            }

            setOnDownListener {
                this.isSelected = true
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
        holder.groupName.text = mList[position].name

        if (mSelectedList.contains(position)){
            holder.deleteIcon.alpha = if(mGroupState === GroupManagerActivity.GroupState.DELETE) 1f else 0f
            holder.deleteIcon.setImageResource(R.drawable.noclass_ic_delete_confirm)
        }else{
            holder.deleteIcon.alpha = if(mGroupState === GroupManagerActivity.GroupState.DELETE) 1f else 0f
            holder.deleteIcon.setImageResource(R.drawable.noclass_ic_delete_normal)
        }
        holder.slideContainer.isSelected = holder.layoutPosition == mSelPosition

        if (holder.layoutPosition != mCurScrollPosition){
            if(holder.slideContainer.isRightSlideOpen()){
                holder.slideContainer.closeRightSlide()
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun onStateChange(state : GroupManagerActivity.GroupState){
        mGroupState = state
        when(state){
            GroupManagerActivity.GroupState.NORMAL -> {
                for(itemView in mViews){
                    val deleteIcon : ImageView = itemView.value.deleteIcon
                    deleteIcon.alphaAnim(1f,0f).start()
                    val groupName : TextView = itemView.value.groupName
                    groupName.translateXLeftAnim(12f.dp2pxF).start()
                    val slideContainer : SlideMenuLayout = itemView.value.slideContainer
                    slideContainer.setSlideMode(ISlideMenuAction.SLIDE_MODE_RIGHT)
                }
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