package com.mredrock.cyxbs.mine.page.mine.adapter

import android.animation.Animator
import android.animation.ValueAnimator

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.mine.R

import com.mredrock.cyxbs.mine.page.mine.ui.activity.IdentityActivity
import android.widget.LinearLayout
import androidx.core.view.get
import java.util.ArrayList


class StatusAdapter(val list: MutableList<String>, val context: Context) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusAdapter.VH {
        val convertView =
            LayoutInflater.from(context).inflate(R.layout.mine_recycler_item_statu, parent, false)
        convertView.setOnTouchListener(this)
        convertView.setOnLongClickListener(this)
        val vh = VH(convertView)

        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
holder.view.setTag(position)
    }
    val sss = TextView(context)
    override fun getItemCount() = list.size
    class VH(val view: View) : RecyclerView.ViewHolder(view) {
        val v = view.findViewById<View>(R.id.ll_mine_statu_item)
    }

    var distance = 0f
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                distance = event.y
            }
            MotionEvent.ACTION_MOVE -> {

                Log.e(
                    "wxtagd",
                    "(StatusAdapter.kt:50)->>event.rawY${event.rawY} event.y${event.y}  item?.y${item?.y}event.rawY-s${event.rawY - distance}"
                )
                item?.y = event.rawY - distance

            }
            MotionEvent.ACTION_UP -> {
                if (event.rawY>=800){  //设置身份失败的动画
                    upAnimatorback(v,event.rawY - distance)
                }else{  //设置身份成功的动画
                    upAnmatiorSet(v,event.rawY - distance)
                }

            }
            MotionEvent.ACTION_CANCEL -> {

            }
        }



        return false
    }

    /**
     * 被长按的view
     */
    var longView:View?=null
    override fun onLongClick(v: View?): Boolean {
        longView = v
        v!!.parent.requestDisallowInterceptTouchEvent(true)
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        val x = location[0].toFloat() // view距离 屏幕左边的距离（即x轴方向）
        starty = location[1].toFloat() // view距离 屏幕顶边的距离（即y轴方向）
        copyItem(v.height, v.width, x, starty)
        v.alpha=0f

        return true
    }


    fun copyItem(height: Int, width: Int, x: Float, y: Float) {

        item =
            LayoutInflater.from(activity).inflate(R.layout.mine_recycler_item_statu, null) as View
        item?.y = y
        item?.x = x
        val layoutParams = LinearLayout.LayoutParams(width, height);
        activity.dataBinding.llStatu.addView(item, layoutParams);
    }

          fun upAnimatorback(v:View,currentY:Float){
                val animator = ValueAnimator.ofFloat(currentY,starty) as ValueAnimator
              animator.duration = 800
              animator.addUpdateListener {
                  item?.y = it.animatedValue as Float
              }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                        v.alpha = 1f
                val activity = context as IdentityActivity
                activity.dataBinding.llStatu.removeView(item)
                item =null
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
              animator.start()
          }

    fun upAnmatiorSet(v:View,currentY: Float){
        val activity = context as IdentityActivity
        val animator = ValueAnimator.ofFloat(currentY,activity.dataBinding.mineRelativelayout.top.toFloat()) as ValueAnimator
        animator.duration = 800
        animator.addUpdateListener {
            item?.y = it.animatedValue as Float
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {


              notifyItemRemoved(longView?.tag.toString().toInt())
                list.removeAt(longView?.tag.toString().toInt())
                notifyItemRangeChanged(1, list.size - 1)
                Log.e("wxs","(StatusAdapter.kt:156)->>位置${longView?.tag.toString().toInt()} ")
             activity.dataBinding.clContentView.visible()

                activity.dataBinding.llStatu.removeView(item)
                item=null

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        animator.start()

    }
}