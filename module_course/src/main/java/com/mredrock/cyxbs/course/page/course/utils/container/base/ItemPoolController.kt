package com.mredrock.cyxbs.course.page.course.utils.container.base

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import java.util.*

/**
 * 管理 Item 回收池
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/11 17:28
 */
@Suppress("UNCHECKED_CAST")
abstract class ItemPoolController<Item, Data : Any>(
  wrapper: ICourseWrapper
) : DiffRefreshController<Data>() where Item : IRecycleItem, Item : IDataOwner<Data> {
  
  // 旧数据与 item 的 map
  protected val mOldDataMap = hashMapOf<Data, Item>()
  
  // 回收的 item 池子
  protected val mRecyclePool = hashSetOf<Item>()
  
  // 在父布局中显示的 item
  protected val mShowItems = hashSetOf<Item>()
  
  init {
    // 课表生命周期的监听
    // 因为使用这个类的一般是 Fragment，他与 View 的生命周期不一致，需要在 View 被摧毁时清空对 item 的引用
    wrapper.addCourseLifecycleObservable(
      object : ICourseWrapper.CourseLifecycleObserver {
        
        // 添加 item 的监听
        // 这里需要以添加监听的方式来修改 mOldDataMap
        // 因为 item 存在被其他地方移除的情况
        val listener = object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem, view: View?) {
            if (itemClass.isInstance(item)) {
              item as Item
              val data = item.data
              if (!mOldDataMap.containsKey(data)) {
                // 不包含的时候说明是通过其他方式添加进来的
                mOldDataMap[data] = item
                addNewDataIntoOldList(data)
              }
              if (view != null) {
                mShowItems.add(item)
              }
            }
          }
  
          override fun onItemRemovedAfter(item: IItem, view: View?) {
            if (itemClass.isInstance(item)) {
              item as Item
              // 当你使用其他方式移除时
              val data = item.data
              if (mOldDataMap.remove(data) != null) {
                // 移除成功说明是通过其他方式移除的
                if (item.onRecycle()) {
                  mRecyclePool.add(item)
                }
                removeDataFromOldList(data) // 这里是通过其他方式删除的
              }
              if (view != null) {
                mShowItems.remove(item)
              }
            }
          }
        }
        
        override fun onCreateCourse(course: ICourseViewGroup) {
          course.addItemExistListener(listener)
        }
  
        override fun onDestroyCourse(course: ICourseViewGroup) {
          // Fragment 的 View 被摧毁，它的子 View 也应该一起被废弃掉
          mOldDataMap.clear()
          mRecyclePool.clear()
          mShowItems.clear()
          clearDataFromOldList()
          course.removeItemExistListener(listener)
        }
      }, true
    )
  }
  
  protected abstract val itemClass: Class<Item>
  
  protected abstract fun addItem(item: Item)
  
  protected abstract fun removeItem(item: Item)
  
  protected abstract fun newItem(data: Data): Item
  
  override fun onInserted(newData: Data) {
    val item = newData.getFreeAffair()
    mOldDataMap[newData] = item
    // 这个 addItem() 需要放在 mOldDataMap[newData] = item 后
    addItem(item)
  }
  
  override fun onRemoved(oldData: Data) {
    val item = mOldDataMap[oldData]
    if (item != null) {
      mOldDataMap.remove(oldData)
      if (item.onRecycle()) {
        mRecyclePool.add(item)
      }
      // removeItem(item) 需要放在 mOldDataMap.remove() 和 mFreePool.add() 后
      // 因为在 onItemRemovedAfter() 回调中会再次 remove() 和 add()
      removeItem(item)
    }
  }
  
  override fun onChanged(oldData: Data, newData: Data) {
    val item = mOldDataMap.remove(oldData)
    if (item != null) {
      mOldDataMap[newData] = item
      item.setNewData(newData)
    }
    // 如果为 null 的话，说明 mOldDataMap 被提前 remove 了，可能是你提前把 view 给 remove 掉了
    // 然后触发了上面的 onItemRemovedAfter() 回调
  }
  
  /**
   * 得到空闲的 [Item]
   */
  private fun Data.getFreeAffair(): Item {
    val iterator = mRecyclePool.iterator()
    while (iterator.hasNext()) {
      val next = iterator.next()
      if (next.onReuse()) {
        iterator.remove()
        next.setNewData(this)
        return next
      }
    }
    return newItem(this)
  }
  
  protected fun getShowItems(): Set<Item> {
    return Collections.unmodifiableSet(mShowItems)
  }
  
  override fun replaceDataFromOldList(oldData: Data, newData: Data) {
    super.replaceDataFromOldList(oldData, newData)
    val item = mOldDataMap.remove(oldData)
    if (item != null) {
      mOldDataMap[newData] = item
      item.setNewData(newData) // 重新设置 item 的数据
    }
  }
}