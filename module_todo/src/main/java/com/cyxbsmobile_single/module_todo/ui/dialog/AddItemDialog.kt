package com.cyxbsmobile_single.module_todo.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate.*
import com.cyxbsmobile_single.module_todo.util.getThisYearDateSting
import com.cyxbsmobile_single.module_todo.util.weekStringList
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.super_rabbit.wheel_picker.OnValueChangeListener
import com.super_rabbit.wheel_picker.WheelAdapter
import com.super_rabbit.wheel_picker.WheelPicker
import kotlinx.android.synthetic.main.todo_inner_add_thing_dialog.*
import kotlinx.android.synthetic.main.todo_inner_add_thing_dialog.view.*
import java.util.*

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * 增加todo用的Dialog
 */
class AddItemDialog(context: Context, onConfirm: (Todo, AddItemDialog) -> Unit) :
    RedRockBottomSheetDialog(context) {

    enum class CurOperate {
        REPEAT,
        NOTIFY
    }

    var curOperate: CurOperate = NOTIFY

    init {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.todo_inner_add_thing_dialog, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            todo_tv_set_notify_time.setOnClickListener {
                todo_inner_add_thing_second.visibility = View.VISIBLE
                todo_inner_add_thing_third.visibility = View.VISIBLE
                showNotifyDatePicker()
            }

            todo_tv_set_repeat_time.setOnClickListener {
                curOperate = REPEAT
                initWheelPicker(todo_inner_add_thing_first, RepeatTypeAdapter())
                todo_inner_add_thing_first.setOnValueChangeListener(
                    object :OnValueChangeListener{
                        override fun onValueChange(
                            picker: WheelPicker,
                            oldVal: String,
                            newVal: String
                        ) {
                            when(newVal){
                                "每天" -> {
                                    todo_inner_add_thing_second.visibility = View.GONE
                                    todo_inner_add_thing_third.visibility = View.GONE
                                }

                                "每周" -> {
                                    todo_inner_add_thing_second.setAdapter(WeekAdapter())
                                    todo_inner_add_thing_third.visibility = View.GONE
                                }

                                "每月" -> {
                                    todo_inner_add_thing_second.setAdapter(NumAdapter(31))
                                    todo_inner_add_thing_third.visibility = View.GONE
                                }

                                "每年" -> {
                                    todo_inner_add_thing_second.setAdapter(NumAdapter(12))
                                    todo_inner_add_thing_second.visibility = View.VISIBLE
                                    todo_inner_add_thing_third.visibility = View.VISIBLE
                                    todo_inner_add_thing_second.setOnValueChangeListener(
                                        object : OnValueChangeListener{
                                            override fun onValueChange(
                                                picker: WheelPicker,
                                                oldVal: String,
                                                newVal: String
                                            ) {
                                                val month = Integer.parseInt(newVal)
                                                val calendar = Calendar.getInstance()
                                                calendar.set(Calendar.MONTH, month)
                                                todo_inner_add_thing_third.setAdapter(NumAdapter(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }

            todo_iv_add_repeat.setOnClickListener {

            }

            todo_thing_add_thing_save.setOnClickListener {
                val todo = Todo(
                    0L,
                    et_discover_other_course_search.text.toString(),
                    "",
                    false,
                    RemindMode(
                        RemindMode.NONE,
                        listOf(
                            "08:07"
                        ),
                        emptyList(),
                        emptyList(),
                        "23:33"
                    )
                )
                onConfirm(todo, this@AddItemDialog)
            }
        }
    }

    private fun showNotifyDatePicker(){
        val dateBeenList = getThisYearDateSting()
        //配置第一个日期时间选择器
        initWheelPicker(todo_inner_add_thing_first, DateAdapter(dateBeenList))
        //配置第二个小时时间选择器
        initWheelPicker(todo_inner_add_thing_second, NumAdapter(24))
        //配置第三个分钟时间选择器
        initWheelPicker(todo_inner_add_thing_third, NumAdapter(60))
    }

    abstract class NormalWheelAdapter(private val size: Int) : WheelAdapter{
        override fun getMaxIndex(): Int = size - 1

        override fun getMinIndex(): Int = 0

        override fun getPosition(vale: String): Int = 0
    }

    private fun initWheelPicker(wheelPicker: WheelPicker, adapter: WheelAdapter){
        wheelPicker.setAdapter(adapter)
        wheelPicker.setSelectorRoundedWrapPreferred(true)
        handleTouchConflict(wheelPicker)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun handleTouchConflict(view :View){
        //解决与bottomSheet的滑动冲突
        view.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    //日期（带星期数）的适配器
    class DateAdapter(private val dateBeenList: List<DateBeen>): NormalWheelAdapter(dateBeenList.size) {
        override fun getTextWithMaximumLength(): String = "12月31日 周天"
        override fun getValue(position: Int): String {
            if (position == 0)
                return "今天"
            val curDateBeen = dateBeenList[position]
            return "${curDateBeen.month}月${curDateBeen.day}日 周${weekStringList[curDateBeen.week]}"
        }
    }

    //数字适配器
    class NumAdapter(size: Int): NormalWheelAdapter(size){
        override fun getTextWithMaximumLength(): String = "24"
        override fun getValue(position: Int): String = "${position+1}"
    }

    //选择重复类型的适配器
    class RepeatTypeAdapter: NormalWheelAdapter(4){
        private val dataList = listOf(
            "每天",
            "每周",
            "每月",
            "每年"
        )
        override fun getTextWithMaximumLength(): String = "每年"
        override fun getValue(position: Int): String = dataList[position]
    }

    //星期
    class WeekAdapter: NormalWheelAdapter(7){
        override fun getTextWithMaximumLength(): String = "周天"
        override fun getValue(position: Int): String = "周"+weekStringList[position]
    }
}