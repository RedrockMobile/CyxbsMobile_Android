package com.cyxbsmobile_single.module_todo.adapter

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.ui.BaseFeedFragment


/**
 * Author: RayleighZ
 * Time: 2021-08-02 11:18
 */
class TodoFeedAdapter(private val todoViewModel: TodoViewModel, private val activity: Activity) :
    BaseFeedFragment.Adapter() {

    private lateinit var feedView: View
    private val checkImageBoomAnime by lazy {
        ValueAnimator.ofFloat(0.5f, 1f).apply {
            interpolator = OvershootInterpolator()
            duration = 500
        }
    }

    override fun onCreateView(context: Context, parent: ViewGroup): View {
      //  feedView = LayoutInflater.from(context).inflate(R.layout.todo_fragment_feed, parent, false)
//        todoViewModel.initDataList(
//            onLoadSuccess = { refresh() }
//        )
        return feedView
    }

    fun refresh() {
        feedView.apply {
        //    LogUtils.d("Gibson", "uncheckTodoList = ${todoViewModel.uncheckTodoList}")
//            if (todoViewModel.uncheckTodoList.isNullOrEmpty()) {
//                changeToEmpty()
//            } else {
//                val todo_rv_todo_list = findViewById<RecyclerView>(R.id.todo_rv_todo_list)
//                val todo_tv_feed_empty_notify = findViewById<AppCompatTextView>(R.id.todo_tv_feed_empty_notify)
//                todo_rv_todo_list.visibility = View.VISIBLE
//                todo_tv_feed_empty_notify.visibility = View.GONE
//                //这里可以保证，viewModel和adapter的list是同一个引用，可以保证操作的同步性
//                val adapter = DoubleListFoldRvAdapter(
//                    todoViewModel.uncheckTodoList,
//                    THREE,
//                    R.layout.todo_rv_item_todo
//                )
//                adapter.onBindView = { view, _, viewType, wrapper ->
//                    //此处不可能出现title，但为了稳一波，还是加上了判断
//                    if (viewType == DoubleListFoldRvAdapter.TODO) {
//                        view.apply {
//                            findViewById<FrameLayout>(R.id.todo_fl_todo_back).setOnClickListener {
//                                wrapper.todo?.let {
//                                    TodoDetailActivity.startActivity(it, context)
//                                }
//                            }
//
//                            findViewById<AppCompatEditText>(R.id.todo_tv_todo_title).setOnClickListener {
//                                wrapper.todo?.let {
//                                    TodoDetailActivity.startActivity(it, context)
//                                }
//                            }
//                            val todo_iv_todo_item = findViewById<CheckLineView>(R.id.todo_iv_todo_item)
//                            val todo_iv_check = findViewById<AppCompatImageView>(R.id.todo_iv_check)
//                            todo_iv_todo_item.setOnSingleClickListener {
//                                todo_iv_check.visibility = View.VISIBLE
//                                checkImageBoomAnime.addUpdateListener {
//                                    todo_iv_check.scaleX = it.animatedValue as Float
//                                    todo_iv_check.scaleY = it.animatedValue as Float
//                                }
//                                checkImageBoomAnime.start()
//                                todo_iv_todo_item.setStatusWithAnime(isChecked = true) {
//                                    adapter.checkItemAndPopUp(wrapper)
//                                    if (adapter.itemCount == 0) {
//                                        //如果所有条目都下去了
//                                        changeToEmpty()
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                todo_rv_todo_list.adapter = adapter
//                todo_rv_todo_list.layoutManager = LinearLayoutManager(context)
//            }
        }
    }

    //转换为没有代办的情况
    private fun changeToEmpty() {
        feedView.apply {
//            findViewById<RecyclerView>(R.id.todo_rv_todo_list).visibility = View.GONE
//            findViewById<AppCompatTextView>(R.id.todo_tv_feed_empty_notify).visibility = View.VISIBLE
        }
    }
}