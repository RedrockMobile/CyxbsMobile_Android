package com.mredrock.cyxbs.mine.page.mine.adapter

import android.animation.Animator
import android.animation.ValueAnimator

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.mine.R

import com.mredrock.cyxbs.mine.page.mine.ui.activity.IdentityActivity
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.util.ColorUntil
import org.w3c.dom.Text
import java.util.ArrayList


class StatusAdapter(
    var list: MutableList<AuthenticationStatus.Data>,
    val context: Context?,
    val redid: String
) :
    RecyclerView.Adapter<StatusAdapter.VH>(), View.OnTouchListener, View.OnLongClickListener {

    val activity = context as IdentityActivity

    /**
     * 那个被拖动的item  复制品
     */
    var item: View? = null

    /**
     * 初始的item的位置
     */
    var starty = 0f

    /**
     * 是否时长按
     */
    var isLongClick = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusAdapter.VH {
        val convertView =
            LayoutInflater.from(context).inflate(R.layout.mine_recycler_item_statu, parent, false)
        convertView.setOnTouchListener(this)
        convertView.setOnLongClickListener(this)
        val vh = VH(convertView)

        initTypeface(vh)
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.view.tag = list[position].id
        loadBitmap(list[position].background) {
            holder.contentView.background = BitmapDrawable(context?.resources, it)
        }
        holder.positionView.text = list[position].position
        holder.nameView.text = list[position].form
        holder.timeView.text = list[position].date
        if (holder.view.alpha !== 1f) {
            holder.view.alpha = 1f
        }
    }

    override fun getItemCount() = list.size
    class VH(val view: View) : RecyclerView.ViewHolder(view) {
        val contentView = view.findViewById<ConstraintLayout>(R.id.cl_content_view)
        val nameView = view.findViewById<TextView>(R.id.tv_item_identity_name)
        val positionView = view.findViewById<TextView>(R.id.tv_item_identity)
        val timeView = view.findViewById<TextView>(R.id.tv_item_identity_time)
    }

    var distance = 0f
    var rawY = 0f

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        animatorBack?.cancel()
        animatorBack?.cancel()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                distance = event.y
                rawY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {

                if (isLongClick) {

                    val centerPoint =
                        activity.dataBinding.mineRelativelayout.top + activity.dataBinding.mineRelativelayout.height / 2
                    loadAnimator((event.rawY - centerPoint) / (rawY - centerPoint))
                    item?.y = event.rawY - distance
                }
            }
            MotionEvent.ACTION_UP -> {
                if (event.rawY >= 800) {  //设置身份失败的动画
                    upAnimatorback(v, event.rawY - distance)
                } else {  //设置身份成功的动画
                    upAnmatiorSet(v, event.rawY - distance)
                }
                activity.dataBinding.mineRelativelayout.background = ResourcesCompat.getDrawable(
                    activity.resources, R.drawable.mine_ic_rl_background, null
                )
                isLongClick = false
            }
            MotionEvent.ACTION_CANCEL -> {
                isLongClick = false
            }
        }
        return false
    }

    /**
     * 被长按的view
     */
    var longView: View? = null
    override fun onLongClick(v: View?): Boolean {
        isLongClick = true
        longView = v
        v!!.parent.requestDisallowInterceptTouchEvent(true)
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        val x = location[0].toFloat() // view距离 屏幕左边的距离（即x轴方向）
        starty = location[1].toFloat() // view距离 屏幕顶边的距离（即y轴方向）
        copyItem(v.height, v.width, x, starty,v)
        item?.background = v.background
        v.alpha = 0f
        activity.dataBinding.mineRelativelayout.background = ResourcesCompat.getDrawable(
            activity.resources, R.drawable.mine_ic_iv_statu_background, null
        )
        return true
    }


    fun copyItem(height: Int, width: Int, x: Float, y: Float, v:View) {
        item =
            LayoutInflater.from(activity).inflate(R.layout.mine_recycler_item_statu, null) as View

        initCopyItem( item,v)
        item?.y = y
        item?.x = x
        val layoutParams = LinearLayout.LayoutParams(width, height)
        activity.dataBinding.llStatu.addView(item, layoutParams)
    }

    fun initCopyItem( item: View?,v:View) {
        item?.findViewById<TextView>(R.id.tv_item_identity_name)?.apply {
            text = v.findViewById<TextView>(R.id.tv_item_identity_name).text
            typeface = Typeface.createFromAsset(context.assets, "YouSheBiaoTiHei-2.ttf")
        }
        item?.findViewById<TextView>(R.id.tv_item_identity_time)?.apply {
            text = v.findViewById<TextView>(R.id.tv_item_identity_time).text
            typeface = Typeface.createFromAsset(context.assets, "YouSheBiaoTiHei-2.ttf")
        }
        item?.findViewById<TextView>(R.id.tv_item_identity)?.apply {
            text = v.findViewById<TextView>(R.id.tv_item_identity).text
            typeface = Typeface.createFromAsset(context.assets, "YouSheBiaoTiHei-2.ttf")
        }
    }

    var animatorBack: ValueAnimator? = null

    /**
     * 身份设置不成功的动画
     */
    fun upAnimatorback(v: View, currentY: Float) {
        animatorBack = ValueAnimator.ofFloat(currentY, starty) as ValueAnimator
        animatorBack?.duration = 800
        animatorBack?.addUpdateListener {
            item?.y = it.animatedValue as Float
            activity.dataBinding.clContentView.scaleX = (it.animatedValue as Float / starty)
            activity.dataBinding.clContentView.scaleY = (it.animatedValue as Float / starty)
            activity.dataBinding.clContentView.alpha = (it.animatedValue as Float / starty)
        }
        animatorBack?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                v.alpha = 1f
                val activity = context as IdentityActivity
                activity.dataBinding.llStatu.removeView(item)
                item = null
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        animatorBack?.start()
    }

    var animatorSuccess: ValueAnimator? = null

    /**
     * 身份设置成功的动画
     */
    fun upAnmatiorSet(v: View, currentY: Float) {
        val activity = context as IdentityActivity
        animatorSuccess = ValueAnimator.ofFloat(
            currentY,
            activity.dataBinding.mineRelativelayout.top.toFloat()
        ) as ValueAnimator
        animatorSuccess?.duration = 800
        animatorSuccess?.addUpdateListener {
            item?.y = it.animatedValue as Float
            activity.dataBinding.clContentView.scaleX = (it.animatedValue as Float / starty)
            activity.dataBinding.clContentView.scaleY = (it.animatedValue as Float / starty)
            activity.dataBinding.clContentView.alpha = (it.animatedValue as Float / starty)
            v.alpha = -(it.animatedValue as Float / starty) + 1f
        }
        animatorSuccess?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
               // (item as View).alpha=0.5f
            }

            override fun onAnimationEnd(animation: Animator) {
                (v.parent as ViewGroup).removeView(v)
                val activity = context
              //  activity.dataBinding.clContentView.alpha = 1f
                //activity.dataBinding.llStatu.removeView(item)
              //  item = null
                activity.dataBinding.clContentView.scaleX = 1f
                activity.dataBinding.clContentView.scaleY = 1f

                activity.dataBinding.clContentView.invisible()
                list.forEach {
                    if (it.id == v.tag.toString()){
                        activity.viewModel.updateStatus(it.id,it.type, redid)
                    }
                }
                activity.viewModel.isFinsh.observeForever {
                    if (activity.viewModel.isUpdata){

                        Log.i("身份设置","item.name="+ item!!.findViewById<TextView>(R.id.tv_item_identity).text+"it"+it)
                        Log.i("身份设置","cl"+activity.dataBinding.tvItemIdentity.text)
                        activity.dataBinding.llStatu.removeView(item)
                        activity.dataBinding.clContentView.visible()
                        activity.dataBinding.clContentView.alpha = 1f
                    }
                    //item = null
                }



            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        animatorSuccess?.start()

    }

    fun loadAnimator(porpation: Float) {


        activity.dataBinding.clContentView.scaleX = porpation
        activity.dataBinding.clContentView.scaleY = porpation
        activity.dataBinding.clContentView.alpha = porpation
    }


    /**
     * 加载网络请求的Bitmap图片出来
     */
    fun loadBitmap(url: String, success: (Bitmap) -> Unit) {
        if (context != null) {
            Glide.with(context) // context，可添加到参数中
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // 成功返回 Bitmap
                        success.invoke(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
    }


    fun initTypeface(vh: StatusAdapter.VH) {
        vh.nameView.setTypeface(Typeface.createFromAsset(context!!.assets, "YouSheBiaoTiHei-2.ttf"))
        vh.positionView.setTypeface(
            Typeface.createFromAsset(
                context.assets,
                "YouSheBiaoTiHei-2.ttf"
            )
        )
        vh.timeView.setTypeface(Typeface.createFromAsset(context.assets, "YouSheBiaoTiHei-2.ttf"))
    }
}