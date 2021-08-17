package com.cyxbsmobile_single.module_todo.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType.NORMAL
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType.THREE
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.util.remindTimeStamp2String
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseActivity
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
    private val showType: ShowType
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

    private var checkedTopMark = 0
    private var isShowItem = true


    //真正用于展示的list，会动态的增减
    private var wrapperCopyList: ArrayList<TodoItemWrapper> =
        todoItemWrapperArrayList.clone() as ArrayList<TodoItemWrapper>

    override fun getItemViewType(position: Int): Int = todoItemWrapperArrayList[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = when (viewType) {
            TODO -> {
                R.layout.todo_rv_item_todo
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

    fun doOnBindView(onBindView: (itemView: View, position: Int, viewType: Int, wrapper: TodoItemWrapper) -> Unit) {
        this.onBindView = onBindView
    }

    //内部check, 将条目置换到下面
    fun checkItemAndSwap(wrapper: TodoItemWrapper) {
        wrapperCopyList.remove(wrapper)
        wrapper.todo?.isChecked = true
        wrapperCopyList.add(checkedTopMark - 1, wrapper)
        refreshList()
        //同步修改todoItemWrapperArrayList
        todoItemWrapperArrayList.remove(wrapper)
        wrapper.todo?.isChecked = true
        todoItemWrapperArrayList.add(checkedTopMark - 1, wrapper)

        //更新database中的todo
        updateTodo(wrapper.todo)
    }

    //feed处的check, check之后将条目上浮
    fun checkItemAndPopUp(wrapperTodo: TodoItemWrapper) {
        wrapperCopyList.remove(wrapperTodo)
        wrapperTodo.todo?.isChecked = true
        refreshList()
        todoItemWrapperArrayList.remove(wrapperTodo)

        //更新database中的todo
        updateTodo(wrapperTodo.todo)
    }

    fun delItem(wrapper: TodoItemWrapper) {
        wrapper.todo?.let { todo ->
            Observable.just(wrapper)
                .map {
                    TodoDatabase.INSTANCE
                        .todoDao()
                        .deleteTodoById(todo.todoId)
                }
                .setSchedulers()
                .safeSubscribeBy {
                    wrapperCopyList.remove(wrapper)
                    refreshList()
                    todoItemWrapperArrayList.remove(wrapper)
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


    fun changeFoldStatus() {
        if (isShowItem) {
            foldItem()
        } else {
            showItem()
        }
        isShowItem = !isShowItem
    }

    fun addTodo(todo: Todo) {
        TodoDatabase.INSTANCE
            .todoDao()
            .insertTodo(todo)
            .toObservable()
            .setSchedulers()
            .safeSubscribeBy {
                //逻辑放在这里的目的是为了更新todo的id
                LogUtils.d("RayJoe", "add one item")
                todo.todoId = it
                val wrapper = TodoItemWrapper.todoWrapper(todo)
                wrapperCopyList.add(1, wrapper)
                refreshList()
                todoItemWrapperArrayList.add(1, wrapper)
            }
    }

    fun refreshList() {
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
            }

            TODO -> {
                if (itemView.todo_cl_item_main.translationX != 0f) {
                    clearView(viewHolder = holder)
                }
                itemView.apply {
                    curWrapper.todo?.let { todo ->
                        todo_fl_del.visibility = View.GONE
                        todo_tv_todo_title.text = todo.title
                        todo_tv_notify_time.text = remindTimeStamp2String(todo.remindTime)
                        todo_clv_todo_item.setStatusWithoutAnime(todo.isChecked)
                        if (todo.isChecked) {
                            //TODO: 替换为res资源
                            todo_tv_todo_title.setTextColor(Color.parseColor("#6615315B"))
                            todo_iv_check.visibility = View.VISIBLE
                        } else {
                            todo_iv_check.visibility = View.GONE
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

    override fun getItemCount(): Int {
        for ((index, wrapper) in wrapperCopyList.withIndex()) {
            if (index != 0 && wrapper.viewType == TODO && wrapperCopyList[index - 1].viewType == TITLE) {
                checkedTopMark = index
            }
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
                if (oldItem.viewType == TITLE) {
                    oldItem.title == newItem.title
                } else {
                    oldItem.todo?.todoId == newItem.todo?.todoId
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
        LogUtils.d("RayleighZ", "clearView")
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

    private fun updateTodo(todo: Todo?){
        todo?.let {
            Observable.just(it)
                .map { todo ->
                    LogUtils.d("RayFucker", "try to update")
                    TodoDatabase.INSTANCE.todoDao()
                        .updateTodo(todo)
                }.setSchedulers()
                .safeSubscribe(
                    ExecuteOnceObserver(
                        onExecuteOnceError = {
                            BaseApp.context.toast("更新数据失败")
                        }
                    )
                )
        }
    }
}