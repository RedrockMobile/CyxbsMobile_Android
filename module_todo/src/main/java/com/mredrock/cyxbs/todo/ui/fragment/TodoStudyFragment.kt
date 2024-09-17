package com.mredrock.cyxbs.todo.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.todo.R
import com.mredrock.cyxbs.todo.adapter.DragAndDropCallback
import com.mredrock.cyxbs.todo.adapter.SwipeDeleteRecyclerView
import com.mredrock.cyxbs.todo.adapter.TodoAllAdapter
import com.mredrock.cyxbs.todo.model.bean.DelPushWrapper
import com.mredrock.cyxbs.todo.model.bean.Todo
import com.mredrock.cyxbs.todo.model.bean.TodoListPushWrapper
import com.mredrock.cyxbs.todo.model.bean.TodoPinData
import com.mredrock.cyxbs.todo.ui.activity.TodoDetailActivity
import com.mredrock.cyxbs.todo.ui.dialog.DeleteTodoDialog
import com.mredrock.cyxbs.todo.util.TodoHelper.updateTodoItem
import com.mredrock.cyxbs.todo.viewmodel.TodoViewModel

/**
 * description ：清单下面四个页面之一
 * author :TaiE
 * email : 1607869392@qq.com
 * date : 2024/8/11 20:16
 * version: 1.0
 */
