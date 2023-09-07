package com.mredrock.cyxbs.ufield.ui.fragment.checkfragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.ui.activity.DetailActivity
import com.mredrock.cyxbs.ufield.adapter.TodoRvAdapter
import com.mredrock.cyxbs.ufield.bean.TodoBean
import com.mredrock.cyxbs.ufield.helper.CheckDialog
import com.mredrock.cyxbs.ufield.helper.GridSpacingItemDecoration
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
                            Log.d("9666", "测试结果-->> $position");
                            passActivity(mDataList[position].activity_id)
                            getTodoData()
                            getTodoUpData(mDataList.lastOrNull()?.activity_id!!)
                            notifyDataSetChanged()
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
                            CheckDialog.Data(
                                content = "不得超过10个字",
                                width = 255,
                                height = 207
                            )
                        ).setPositiveClick {
                            mViewModel.apply {
                                if (!getInput().isEmpty()) {
                                    rejectActivity(mDataList[position].activity_id, getInput())
                                    getTodoData()
                                    getTodoUpData(mDataList.lastOrNull()?.activity_id!!)
                                    notifyDataSetChanged()
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
                    intent.putExtra("actID", mDataList[it].activity_id)
                    Log.d("595995", "测试结果-->> ${mDataList[it].activity_id}");
                    startActivity(intent)
                }

                addItemDecoration(GridSpacingItemDecoration(3))
            }
        }

    }

    /**
     * 处理刷新和加载
     */
    private fun iniRefresh() {
        /**
         * 我最初的理解是 刷新和加载都一个效果，所以把头和尾的数据都刷新了，但是逻辑复杂 而且错误较为复杂（有异常情况）
         * 现在统一一下，上拉加载只能在后面加数据 上拉刷新只加载表头数据
         */
        mRefresh.apply {
            setRefreshHeader(ClassicsHeader(requireContext()))
            setRefreshFooter(ClassicsFooter(requireContext()))
            //下拉刷新
            setOnRefreshListener {
                mViewModel.apply {
                    getTodoData()
                    //  getTodoUpData(mDataList.lastOrNull()?.activity_id?:1)
                }
                finishRefresh(1000)
            }
            //上拉加载
            setOnLoadMoreListener {
                mViewModel.apply {
                    getTodoUpData(mDataList.lastOrNull()?.activity_id ?: 1)
                }
                finishLoadMore(1000)
            }
        }
    }

}