package com.cyxbsmobile_single.module_todo.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType.NORMAL
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback
import com.cyxbsmobile_single.module_todo.component.CheckLineView
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.ui.dialog.AddTodoDialog
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoAllFragment
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoLifeFragment
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoOtherFragement
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoStudyFragment
import com.cyxbsmobile_single.module_todo.util.isOutOfTime
import com.cyxbsmobile_single.module_todo.util.setMargin
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter


@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity : BaseViewModelActivity<TodoViewModel>() {

    //在详情页面是否有做出修改的flag
    private var changedFlag = false
    private val todo_inner_home_bar_add by R.id.todo_inner_home_bar_add.view<ImageView>()
    private val todo_inner_home_bar_back by R.id.todo_inner_home_bar_back.view<ImageView>()
    private val mTabLayout: TabLayout by R.id.tab_layout.view()
    private val mVp: ViewPager2 by R.id.view_pager .view()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_inner_main)
        initTab()
        changedFlag = false
        viewModel.initDataList(
                onLoadSuccess = {
                    onDateLoaded()
                }
        )
    }

    private fun initTab() {
     mVp.adapter=FragmentVpAdapter(this)
         .add {TodoAllFragment()}
         .add { TodoOtherFragement() }
         .add { TodoLifeFragment() }
         .add { TodoStudyFragment() }
        TabLayoutMediator(mTabLayout,mVp){tab,position->
            when(position){
                0->tab.text="全部"
                1->tab.text="学习"
                2->tab.text="生活"
                else->tab.text="其他"
            }
        }.attach()


    }

    override fun onResume() {
        super.onResume()
        if (changedFlag) {
            //私以为都在子线程进行，不会ANR
            viewModel.initDataList(
                    onLoadSuccess = {
                        onDateLoaded()
                    }
            )
        }
    }

    private fun changeItemToChecked(itemView: View) {
        itemView.apply {
            findViewById<AppCompatEditText>(R.id.todo_tv_todo_title).setTextColor(
                    ContextCompat.getColor(
                            context,
                            R.color.todo_item_checked_color
                    )
            )
            findViewById<AppCompatImageView>(R.id.todo_iv_check).visibility = View.VISIBLE
        }
    }

    private fun onDateLoaded() {
        val adapter =
                DoubleListFoldRvAdapter(viewModel.wrapperList, NORMAL, R.layout.todo_rv_item_todo_inner)
        val callback = SlideCallback()

        todo_inner_home_bar_add.setOnClickListener {
            AddTodoDialog(this) {
                adapter.addTodo(it)
            }.show()
        }

        todo_inner_home_bar_back.setOnClickListener {
            finish()
        }
        adapter.onBindView = { view, pos, viewType, wrapper ->
            when (viewType) {
                DoubleListFoldRvAdapter.TITLE -> {
                    if (pos != 0) {//确定是已完成事项的title
                        view.setOnClickListener {
                            adapter.changeFoldStatus(view.findViewById<AppCompatImageView>(R.id.todo_iv_hide_list))
                        }
                    } else {
                        view.isClickable = false
                    }
                }

                DoubleListFoldRvAdapter.TODO -> {
                    view.apply {
                        findViewById<FrameLayout>(R.id.todo_fl_todo_back).setOnClickListener {
                            wrapper.todo?.let {
                                TodoDetailActivity.startActivity(
                                        it,
                                        this@TodoInnerMainActivity
                                )
                                changedFlag = true
                            }
                        }
                        findViewById<AppCompatEditText>(R.id.todo_tv_todo_title).setOnClickListener {
                            wrapper.todo?.let {
                                TodoDetailActivity.startActivity(
                                        it,
                                        this@TodoInnerMainActivity
                                )
                                changedFlag = true
                            }
                        }
                        findViewById<FrameLayout>(R.id.todo_fl_del).visibility = View.VISIBLE
                        findViewById<FrameLayout>(R.id.todo_fl_del).setOnClickListener {
                            if (findViewById<ConstraintLayout>(R.id.todo_cl_item_main).translationX != 0f) {
                                adapter.delItem(wrapper)
                            }
                        }

                        findViewById<ConstraintLayout>(R.id.todo_cl_item_main).setBackgroundColor(
                                ContextCompat.getColor(
                                        this@TodoInnerMainActivity,
                                        com.mredrock.cyxbs.common.R.color.common_white_background
                                )
                        )

                        wrapper.todo?.let { todo ->
                            if (todo.remindMode.notifyDateTime == "" || isOutOfTime(todo)) {
                                //没有提醒，或者已经过时，就调整margin
                                setMargin(
                                        findViewById<AppCompatEditText>(R.id.todo_tv_todo_title),
                                        top = dip(29),
                                        bottom = dip(29)
                                )
                            } else {
                                setMargin(
                                        findViewById<AppCompatEditText>(R.id.todo_tv_todo_title),
                                        top = dip(18),
                                        bottom = dip(40)
                                )
                            }

                            val todo_iv_todo_item = findViewById<CheckLineView>(R.id.todo_iv_todo_item)
                            todo_iv_todo_item.setOnClickListener {
                                wrapper.todo?.apply {
                                    if (isChecked == 0) {
                                        todo_iv_todo_item.setStatusWithAnime(true) {
                                            adapter.checkItemAndSwap(wrapper)
                                            changeItemToChecked(view)
                                        }
                                    } else {
                                        todo_iv_todo_item.isClickable = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }
}