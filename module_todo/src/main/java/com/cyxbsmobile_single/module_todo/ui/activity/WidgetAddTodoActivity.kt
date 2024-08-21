package com.cyxbsmobile_single.module_todo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.TODO_ADD_TODO_BY_WIDGET

//这里不使用BaseActivity的原因是需要设置成透明的theme
@Route(path = TODO_ADD_TODO_BY_WIDGET)
class WidgetAddTodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}