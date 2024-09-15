package com.mredrock.cyxbs.todo.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.mredrock.cyxbs.todo.R
import com.mredrock.cyxbs.todo.adapter.RepeatTimeRvAdapter
import com.mredrock.cyxbs.todo.model.bean.RemindMode
import com.mredrock.cyxbs.todo.util.addWithoutRepeat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.toastWithYOffset
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.todo.model.bean.Todo
import com.mredrock.cyxbs.todo.util.transformRepeat

/**
 * description: 挑选重复的dialog
 * author: sanhuzhen
 * date: 2024/8/22 1:43
 */
class SelectRepeatDialog(
    context: Context,
    style: Int,
    private val lineVisibility: Int,
    private val todo: Todo,
    val selectRepeat: (List<Int>, List<String>, Int) -> Unit
) :
    BottomSheetDialog(context, style) {

    private var selectRepeatTimeList = arrayListOf<String>()
    private var selectRepeatTimeListIndex = arrayListOf<Int>()
    private var repeatMode = RemindMode.NONE

    private val repeatTimeAdapter by lazy { RepeatTimeRvAdapter(0) }
    private lateinit var wpRepeatMode: WheelPicker
    private lateinit var wpRepeatTime: WheelPicker
    private lateinit var rvSelectRepeatTime: RecyclerView
    private lateinit var btnAddRepeatTime: AppCompatButton
    private lateinit var btnAddRepeatBt: AppCompatTextView
    private lateinit var btnAddRepeatBtCancel: AppCompatTextView
    private lateinit var repeatLine: View

    init {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.todo_dialog_bottom_sheet_selectrepeat, null, false)
        setContentView(dialogView)

        initView(this)
        dialogView?.apply {
            if (lineVisibility == 1) repeatLine.visible()
            else repeatLine.gone()
            initClick()
            initWp()
        }
        if (todo.remindMode.repeatMode != RemindMode.NONE){
            initRv()
        }
    }

    private fun initRv() {
        repeatMode = todo.remindMode.repeatMode

        val (indices, mode) = when (repeatMode) {
            RemindMode.WEEK -> todo.remindMode.week.toMutableList() to RemindMode.WEEK
            RemindMode.MONTH -> todo.remindMode.day.toMutableList() to RemindMode.MONTH
            else -> todo.remindMode.day.toMutableList() to RemindMode.DAY
        }

        selectRepeatTimeListIndex = indices as ArrayList
        selectRepeatTimeList = transformRepeat(indices.map { it.toString() }, mode)
        repeatTimeAdapter.submitList(selectRepeatTimeList.toList())
    }

    private fun initView(dialog: Dialog) {
        wpRepeatMode = dialog.findViewById(R.id.todo_wp_addtodo_repeat_mode)
        wpRepeatTime = dialog.findViewById(R.id.todo_wp_addtodo_repeat_time)
        rvSelectRepeatTime = dialog.findViewById(R.id.todo_rv_addtodo_repeat_list)
        btnAddRepeatTime = dialog.findViewById(R.id.todo_btn_addtodo_repeat_add)
        btnAddRepeatBt = dialog.findViewById(R.id.todo_btn_confirm_addrepeat)
        btnAddRepeatBtCancel = dialog.findViewById(R.id.todo_btn_cancel_addrepeat)
        repeatLine = dialog.findViewById(R.id.todo_view_addtodo_repeat_line)
    }

    private fun initClick() {
        btnAddRepeatBt.setOnClickListener {
            selectRepeat(selectRepeatTimeListIndex, selectRepeatTimeList, repeatMode)
            dismiss()
        }
        btnAddRepeatBtCancel.setOnClickListener {
            dismiss()
        }

        btnAddRepeatTime.setOnClickListener {
            addRepeatTime()
        }
        rvSelectRepeatTime.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = repeatTimeAdapter.apply {
                setOnItemClick { position ->
                    if (position in selectRepeatTimeList.indices) {
                        selectRepeatTimeList.removeAt(position)
                        if (selectRepeatTimeListIndex.isNotEmpty()){
                            selectRepeatTimeListIndex.removeAt(position)
                        }
                        submitList(selectRepeatTimeList.toList())
                        if (selectRepeatTimeList.isEmpty()) {
                            repeatMode = RemindMode.NONE
                        }
                    }
                }
            }
        }
    }

    private fun initWp() {
        val repeatModeList = listOf("每天", "每周", "每月", "", "", "", "")
        wpRepeatMode.data = repeatModeList
        when (wpRepeatMode.currentItemPosition) {
            0 -> {
                wpRepeatTime.data = emptyList<String>()
            }

            1 -> {
                wpRepeatTime.data =
                    listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日", "")
                wpRepeatTime.selectedItemPosition = 0
            }

            2 -> {
                wpRepeatTime.data = (1..31).toList()
                wpRepeatTime.selectedItemPosition = 0
            }
        }
        wpRepeatMode.setOnItemSelectedListener { _, _, position ->
            when (position) {
                0 -> {
                    wpRepeatTime.data = emptyList<String>()
                }

                1 -> {
                    wpRepeatTime.data =
                        listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                    wpRepeatTime.selectedItemPosition = 0
                }

                2 -> {
                    wpRepeatTime.data = (1..31).toList()
                    wpRepeatTime.selectedItemPosition = 0
                }
            }
        }
    }


    //添加重复时间
    private fun addRepeatTime() {

        if (selectRepeatTimeList.isEmpty()) {
            repeatMode = RemindMode.NONE
        }

        val currentRepeatMode = wpRepeatMode.currentItemPosition.plus(1)

        if (currentRepeatMode == repeatMode || repeatMode == RemindMode.NONE) {
            repeatMode = currentRepeatMode
            // 获取当前选中的重复时间
            if (wpRepeatTime.currentItemPosition in 0..wpRepeatTime.data.size || wpRepeatTime.data.isEmpty()) {
                val currentRepeatTime = when (repeatMode) {
                    RemindMode.DAY -> "每天"
                    RemindMode.WEEK -> {
                        selectRepeatTimeListIndex.addWithoutRepeat(
                            0,
                            wpRepeatTime.currentItemPosition + 1
                        )
                        wpRepeatTime.data[wpRepeatTime.currentItemPosition].toString()
                    }

                    RemindMode.MONTH -> {
                        val day =
                            wpRepeatTime.data.get(wpRepeatTime.currentItemPosition).toString()
                        selectRepeatTimeListIndex.addWithoutRepeat(
                            0,
                            wpRepeatTime.currentItemPosition + 1
                        )
                        "每月${day}日"
                    }

                    else -> null
                }
                // 确保当前选中的重复时间不为空且不在列表中
                if (!selectRepeatTimeList.contains(currentRepeatTime)) {
                    // 将新项插入到列表的开头
                    currentRepeatTime?.let { selectRepeatTimeList.add(0, it) }
                    repeatTimeAdapter.submitList(selectRepeatTimeList.toList()) {
                        // 确保RecyclerView显示在第一个项目
                        rvSelectRepeatTime.scrollToPosition(0)
                    }  // 刷新适配器
                }
            }
        } else {
            "只能选择一种重复模式哦！".toastWithYOffset(1000)
        }


    }
}