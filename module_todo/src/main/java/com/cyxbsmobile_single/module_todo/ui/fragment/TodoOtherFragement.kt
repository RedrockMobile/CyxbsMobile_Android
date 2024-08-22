package com.cyxbsmobile_single.module_todo.ui.fragment

import DragAndDropCallback
import TodoAllAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.SwipeDeleteRecyclerView
import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoListSyncTimeWrapper
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp

/**
 * description ：清单下面四个页面之一
 * author :TaiE
 * email : 1607869392@qq.com
 * date : 2024/8/11 20:16
 * version: 1.0
 */
class TodoOtherFragement : BaseFragment(), TodoAllAdapter.OnItemClickListener {
    private lateinit var todoAllAdapter: TodoAllAdapter
    private val mRecyclerView by R.id.todo_otherrv.view<SwipeDeleteRecyclerView>()
    private val emptyview by R.id.empty_view.view<View>()
    private lateinit var todoListSyncTimeWrapper: TodoListSyncTimeWrapper
    private val emptyBottom by R.id.todo_bottom_action_layout_other.view<LinearLayoutCompat>()
    private val acDeleteButton by R.id.button_bottom_right_other.view<FrameLayout>()
    private val acTopButton by R.id.button_bottom_left_other.view<FrameLayout>()
    private val checkall by R.id.todo_bottom_check_al_other.view<CheckBox>()
    private val mViewModel: TodoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.todo_fragment_other, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        todoAllAdapter = TodoAllAdapter(this)
        mRecyclerView.adapter = todoAllAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        inittoList()
        initList()
//        todoAllAdapter.submitList(todoDataDetails.changed_todo_array)
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
            val builder = AlertDialog.Builder(requireContext())
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.todo_dialog_custom, null)
            builder.setView(dialogView)
            val closeButton = dialogView.findViewById<Button>(R.id.todo_dialog_positive_button)
            val deleteButton = dialogView.findViewById<Button>(R.id.todo_dialog_negative_button)
            // 创建并显示对话框
            val dialog = builder.create()
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
            deleteButton.setOnClickListener {
                // 移除指定位置的 item
                todoAllAdapter.deleteSelectedItems()
                val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                mViewModel.delTodo(
                    DelPushWrapper(
                        todoAllAdapter.selectItems.map { it.todoId },
                        syncTime
                    )
                )
                dialog.dismiss()
            }
            dialog.show()
        }
        acTopButton.setOnClickListener {
            todoAllAdapter.topSelectedItems()
        }
        checkall.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                todoAllAdapter.selectedall()
            }
            else{
                todoAllAdapter.toSelectedall()
            }
        }
    }

    private fun ifClick() {
        mViewModel.isEnabled.observe(viewLifecycleOwner) {
            Log.d("TodoAllFragment", "isEnabled changed: $it")
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
        Log.d("TodoAllFragment", "Checking if empty. Item count: ${todoAllAdapter.itemCount}")
        if (todoAllAdapter.itemCount == 0) {
            mRecyclerView.visibility = View.GONE
            emptyview.visibility = View.VISIBLE
        } else {
            mRecyclerView.visibility = View.VISIBLE
            emptyview.visibility = View.GONE
        }
    }

    private fun initList() {
        //viewModeldata.allTodo.observe(viewLifecycleOwner) {
        // Log.d("viemodeldata",it.toString())
        //todoAllAdapter.submitList( it.todoArray)
        // if (it.todoArray==null){
        mViewModel.categoryTodoOther.observe(viewLifecycleOwner) {
            todoAllAdapter.submitList(it.todoList){
                checkIfEmpty()
                Log.d("initdata", "initList: ${it.todoList}")
            }
        }
        // }

        //}
    }

    //测试用的数据类
    private fun inittoList() {
// 测试数据1
//        val todo1 = Todo(
//            todoId = 1L,
//            title = "Complete Android project",
//            detail = "Finish the RecyclerView implementation and test the swipe to delete functionality.",
//            isChecked = 0,
//            remindMode = RemindMode(
//                repeatMode = 1,
//                date = arrayListOf("2024-08-20"),
//                week = arrayListOf(),
//                day = arrayListOf(),
//                notifyDateTime = "2024-08-20 09:00:00"
//            ),
//            lastModifyTime = System.currentTimeMillis(),
//            type = "Work",
//            repeatStatus = Todo.SET_UNCHECK_BY_REPEAT
//        )
//
//// 测试数据2
//        val todo2 = Todo(
//            todoId = 2L,
//            title = "Grocery Shopping",
//            detail = "Buy milk, bread, eggs, and vegetables.",
//            isChecked = 0,
//            remindMode = RemindMode(
//                repeatMode = 0,
//                date = arrayListOf("2024-08-19"),
//                week = arrayListOf(),
//                day = arrayListOf(),
//                notifyDateTime = "2024-08-19 17:00:00"
//            ),
//            lastModifyTime = System.currentTimeMillis(),
//            type = "Personal",
//            repeatStatus = Todo.SET_UNCHECK_BY_REPEAT
//        )
//
//// 测试数据3
//        val todo3 = Todo(
//            todoId = 3L,
//            title = "Call the dentist",
//            detail = "Schedule an appointment for a routine check-up.",
//            isChecked = 0,
//            remindMode = RemindMode(
//                repeatMode = 2,
//                date = arrayListOf("2024-08-21"),
//                week = arrayListOf(),
//                day = arrayListOf(),
//                notifyDateTime = "2024-08-21 10:30:00"
//            ),
//            lastModifyTime = System.currentTimeMillis(),
//            type = "Health",
//            repeatStatus = Todo.NONE_WITH_REPEAT
//        )
//
//// 将这些 Todo 数据包装在 TodoListSyncTimeWrapper 中
//        todoListSyncTimeWrapper = TodoListSyncTimeWrapper(
//            syncTime = System.currentTimeMillis(),
//            todoArray = listOf(todo1, todo2, todo3)
//        )


    }

    //处理点击事件
    override fun onItemClick(item: Todo) {
        Toast.makeText(context, "不好意思，多模块还没做", Toast.LENGTH_SHORT).show()
        Log.d("click", "点击事件触发")
    }

    @SuppressLint("MissingInflatedId")
    override fun ondeleteButtonClick(item: Todo, position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()

        // 检查索引是否在当前列表的有效范围内
        if (position >= 0 && position < currentList.size) {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.todo_dialog_custom, null)
            builder.setView(dialogView)
            val closeButton = dialogView.findViewById<Button>(R.id.todo_dialog_positive_button)
            val deleteButton = dialogView.findViewById<Button>(R.id.todo_dialog_negative_button)
            // 创建并显示对话框
            val dialog = builder.create()
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
            deleteButton.setOnClickListener {
                val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                mViewModel.delTodo(DelPushWrapper(listOf(item.todoId), syncTime))
                // 移除指定位置的 item
                currentList.removeAt(position)
                // 提交更新后的列表
                todoAllAdapter.submitList(currentList)

                dialog.dismiss()
            }
            dialog.show()

        } else {
            // 如果索引无效，记录错误日志
            Log.e("TodoAllFragment", "Invalid index: $position, Size: ${currentList.size}")

        }
    }

    override fun ontopButtonClick(item: Todo, position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()
        // 移除当前项
        currentList.removeAt(position)

        // 将项添加到列表的顶部
        currentList.add(0, item)

        // 提交更新后的列表
        todoAllAdapter.submitList(currentList) {
            // 滚动到顶部以显示置顶的项
            mRecyclerView.scrollToPosition(0)
        }
    }


}