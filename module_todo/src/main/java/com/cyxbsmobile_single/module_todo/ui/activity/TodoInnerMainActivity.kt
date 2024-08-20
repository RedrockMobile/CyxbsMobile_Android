package com.cyxbsmobile_single.module_todo.ui.activity

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback
import com.cyxbsmobile_single.module_todo.component.CheckLineView
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoAllFragment
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoLifeFragment
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoOtherFragement
import com.cyxbsmobile_single.module_todo.ui.fragment.TodoStudyFragment
import com.cyxbsmobile_single.module_todo.util.isOutOfTime
import com.cyxbsmobile_single.module_todo.util.setMargin
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter


@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity : BaseActivity() {

    //在详情页面是否有做出修改的flag
    private var changedFlag = false
    private val todo_inner_home_bar_add by R.id.todo_inner_home_bar_add.view<ImageView>()
    private val todo_inner_home_bar_back by R.id.todo_inner_home_bar_back.view<ImageView>()
    private val mTabLayout: TabLayout by R.id.tab_layout.view()
    private val manageButton by R.id.todo_custom_button.view<FrameLayout>()
    private val changeManageButton by R.id.todo_custom_button_change.view<FrameLayout>()
    private val deleteCheckBox by R.id.button_bottom_right.view<FrameLayout>()
    private val topCheckBox by R.id.button_bottom_left.view<FrameLayout>()
    private val bottomButtons by R.id.todo_bottom_action_layout.view<LinearLayoutCompat>()
    private val mVp: ViewPager2 by R.id.view_pager.view()
    private val addButton by R.id.todo_inner_home_bar_add.view<FloatingActionButton>()
    private val todoViewModel by viewModels<TodoViewModel>()
    private val todoViewModeldata by viewModels<TodoViewModel2>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_inner_main)
        initView()
        initTab()
        initClick()
        changedFlag = false

    }

    private fun initView() {
        todoViewModeldata.getAllTodo()

    }

    private fun initClick() {
        todo_inner_home_bar_back.setOnClickListener {
            finish()
        }
        manageButton.setOnClickListener {
            manageButton.visibility = View.GONE
            bottomButtons.visibility = View.VISIBLE
            changeManageButton.visibility = View.VISIBLE
            addButton.visibility=View.GONE
            todoViewModel.setEnabled(true)
        }
        changeManageButton.setOnClickListener {
            bottomButtons.visibility = View.GONE
            changeManageButton.visibility = View.GONE
            manageButton.visibility = View.VISIBLE
            addButton.visibility=View.VISIBLE
            todoViewModel.setEnabled(false)
        }

    }


    @SuppressLint("MissingInflatedId")
    private fun initTab() {
        mVp.adapter = FragmentVpAdapter(this)
            .add { TodoAllFragment() }
            .add { TodoOtherFragement() }
            .add { TodoLifeFragment() }
            .add { TodoStudyFragment() }
        TabLayoutMediator(mTabLayout, mVp) { tab, position ->
            val tabView = LayoutInflater.from(this).inflate(R.layout.todo_custom_tab, null)
            val tabTextView = tabView.findViewById<TextView>(R.id.tabTextView)
            when (position) {
                0 -> tabTextView.text = "全部"
                1 -> tabTextView.text = "学习"
                2 -> tabTextView.text = "生活"
                else -> tabTextView.text = "其他"
            }
            tab.customView = tabView
        }.attach()

        //选中字体加粗
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabTextView = tab.customView as TextView?
                tabTextView?.setTypeface(null, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabTextView = tab.customView as TextView?
                tabTextView?.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    override fun onResume() {
        super.onResume()
        if (changedFlag) {
            //私以为都在子线程进行，不会ANR
//            viewModel.initDataList(
//                    onLoadSuccess = {
//                        onDateLoaded()
//                    }
//            )
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
//        val adapter =
//                DoubleListFoldRvAdapter(viewModel.wrapperList, NORMAL, R.layout.todo_rv_item_todo_inner)
        val callback = SlideCallback()

//        todo_inner_home_bar_add.setOnClickListener {
//            AddItemDialog(this) {
//                adapter.addTodo(it)
//            }.show()
//        }
//返回按钮点击事件


        findViewById<ConstraintLayout>(R.id.todo_cl_item_main).setBackgroundColor(
            ContextCompat.getColor(
                this@TodoInnerMainActivity,
                com.mredrock.cyxbs.common.R.color.common_white_background
            )
        )


    }
}



