/**
 * @Project: CyxbsMobile_Android
 * @File: SwipeCallback
 * @Author: 86199
 * @Date: 2024/8/14
 * @Description: recycleview侧滑
 */
import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R

class SwipeCallback : ItemTouchHelper.Callback() {

    private var currentStatus = CLOSE
    private var itemView: View? = null
    private var deleteButton: View? = null
    private var mainItem: View? = null
    private var deleteButtonWidth = 0f

    companion object {
        const val OPEN = 1
        const val CLOSE = 0
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 不处理直接删除
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.5f
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        itemView = viewHolder.itemView
        deleteButton = itemView?.findViewById(R.id.todo_delete)
        mainItem = itemView?.findViewById(R.id.todo_item_main)

        // 获取删除按钮的宽度
        deleteButtonWidth = deleteButton?.width?.toFloat() ?: 0f

        if (dX < 0) { // 左滑
            deleteButton?.visibility = View.VISIBLE

            // 只允许滑动到显示删除按钮的宽度
            val translationX = dX.coerceAtLeast(-deleteButtonWidth)
            mainItem?.translationX = translationX

            if (translationX == -deleteButtonWidth) {
                currentStatus = OPEN
            }
        } else if (dX > 0 && currentStatus == OPEN) { // 右滑
            // 当删除按钮可见时允许右滑
            val translationX = dX.coerceAtMost(0f)
            mainItem?.translationX = translationX

            if (translationX == 0f) {
                currentStatus = CLOSE
                deleteButton?.visibility = View.GONE
            }
        } else {
            // 禁止继续左滑
            mainItem?.translationX = 0f
        }
    }
}
