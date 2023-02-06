package com.mredrock.cyxbs.course.page.course.utils.container.base

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import java.util.Collections

/**
 * 差分比较刷新
 *
 * ## 原因
 * - 因为关联人课程可进行隐藏操作，但自己的课、关联人的课和事务应该被合并成一条 流 添加进课表，所以需要增加差分刷新功能
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/11 14:47
 */
abstract class DiffRefreshController<Data : Any> : DiffUtil.ItemCallback<Data>() {
  
  private var mOldData = arrayListOf<Data>()
  private var mNewData = arrayListOf<Data>()
  
  val currentData: List<Data>
    get() = mAsyncListDiffer.currentList
  
  /**
   * 异步差分刷新
   */
  fun diffRefresh(newData: List<Data>, action: ((List<Data>) -> Unit)? = null) {
    mNewData = ArrayList(newData) // 不能直接使用传来的数据
    mAsyncListDiffer.submitList(newData) {
      mOldData = mNewData // 保存旧数据，提供给下次刷新使用
      action?.invoke(Collections.unmodifiableList(mNewData))
    }
  }
  
  /**
   * 从旧数据集合中增加 data，应该在被意外删除时调用
   */
  fun addNewDataIntoOldList(data: Data) {
    mOldData.add(data)
  }
  
  /**
   * 从旧数据集合中移除 data，应该在被意外删除时调用
   */
  fun removeDataFromOldList(data: Data) {
    mOldData.remove(data)
  }
  
  /**
   * 从旧数据集合中清空 data，应该在 Fragment 回调 onDestroyView() 时调用
   */
  fun clearDataFromOldList() {
    mOldData.clear()
  }
  
  protected abstract fun onInserted(newData: Data)
  
  protected abstract fun onRemoved(oldData: Data)
  
  protected abstract fun onChanged(oldData: Data, newData: Data)
  
  @Suppress("LeakingThis")
  private val mAsyncListDiffer = AsyncListDiffer(
    object : ListUpdateCallback {
      override fun onInserted(position: Int, count: Int) {
        repeat(count) {
          onInserted(mNewData[position + it])
        }
      }
      
      override fun onRemoved(position: Int, count: Int) {
        repeat(count) {
          onRemoved(mOldData[position + it])
        }
      }
      
      override fun onMoved(fromPosition: Int, toPosition: Int) {
        onChanged(mOldData[fromPosition], mNewData[toPosition])
      }
      
      override fun onChanged(position: Int, count: Int, payload: Any?) {
        repeat(count) {
          onChanged(mOldData[position + it], mNewData[position + it])
        }
      }
    }, AsyncDifferConfig.Builder(this).build()
  )
}