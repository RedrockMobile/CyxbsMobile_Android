package com.mredrock.cyxbs.store.page.center.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.page.center.ui.item.StampTaskListItem
import com.mredrock.cyxbs.store.page.center.ui.item.StampTaskTitleItem
import com.mredrock.cyxbs.store.page.center.viewmodel.StoreCenterViewModel
import com.mredrock.cyxbs.store.utils.Type
import com.mredrock.cyxbs.store.base.SimpleRvAdapter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/14
 */
class StampTaskFragment : BaseFragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mImageView: ImageView
    private lateinit var mTextView: TextView

    // 因为我只需要 Activity 的 ViewModel, 所以没有继承于 BaseViewModelFragment
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(requireActivity()).get(StoreCenterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.store_item_stamp_task, container, false)
    }

    // 建立 ViewModel 的数据观察
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initObserve()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.store_rv_fragment_stamp_task)
        mImageView = view.findViewById(R.id.store_iv_fragment_stamp_task)
        mTextView = view.findViewById(R.id.store_tv_fragment_stamp_task)
    }

    private fun initObserve() {
        viewModel.refreshIsSuccessful.observe(viewLifecycleOwner, Observer {
            if (it) {
                mImageView.gone()
                mTextView.gone()
                mRecyclerView.visible()
            }else {
                mImageView.visible()
                mTextView.visible()
                mRecyclerView.gone()
                mImageView.setImageResource(R.drawable.store_ic_no_internet)
                mTextView.text = getText(R.string.store_no_internet)
            }
        })

        viewModel.stampCenterData.observe(viewLifecycleOwner, Observer {
            if (it.task == null) {
                resetData(emptyList())
            }else {
                resetData(it.task) // 重新设置数据
            }
            if (mRecyclerView.adapter == null) {
                setAdapter() // 第一次得到数据时设置 adapter
            }else {
                refreshAdapter() // 再次得到数据时刷新
            }
        })
    }

    private lateinit var mStampTaskTitleItem: StampTaskTitleItem
    private lateinit var mStampTaskListItem: StampTaskListItem
    private fun setAdapter() {
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mStampTaskTitleItem = StampTaskTitleItem(titleMap)
        mStampTaskListItem = StampTaskListItem(taskMap)
        mRecyclerView.adapter = SimpleRvAdapter() // 自己写的解耦 Adapter 的封装类, 可用于解耦不同的 item
            .addItem(mStampTaskTitleItem)
            .addItem(mStampTaskListItem)
            .show()
        mRecyclerView.layoutAnimation =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.store_slide_from_left_to_right_in
                )
            )
    }

    // 用于再次得到数据后的刷新, 我在 Item 中整合了 DiffUtil 的自动刷新, 不用再使用 notifyDataSetChanged()
    private fun refreshAdapter() {
        mStampTaskTitleItem.resetData(titleMap)
        mStampTaskListItem.resetData(taskMap)
    }

    private val baseList = ArrayList<StampCenter.Task>()
    private val moreList = ArrayList<StampCenter.Task>()
    private val titleMap = HashMap<Int, String>() // adapter 的 position 与标题的映射
    private val taskMap = HashMap<Int, StampCenter.Task>() // adapter 的 position 与任务的映射
    private fun resetData(tasks: List<StampCenter.Task>) {
        titleMap.clear()
        taskMap.clear()
        baseList.clear()
        moreList.clear()
        for (task in tasks) { // 后端返回的 type = "base" 时为每日任务, type = "more" 时为更多任务
            when (task.type) {
                Type.Task.Base -> baseList.add(task)
                Type.Task.More -> moreList.add(task)
            }
        }
        titleMap[baseList.size] = "更多任务"
        for (i in baseList.indices) {
            taskMap[i] = baseList[i]
        }
        for (i in moreList.indices) {
            taskMap[baseList.size + 1 + i] = moreList[i]
        }
    }
}