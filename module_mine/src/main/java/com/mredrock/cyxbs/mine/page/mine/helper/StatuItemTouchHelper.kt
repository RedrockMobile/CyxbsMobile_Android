package com.mredrock.cyxbs.mine.page.mine.helper

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections




class StatuItemTouchHelper: ItemTouchHelper.Callback() {



    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        Log.e("wxtagss","(StatuItemTouchHelper.kt:21)->>getMovementFlags ")
        val dragFlags =
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeFlag(ACTION_STATE_DRAG,dragFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
//        if (recyclerView == null) {
//            return false
//        }
//
//        val adapter = recyclerView.adapter ?: return false
//
//        if (list != null && list.size() > 0) {
//            //获取被拖拽的Item的Position
//            val from = viewHolder.adapterPosition
//            //获取目标Item的Position
//            val endPosition = target.adapterPosition
//            //交换List集合中两个元素的位置
//            Collections.swap(list, from, endPosition)
//            //交换界面上两个Item的位置
//            adapter.notifyItemMoved(from, endPosition)
//        }
      //  val v= target as View
        Log.e("wxtagss","(StatuItemTouchHelper.kt:45)->> x的距离")
       // Log.e("wxtagss","(StatuItemTouchHelper.kt:45)->> x的距离${v.x} y的距离${v.y}")
       return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.e("wxtagss","(StatuItemTouchHelper.kt:45)->> onSwiped")
        TODO("Not yet implemented")
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ) {
        Log.e("wxtagss","(StatuItemTouchHelper.kt:69)->>  onMoved")
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
       Log.e("wxtagss","(StatuItemTouchHelper.kt:69)->>  onMoved")
    }



}