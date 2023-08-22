package com.mredrock.cyxbs.ufield.lyt.fragment.checkfragment

import android.annotation.SuppressLint
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
import com.mredrock.cyxbs.ufield.lyt.adapter.TodoRvAdapter
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import com.mredrock.cyxbs.ufield.lyt.viewmodel.fragment.TodoViewModel
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
                Log.d("iniRv", "测试结果-->> $it");
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
                        Log.d("96366", "测试结果-->> $position");
                        mViewModel.apply {
                            passActivity(mDataList[position].activity_id)
                            Log.d("logTest", "测试结果-->> ${mDataList[position].activity_id}");
                            getTodoData()
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
                        mViewModel.apply {
                            rejectActivity(mDataList[position].activity_id)
                            getTodoData()
                            notifyDataSetChanged()
                        }
                    }
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
            //下拉刷新
            setOnRefreshListener {


                mRefresh.finishRefresh(1000)


            }
            //上拉加载
            setOnLoadMoreListener {
                mRefresh.finishLoadMore(500)
                toast("已经加载到底啦")
            }
        }

    }

}