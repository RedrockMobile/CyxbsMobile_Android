package com.mredrock.cyxbs.store.page.center.ui.item

import android.content.res.ColorStateList
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemStampTaskListBinding
import com.mredrock.cyxbs.store.utils.SimpleRvAdapter
import com.mredrock.cyxbs.store.utils.StoreType

/**
 * 自己写了个用于解耦不同的 item 的 Adapter 的封装类, 详情请看 [SimpleRvAdapter]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/9
 */
class StampTaskListItem(
    taskMap: Map<Int, StampCenter.Task>
) : SimpleRvAdapter.DBItem<StoreRecyclerItemStampTaskListBinding, StampCenter.Task>(
    taskMap, R.layout.store_recycler_item_stamp_task_list
) {

    /**
     * 该方法调用了 [diffRefreshAllItemMap] 用于自动刷新
     *
     * 因为我在 Item 中整合了 DiffUtil 自动刷新, 只有你全部的 Item 都调用了 [diffRefreshAllItemMap],
     * 就会自动启动 DiffUtil
     */
    fun resetData(taskMap: Map<Int, StampCenter.Task>) {
        diffRefreshAllItemMap(taskMap,
            isSameName = { oldData, newData ->
                // 这个是判断新旧数据中 张三 是否是 张三 (可以点进去看注释)
                oldData.type == newData.type
                        && oldData.title == newData.title
                        && oldData.maxProgress == newData.maxProgress
            },
            isSameData = {oldData, newData ->
                oldData == newData
            }
        )
    }

    private var mInitialRippleColor: ColorStateList? = null
    override fun onCreate(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRvAdapter.BindingVH,
        map: Map<Int, StampCenter.Task>
    ) {
        // 记录按钮默认的水波纹颜色, 因为后面要还原
        mInitialRippleColor = binding.storeBtnStampTaskListGo.rippleColor
        binding.storeBtnStampTaskListGo.setOnSingleClickListener {
            // 点击事件的跳转
            val position = holder.layoutPosition
            val task = map[position]
            if (task != null) {
                StoreType.Task.jumpOtherUi(it.context, task) // 跳转统一写在这个类里
            }
        }
    }

    override fun onRefactor(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRvAdapter.BindingVH,
        position: Int,
        value: StampCenter.Task
    ) {
        binding.task = value
        binding.storeProgressBarStampTask.post { // 不加 post 就不显示进度条加载动画, 很奇怪
            binding.storeProgressBarStampTask.setProgressCompat(
                value.currentProgress, value.currentProgress != 0
            )
        }
        if (value.currentProgress != value.maxProgress) {
            if (position == 0) {
                binding.storeBtnStampTaskListGo.text = "去签到"
            }else {
                binding.storeBtnStampTaskListGo.text = "去完成"
            }

            // 因为复用的原因, 在下面设置后要还原
            binding.storeBtnStampTaskListGo.rippleColor = mInitialRippleColor
        }else {
            binding.storeBtnStampTaskListGo.text = "已完成"
            // 设置点击效果的水波纹颜色为透明, 相当于禁用水波纹
            binding.storeBtnStampTaskListGo.rippleColor = ColorStateList.valueOf(0x00000000)
        }
    }

    override fun onViewRecycled(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRvAdapter.BindingVH
    ) {
        super.onViewRecycled(binding, holder)
        // 当 item 被回收时就设置进度为 0, 防止因为任务过多在滑回来时而出现 item 复用时闪进度的 bug
        binding.storeProgressBarStampTask.progress = 0
    }
}