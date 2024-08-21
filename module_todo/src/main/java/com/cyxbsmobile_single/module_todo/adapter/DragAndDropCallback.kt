import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.adapter.SwipeDeleteRecyclerView

/**
 * @Project: CyxbsMobile_Android
 * @File: DragCallback
 * @Author: 86199
 * @Date: 2024/8/14
 * @Description: 用于 item 上下拖动
 */
class DragAndDropCallback(
    private val recyclerView: SwipeDeleteRecyclerView,
    private val adapter: TodoAllAdapter
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (this.recyclerView.isMenuOpen()) {
            return 0
        }

        // 获取当前项
        val position = viewHolder.bindingAdapterPosition
        val item = adapter.getItem(position)

        // 检查当前项是否在置顶列表中
        val isPinned = adapter.pinnedItems.contains(item)

        // 如果是置顶项，则不允许拖动
        if (isPinned) {
            return 0
        }

        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = source.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition

        // 获取拖动的项
        val item = adapter.getItem(fromPosition)
        val targetItem = adapter.getItem(toPosition)

        // 如果源项或目标项在置顶列表中，则不允许拖动
        val isPinnedSource = adapter.pinnedItems.contains(item)
        val isPinnedTarget = adapter.pinnedItems.contains(targetItem)

        if (isPinnedSource || isPinnedTarget) {
            return false
        }

        (adapter as? ItemTouchHelperAdapter)?.onItemMove(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 不处理侧滑操作
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true // 允许长按拖动
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true // 禁止侧滑
    }
}


interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
}


