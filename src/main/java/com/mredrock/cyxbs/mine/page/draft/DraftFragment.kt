package com.mredrock.cyxbs.mine.page.draft

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_item_draft_rv.view.*

/**
 * Created by zia on 2018/9/10.
 */
class DraftFragment : BaseRVFragment<Draft>() {

    private var view: IDraftView? = null
    private var toRight = false
    private val translation by lazy { dip(55).toFloat() } //移动距离

    @kotlin.jvm.Volatile
    private var animateCount = 0

    override fun getItemLayout(): Int {
        return R.layout.mine_item_draft_rv
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (getFooter().state == RvFooter.State.ERROR
                || getFooter().state == RvFooter.State.NOMORE
                || getFooter().state == RvFooter.State.NOTHING) {
            return
        }
        view?.loadMore()
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: Draft) {
        //动画相关
        holder.setIsRecyclable(false)//无奈之举，等待大神优化
        val x = if (toRight) translation else 0f
        val scale = if (toRight) 1f else 0f
        if (animateCount > 0) {
            //放这里会提升性能，但是有bug
//            holder.setIsRecyclable(false)
            animate(holder.itemView, 0, toRight)
        }
        holder.itemView.mine_item_draft_contentContainer.translationX = x
        holder.itemView.mine_item_draft_deleteButton.scaleX = scale
        holder.itemView.mine_item_draft_deleteButton.scaleY = scale

        //绑定数据
        holder.itemView.mine_item_draft_content.text = data.contentDisplay
        holder.itemView.mine_item_draft_title.text = data.titleDisplay
        holder.itemView.mine_item_draft_time.text = data.timeDisplay

        if (data.isQuestion){
            holder.itemView.mine_item_draft_content.gone()
        }

        holder.itemView.mine_item_draft_deleteButton.setOnClickListener {
            view?.deleteDraft(data)
        }

        holder.itemView.mine_item_draft_contentContainer.setOnClickListener {
            view?.onClickItem(data, position)
        }
    }

    override fun onSwipeLayoutRefresh() {
        clearData()
        view?.initData()
        getSwipeLayout().isRefreshing = false
    }

    fun setIsEdit(isEdit: Boolean) {
        this.toRight = isEdit
        showAnim(isEdit)
        getSwipeLayout().isEnabled = !isEdit
    }

    fun removeItem(draft: Draft) {
        val index = getAdapter().dataList.indexOf(draft)
        getAdapter().dataList.remove(draft)
        getAdapter().notifyItemRemoved(index)
        getAdapter().notifyDataSetChanged()
        if (getAdapter().dataList.isEmpty()) {
            view?.showEmpty()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        view?.initData()
    }

    // 为了实时更新数据直接在onResume里面进行加载
    override fun onResume() {
        super.onResume()
        onSwipeLayoutRefresh()
    }

    //传递回调
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is IDraftView) {
            view = context
        } else {
            LogUtils.e("DraftFragment", "context must implements IDraftView")
        }
    }

    override fun onDetach() {
        super.onDetach()
        view = null
    }

    /*以下是动画相关代码*/

    fun showAnim(toRight: Boolean) {
        val lm = getRecyclerView().layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
        val visibleItemSize = lastVisibleItemPosition - firstVisibleItemPosition
        //从可见的item往下，分别移动
        for (i in 0..visibleItemSize) {
            val item = lm.getChildAt(i)
            if (item == null || item is RvFooter) {
                continue
            }

            val delay = if (i in 0..visibleItemSize)
                (i) * 100L
            else
                0

            animate(item, delay, toRight)
        }
    }

    private fun animate(item: View, delay: Long, right: Boolean) {
        val floatAnimation = if (right)
            ObjectAnimator.ofFloat(0f, 1f) else
            ObjectAnimator.ofFloat(1f, 0f)

        floatAnimation.interpolator = AccelerateDecelerateInterpolator()
        floatAnimation.duration = 300
        floatAnimation.addUpdateListener {
            val process = it.animatedValue as Float
            item.mine_item_draft_contentContainer.translationX = process * translation
            item.mine_item_draft_deleteButton.scaleX = process
            item.mine_item_draft_deleteButton.scaleY = process
        }
        floatAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                animateCount--
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                animateCount++
            }
        })
        floatAnimation.startDelay = delay
        floatAnimation.start()
    }

    private fun dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}