package com.mredrock.cyxbs.course.page.course.utils.container.base

import androidx.collection.ArraySet
import androidx.collection.arraySetOf
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/11 14:47
 */
abstract class BaseDiffRefresh<Data : Any> : DiffUtil.ItemCallback<Data>() {
  
  // 使用 set 去重，因为后面差分需要 valueAt()，所以综合考虑下使用 ArraySet 比较合适
  private var mOldData: ArraySet<Data> = arraySetOf()
  private lateinit var mNewData: ArraySet<Data>
  
  val currentData: List<Data>
    get() = mAsyncListDiffer.currentList
  
  /**
   * 异步差分刷新
   */
  fun diffRefresh(newData: List<Data>) {
    mNewData = ArraySet(newData)
    mAsyncListDiffer.submitList(newData) {
      mOldData = mNewData // 保存旧数据，提供给下次刷新使用
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
          onInserted(mNewData.valueAt(position + it))
        }
      }
      
      override fun onRemoved(position: Int, count: Int) {
        repeat(count) {
          onRemoved(mOldData.valueAt(position + it))
        }
      }
      
      override fun onMoved(fromPosition: Int, toPosition: Int) {
        onChanged(mOldData.valueAt(fromPosition), mNewData.valueAt(toPosition))
      }
      
      override fun onChanged(position: Int, count: Int, payload: Any?) {
        repeat(count) {
          onChanged(mOldData.valueAt(position + it), mNewData.valueAt(position + it))
        }
      }
    }, AsyncDifferConfig.Builder(this).build()
  )
}