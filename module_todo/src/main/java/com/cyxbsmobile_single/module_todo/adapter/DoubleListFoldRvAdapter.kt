package com.cyxbsmobile_single.module_todo.adapter

import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType.NORMAL
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType.THREE
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback
import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import com.cyxbsmobile_single.module_todo.util.repeatMode2RemindTime
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.ExecuteOnceObserver
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.todo_rv_item_empty.view.*
import kotlinx.android.synthetic.main.todo_rv_item_title.view.*
import kotlinx.android.synthetic.main.todo_rv_item_todo.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-02 11:27
 * 双列表可折叠RecyclerView
 */
@Suppress("UNCHECKED_CAST")
class DoubleListFoldRvAdapter(
    private val todoItemWrapperArrayList: ArrayList<TodoItemWrapper>,
    private val showType: ShowType,
    private val itemRes: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TODO = 0
        const val TITLE = 1
        const val EMPTY = 2
    }

    enum class ShowType {
        NORMAL,
        THREE
    }

    private val uncheckedArray by lazy { ArrayList<TodoItemWrapper>() }
    private val checkedArray by lazy { ArrayList<TodoItemWrapper>() }

    private var isFirstTimeGetItemCount = true

    private var checkedTopMark = 0
    private var isShowItem = true

    private var isAddedUpEmpty = false
    private var isAddedDownEmpty = false

    private val upEmptyHolder by lazy { TodoItemWrapper(EMPTY, title = "1") }
    private val downEmptyHolder by lazy { TodoItemWrapper(EMPTY, title = "2") }

    //真正用于展示的list，会动态的增减
    private var wrapperCopyList: ArrayList<TodoItemWrapper> =
        todoItemWrapperArrayList.clone() as ArrayList<TodoItemWrapper>

    override fun getItemViewType(position: Int): Int = todoItemWrapperArrayList[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = when (viewType) {
            TODO -> {
                itemRes
            }
            TITLE -> {
                R.layout.todo_rv_item_title
            }
            EMPTY -> {
                R.layout.todo_rv_item_empty
            }
            else -> {
                R.layout.todo_rv_item_title
            }
        }
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false).apply {
                tag = SlideCallback.CurStatus.CLOSE
            }
        ) {}
    }

    lateinit var onBindView: (itemView: View, position: Int, viewType: Int, wrapper: TodoItemWrapper) -> Unit

    //内部check, 将条目置换到下面
    fun checkItemAndSwap(wrapper: TodoItemWrapper) {
        LogUtils.d("RayZY", "checkedTopMark = $checkedTopMark")
        wrapperCopyList.remove(wrapper)
        wrapper.todo?.isChecked = 1
        wrapperCopyList.add(checkedTopMark - 1, wrapper)
        refreshList()
        //同步修改todoItemWrapperArrayList
        todoItemWrapperArrayList.remove(wrapper)
        wrapper.todo?.isChecked = 1
        todoItemWrapperArrayList.add(checkedTopMark - 1, wrapper)
        //更新database中的todo
        updateTodo(wrapper.todo)
        checkedArray.add(wrapper)
        uncheckedArray.remove(wrapper)
        checkEmptyItem(true)
    }

    //feed处的check, check之后将条目上浮
    fun checkItemAndPopUp(wrapperTodo: TodoItemWrapper) {
        wrapperCopyList.remove(wrapperTodo)
        wrapperTodo.todo?.isChecked = 1
        refreshList()
        todoItemWrapperArrayList.remove(wrapperTodo)
        //更新database中的todo
        updateTodo(wrapperTodo.todo)
    }

    fun delItem(wrapper: TodoItemWrapper) {
        wrapper.todo?.let { todo ->
            if (todo.isChecked == 1) checkedArray.remove(wrapper) else uncheckedArray.remove(wrapper)
            TodoModel.INSTANCE.delTodo(todo.todoId){
                wrapperCopyList.remove(wrapper)
                refreshList()
                todoItemWrapperArrayList.remove(wrapper)
                checkEmptyItem(true)
            }
        }
    }

    private fun foldItem() {
        wrapperCopyList.subList(checkedTopMark, itemCount).clear()
        refreshList()
    }

    private fun showItem() {
        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack(wrapperCopyList, todoItemWrapperArrayList))
        wrapperCopyList.clear()
        wrapperCopyList.addAll(todoItemWrapperArrayList)
        diffRes.dispatchUpdatesTo(this)
    }

    //检查是否需要缺省图
    private fun checkEmptyItem(needShow: Boolean) {
        if (showType == THREE)
            return
        if (uncheckedArray.size == 0 && !isAddedUpEmpty) {
            //已经莫得待办事项了，将上部更替为缺省图
            isAddedUpEmpty = true
            wrapperCopyList.add(1, upEmptyHolder)
            if (needShow) {
                refreshList()
            }
            LogUtils.d(
                "TestRay",
                "new size = ${wrapperCopyList.size}, old size = ${todoItemWrapperArrayList.size}, needShow = $needShow"
            )
            todoItemWrapperArrayList.add(1, upEmptyHolder)
            LogUtils.d("RayG", "checkEmpty1")
        }
        if (checkedArray.size == 0 && !isAddedDownEmpty) {
            //如果已经莫得已完成事项了，将下部替换为缺省图
            isAddedDownEmpty = true
            wrapperCopyList.add(downEmptyHolder)
            if (needShow) {
                refreshList()
            }
            todoItemWrapperArrayList.add(downEmptyHolder)
            LogUtils.d("RayG", "checkEmpty2")
        }

        if (uncheckedArray.size != 0 && isAddedUpEmpty) {
            wrapperCopyList.remove(upEmptyHolder)
            if (needShow) {
                refreshList()
            }
            todoItemWrapperArrayList.remove(upEmptyHolder)
            isAddedUpEmpty = false
            LogUtils.d("RayG", "checkEmpty3")
        }

        if (checkedArray.size != 0 && isAddedDownEmpty) {
            wrapperCopyList.remove(downEmptyHolder)
            if (needShow) {
                refreshList()
            }
            todoItemWrapperArrayList.remove(downEmptyHolder)
            isAddedDownEmpty = false
            LogUtils.d("RayG", "checkEmpty4")
        }
    }

    fun changeFoldStatus(hideIcon: View) {
        if (isShowItem) {
            foldItem()
            ObjectAnimator.ofFloat(hideIcon, "rotationX", 0f, 180f).apply {
                repeatCount = 0
                start()
            }
        } else {
            showItem()
            ObjectAnimator.ofFloat(hideIcon, "rotationX", 180f, 0f).apply {
                repeatCount = 0
                start()
            }
        }
        isShowItem = !isShowItem
    }

    fun addTodo(todo: Todo) {
        TodoModel.INSTANCE
            .addTodo(todo){
                //逻辑放在这里的目的是为了更新todo的id
                LogUtils.d("RayJoe", "add one item")
                todo.todoId = it
                val wrapper = TodoItemWrapper.todoWrapper(todo)
                uncheckedArray.add(wrapper)
                wrapperCopyList.add(1, wrapper)
                refreshList()
                todoItemWrapperArrayList.add(1, wrapper)
                checkEmptyItem(true)
                BaseApp.context.sendBroadcast(Intent("cyxbs.widget.todo.refresh").apply {
                    component = ComponentName(BaseApp.context, TodoWidget::class.java)
                })
            }
    }

    private fun refreshList() {
        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack(todoItemWrapperArrayList, wrapperCopyList))
        diffRes.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curWrapper = wrapperCopyList[position]
        val itemView = holder.itemView
        //在这里进行基础的数据类绑定
        when (curWrapper.viewType) {
            TITLE -> {
                itemView.todo_tv_item_title.text = curWrapper.title
                itemView.todo_iv_hide_list.visibility = if (position != 0){ View.VISIBLE } else { View.GONE }
            }

            TODO -> {
                if (itemView.todo_cl_item_main.translationX != 0f) {
                    clearView(viewHolder = holder)
                }
                itemView.apply {
                    curWrapper.todo?.let { todo ->
                        todo_fl_del.visibility = View.GONE
                        todo_tv_todo_title.text = todo.title
                        todo_tv_notify_time.text = repeatMode2RemindTime(todo.remindMode)
                        todo_tv_todo_title.setOnClickListener { }//防止穿透点击
                        if (todo.remindMode.notifyDateTime == ""){
                            todo_tv_notify_time.visibility = View.GONE
                            todo_iv_bell.visibility = View.GONE
                        } else {
                            todo_tv_notify_time.visibility = View.VISIBLE
                            todo_iv_bell.visibility = View.VISIBLE
                        }
                        todo_iv_todo_item.setStatusWithoutAnime(todo.isChecked == 1)
                        if (todo.isChecked == 1) {
                            todo_tv_todo_title.setTextColor(ContextCompat.getColor(context, R.color.todo_item_checked_color))
                            todo_iv_check.visibility = View.VISIBLE
                        } else {
                            todo_iv_check.visibility = View.GONE
                            todo_tv_todo_title.setTextColor(ContextCompat.getColor(context, R.color.todo_check_line_color))
                        }
                    }
                }
            }

            EMPTY -> {
                itemView.apply {
                    if (position != 1) {
                        //认定为下方的缺省
                        todo_rv_item_iv_empty.setImageResource(R.drawable.todo_ic_empty2)
                        todo_rv_item_tv_empty.text = "还没有已完成事项哦，期待你的好消息！"
                    }
                }
            }
        }
        onBindView(holder.itemView, position, curWrapper.viewType, curWrapper)
    }

    inner class DiffCallBack(
        private val oldList: ArrayList<TodoItemWrapper>,
        private val newList: ArrayList<TodoItemWrapper>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return if (oldItem.viewType == newItem.viewType) {
                return when(oldItem.viewType){
                    TITLE -> {
                        oldItem.title == newItem.title
                    }

                    TODO -> {
                        oldItem.todo?.todoId == newItem.todo?.todoId
                    }

                    EMPTY -> {
                        LogUtils.d("RayG", "EMPTY judge")
                        oldItem === newItem
                    }

                    else -> {
                        false
                    }
                }
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.toString() == newItem.toString()
        }

    }

    private fun clearView(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.apply {
            todo_fl_del.apply {
                pivotX = 0f
                pivotY = height.toFloat() / 2f
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
                isClickable = false
            }
            todo_cl_item_main.translationX = 0f
        }
    }

    private fun updateTodo(todo: Todo?) {
        todo?.let {
            TodoModel.INSTANCE.updateTodo(todo){ }
        }
    }

    override fun getItemCount(): Int {
        checkedTopMark = 0
        checkedArray.clear()
        uncheckedArray.clear()
        for (wrapper in wrapperCopyList) {
            if (wrapper.viewType == TODO) {
                wrapper.todo?.let {
                    if (it.isChecked == 1) {
                        checkedArray.add(wrapper)
                    } else {
                        uncheckedArray.add(wrapper)
                    }
                }
            }
        }
        checkedTopMark = 2 + uncheckedArray.size.coerceAtLeast(1)
        //仅在第一次加载的时候判断是否需要插入缺省
        if (isFirstTimeGetItemCount) {
            checkEmptyItem(false)
            isFirstTimeGetItemCount = false
        }
        return when (showType) {
            NORMAL -> {
                wrapperCopyList.size
            }

            THREE -> {
                wrapperCopyList.size.coerceAtMost(3)
            }
        }
    }
}