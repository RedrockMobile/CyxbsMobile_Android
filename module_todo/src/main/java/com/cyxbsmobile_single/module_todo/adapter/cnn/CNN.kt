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

fun getUncheckTodoList(): List<Todo> =
    listOf(
        Todo(
            1,
            "今日待办事项1",
            114514191,
            0,
            "详情",
            false,
            1145141919
        ),
        Todo(
            2,
            "今日待办事项2",
            114514191,
            0,
            "详情",
            false,
            1145141919
        ),
        Todo(
            3,
            "今日待办事项3",
            114514191,
            0,
            "详情",
            false,
            1145141919
        )
    )
fun getCheckTodoList(): List<Todo> = listOf(
    Todo(
        4,
        "今日待办事项4",
        114514191,
        0,
        "详情",
        true,
        1145141919
    ),
    Todo(
        5,
        "今日待办事项5",
        114514191,
        0,
        "详情",
        true,
        1145141919
    ),
    Todo(
        6,
        "今日待办事项6",
        114514191,
        0,
        "详情",
        true,
        1145141919
    )
)