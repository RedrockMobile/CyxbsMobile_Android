package com.cyxbsmobile_single.module_todo.adapter.cnn

import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper

/**
 * Author: RayleighZ
 * Time: 2021-08-09 20:25
 */

fun getFakeData(): List<TodoItemWrapper> = listOf(
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TITLE,
            "待办"
        ),
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TODO,
            todo = Todo(
                1,
                "今日待办事项",
                114514191,
                0,
                "详情",
                false,
                1145141919
            )
        ),
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TODO,
            todo = Todo(
                1,
                "今日待办事项",
                114514191,
                0,
                "详情",
                false,
                1145141919
            )
        ),
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TODO,
            todo = Todo(
                1,
                "今日待办事项",
                114514191,
                0,
                "详情",
                false,
                1145141919
            )
        ),
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TITLE,
            "已完成"
        ),
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TODO,
            todo = Todo(
                1,
                "今日待办事项",
                114514191,
                0,
                "详情",
                false,
                1145141919
            )
        ),
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TODO,
            todo = Todo(
                1,
                "今日待办事项",
                114514191,
                0,
                "详情",
                false,
                1145141919
            )
        ),
        TodoItemWrapper(
            DoubleListFoldRvAdapter.TODO,
            todo = Todo(
                1,
                "今日待办事项",
                114514191,
                0,
                "详情",
                false,
                1145141919
            )
        )
    )
