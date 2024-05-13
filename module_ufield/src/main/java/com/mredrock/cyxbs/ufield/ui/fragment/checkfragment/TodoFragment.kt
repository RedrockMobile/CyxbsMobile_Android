package com.mredrock.cyxbs.ufield.ui.fragment.checkfragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.adapter.TodoRvAdapter
import com.mredrock.cyxbs.ufield.bean.TodoBean
import com.mredrock.cyxbs.ufield.helper.CheckDialog
import com.mredrock.cyxbs.ufield.ui.activity.DetailActivity
import com.mredrock.cyxbs.ufield.viewmodel.TodoViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * description ：还没有审核的活动
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:49
 * version: 1.0
 */
class TodoFragment : BaseFragment() {

    private val mRv: RecyclerView by R.id.uField_todo_rv.view()

    private val mViewModel by viewModels<TodoViewModel>()

    private val mAdapter: TodoRvAdapter by lazy { TodoRvAdapter() }

    private lateinit var mDataList: MutableList<TodoBean>

    private val mRefresh: SmartRefreshLayout by R.id.uField_check_refresh_todo.view()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRv()
        iniRefresh()

    }

    /**
     * 初始化Rv，展示待审核的数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun iniRv() {

        mViewModel.apply {
            todoList.observe {
                mAdapter.submitList(it)
                mDataList = it as MutableList<TodoBean>

            }
        }
        mRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.apply {
                /**
                 * 同意活动的点击事件
                 */
                setOnPassClick { position ->
                    run {
                        mViewModel.apply {
                            passActivity(mDataList[position].activityId)
                            isPassSuccess.observe {
                                when (it.status) {
                                    50001 -> toast("活动不存在")
                                    50003 -> toast("活动已结束，请拒绝该活动")
                                }
                            }
                            getTodoData()
                            getTodoUpData(mDataList.lastOrNull()?.activityId!!)
                        }
                    }
                }
                /**
                 * 拒绝活动的点击事件
                 *
                 */
                setOnRejectClick { position ->
                    run {
                        CheckDialog.Builder(
                            requireContext(),
                            CheckDialog.DataImpl(
                                content = "不得超过10个字",
                                width = 255,
                                height = 207
                            )
                        ).setPositiveClick {
                            mViewModel.apply {
                                if (getInput().isNotEmpty()) {
                                    rejectActivity(mDataList[position].activityId, getInput())
                                    getTodoData()
                                    getTodoUpData(mDataList.lastOrNull()?.activityId!!)
                                    dismiss()
                                    toast("已经驳回")
                                } else {
                                    toast("输入不能为空")
                                }
                            }
                        }.setNegativeClick {
                            dismiss()
                        }.show()

                    }
                }
                /**
                 * 点击每一个item
                 */
                setOnItemClick {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("actID", mDataList[it].activityId)
                    startActivity(intent)
                }
            }
        }

    }

    /**
     * 处理刷新和加载
     */
    private fun iniRefresh() {
        mRefresh.apply {
            setRefreshHeader(ClassicsHeader(requireContext()))
            setRefreshFooter(ClassicsFooter(requireContext()))
            setOnRefreshListener {
                mViewModel.apply {
                    getTodoData()
                }
                finishRefresh(600)
            }
            setOnLoadMoreListener {
                mViewModel.apply {
                    getTodoUpData(mDataList.lastOrNull()?.activityId ?: 1)
                }
                finishLoadMore(500)
            }
        }
    }

}