class TodoStudyFragment : BaseFragment(), TodoAllAdapter.OnItemClickListener {
    private lateinit var todoAllAdapter: TodoAllAdapter
    private val mRecyclerView by R.id.todo_studyrv.view<SwipeDeleteRecyclerView>()
    private val emptyview by R.id.empty_view.view<View>()
    private val emptyBottom by R.id.todo_bottom_action_layout_study.view<ConstraintLayout>()
    private val acDeleteButton by R.id.button_bottom_right_study.view<FrameLayout>()
    private val acTopButton by R.id.button_bottom_left_study.view<FrameLayout>()
    private val checkall by R.id.todo_bottom_check_al_study.view<CheckBox>()
    private val mViewModel: TodoViewModel by activityViewModels()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingUpdateTask: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.todo_fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        todoAllAdapter = TodoAllAdapter(this)
        mRecyclerView.adapter = todoAllAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        initList()
        val callback = DragAndDropCallback(mRecyclerView, todoAllAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)
        initClick()
        checkIfEmpty()
        ifClick()
        todoAllAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIfEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkIfEmpty()
            }
        })

    }

    private fun initClick() {

        acDeleteButton.setOnClickListener {
            DeleteTodoDialog.Builder(requireContext())
                .setPositiveClick {
                    // 移除指定位置的 item
                    todoAllAdapter.deleteSelectedItems()
                    val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                    mViewModel.delTodo(
                        DelPushWrapper(
                            todoAllAdapter.selectItems.map { it.todoId },
                            syncTime
                        )

                    )
                    todoAllAdapter.selectItems.clear()
                    dismiss()
                }.setNegativeClick {
                    dismiss()
                }.show()

        }
        acTopButton.setOnClickListener {
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            for (item in todoAllAdapter.selectItems) {
                mViewModel.pinTodo(TodoPinData(1, 1, syncTime.toInt(), item.todoId.toInt()))
            }
            todoAllAdapter.topSelectedItems()
        }
        checkall.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                todoAllAdapter.selectedall()
            } else {
                todoAllAdapter.toSelectedall()
            }
        }
    }

    private fun ifClick() {
        mViewModel.isEnabled.observe(viewLifecycleOwner) {
            if (it) {
                showBatchManagementLayout()
            } else {
                hideBatchManagementLayout()
            }
        }


    }

    private fun hideBatchManagementLayout() {
        todoAllAdapter.updateEnabled(false)
        emptyBottom.visibility = View.GONE

    }

    private fun showBatchManagementLayout() {
        emptyBottom.visibility = View.VISIBLE
        todoAllAdapter.updateEnabled(true)
    }

    private fun checkIfEmpty() {
        if (todoAllAdapter.itemCount == 0) {
            mRecyclerView.visibility = View.GONE
            emptyview.visibility = View.VISIBLE
        } else {
            mRecyclerView.visibility = View.VISIBLE
            emptyview.visibility = View.GONE
        }
    }

    private fun initList() {

        mViewModel.categoryTodoStudy.observe(viewLifecycleOwner) {
            todoAllAdapter.submitList(it.todoArray) {
                checkIfEmpty()
            }
        }

    }

    //处理点击事件
    override fun onItemClick(item: Todo) {
        TodoDetailActivity.startActivity(item, requireContext())
    }

    override fun onListtextClick(item: Todo) {
        TodoDetailActivity.startActivity(item, requireContext())

    }

    @SuppressLint("MissingInflatedId")
    override fun ondeleteButtonClick(item: Todo, position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()

        // 检查索引是否在当前列表的有效范围内
        if (position >= 0 && position < currentList.size) {
            DeleteTodoDialog.Builder(requireContext())
                .setPositiveClick {
                    val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                    mViewModel.delTodo(DelPushWrapper(listOf(item.todoId), syncTime))
                    // 移除指定位置的 item
                    currentList.removeAt(position)
                    // 提交更新后的列表
                    todoAllAdapter.submitList(currentList)
                    dismiss()
                }.setNegativeClick {
                    dismiss()
                }.show()


        }
    }

    override fun ontopButtonClick(item: Todo, position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()
        if (item.isPinned == 0) {
            // 更新isPinned状态并移除当前项
            item.isPinned = 1
            currentList.removeAt(position)

            // 将项添加到列表的顶部
            currentList.add(0, item)

            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)

            mViewModel.pinTodo(TodoPinData(1, 1, syncTime.toInt(), item.todoId.toInt()))

            // 提交更新后的列表
            todoAllAdapter.submitList(currentList) {
                // 滚动到顶部以显示置顶的项
                mRecyclerView.scrollToPosition(0)
            }
        } else {
            item.isPinned = 0
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            mViewModel.pushTodo(TodoListPushWrapper(listOf(item), syncTime, 1, 0))
            currentList.removeAt(position)
            if (getTopItems() == 0) {
                currentList.add(1, item)
            } else {
                currentList.add(getTopItems(), item)
            }
            todoAllAdapter.submitList(currentList)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onFinishCheck(item: Todo) {
        pendingUpdateTask?.let { handler.removeCallbacks(it) }
        if (item.remindMode.repeatMode != 0) {
            // 创建新的任务
            pendingUpdateTask = Runnable {
                updateTodoItem(
                    item,
                    requireContext(),
                    mViewModel,
                    todoAllAdapter,
                    mRecyclerView,
                    viewLifecycleOwner
                )
            }
            // 延迟 1.5秒执行新的任务
            pendingUpdateTask?.let { handler.postDelayed(it, 1500) }
        } else {
            pendingUpdateTask = Runnable {
                val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                mViewModel.delTodo(DelPushWrapper(listOf(item.todoId), syncTime, 1))
                val currentList = todoAllAdapter.currentList.toMutableList()
                currentList.remove(item)
                todoAllAdapter.submitList(currentList)
            }
            pendingUpdateTask?.let { handler.postDelayed(it, 1500) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemnotify(item: Todo) {
        if (item.endTime != "" && item.remindMode.repeatMode != 0) {
            updateTodoItem(
                item,
                requireContext(),
                mViewModel,
                todoAllAdapter,
                mRecyclerView,
                viewLifecycleOwner
            )
        } else {
            if (item.remindMode.repeatMode != 0) {
                updateTodoItem(
                    item,
                    requireContext(),
                    mViewModel,
                    todoAllAdapter,
                    mRecyclerView,
                    viewLifecycleOwner
                )
            } else {
                item.remindMode.notifyDateTime = item.endTime
            }

        }

    }

    fun getTopItems(): Int {
        return todoAllAdapter.currentList.count { it.isPinned == 1 }
    }

    override fun onResume() {
        mViewModel.getAllTodo()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 取消所有待执行任务
        pendingUpdateTask?.let { handler.removeCallbacks(it) }
    }
}