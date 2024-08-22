package com.cyxbsmobile_single.module_todo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.TodoFeedAdapter
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.config.route.DISCOVER_TODO_FEED
import com.mredrock.cyxbs.config.route.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * description: 首页的邮子清单
 * author: sanhuzhen
 * date: 2024/8/20 17:30
 */
@Route(path = DISCOVER_TODO_FEED)
class TodoFeedFragment : BaseFragment() {

    private val mRv by R.id.todo_rv_feed_todo_list.view<RecyclerView>()
    private val mTv by R.id.todo_tv_feed_empty_notify.view<TextView>()
    private val mCl by R.id.todo_cl_todo_feed.view<ConstraintLayout>()

    private val mAdapter by lazy { TodoFeedAdapter() }

    private val mViewModel: TodoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.todo_fragment_todo_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        mCl.setOnClickListener {
            ARouter.getInstance().build(DISCOVER_TODO_MAIN).navigation()
        }
        mRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
        mViewModel.allTodo.observe(viewLifecycleOwner) {
            if (it.todoArray.isEmpty()) {
                mRv.gone()
                mTv.visible()
            } else {
                mRv.visible()
                mTv.gone()
                val list = it.todoArray.filter { todo -> todo.isChecked == 0 }
                    .take(3) // 只取未选中的前3个
                mAdapter.submitList(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getAllTodo()
    }

}