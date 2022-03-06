package com.cyxbsmobile_single.module_todo.model.bean

import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-05 16:32
 * 适用于DoubleListFoldableRvAdapter的数据类
 * viewType用于标识不同item类型
 */
data class TodoItemWrapper(
    val viewType: Int,
    var title: String? = null,
    var todo: Todo? = null
) : Serializable {

    companion object {
        fun titleWrapper(title: String): TodoItemWrapper =
            TodoItemWrapper(DoubleListFoldRvAdapter.TITLE, title)

        fun todoWrapper(todo: Todo): TodoItemWrapper =
            TodoItemWrapper(DoubleListFoldRvAdapter.TODO, todo = todo)
    }
}