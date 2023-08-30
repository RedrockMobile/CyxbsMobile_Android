package com.mredrock.cyxbs.course.page.course.utils.container.base

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

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
  
  val currentData: List<Data>
    get() = mAsyncListDiffer.currentList
  
  /**
   * 异步差分刷新
   */
  fun diffRefresh(newData: List<Data>, action: (() -> Unit)? = null) {
    val oldData = mOldData
    val data = ArrayList(newData) // 不能直接使用传来的数据
    mAsyncListDiffer.submitList(data) {
      mOldData = data // 保存旧数据，提供给下次刷新使用
      action?.invoke()
      onDiffRefreshOver(oldData, data)
    }
  }
  
  /**
   * 用 [newData] 替换 [oldData]，位置不变
   */
  @CallSuper
  open fun replaceDataFromOldList(oldData: Data, newData: Data) {
    val index = mOldData.indexOf(oldData)
    mOldData.removeAt(index)
    mOldData.add(index, newData)
  }
  
  /**
   * 从旧数据集合中增加 data，应该在被意外删除时调用
   */
  protected fun addNewDataIntoOldList(data: Data) {
    mOldData.add(data)
  }
  
  /**
   * 从旧数据集合中移除 data，应该在被意外删除时调用
   */
  protected fun removeDataFromOldList(data: Data) {
    mOldData.remove(data)
  }
  
  /**
   * 从旧数据集合中清空 data，应该在 Fragment 回调 onDestroyView() 时调用
   */
  protected fun clearDataFromOldList() {
    mOldData.clear()
  }
  
  protected abstract fun onInserted(newData: Data)
  
  protected abstract fun onRemoved(oldData: Data)
  
  protected abstract fun onChanged(oldData: Data, newData: Data)
  
  /**
   * 在差分刷新结束时回调
   */
  protected open fun onDiffRefreshOver(oldData: List<Data>, newData: List<Data>) {}
  
  @Suppress("LeakingThis")
  private val mAsyncListDiffer = AsyncListDiffer(
    object : ListUpdateCallback {
      override fun onInserted(position: Int, count: Int) {
        repeat(count) {
          onInserted(currentData[position + it])
        }
      }
      
      override fun onRemoved(position: Int, count: Int) {
        repeat(count) {
          onRemoved(mOldData[position + it])
        }
      }
      
      override fun onMoved(fromPosition: Int, toPosition: Int) {
        val data = mOldData.removeAt(fromPosition)
        mOldData.add(toPosition, data)
      }
      
      override fun onChanged(position: Int, count: Int, payload: Any?) {
        repeat(count) {
          onChanged(mOldData[position + it], currentData[position + it])
        }
      }
    }, AsyncDifferConfig.Builder(this).build()
  )
}