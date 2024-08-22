package com.cyxbsmobile_single.module_todo.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity


@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity : BaseViewModelActivity<TodoViewModel>() {
}