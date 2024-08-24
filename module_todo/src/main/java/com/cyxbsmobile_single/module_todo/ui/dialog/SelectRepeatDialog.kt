package com.cyxbsmobile_single.module_todo.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.RepeatTimeRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.util.addWithoutRepeat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * description: 挑选重复的dialog
 * author: sanhuzhen
 * date: 2024/8/22 1:43
 */
class SelectRepeatDialog(context: Context, val selectRepeat: (List<Int>, List<String>, Int) -> Unit) :
    BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

    private val selectRepeatTimeList = arrayListOf<String>()
    private val selectRepeatTimeListIndex = arrayListOf<Int>()
    private var repeatMode = RemindMode.NONE

    private val repeatTimeAdapter by lazy { RepeatTimeRvAdapter(0) }
    private val wpRepeatMode by lazy { findViewById<WheelPicker>(R.id.todo_wp_addtodo_repeat_mode)!! }
    private val wpRepeatTime by lazy { findViewById<WheelPicker>(R.id.todo_wp_addtodo_repeat_time)!! }
    private val rvSelectRepeatTime by lazy { findViewById<RecyclerView>(R.id.todo_rv_addtodo_repeat_list)!! }
    private val btnAddRepeatTime by lazy { findViewById<AppCompatButton>(R.id.todo_btn_addtodo_repeat_add)!! }
    private val btnAddRepeatBt by lazy { findViewById<AppCompatButton>(R.id.todo_btn_confirm_addrepeat)!! }
    private val btnAddRepeatBtCancel by lazy { findViewById<AppCompatButton>(R.id.todo_btn_cancel_addrepeat)!! }
    init {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.todo_dialog_bottom_sheet_selectrepeat, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            initClick()
            initWp()
        }
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
                        submitList(selectRepeatTimeList.toList())
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
                    listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日","")
                wpRepeatTime.setSelectedItemPosition(0)
            }

            2 -> {
                wpRepeatTime.data = (1..31).toList()
                wpRepeatTime.setSelectedItemPosition(0)
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
                    wpRepeatTime.setSelectedItemPosition(0)
                }

                2 -> {
                    wpRepeatTime.data = (1..31).toList()
                    wpRepeatTime.setSelectedItemPosition(0)
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
            if (wpRepeatTime.currentItemPosition in 0..wpRepeatTime.data.size){
                val currentRepeatTime = when (repeatMode) {
                    RemindMode.DAY -> "每天"
                    RemindMode.WEEK -> {
                        selectRepeatTimeListIndex.addWithoutRepeat(0, wpRepeatTime.currentItemPosition + 1)
                        wpRepeatTime.data[wpRepeatTime.currentItemPosition].toString()
                    }

                    RemindMode.MONTH -> {
                        val day =
                            wpRepeatTime.data.get(wpRepeatTime.currentItemPosition).toString()
                        selectRepeatTimeListIndex.addWithoutRepeat(0, wpRepeatTime.currentItemPosition + 1)
                        "每月${day}日"
                    }

                    else -> null
                }
                // 确保当前选中的重复时间不为空且不在列表中
                if (!selectRepeatTimeList.contains(currentRepeatTime)) {
                    // 将新项插入到列表的开头
                    currentRepeatTime?.let { selectRepeatTimeList.add(0, it) }
                    repeatTimeAdapter.submitList(ArrayList(selectRepeatTimeList)) {
                        // 确保RecyclerView显示在第一个项目
                        rvSelectRepeatTime.layoutManager?.scrollToPosition(0)
                    }  // 刷新适配器
                }
            }
        } else {
            toast("只能选择一种重复模式哦！")
        }


    }
}