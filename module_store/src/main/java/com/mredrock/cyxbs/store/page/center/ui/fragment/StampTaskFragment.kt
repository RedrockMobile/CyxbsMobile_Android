package com.mredrock.cyxbs.store.page.center.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.api.store.IStoreService
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.page.center.ui.item.StampTaskListItem
import com.mredrock.cyxbs.store.page.center.ui.item.StampTaskTitleItem
import com.mredrock.cyxbs.store.page.center.viewmodel.StoreCenterViewModel
import com.mredrock.cyxbs.store.utils.SimpleRvAdapter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/14
 */
class StampTaskFragment : BaseFragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mImageView: ImageView
    private lateinit var mTextView: TextView

    private val mActivityViewModel by activityViewModels<StoreCenterViewModel>()

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

        if (mIsFragmentRebuilt) {
            // 如果是异常重启的 Fragment，则直接设置 Adapter，否则在下面的观察中设置
            setAdapter()
        }

        mActivityViewModel.loadStampTaskRvEvent.collectLaunch {
            if (mRecyclerView.adapter == null) {
                setAdapter() // 为什么在这里设置 Adapter? 可以点进去看这个回调上写的注释
            }
        }

        mActivityViewModel.refreshIsSuccessfulState.observe {
            if (it) {
                // 取消断网图片的显示
                mImageView.invisible()
                mTextView.invisible()
                mRecyclerView.visible()
            } else {
                // 显示断网图片
                mImageView.visible()
                mTextView.visible()
                mRecyclerView.gone()
                mImageView.setImageResource(R.drawable.store_ic_no_internet)
                mTextView.text = getText(R.string.store_no_internet)
            }
        }

        mActivityViewModel.stampCenterData.observe {
            if (it.task == null) {
                resetData(emptyList())
            } else {
                resetData(it.task) // 重新设置数据
            }
            if (mRecyclerView.adapter != null) {
                refreshAdapter() // 再次得到数据时刷新
            }
        }
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
                IStoreService.TaskType.BASE.type -> baseList.add(task)
                IStoreService.TaskType.MORE.type -> moreList.add(task)
            }
        }
        titleMap[0] = "每日任务"
        titleMap[baseList.size + 1] = "更多任务"
        for (i in baseList.indices) {
            taskMap[i + 1] = baseList[i]
        }
        for (i in moreList.indices) {
            taskMap[baseList.size + 2 + i] = moreList[i]
        }
    }
}